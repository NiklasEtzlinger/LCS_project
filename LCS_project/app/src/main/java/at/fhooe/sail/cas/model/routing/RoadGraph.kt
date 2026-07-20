package at.fhooe.sail.cas.model.routing

import at.fhooe.sail.cas.model.util.mercatorGroundDistance
import java.util.PriorityQueue

/**
 * Ungerichteter Straßengraph in EPSG:3857.
 *
 * Die Knoten entstehen aus den Stützpunkten der OSM-Linien: Punkte mit
 * identischen Koordinaten (auf [QUANT_CM] cm gerundet) werden zu einem Knoten
 * verschmolzen. Da OSM-Wege sich an Kreuzungen echte Knoten teilen, entsteht so
 * ohne weitere Vorverarbeitung ein zusammenhängendes Netz; Brücken und Tunnel
 * bleiben korrekt unverbunden, weil sie keine gemeinsamen Punkte besitzen.
 *
 * Die Nachbarschaft ist als CSR-Struktur abgelegt (Offset-Array + flache
 * Ziel-/Gewichts-Arrays), damit auch ~75.000 Knoten ohne Objekt-Overhead und
 * ohne Boxing in den Speicher passen.
 */
class RoadGraph(
    private val xs: DoubleArray,
    private val ys: DoubleArray,
    private val adjOffset: IntArray,
    private val adjTarget: IntArray,
    private val adjWeight: FloatArray,
    private val component: IntArray,
    private val mainComponent: Int
) {

    val nodeCount: Int get() = xs.size

    /** Kanten sind in beide Richtungen abgelegt */
    val edgeCount: Int get() = adjTarget.size / 2

    val mainComponentNodeCount: Int get() = component.count { it == mainComponent }

    fun nodeX(node: Int): Double = xs[node]

    fun nodeY(node: Int): Double = ys[node]

    /**
     * Nächstgelegener Knoten zu einem Punkt. Standardmäßig werden nur Knoten der
     * größten Zusammenhangskomponente berücksichtigt – sonst könnte ein POI auf
     * eine isolierte Insel (z. B. eine abgeschnittene Parkplatzgasse) gesnappt
     * werden, von der aus es keine Route gibt.
     */
    fun nearestNode(x: Double, y: Double, mainComponentOnly: Boolean = true): Int {
        var best: Int = -1
        var bestDistSq: Double = Double.MAX_VALUE
        for (i in xs.indices) {
            if (mainComponentOnly && component[i] != mainComponent) continue
            val dx: Double = xs[i] - x
            val dy: Double = ys[i] - y
            val distSq: Double = dx * dx + dy * dy
            if (distSq < bestDistSq) {
                bestDistSq = distSq
                best = i
            }
        }
        return best
    }

    /**
     * Kürzester Weg mit A*; Heuristik ist die Luftlinie in Metern und damit
     * zulässig (nie größer als die tatsächliche Wegstrecke).
     */
    fun findRoute(startNode: Int, goalNode: Int): Route? {
        if (startNode < 0 || goalNode < 0 || startNode >= nodeCount || goalNode >= nodeCount) {
            return null
        }
        if (startNode == goalNode) {
            return Route(
                floatArrayOf(xs[startNode].toFloat()),
                floatArrayOf(ys[startNode].toFloat()),
                0f
            )
        }

        val gScore = DoubleArray(nodeCount) { Double.MAX_VALUE }
        val cameFrom = IntArray(nodeCount) { -1 }
        val closed = BooleanArray(nodeCount)
        val open = PriorityQueue<Entry>(compareBy { it.f })

        gScore[startNode] = 0.0
        open.add(Entry(startNode, heuristic(startNode, goalNode)))

        while (open.isNotEmpty()) {
            val current: Int = open.poll()?.node ?: break
            if (closed[current]) continue // veralteter Eintrag (lazy deletion)
            if (current == goalNode) {
                return reconstruct(cameFrom, goalNode, gScore[goalNode])
            }
            closed[current] = true

            for (e in adjOffset[current] until adjOffset[current + 1]) {
                val neighbour: Int = adjTarget[e]
                if (closed[neighbour]) continue
                val tentative: Double = gScore[current] + adjWeight[e]
                if (tentative < gScore[neighbour]) {
                    gScore[neighbour] = tentative
                    cameFrom[neighbour] = current
                    open.add(Entry(neighbour, tentative + heuristic(neighbour, goalNode)))
                }
            }
        }
        return null // Ziel nicht erreichbar
    }

    private fun heuristic(from: Int, to: Int): Double =
        mercatorGroundDistance(xs[from], ys[from], xs[to], ys[to])

    private fun reconstruct(cameFrom: IntArray, goal: Int, length: Double): Route {
        var count = 1
        var cur: Int = goal
        while (cameFrom[cur] != -1) {
            cur = cameFrom[cur]
            count++
        }
        val rx = FloatArray(count)
        val ry = FloatArray(count)
        cur = goal
        for (i in count - 1 downTo 0) {
            rx[i] = xs[cur].toFloat()
            ry[i] = ys[cur].toFloat()
            if (cameFrom[cur] != -1) cur = cameFrom[cur]
        }
        return Route(rx, ry, length.toFloat())
    }

    private class Entry(val node: Int, val f: Double)

    /** Ergebnisroute als Punktfolge in EPSG:3857 plus Länge in Metern */
    class Route(val x: FloatArray, val y: FloatArray, val lengthMeters: Float) {
        val pointCount: Int get() = x.size
    }

    /**
     * Sammelt Linienzüge und baut daraus den Graphen.
     * Wächst mit primitiven Arrays, um Boxing bei ~85.000 Stützpunkten zu vermeiden.
     */
    class Builder {
        private val nodeIndex = HashMap<Long, Int>(1 shl 17)
        private var xs = DoubleArray(1 shl 14)
        private var ys = DoubleArray(1 shl 14)
        private var nodes = 0

        private var edgeA = IntArray(1 shl 14)
        private var edgeB = IntArray(1 shl 14)
        private var edgeW = FloatArray(1 shl 14)
        private var edges = 0

        /** Einen Linienzug (x/y-Paare in EPSG:3857) aufnehmen */
        fun addLine(points: List<DoubleArray>) {
            if (points.size < 2) return
            var prev: Int = nodeFor(points[0][0], points[0][1])
            for (i in 1 until points.size) {
                val cur: Int = nodeFor(points[i][0], points[i][1])
                if (cur != prev) {
                    addEdge(prev, cur)
                    prev = cur
                }
            }
        }

        private fun nodeFor(x: Double, y: Double): Int {
            val key: Long = (Math.round(x * QUANT_CM) shl 32) or
                    (Math.round(y * QUANT_CM) and 0xFFFFFFFFL)
            nodeIndex[key]?.let { return it }
            if (nodes == xs.size) {
                xs = xs.copyOf(nodes * 2)
                ys = ys.copyOf(nodes * 2)
            }
            xs[nodes] = x
            ys[nodes] = y
            nodeIndex[key] = nodes
            return nodes++
        }

        private fun addEdge(a: Int, b: Int) {
            if (edges == edgeA.size) {
                edgeA = edgeA.copyOf(edges * 2)
                edgeB = edgeB.copyOf(edges * 2)
                edgeW = edgeW.copyOf(edges * 2)
            }
            edgeA[edges] = a
            edgeB[edges] = b
            edgeW[edges] = mercatorGroundDistance(xs[a], ys[a], xs[b], ys[b]).toFloat()
            edges++
        }

        fun build(): RoadGraph {
            val nx: DoubleArray = xs.copyOf(nodes)
            val ny: DoubleArray = ys.copyOf(nodes)

            // CSR aufbauen: Grade zählen -> Präfixsumme -> Ziele einsortieren
            val offset = IntArray(nodes + 1)
            for (i in 0 until edges) {
                offset[edgeA[i]]++
                offset[edgeB[i]]++
            }
            var running = 0
            for (i in 0 until nodes) {
                val degree: Int = offset[i]
                offset[i] = running
                running += degree
            }
            offset[nodes] = running

            val cursor: IntArray = offset.copyOf(nodes)
            val target = IntArray(running)
            val weight = FloatArray(running)
            for (i in 0 until edges) {
                val a: Int = edgeA[i]
                val b: Int = edgeB[i]
                target[cursor[a]] = b
                weight[cursor[a]] = edgeW[i]
                cursor[a]++
                target[cursor[b]] = a
                weight[cursor[b]] = edgeW[i]
                cursor[b]++
            }

            val (component, main) = findComponents(nodes, offset, target)
            return RoadGraph(nx, ny, offset, target, weight, component, main)
        }

        /** Zusammenhangskomponenten per iterativer Tiefensuche */
        private fun findComponents(
            nodes: Int,
            offset: IntArray,
            target: IntArray
        ): Pair<IntArray, Int> {
            val component = IntArray(nodes) { -1 }
            val stack = IntArray(nodes)
            var currentComponent = 0
            var mainComponent = 0
            var mainSize = 0

            for (start in 0 until nodes) {
                if (component[start] != -1) continue
                var top = 0
                stack[top++] = start
                component[start] = currentComponent
                var size = 0
                while (top > 0) {
                    val node: Int = stack[--top]
                    size++
                    for (e in offset[node] until offset[node + 1]) {
                        val nb: Int = target[e]
                        if (component[nb] == -1) {
                            component[nb] = currentComponent
                            stack[top++] = nb
                        }
                    }
                }
                if (size > mainSize) {
                    mainSize = size
                    mainComponent = currentComponent
                }
                currentComponent++
            }
            return component to mainComponent
        }
    }

    companion object {
        /** Rundung der Koordinaten beim Verschmelzen: 100 = 1 cm */
        const val QUANT_CM: Double = 100.0
    }
}

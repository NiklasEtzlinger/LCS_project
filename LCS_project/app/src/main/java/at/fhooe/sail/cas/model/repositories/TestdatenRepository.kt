package at.fhooe.sail.mc.cas.model.repositories

import android.graphics.Path
import at.fhooe.sail.cas.model.repositories.pschema.IPresSchema
import at.fhooe.sail.cas.model.repositories.pschema.TestdatenPresSchema
import at.fhooe.sail.cas.ui.viewmodel.features.GeoFeature

class TestdatenRepository {
    fun fetchFeatures( ): MutableList<GeoFeature>  {
        return mutableListOf(
            GeoFeature( // ein singulärer Punkt
                id = "Point_01",
                type = 1000,
                attributes = mapOf(),
                geometry = Path().apply {
                    addCircle(10f, 10f, 5f, Path.Direction.CW)
                }
            ),
            GeoFeature( // einfacher Linienzug
                id = "Line_01",
                type = 2000,
                attributes = mapOf(),
                geometry = Path().apply {
                    moveTo(30f, 10f)
                    lineTo(40f, 10f)
                    lineTo(40f, 20f)
                    lineTo(50f, 30f)
                }
            ),
            GeoFeature( // konvexes Polygon
                id = "Poly_01",
                type = 3000,
                attributes = mapOf(),
                geometry = Path().apply {
                    moveTo(60f, 10f)
                    lineTo(80f, 10f)
                    lineTo(80f, 30f)
                    lineTo(60f, 30f)
                    close()
                }
            ),

            GeoFeature( // konvexes Polygon mit Loch
                id = "Poly_02",
                type = 3000,
                attributes = mapOf(),
                geometry = Path().apply {
                    // Wichtig für Löcher!
                    fillType = Path.FillType.EVEN_ODD

                    // Außenring
                    moveTo(10f, 40f)
                    lineTo(40f, 40f)
                    lineTo(40f, 70f)
                    lineTo(10f, 70f)
                    close()

                    // Innenring (Loch)
                    moveTo(20f, 50f)
                    lineTo(30f, 50f)
                    lineTo(30f, 60f)
                    lineTo(20f, 60f)
                    close()
                }
            ),
            GeoFeature( // Multipolygon (konvex)
                id = "Poly_03",
                type = 3000,
                attributes = mapOf(),
                geometry = Path().apply {
                    fillType = Path.FillType.EVEN_ODD

                    // Polygon 1
                    moveTo(90f, 10f)
                    lineTo(110f, 10f)
                    lineTo(100f, 30f)
                    close()

                    // Polygon 2
                    moveTo(120f, 10f)
                    lineTo(130f, 30f)
                    lineTo(110f, 30f)
                    close()
                }
            ),
            GeoFeature( // Multipolygon (konvex) mit Loch
                id = "Poly_04",
                type = 3000,
                attributes = mapOf(),
                geometry = Path().apply {
                    fillType = Path.FillType.EVEN_ODD

                    // --- Polygon 1 (Außenring) ---
                    moveTo(60f, 40f)
                    lineTo(100f, 40f)
                    lineTo(100f, 70f)
                    lineTo(60f, 70f)
                    close()

                    // --- Polygon 1 (Loch) ---
                    moveTo(70f, 50f)
                    lineTo(90f, 50f)
                    lineTo(80f, 60f)
                    close()

                    // --- Polygon 2 ---
                    moveTo(110f, 40f)
                    lineTo(130f, 40f)
                    lineTo(130f, 70f)
                    lineTo(110f, 70f)
                    close()
                }
            ),
            GeoFeature( // Multipolygon (konvex) mit Löchern
                id = "Poly_05",
                type = 3000,
                attributes = mapOf(),
                geometry = Path().apply {
                    fillType = Path.FillType.EVEN_ODD

                    // ===== Polygon 1 =====
                    // Außenring
                    moveTo(10f, 80f)
                    lineTo(40f, 80f)
                    lineTo(40f, 130f)
                    lineTo(10f, 130f)
                    close()

                    // Loch 1
                    moveTo(20f, 90f)
                    lineTo(30f, 90f)
                    lineTo(30f, 100f)
                    lineTo(20f, 100f)
                    close()

                    // Loch 2
                    moveTo(20f, 110f)
                    lineTo(30f, 110f)
                    lineTo(30f, 120f)
                    lineTo(20f, 120f)
                    close()

                    // ===== Polygon 2 =====
                    // Außenring
                    moveTo(50f, 80f)
                    lineTo(90f, 80f)
                    lineTo(90f, 130f)
                    lineTo(50f, 130f)
                    close()

                    // Loch 1 (Dreieck)
                    moveTo(60f, 90f)
                    lineTo(80f, 90f)
                    lineTo(70f, 100f)
                    close()

                    // Loch 2 (Dreieck)
                    moveTo(60f, 110f)
                    lineTo(80f, 110f)
                    lineTo(70f, 120f)
                    close()
                }
            ),
        )
    }
    fun getPresSchema(): IPresSchema = TestdatenPresSchema()
}
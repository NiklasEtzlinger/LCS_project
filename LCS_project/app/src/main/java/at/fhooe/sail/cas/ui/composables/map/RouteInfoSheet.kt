package at.fhooe.sail.cas.ui.composables.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import at.fhooe.sail.cas.R
import at.fhooe.sail.cas.ui.theme.CASProjectTheme
import java.util.Locale
import kotlin.math.roundToInt

/** durchschnittliche Gehgeschwindigkeit in m/s (~5 km/h) */
private const val WALK_SPEED_MPS = 1.4f

/**
 * Sheet für die berechnete Route: Ziel, Gehzeit und Distanz.
 * Zeigt während der Berechnung einen Ladezustand und im Fehlerfall
 * eine Meldung – jeweils mit Schließen-Button.
 */
@Composable
fun RouteInfoSheet(
    modifier: Modifier = Modifier,
    targetName: String = "",
    distanceMeters: Float = 0f,
    calculating: Boolean = false,
    error: String? = null,
    onClose: () -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(32.dp)
                    .height(4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when {
                            error != null -> "Route nicht möglich"
                            calculating -> "Route wird berechnet …"
                            else -> formatDuration(distanceMeters)
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (error != null) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                    Text(
                        text = when {
                            error != null -> error
                            calculating -> "Straßennetz wird ausgewertet"
                            else -> "${formatDistance(distanceMeters)} zu Fuß  ·  $targetName"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (calculating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                }
                Icon(
                    painter = painterResource(R.drawable.close_24dp),
                    contentDescription = "Route schließen",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { onClose() }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

private fun formatDistance(meters: Float): String =
    if (meters >= 1000f) {
        String.format(Locale.getDefault(), "%.1f km", meters / 1000f)
    } else {
        "${meters.roundToInt()} m"
    }

private fun formatDuration(meters: Float): String {
    val minutes: Int = (meters / WALK_SPEED_MPS / 60f).roundToInt()
    if (minutes < 1) return "unter 1 Min."
    if (minutes < 60) return "$minutes Min."
    return "${minutes / 60} h ${minutes % 60} Min."
}

@Preview
@Composable
private fun RouteInfoSheetPreview() {
    CASProjectTheme {
        RouteInfoSheet(targetName = "Schlosskirche", distanceMeters = 1240f)
    }
}

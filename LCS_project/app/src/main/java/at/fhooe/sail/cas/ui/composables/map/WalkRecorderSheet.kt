package at.fhooe.sail.cas.ui.composables.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.HorizontalDivider
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

/**
 * Bottom sheet on the map screen for recording a walk: start/stop the
 * recording, follow the moving position, and see live stats (distance,
 * duration, average speed) while a walk is running.
 */
@Composable
fun WalkRecorderSheet(
    modifier: Modifier = Modifier,
    walkActive: Boolean = false,
    distanceMeters: Float = 0f,
    durationMillis: Long = 0L,
    followActive: Boolean = false,
    onStartStop: () -> Unit = {},
    onFollowToggle: () -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            // decorative drag handle
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
                        text = "Record walk",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (walkActive) "Recording…" else "Track your route",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (walkActive) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
                FilledIconToggleButton(
                    checked = followActive,
                    onCheckedChange = { onFollowToggle() }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.route_24dp),
                        contentDescription = if (followActive) {
                            "Stop following location"
                        } else {
                            "Follow location"
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onStartStop,
                    colors = if (walkActive) {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    } else {
                        ButtonDefaults.buttonColors()
                    }
                ) {
                    Text(if (walkActive) "Stop" else "Start")
                }
            }

            // live stats, revealed while recording
            AnimatedVisibility(
                visible = walkActive,
                enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        WalkStat(value = formatDistance(distanceMeters), label = "Distance")
                        WalkStat(value = formatDuration(durationMillis), label = "Duration")
                        WalkStat(
                            value = formatSpeed(distanceMeters, durationMillis),
                            label = "Avg speed"
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
private fun WalkStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatDistance(meters: Float): String =
    if (meters >= 1000f) {
        String.format(Locale.getDefault(), "%.2f km", meters / 1000f)
    } else {
        "${meters.roundToInt()} m"
    }

private fun formatDuration(millis: Long): String {
    val totalSeconds: Long = millis / 1000
    val hours: Long = totalSeconds / 3600
    val minutes: Long = (totalSeconds % 3600) / 60
    val seconds: Long = totalSeconds % 60
    return if (hours > 0) {
        String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
}

private fun formatSpeed(meters: Float, millis: Long): String {
    if (millis < 3000) return "–"
    val kmh: Float = (meters / 1000f) / (millis / 3_600_000f)
    return String.format(Locale.getDefault(), "%.1f km/h", kmh)
}

@Preview
@Composable
private fun WalkRecorderSheetPreview() {
    CASProjectTheme {
        WalkRecorderSheet(
            walkActive = true,
            distanceMeters = 423f,
            durationMillis = 754_000L,
            followActive = true
        )
    }
}

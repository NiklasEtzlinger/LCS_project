package at.fhooe.sail.cas.ui.composables.map

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import at.fhooe.sail.cas.R
import at.fhooe.sail.cas.TAG
import at.fhooe.sail.cas.ui.theme.CASProjectTheme
import at.fhooe.sail.cas.ui.theme.ThemeController

/**
 * Location related map controls: centre once on the current position and
 * toggle follow-me mode. Zooming and panning are gesture-only (pinch/drag).
 * Styled as round "maps style" buttons floating over the map.
 */
@Composable
fun LocationControls(
    modifier: Modifier = Modifier,
    onCentreLocation: () -> Unit = {},
    onFollowToggle: () -> Unit = {},
    followActive: Boolean = false
) {
    // resting buttons: white circles in light mode, elevated dark circles in dark mode
    val restingColor: Color = if (ThemeController.isDarkTheme()) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        MaterialTheme.colorScheme.surface
    }

    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FloatingActionButton(
            onClick = {
                Log.i(TAG, "LocationControls::FAB::onClick (my location) ... ")
                onCentreLocation()
            },
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            containerColor = restingColor,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                painter = painterResource(R.drawable.my_location_24dp),
                contentDescription = "Centre on my location"
            )
        }
        FloatingActionButton(
            onClick = {
                Log.i(TAG, "LocationControls::FAB::onClick (follow toggle) ... ")
                onFollowToggle()
            },
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            containerColor = if (followActive) MaterialTheme.colorScheme.primary else restingColor,
            contentColor = if (followActive) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.primary
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.route_24dp),
                contentDescription = if (followActive) "Stop following location" else "Follow location"
            )
        }
    }
}

@Preview
@Composable
private fun LocationControlsPreview() {
    CASProjectTheme {
        LocationControls()
    }
}

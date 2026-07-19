package at.fhooe.sail.cas.ui.composables.map

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import at.fhooe.sail.cas.R
import at.fhooe.sail.cas.TAG
import at.fhooe.sail.cas.ui.theme.CASProjectTheme

/**
 * Location related map controls: centre once on the current position and
 * toggle follow-me mode. Zooming and panning are gesture-only (pinch/drag).
 */
@Composable
fun LocationControls(
    modifier: Modifier = Modifier,
    onCentreLocation: () -> Unit = {},
    onFollowToggle: () -> Unit = {},
    followActive: Boolean = false
) {
    Column(
        modifier = Modifier.padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FloatingActionButton(
            onClick = {
                Log.i(TAG, "LocationControls::FAB::onClick (my location) ... ")
                onCentreLocation()
            }
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
            containerColor = if (followActive) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                FloatingActionButtonDefaults.containerColor
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

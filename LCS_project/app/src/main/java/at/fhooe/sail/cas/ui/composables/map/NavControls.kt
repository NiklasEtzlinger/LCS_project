package at.fhooe.sail.cas.ui.composables.map

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import at.fhooe.sail.cas.TAG
import at.fhooe.sail.cas.ui.theme.CASProjectTheme

@Composable
fun NavControls(
    modifier: Modifier = Modifier,
    onCentre: () -> Unit = {},
    onNorth: () -> Unit = {},
    onSouth: () -> Unit = {},
    onWest: () -> Unit = {},
    onEast: () -> Unit = {}
) {
    Column(
        modifier = Modifier.padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FloatingActionButton(
            onClick = {
                Log.i(TAG, "NavControls::FAB::onClick (N) ... ")
                onNorth()
            }
        ) { Text("N") }
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    Log.i(TAG, "NavControls::FAB::onClick (W) ... ")
                    onWest()
                }
            ) { Text("W") }
            FloatingActionButton(
                onClick = {
                    Log.i(TAG, "NavControls::FAB::onClick (C) ... ")
                    onCentre()
                }
            ) { Text("C") }
            FloatingActionButton(
                onClick = {
                    Log.i(TAG, "NavControls::FAB::onClick (E) ... ")
                    onEast()
                }
            ) { Text("E") }
        }
        FloatingActionButton(
            onClick = {
                Log.i(TAG, "NavControls::FAB::onClick (S) ... ")
                onSouth()
            }
        ) { Text("S") }
    }

}

@Preview
@Composable
private fun NavControlsPreview() {
    CASProjectTheme() {
        NavControls()
    }
}
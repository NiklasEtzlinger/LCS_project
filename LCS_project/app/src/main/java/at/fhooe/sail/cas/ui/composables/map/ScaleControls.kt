package at.fhooe.sail.cas.ui.composables.map

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import at.fhooe.sail.cas.TAG
import at.fhooe.sail.cas.ui.theme.CASProjectTheme

@Composable
fun ScaleControls(
    modifier: Modifier = Modifier,
    onZoomIn: () -> Unit = {},
    onZoomOut: () -> Unit = {}
) {
    Column(
        modifier = Modifier.padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FloatingActionButton(
            onClick = {
                Log.i(TAG, "ScaleControls::FAB::onClick (+) ... ")
                onZoomIn()
            }
        ) { Text("+") }
        FloatingActionButton(
            onClick = {
                Log.i(TAG, "ScaleControls::FAB::onClick (-) ... ")
                onZoomOut()
            }
        ) { Text("-") }
    }

}

@Preview
@Composable
private fun ScaleControlsPreview() {
    CASProjectTheme{
        ScaleControls()
    }
}
package at.fhooe.sail.cas.ui.composables.map

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import at.fhooe.sail.cas.R
import at.fhooe.sail.cas.TAG
import at.fhooe.sail.cas.ui.theme.CASProjectTheme

@Composable
fun TmpControls(
    modifier: Modifier = Modifier,
    onPaintToggle: () -> Unit = {},
    onBClicked: () -> Unit? = {}
) {
    Column(
        modifier = Modifier.padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FloatingActionButton(
            onClick = {
                Log.i(TAG, "MapComponentScreen::FAB::onClick ... ")
                onPaintToggle()
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.bolt),
                contentDescription = "Tu was!"
            )
        }
        FloatingActionButton(
            onClick = {  onBClicked()  }
        ) { Text("B") }
    }
}

@Preview
@Composable
private fun TmpControlsPreview() {
    CASProjectTheme() {
        TmpControls()
    }
}
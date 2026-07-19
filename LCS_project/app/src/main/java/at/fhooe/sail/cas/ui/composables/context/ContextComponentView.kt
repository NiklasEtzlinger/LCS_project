package at.fhooe.sail.cas.ui.composables.context

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import at.fhooe.sail.cas.ui.theme.CASProjectTheme
import at.fhooe.sail.cas.ui.viewmodel.ContextViewModel

@Composable
fun ContextComponentView(
    modifier: Modifier = Modifier,
    contextViewModel: ContextViewModel = viewModel(),
) {

    val onClick: () -> Unit =  { contextViewModel.testBroadcast() }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment =  Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Context Component")
            Button(onClick = onClick) {
                Text("Broadcast Context")
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun ContextComponentViewPreview() {
    CASProjectTheme() {
        ContextComponentView()
    }
}
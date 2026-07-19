package at.fhooe.sail.cas.ui.composables.poi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import at.fhooe.sail.cas.model.features.DummyFeature
import at.fhooe.sail.cas.ui.theme.CASProjectTheme
import at.fhooe.sail.cas.ui.viewmodel.PoiViewModel

@Composable
fun PoiComponentView(
    poiViewModel: PoiViewModel = viewModel()
) {

    val value: DummyFeature by poiViewModel.dummyState.collectAsState()

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("PoiComponent")
            Text(value.txt)
        }
    }
}

@Preview
@Composable
private fun PreviewPoiComponentView() {
    CASProjectTheme() {
        PoiComponentView()
    }
}
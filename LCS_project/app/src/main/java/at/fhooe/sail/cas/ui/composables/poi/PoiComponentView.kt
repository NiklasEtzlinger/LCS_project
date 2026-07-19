package at.fhooe.sail.cas.ui.composables.poi

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import at.fhooe.sail.cas.model.features.DummyFeature
import at.fhooe.sail.cas.ui.theme.CASProjectTheme
import at.fhooe.sail.cas.ui.viewmodel.PoiViewModel
import at.fhooe.sail.cas.ui.viewmodel.features.PoiFeature

@Composable
fun PoiComponentView(
    poiViewModel: PoiViewModel = viewModel()
) {

    val value: DummyFeature by poiViewModel.dummyState.collectAsState()
    val pois: List<PoiFeature> by poiViewModel.pois.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "PoiComponent (${pois.size} POIs)",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = value.txt,
                style = MaterialTheme.typography.bodySmall
            )
        }
        HorizontalDivider()
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(pois) { poi ->
                PoiListItem(poi)
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun PoiListItem(poi: PoiFeature) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            bitmap = poi.icon.asImageBitmap(),
            contentDescription = poi.category,
            modifier = Modifier.size(40.dp)
        )
        Column {
            Text(
                text = poi.name.ifBlank { poi.id },
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = poi.category.ifBlank { "type ${poi.type}" },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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

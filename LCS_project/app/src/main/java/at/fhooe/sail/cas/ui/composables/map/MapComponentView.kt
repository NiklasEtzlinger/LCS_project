package at.fhooe.sail.cas.ui.composables.map

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import at.fhooe.sail.cas.TAG
import at.fhooe.sail.cas.ui.theme.CASProjectTheme
import at.fhooe.sail.cas.ui.theme.ThemeController
import at.fhooe.sail.cas.ui.viewmodel.MapViewModel


@Composable
fun MapComponentView(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = viewModel()
) {
    // keep the map palette in sync with the resolved app theme
    val darkTheme: Boolean = ThemeController.isDarkTheme()
    LaunchedEffect(darkTheme) {
        viewModel.setColorMode(if (darkTheme) "Night" else "Day")
    }

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { pos ->
                        Log.i(TAG, "tap at $pos detected")
                        viewModel.onMapTap(pos.x, pos.y)
                    }
                }
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        Log.i(TAG, "gesture detected (pan: $pan/zoom: $zoom")
                        pan.apply {
                            if (x != 0f) {
                                viewModel.scrollHorizontal(x)
                                // deltaX += x
                            }
                            if (y != 0f) {
                                viewModel.scrollVertical(y)
                                // deltaY += y
                            }
                        }
                        zoom.apply {
                            if (zoom != 1f) {
                                viewModel.zoom(zoom)
                                // scale *= zoom
                            }
                        }
                    }
                },
        ) {

            if (viewModel.width != constraints.maxWidth ||
                viewModel.height != constraints.maxHeight) {
                viewModel.resizeCanvas(
                    constraints.maxWidth,
                    constraints.maxHeight
                )
            }
            viewModel.displayBitmap?.let {
                drawImage(it)
            }
        } // canvas

        Box(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.align(Alignment.TopEnd)) {
                // Location Controls (zoom/pan are gesture-only)
                LocationControls(
                    onCentreLocation = { viewModel.centerOnLocation() },
                    onFollowToggle = { viewModel.toggleFollowLocation() },
                    followActive = viewModel.followLocation
                )
            }
            // info card for the tapped POI
            viewModel.selectedPoi?.let { poi ->
                Card(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .clickable { viewModel.clearSelection() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Text(
                            text = poi.name.ifBlank { poi.id },
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = poi.category.ifBlank { "type ${poi.type}" },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewMapComponentView() {
    CASProjectTheme() {
        MapComponentView()
    }
}
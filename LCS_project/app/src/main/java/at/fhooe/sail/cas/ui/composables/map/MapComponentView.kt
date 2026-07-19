package at.fhooe.sail.cas.ui.composables.map

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import at.fhooe.sail.cas.TAG
import at.fhooe.sail.cas.ui.theme.CASProjectTheme
import at.fhooe.sail.cas.ui.viewmodel.DELTA_DRAG
import at.fhooe.sail.cas.ui.viewmodel.DELTA_ZOOM
import at.fhooe.sail.cas.ui.viewmodel.MapViewModel


@Composable
fun MapComponentView(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = viewModel()
) {
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
                // Scale Controls
                ScaleControls(
                    onZoomIn = { viewModel.zoom(DELTA_ZOOM) },
                    onZoomOut = { viewModel.zoom(1/ DELTA_ZOOM) },
                )
            }
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nav Controls
                NavControls(
                    onCentre = { viewModel.zoomToFit() },
                    onNorth = { viewModel.scrollVertical(-DELTA_DRAG) },
                    onSouth = { viewModel.scrollVertical(DELTA_DRAG) },
                    onWest = { viewModel.scrollHorizontal(-DELTA_DRAG) },
                    onEast = { viewModel.scrollHorizontal(DELTA_DRAG) }
                )
                Spacer(modifier = Modifier.weight(1f))
                // Tmp Controls
                TmpControls(
                    onPaintToggle = { viewModel.repaintAsync() },
                    onBClicked = { viewModel.service?.someApiMethod() }
                )
            } // Box
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
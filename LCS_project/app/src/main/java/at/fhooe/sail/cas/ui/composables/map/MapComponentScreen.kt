package at.fhooe.sail.cas.ui.composables.map

import android.annotation.SuppressLint
import android.graphics.Rect
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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import at.fhooe.sail.cas.TAG
import at.fhooe.sail.cas.model.util.getBBox
import at.fhooe.sail.cas.ui.theme.CASProjectTheme
import at.fhooe.sail.cas.ui.viewmodel.MapViewModel
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapComponentScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = viewModel()
){
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Map Screen") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = { /* define navigation icon */ },
                actions = { /* define actions for screen */ }
            )
        },
        bottomBar =  { /* ToDo fill */ },
        floatingActionButton = {
        },
        modifier = Modifier
    ) { innerPadding ->
        MapComponentView(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding))
    }
}

@Preview
@Composable
private fun MapComponentScreenPreview() {
    CASProjectTheme() {
        MapComponentScreen()
    }
}


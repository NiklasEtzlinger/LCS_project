package at.fhooe.sail.cas.ui.composables

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.DisposableEffectScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import at.fhooe.sail.cas.ui.composables.context.ContextComponentView
import at.fhooe.sail.cas.ui.composables.map.MapComponentView
import at.fhooe.sail.cas.ui.composables.poi.PoiComponentView
import at.fhooe.sail.cas.ui.navigation.Screens
import at.fhooe.sail.cas.ui.viewmodel.ContextViewModel
import at.fhooe.sail.cas.ui.viewmodel.MapViewModel
import at.fhooe.sail.cas.ui.viewmodel.PoiViewModel

@Composable
fun NavigationAnchor(
    navController: NavHostController,
    onUpdateAction: (Any?, @Composable (() -> Unit)?) -> Unit
) {

    NavHost(
        navController = navController,
        startDestination = Screens.MapComponent
    ) {
        /*********************/
        /* MapComponent      */
        /*********************/
        composable<Screens.MapComponent> {
            DisposableEffect(Unit) {
                onUpdateAction(Screens.MapComponent, { Text("Map Ctrl") })
                onDispose { onUpdateAction(Screens.MapComponent, null) }
            }
            val mapViewModel: MapViewModel = viewModel()
            MapComponentView(viewModel = mapViewModel)
        }
        /*********************/
        /* PoiComponent      */
        /*********************/
        composable<Screens.PoiComponent> {
            DisposableEffect(Unit) {
                onUpdateAction(Screens.PoiComponent, { Text("Poi Ctrl") })
                onDispose { onUpdateAction(Screens.PoiComponent, null) }
            }
            val poiViewModel: PoiViewModel = viewModel()
            PoiComponentView(poiViewModel = poiViewModel)
        }
        /*********************/
        /* ContextComponent  */
        /*********************/
        composable<Screens.ContextComponent> {
            DisposableEffect(Unit) {
                onUpdateAction(Screens.ContextComponent, { Text("Context Ctrl") })
                onDispose { onUpdateAction(Screens.ContextComponent, null) }
            }
            val contextViewModel: ContextViewModel = viewModel()
            ContextComponentView(contextViewModel = contextViewModel)
        }
    }
}

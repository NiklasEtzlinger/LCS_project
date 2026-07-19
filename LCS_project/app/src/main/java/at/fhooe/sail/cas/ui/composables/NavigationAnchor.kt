package at.fhooe.sail.cas.ui.composables

import androidx.compose.runtime.Composable
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
    navController: NavHostController
) {

    NavHost(
        navController = navController,
        startDestination = Screens.MapComponent
    ) {
        /*********************/
        /* MapComponent      */
        /*********************/
        composable<Screens.MapComponent> {
            val mapViewModel: MapViewModel = viewModel()
            MapComponentView(viewModel = mapViewModel)
        }
        /*********************/
        /* PoiComponent      */
        /*********************/
        composable<Screens.PoiComponent> {
            val poiViewModel: PoiViewModel = viewModel()
            PoiComponentView(poiViewModel = poiViewModel)
        }
        /*********************/
        /* ContextComponent  */
        /*********************/
        composable<Screens.ContextComponent> {
            val contextViewModel: ContextViewModel = viewModel()
            ContextComponentView(contextViewModel = contextViewModel)
        }
    }
}

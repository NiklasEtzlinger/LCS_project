package at.fhooe.sail.cas.ui.navigation

import at.fhooe.sail.cas.R
import kotlinx.serialization.Serializable

@Serializable
sealed class Screens(val label: String, val iconId: Int) {
    @Serializable
    data object MapComponent: Screens(label = "Map", iconId = R.drawable.map_24dp)
    @Serializable
    data object PoiComponent: Screens(label = "Poi", iconId = R.drawable.poi_24dp)
    @Serializable
    data object ContextComponent: Screens(label = "Context", iconId = R.drawable.virus_24dp)
}

val navItems = listOf(Screens.MapComponent, Screens.PoiComponent, Screens.ContextComponent)
package at.fhooe.sail.cas.ui.viewmodel

import androidx.lifecycle.ViewModel
import at.fhooe.sail.cas.model.features.ContextFeature
import at.fhooe.sail.cas.model.features.ContextValue
import at.fhooe.sail.cas.model.mediators.MainServiceMediator
import at.fhooe.sail.cas.model.service.IMainService
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ContextViewModel: ViewModel() {
    val mainService: IMainService? by lazy { MainServiceMediator.getInstance() }

    fun testBroadcast() {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd--HH:mm:ss")
        val timestamp: String = LocalDateTime.now().format(formatter)
        val cf: ContextFeature = ContextFeature(
            id = "DayTimeContext",
            type = 5000,
            value = ContextValue.StringValue("Day")
        )
        mainService?.broadcastContextFeatures(mutableListOf(cf))
    }
}
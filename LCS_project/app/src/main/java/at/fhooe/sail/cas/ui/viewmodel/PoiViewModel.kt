package at.fhooe.sail.cas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.fhooe.sail.cas.model.features.DummyFeature
import at.fhooe.sail.cas.model.mediators.DummyFeatureMediator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PoiViewModel: ViewModel() {

    private val _dummyState: MutableStateFlow<DummyFeature> =
        MutableStateFlow<DummyFeature>(DummyFeature("undefined") )

    val dummyState: StateFlow<DummyFeature> = _dummyState.asStateFlow()

    init {
        viewModelScope.launch {
            DummyFeatureMediator.dataFlow.collect { value ->
                _dummyState.value = value
            }
        }
    }

}
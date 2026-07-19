package at.fhooe.sail.cas.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import at.fhooe.sail.cas.model.features.DummyFeature
import at.fhooe.sail.cas.model.mediators.DummyFeatureMediator
import at.fhooe.sail.cas.model.repositories.PoiRepository
import at.fhooe.sail.cas.ui.viewmodel.features.PoiFeature
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PoiViewModel(application: Application): AndroidViewModel(application) {

    private val _dummyState: MutableStateFlow<DummyFeature> =
        MutableStateFlow<DummyFeature>(DummyFeature("undefined") )

    val dummyState: StateFlow<DummyFeature> = _dummyState.asStateFlow()

    private val _pois: MutableStateFlow<List<PoiFeature>> =
        MutableStateFlow<List<PoiFeature>>(emptyList())

    val pois: StateFlow<List<PoiFeature>> = _pois.asStateFlow()

    init {
        viewModelScope.launch {
            DummyFeatureMediator.dataFlow.collect { value ->
                _dummyState.value = value
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            _pois.value = PoiRepository(application).fetchPois()
        }
    }

}

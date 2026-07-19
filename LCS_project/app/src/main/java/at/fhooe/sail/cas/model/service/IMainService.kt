package at.fhooe.sail.cas.model.service

import at.fhooe.sail.cas.model.features.ContextFeature

interface IMainService {
    fun someApiMethod()

    fun broadcastContextFeatures(values:List<ContextFeature>)

    /** start/stop walk recording: while inactive the (mock) position stands still */
    fun setWalkActive(active: Boolean)
}
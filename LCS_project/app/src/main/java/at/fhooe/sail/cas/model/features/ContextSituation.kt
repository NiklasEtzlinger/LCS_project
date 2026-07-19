package at.fhooe.sail.cas.model.features

data class ContextSituation(
    val metadata: ContextMetaData,
    val contextElements: List<ContextFeature>
) {
}
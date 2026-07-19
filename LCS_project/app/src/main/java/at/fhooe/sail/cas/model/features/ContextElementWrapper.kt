package at.fhooe.sail.cas.model.features

import android.util.Log
import at.fhooe.sail.android.dsl_parser.tree.context.ContextDaytime
import at.fhooe.sail.android.dsl_parser.tree.context.ContextObject
import at.fhooe.sail.cas.TAG

object ContextElementWrapper {

    fun toJavaContextObject(element: ContextFeature): ContextObject? {
        return when (element.type) {
            5000 -> {
                if (element.value is ContextValue.StringValue) {
                    ContextDaytime(element.value.v)
                } else {
                    Log.e(TAG, "ContextElementWrapper::toJavaContextObject() wrong type encountered")
                    null
                }
            }
            else -> null
        }
    }
}
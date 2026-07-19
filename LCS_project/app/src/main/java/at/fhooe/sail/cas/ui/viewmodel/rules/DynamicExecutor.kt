package at.fhooe.sail.cas.ui.viewmodel.rules

import android.util.Log
import at.fhooe.sail.cas.TAG
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberFunctions

object DynamicExecutor {
    fun execute(instance: Any, methodName: String, vararg paramValues: String) {
        val kClass: KClass<out Any> = instance::class

        val function: KFunction<*> = kClass.memberFunctions.find {
            it.name == methodName && it.parameters.size == paramValues.size + 1
        } ?: throw IllegalArgumentException("Methode nicht gefunden ... ").also {
            Log.e(TAG, "DynamicExecutor::execute() --> method ($methodName) in class (${kClass.qualifiedName}) not found")
        }

        val convertedParameter: MutableList<Any> = mutableListOf()
        for (i in paramValues.indices) {
            val targetParameter: KParameter = function.parameters[i+1]
            val rawValue: String = paramValues[i]

            val convertedValue: Any =
                when(targetParameter.type.classifier) {
                    String::class -> rawValue
                    Int::class -> rawValue.toInt()
                    else -> throw IllegalArgumentException("Parameter Typ unbekannt ... ")
                }
            convertedParameter.add(convertedValue)
        }
        function.call(instance, *convertedParameter.toTypedArray())
    }
}
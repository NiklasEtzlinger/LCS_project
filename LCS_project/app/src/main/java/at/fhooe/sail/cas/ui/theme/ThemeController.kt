package at.fhooe.sail.cas.ui.theme

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class ThemeMode { SYSTEM, LIGHT, DARK }

/**
 * App-wide theme mode (system / light / dark), persisted in SharedPreferences.
 * Backed by Compose state so every composable reading [mode] recomposes on change.
 */
object ThemeController {
    private const val PREFS_NAME = "cas_settings"
    private const val KEY_THEME_MODE = "theme_mode"

    var mode: ThemeMode by mutableStateOf(ThemeMode.SYSTEM)
        private set

    fun init(context: Context) {
        val stored: String? = context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name)
        mode = runCatching { ThemeMode.valueOf(stored ?: "") }
            .getOrDefault(ThemeMode.SYSTEM)
    }

    fun cycle(context: Context) {
        mode = when (mode) {
            ThemeMode.SYSTEM -> ThemeMode.LIGHT
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.SYSTEM
        }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_THEME_MODE, mode.name)
            .apply()
    }

    @Composable
    fun isDarkTheme(): Boolean = when (mode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
}

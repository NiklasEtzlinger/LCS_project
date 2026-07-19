package at.fhooe.sail.cas.ui.permission

import android.app.Activity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun PermissionWrapper(
    permissions: List<String>,
    content: @Composable (PermissionController) -> Unit
) {
    val activity: Activity = LocalContext.current as Activity

    // Verwendung eines einfachen Keys, um Re-Compositions bei Permission-Änderungen zu erzwingen
    var triggerPermissionCheck: Int by remember { mutableStateOf(0) }

    val launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>> =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { triggerPermissionCheck++ }

    val controller: PermissionController =
        remember(triggerPermissionCheck, activity) {
            PermissionController(activity, permissions, launcher)
        }

    // OnResume-Check für Split-Screen/Einstellungs-Rückkehrer
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) triggerPermissionCheck++
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Automatischer Check beim ersten Start
    LaunchedEffect(Unit) {
        // Wenn noch nicht gewährt, triggere den Dialog sofort
        if (!controller.isGranted) {
            launcher.launch(permissions.toTypedArray())
        }
    }

    // visualize content (child Composable) while providing controller instance
    content(controller)
}
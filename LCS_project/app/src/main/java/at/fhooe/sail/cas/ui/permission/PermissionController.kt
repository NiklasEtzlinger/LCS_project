package at.fhooe.sail.cas.ui.permission

import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionController(
    val activity: Activity,
    val permissions: List<String>,
    private val launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>
) {
    val isGranted: Boolean
        get() = permissions.all {
            ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
        }

    val shouldShowRationale: Boolean
        get() = permissions.any { perm ->
            ActivityCompat.shouldShowRequestPermissionRationale(activity, perm)
        }


    fun runOrRequestPermissions(onGranted: () -> Unit) {
        // get() call on is granted triggers checkSelfPermission cascade
        if (isGranted) {
            onGranted()
        } else {
            // at least on permission wasn't granted
            // ToDo: use shouldShowRational for explantion and link to properties
            launcher.launch(permissions.toTypedArray())
        }
    }
}
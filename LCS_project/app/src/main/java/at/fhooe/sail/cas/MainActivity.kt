package at.fhooe.sail.cas

import android.Manifest
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import at.fhooe.sail.cas.model.mediators.MainServiceMediator
import at.fhooe.sail.cas.ui.composables.CasMainScreen
import at.fhooe.sail.cas.ui.permission.PermissionWrapper
import at.fhooe.sail.cas.ui.theme.CASProjectTheme

const val TAG: String = "CAS-Test"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val permissions: List<String> = listOf(
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )

        setContent {
            CASProjectTheme {
                PermissionWrapper(permissions) { permissionCtrl ->
                    if(permissionCtrl.isGranted) {
                        val appContext: Application =
                            LocalContext.current.applicationContext as Application
                        DisposableEffect(Unit) {
                            MainServiceMediator.bind(appContext)
                            onDispose {
                                MainServiceMediator.unbind()
                            }
                        }

                        CasMainScreen(permissionCtrl = permissionCtrl)
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

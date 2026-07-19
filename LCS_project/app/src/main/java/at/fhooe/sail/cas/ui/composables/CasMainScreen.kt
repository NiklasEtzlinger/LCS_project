package at.fhooe.sail.cas.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import at.fhooe.sail.cas.ui.navigation.navItems
import at.fhooe.sail.cas.ui.permission.PermissionController
import at.fhooe.sail.cas.ui.theme.CASProjectTheme


@Composable
fun CasMainScreen(
    modifier: Modifier = Modifier,
    permissionCtrl: PermissionController? = null
){

    val navController: NavHostController = rememberNavController()

    Scaffold(
        bottomBar =  {
            NavigationBar() {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute: String? = navBackStackEntry?.destination?.route
                navItems.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(painter = painterResource(screen.iconId),
                                 contentDescription = screen.label)
                        },
                        label = { Text(screen.label) },
                        selected = screen::class.qualifiedName.equals(currentRoute),
                        onClick = {
                            navController.navigate(screen) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        // no top bar: screens run edge-to-edge below the status bar
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = Modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)) {
            NavigationAnchor(navController = navController)
        }
    }
}

@Preview
@Composable
private fun CasMainScreenPreview() {
    CASProjectTheme() {
        CasMainScreen()
    }
}

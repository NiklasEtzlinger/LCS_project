package at.fhooe.sail.cas.ui.composables

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import at.fhooe.sail.cas.R
import at.fhooe.sail.cas.ui.navigation.navItems
import at.fhooe.sail.cas.ui.permission.PermissionController
import at.fhooe.sail.cas.ui.theme.CASProjectTheme
import at.fhooe.sail.cas.ui.theme.ThemeController


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CasMainScreen(
    modifier: Modifier = Modifier,
    permissionCtrl: PermissionController? = null
){

    val navController: NavHostController = rememberNavController()

    var appBarOwner       by remember { mutableStateOf<Any?>(null) }
    var appBarActionsSlot by remember { mutableStateOf<(@Composable () -> Unit)?>(null) }
    var onUpdateAction: (Any?, (@Composable () -> Unit)?) -> Unit = { key, content ->
        if (content != null) {
            appBarOwner = key
            appBarActionsSlot = content
        } else if (appBarOwner == key) {
            appBarActionsSlot = null
            appBarOwner = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "CAS App") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = { /* define navigation icon */ },
                actions = {
                    appBarActionsSlot?.invoke()
                    val context = LocalContext.current
                    IconButton(onClick = { ThemeController.cycle(context) }) {
                        Icon(
                            painter = painterResource(R.drawable.theme_mode_24dp),
                            contentDescription = "Theme mode: ${ThemeController.mode}"
                        )
                    }
                }
            )
        },
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
        floatingActionButton = {
        },
        modifier = Modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)) {
            NavigationAnchor(
                navController = navController,
                onUpdateAction = onUpdateAction,
            )
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


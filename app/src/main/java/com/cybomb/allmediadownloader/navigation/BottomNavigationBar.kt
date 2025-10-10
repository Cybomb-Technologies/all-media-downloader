package com.cybomb.allmediadownloader.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.cybomb.allmediadownloader.datamodels.Screen
import kotlin.collections.forEach

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.graphics.Color
import com.cybomb.allmediadownloader.ui.theme.LightBlue

// ... other imports

@Composable
fun BottomNavigationBar(navController: NavController, items: List<Screen>) {
    // Define your Custom Colors (These should ideally come from your AppColors/Theme.kt)
    // Using the blues you provided earlier for demonstration:
    val SelectedColor = Color(0xFF06AAE9) // PrimaryBlue (06AAE9)
    val UnselectedColor = Color(0xFF9E9E9E) // A standard gray for unselected icons

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Remember last tap state
    var lastTapTime by remember { mutableStateOf(0L) }
    var lastTappedRoute by remember { mutableStateOf<String?>(null) }
    val DOUBLE_TAP_TIMEOUT = 300 // milliseconds

    NavigationBar(
        containerColor = LightBlue.copy(alpha = 0.2F)
    ) {
        items.forEach { screen ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = isSelected,
                // 2. APPLY THE CUSTOM COLORS HERE
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SelectedColor,
                    selectedTextColor = SelectedColor,
                    unselectedIconColor = UnselectedColor,
                    unselectedTextColor = UnselectedColor,
                    indicatorColor = LightBlue.copy(alpha = 0.4F)
                ),
                onClick = {
                    val now = System.currentTimeMillis()
                    if (isSelected) {
                        // This is a re-selection tap
                        if (lastTappedRoute == screen.route && now - lastTapTime < DOUBLE_TAP_TIMEOUT) {
                            // Detected a double-tap
                            navController.popBackStack(screen.route, inclusive = false)
                            // Optionally, scroll to top or run other logic
                        }
                        lastTappedRoute = screen.route
                        lastTapTime = now
                    } else {
                        // Normal navigation to a new route
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                        lastTappedRoute = screen.route
                        lastTapTime = now
                    }
                }
            )
        }
    }
}


//@Composable
//fun BottomNavigationBar(navController: NavController, items: List<Screen>) {
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = navBackStackEntry?.destination?.route
//
//    NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh) {
//        items.forEach { screen ->
//            val isSelected = currentRoute == screen.route
//            NavigationBarItem(
//                icon = { Icon(screen.icon, contentDescription = screen.title) },
//                label = { Text(screen.title) },
//                selected = isSelected,
//                onClick = {
//                    if (currentRoute != screen.route) {
//                        navController.navigate(screen.route) {
//                            // Avoid building up a large stack of destinations on the back stack as users select items
//                            popUpTo(navController.graph.startDestinationId) {
//                                saveState = true
//                            }
//                            // Avoid multiple copies of the same destination when reselecting the same item
//                            launchSingleTop = true
//                            // Restore state when reselecting a previously selected item
//                            restoreState = true
//                        }
//                    }
//                }
//            )
//        }
//    }
//}

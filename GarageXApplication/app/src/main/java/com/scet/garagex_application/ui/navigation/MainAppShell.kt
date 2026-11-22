package com.scet.garagex_application.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.scet.garagex_application.ui.account.AccountScreen
import com.scet.garagex_application.ui.sos.SosRoadsideScreen
import com.scet.garagex_application.ui.superapp.SuperAppHomeScreen
import com.scet.garagex_application.ui.theme.DeepBurgundyPrimary
import com.scet.garagex_application.ui.theme.TextSecondary
import com.scet.garagex_application.ui.theme.WarmCreamBackground

enum class SuperAppTab(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    HELP("Help", Icons.Default.HelpOutline),
    STORE("GoStore", Icons.Default.Storefront),
    SOS("SOS", Icons.Default.Emergency),
    ACCOUNT("Account", Icons.Default.Person)
}

/**
 * 5-Tab Bottom Navigation Shell matching Screenshots 4 & 5
 */
@Composable
fun MainAppShell(
    initialTab: SuperAppTab = SuperAppTab.HOME,
    onNavigateToSelectBrand: () -> Unit,
    onNavigateToBooking: (String) -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToVehicles: () -> Unit,
    onLogout: () -> Unit
) {
    var currentTab by remember { mutableStateOf(initialTab) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = DeepBurgundyPrimary
            ) {
                SuperAppTab.entries.forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                tint = if (isSelected) DeepBurgundyPrimary else TextSecondary
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) DeepBurgundyPrimary else TextSecondary
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = DeepBurgundyPrimary.copy(alpha = 0.12f)
                        )
                    )
                }
            }
        },
        containerColor = WarmCreamBackground
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            when (currentTab) {
                SuperAppTab.HOME -> {
                    SuperAppHomeScreen(
                        onNavigateToSelectBrand = onNavigateToSelectBrand,
                        onNavigateToBooking = onNavigateToBooking,
                        onNavigateToSos = { currentTab = SuperAppTab.SOS },
                        onNavigateToVehicles = onNavigateToVehicles,
                        onLogout = onLogout
                    )
                }
                SuperAppTab.HELP -> {
                    HelpScreen()
                }
                SuperAppTab.STORE -> {
                    GoStoreScreen()
                }
                SuperAppTab.SOS -> {
                    SosRoadsideScreen(
                        onNavigateBack = { currentTab = SuperAppTab.HOME }
                    )
                }
                SuperAppTab.ACCOUNT -> {
                    AccountScreen(
                        onNavigateToOrders = onNavigateToOrders,
                        onNavigateToVehicles = onNavigateToVehicles,
                        onNavigateToHelp = { currentTab = SuperAppTab.HELP },
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}

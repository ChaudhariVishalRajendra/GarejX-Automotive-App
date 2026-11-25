package com.scet.garagex_application.ui.home

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import com.scet.garagex_application.ui.theme.TextPrimary
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scet.garagex_application.data.local.TokenManager
import com.scet.garagex_application.ui.theme.DeepBurgundy
import com.scet.garagex_application.ui.theme.MutedSageGrey
import com.scet.garagex_application.ui.theme.WarmCreamBeige
import kotlinx.coroutines.launch

// =========================================================================
// CustomerHomeScreen: Main Customer Hub with Navigation Drawer & Server IP
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeScreen(
    onNavigateToVehicles: () -> Unit,
    onNavigateToServices: () -> Unit,
    onNavigateToBooking: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToBills: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context.applicationContext) }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val userName by tokenManager.getUserName().collectAsState(initial = "Customer")
    val currentServerUrl by tokenManager.getServerUrl().collectAsState(initial = TokenManager.DEFAULT_BASE_URL)

    var showServerSettingsDialog by remember { mutableStateOf(false) }
    var tempServerUrl by remember { mutableStateOf("") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = WarmCreamBeige,
                modifier = Modifier.width(300.dp)
            ) {
                // Drawer Header
                Surface(
                    color = DeepBurgundy,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(WarmCreamBeige),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = DeepBurgundy,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = userName ?: "Customer",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = WarmCreamBeige
                        )
                        Surface(
                            color = WarmCreamBeige.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = "CUSTOMER ACCOUNT",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = WarmCreamBeige
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Navigation Items
                NavigationDrawerItem(
                    label = { Text("My Vehicle Fleet") },
                    icon = { Icon(Icons.Default.DirectionsCar, null, tint = DeepBurgundy) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToVehicles()
                    },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = WarmCreamBeige)
                )

                NavigationDrawerItem(
                    label = { Text("Book Service") },
                    icon = { Icon(Icons.Default.CalendarMonth, null, tint = DeepBurgundy) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToBooking()
                    },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = WarmCreamBeige)
                )

                NavigationDrawerItem(
                    label = { Text("Service Catalog") },
                    icon = { Icon(Icons.Default.Build, null, tint = DeepBurgundy) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToServices()
                    },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = WarmCreamBeige)
                )

                NavigationDrawerItem(
                    label = { Text("Active Bookings & Trackers") },
                    icon = { Icon(Icons.Default.EventNote, null, tint = DeepBurgundy) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToAppointments()
                    },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = WarmCreamBeige)
                )

                NavigationDrawerItem(
                    label = { Text("Invoices & Bills") },
                    icon = { Icon(Icons.Default.Receipt, null, tint = DeepBurgundy) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToBills()
                    },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = WarmCreamBeige)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                NavigationDrawerItem(
                    label = { Text("Server IP Settings") },
                    icon = { Icon(Icons.Default.Dns, null, tint = DeepBurgundy) },
                    selected = false,
                    onClick = {
                        tempServerUrl = currentServerUrl
                        showServerSettingsDialog = true
                        scope.launch { drawerState.close() }
                    },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = WarmCreamBeige)
                )

                NavigationDrawerItem(
                    label = { Text("Call Garage Helpline") },
                    icon = { Icon(Icons.Default.Call, null, tint = DeepBurgundy) },
                    selected = false,
                    onClick = {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+919876543210"))
                        context.startActivity(dialIntent)
                        scope.launch { drawerState.close() }
                    },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = WarmCreamBeige)
                )

                NavigationDrawerItem(
                    label = { Text("Logout", color = MaterialTheme.colorScheme.error) },
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = MaterialTheme.colorScheme.error) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            tokenManager.clearSession()
                            onLogout()
                        }
                    },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = WarmCreamBeige)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "GarejX Workshop",
                            color = WarmCreamBeige,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = WarmCreamBeige
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            scope.launch {
                                tokenManager.clearSession()
                                onLogout()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Logout",
                                tint = WarmCreamBeige
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DeepBurgundy
                    )
                )
            },
            containerColor = WarmCreamBeige
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Garage Location & Helpline Banner
                item {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "GarejX Auto Hub",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepBurgundy
                                    )
                                    Text(
                                        text = "Ring Road, Surat • Open (09:00 - 20:00)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MutedSageGrey
                                    )
                                }

                                Surface(
                                    color = MutedSageGrey.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "OPEN NOW",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepBurgundy
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+919876543210"))
                                        context.startActivity(dialIntent)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Call, null, modifier = Modifier.size(16.dp), tint = DeepBurgundy)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Helpline", color = DeepBurgundy, style = MaterialTheme.typography.bodySmall)
                                }

                                Button(
                                    onClick = {
                                        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=GarejX+Workshop+Surat"))
                                        context.startActivity(mapIntent)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = DeepBurgundy,
                                        contentColor = WarmCreamBeige
                                    )
                                ) {
                                    Icon(Icons.Default.Directions, null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Directions", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }

                // 2. Greeting
                item {
                    Text(
                        text = "Quick Garage Actions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepBurgundy
                    )
                }

                // 3. Action Cards
                item {
                    DashboardNavCard(
                        title = "My Vehicle Fleet",
                        subtitle = "Manage registered cars, bikes & HSRP plates",
                        icon = Icons.Default.DirectionsCar,
                        onClick = onNavigateToVehicles
                    )
                }

                item {
                    DashboardNavCard(
                        title = "Book a Repair Service",
                        subtitle = "4-Step guided booking with date & time selection",
                        icon = Icons.Default.CalendarMonth,
                        onClick = onNavigateToBooking
                    )
                }

                item {
                    DashboardNavCard(
                        title = "Service Catalog & Pricing",
                        subtitle = "Periodic maintenance, AC care, brake inspection",
                        icon = Icons.Default.Build,
                        onClick = onNavigateToServices
                    )
                }

                item {
                    DashboardNavCard(
                        title = "Live Bay Stepper & Bookings",
                        subtitle = "Track vehicle status through diagnostic stages",
                        icon = Icons.Default.EventNote,
                        onClick = onNavigateToAppointments
                    )
                }

                item {
                    DashboardNavCard(
                        title = "Invoices & UPI Payments",
                        subtitle = "Itemized spare parts bills & Google Pay / PhonePe",
                        icon = Icons.Default.Receipt,
                        onClick = onNavigateToBills
                    )
                }
            }
        }
    }

    // In-App Server IP Switcher Dialog (Universal APK Support)
    if (showServerSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showServerSettingsDialog = false },
            title = { Text("Server Backend IP Settings") },
            text = {
                Column {
                    Text(
                        text = "Change the backend API address to test this APK on your physical phone or local Wi-Fi network without rebuilding.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = tempServerUrl,
                        onValueChange = { tempServerUrl = it },
                        label = { Text("Backend URL") },
                        placeholder = { Text("https://garejx-automotive-app.onrender.com/api/") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = androidx.compose.ui.graphics.Color.White,
                            unfocusedContainerColor = androidx.compose.ui.graphics.Color.White,
                            focusedBorderColor = DeepBurgundy,
                            cursorColor = DeepBurgundy
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Emulator default: http://10.0.2.2:5000/api/",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedSageGrey
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            tokenManager.saveServerUrl(tempServerUrl.trim())
                            showServerSettingsDialog = false
                            Toast.makeText(context, "Server URL updated!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepBurgundy)
                ) {
                    Text("Save & Connect", color = WarmCreamBeige)
                }
            },
            dismissButton = {
                TextButton(onClick = { showServerSettingsDialog = false }) {
                    Text("Cancel", color = MutedSageGrey)
                }
            }
        )
    }
}

/**
 * Reusable Dashboard Navigation Action Card
 */
@Composable
fun DashboardNavCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(DeepBurgundy.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = DeepBurgundy,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepBurgundy
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedSageGrey
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = DeepBurgundy,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

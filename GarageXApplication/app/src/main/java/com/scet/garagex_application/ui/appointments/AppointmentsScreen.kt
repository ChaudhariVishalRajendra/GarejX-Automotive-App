package com.scet.garagex_application.ui.appointments

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import com.scet.garagex_application.ui.components.drawLazyVerticalScrollbar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scet.garagex_application.data.remote.dto.AppointmentDto
import com.scet.garagex_application.ui.components.LicensePlateBadge
import com.scet.garagex_application.ui.theme.DeepBurgundy
import com.scet.garagex_application.ui.theme.MutedSageGrey
import com.scet.garagex_application.ui.theme.StatusCompleted
import com.scet.garagex_application.ui.theme.StatusInProgress
import com.scet.garagex_application.ui.theme.StatusPending
import com.scet.garagex_application.ui.theme.WarmCreamBeige

// =========================================================================
// AppointmentsScreen: Displays Customer's Active and Past Service Bookings
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(
    viewModel: AppointmentsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToBooking: () -> Unit,
    onSelectAppointment: (String) -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAppointments(context)
    }

    val tabs = listOf("Active / Bay (${uiState.activeAppointments.size})", "History (${uiState.historyAppointments.size})")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Service Bookings",
                        fontWeight = FontWeight.Bold,
                        color = WarmCreamBeige
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Dual Tab Row
            TabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = WarmCreamBeige,
                contentColor = DeepBurgundy,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab]),
                        color = DeepBurgundy
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = uiState.selectedTab == index,
                        onClick = { viewModel.selectTab(index) },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (uiState.selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (uiState.selectedTab == index) DeepBurgundy else MutedSageGrey
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = DeepBurgundy)
                    }
                }

                uiState.errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = uiState.errorMessage!!,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.loadAppointments(context) },
                                colors = ButtonDefaults.buttonColors(containerColor = DeepBurgundy)
                            ) {
                                Text("Retry", color = WarmCreamBeige)
                            }
                        }
                    }
                }

                else -> {
                    val displayList = if (uiState.selectedTab == 0) uiState.activeAppointments else uiState.historyAppointments

                    if (displayList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.EventNote,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MutedSageGrey
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (uiState.selectedTab == 0) "No active service bookings found." else "No past appointment history.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (uiState.selectedTab == 0) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = onNavigateToBooking,
                                        colors = ButtonDefaults.buttonColors(containerColor = DeepBurgundy)
                                    ) {
                                        Text("Book a Service", color = WarmCreamBeige)
                                    }
                                }
                            }
                        }
                    } else {
                        val listState = rememberLazyListState()
                        LazyColumn(
                            state = listState,
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .drawLazyVerticalScrollbar(listState)
                                .padding(horizontal = 16.dp)
                        ) {
                            items(displayList, key = { it.getEffectiveId() }) { appt ->
                                AppointmentItemCard(
                                    appointment = appt,
                                    onClick = { onSelectAppointment(appt.getEffectiveId()) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Appointment Card with Status Pill & License Plate
 */
@Composable
fun AppointmentItemCard(
    appointment: AppointmentDto,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Vehicle brand & model
                Text(
                    text = "${appointment.vehicle?.brand ?: "Vehicle"} ${appointment.vehicle?.model ?: ""}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepBurgundy
                )

                // Status Badge
                AppointmentStatusBadge(status = appointment.getEffectiveStatus())
            }

            Spacer(modifier = Modifier.height(8.dp))

            // License plate
            appointment.vehicle?.vehicleNumber?.let { plate ->
                LicensePlateBadge(vehicleNumber = plate)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Service Name
            Text(
                text = "Service: ${appointment.service?.name ?: "General Service"}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MutedSageGrey
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = appointment.appointmentDate?.take(10) ?: "Scheduled",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedSageGrey
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "View Tracker",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepBurgundy
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = DeepBurgundy
                    )
                }
            }
        }
    }
}

/**
 * Colored Status Pill
 */
@Composable
fun AppointmentStatusBadge(status: String) {
    val (bgColor, textColor) = when (status) {
        "PENDING" -> StatusPending.copy(alpha = 0.15f) to StatusPending
        "CONFIRMED" -> Color(0xFF1976D2).copy(alpha = 0.15f) to Color(0xFF1976D2)
        "ASSIGNED" -> Color(0xFF7B1FA2).copy(alpha = 0.15f) to Color(0xFF7B1FA2)
        "IN_PROGRESS" -> StatusInProgress.copy(alpha = 0.15f) to StatusInProgress
        "COMPLETED" -> StatusCompleted.copy(alpha = 0.15f) to StatusCompleted
        "CANCELLED" -> Color.Red.copy(alpha = 0.15f) to Color.Red
        else -> MutedSageGrey.copy(alpha = 0.15f) to MutedSageGrey
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status.replace("_", " "),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

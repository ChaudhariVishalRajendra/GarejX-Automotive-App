package com.scet.garagex_application.ui.booking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.scet.garagex_application.ui.components.drawLazyVerticalScrollbar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scet.garagex_application.data.remote.dto.ServiceDto
import com.scet.garagex_application.data.remote.dto.VehicleDto
import com.scet.garagex_application.ui.components.LicensePlateBadge
import com.scet.garagex_application.ui.theme.DeepBurgundy
import com.scet.garagex_application.ui.theme.MutedSageGrey
import com.scet.garagex_application.ui.theme.WarmCreamBeige

// =========================================================================
// BookingWizardScreen: 4-Step Interactive Service Booking Flow
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingWizardScreen(
    viewModel: BookingViewModel,
    preselectedServiceId: String? = null,
    preselectedCategory: String? = null,
    onNavigateBack: () -> Unit,
    onNavigateToVehicles: () -> Unit,
    onBookingSuccess: (String) -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadInitialData(context, preselectedServiceId, preselectedCategory)
    }

    LaunchedEffect(uiState.isBookingSuccess) {
        if (uiState.isBookingSuccess && uiState.createdAppointmentId != null) {
            onBookingSuccess(uiState.createdAppointmentId!!)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Book Service",
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
        bottomBar = {
            Surface(
                color = WarmCreamBeige,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (uiState.currentStep > 1) {
                        OutlinedButton(
                            onClick = { viewModel.previousStep() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepBurgundy),
                            border = BorderStroke(1.dp, DeepBurgundy)
                        ) {
                            Text("Back")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    if (uiState.currentStep < 4) {
                        Button(
                            onClick = { viewModel.nextStep() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DeepBurgundy,
                                contentColor = WarmCreamBeige
                            )
                        ) {
                            Text("Next Step")
                        }
                    } else {
                        Button(
                            onClick = { viewModel.submitBooking(context) },
                            enabled = !uiState.isSubmitting,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DeepBurgundy,
                                contentColor = WarmCreamBeige
                            )
                        ) {
                            if (uiState.isSubmitting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = WarmCreamBeige,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Confirm Booking")
                            }
                        }
                    }
                }
            }
        },
        containerColor = WarmCreamBeige
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Step Progress Indicator
            WizardStepIndicator(currentStep = uiState.currentStep)

            Spacer(modifier = Modifier.height(16.dp))

            // Error banner if any
            if (uiState.errorMessage != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = uiState.errorMessage!!,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            if (uiState.isLoadingData) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = DeepBurgundy)
                }
            } else {
                when (uiState.currentStep) {
                    1 -> Step1SelectVehicle(
                        vehicles = uiState.vehicles,
                        selectedVehicle = uiState.selectedVehicle,
                        onSelectVehicle = { viewModel.selectVehicle(it) },
                        onAddNewVehicle = onNavigateToVehicles
                    )

                    2 -> Step2SelectService(
                        services = uiState.services,
                        selectedService = uiState.selectedService,
                        onSelectService = { viewModel.selectService(it) }
                    )

                    3 -> Step3SelectDate(
                        formattedDate = uiState.formattedDate,
                        onOpenDatePicker = { showDatePicker = true }
                    )

                    4 -> Step4ConfirmBooking(
                        vehicle = uiState.selectedVehicle,
                        service = uiState.selectedService,
                        date = uiState.formattedDate,
                        problemDescription = uiState.problemDescription,
                        onProblemChange = { viewModel.onProblemDescriptionChange(it) }
                    )
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis() + 86400000L // default tomorrow
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            viewModel.setAppointmentDate(it)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = DeepBurgundy, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = MutedSageGrey)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

/**
 * Visual 4-Step Progress Indicator
 */
@Composable
fun WizardStepIndicator(currentStep: Int) {
    val steps = listOf("Vehicle", "Service", "Date", "Confirm")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, title ->
            val stepNum = index + 1
            val isDone = stepNum < currentStep
            val isCurrent = stepNum == currentStep

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isDone -> MutedSageGrey
                                isCurrent -> DeepBurgundy
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDone) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = WarmCreamBeige,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Text(
                            text = "$stepNum",
                            color = if (isCurrent) WarmCreamBeige else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCurrent) DeepBurgundy else MutedSageGrey,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

/**
 * Step 1: Select Registered Vehicle
 */
@Composable
fun Step1SelectVehicle(
    vehicles: List<VehicleDto>,
    selectedVehicle: VehicleDto?,
    onSelectVehicle: (VehicleDto) -> Unit,
    onAddNewVehicle: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Step 1: Choose Your Vehicle",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = DeepBurgundy
        )
        Text(
            text = "Select which vehicle needs inspection or maintenance.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (vehicles.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MutedSageGrey
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("You haven't registered any vehicles yet.")
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onAddNewVehicle,
                        colors = ButtonDefaults.buttonColors(containerColor = DeepBurgundy)
                    ) {
                        Text("Add a Vehicle Now", color = WarmCreamBeige)
                    }
                }
            }
        } else {
            val listState = rememberLazyListState()
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .drawLazyVerticalScrollbar(listState)
            ) {
                items(vehicles, key = { it.getEffectiveId() }) { v ->
                    val isSelected = selectedVehicle?.getEffectiveId() == v.getEffectiveId()
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectVehicle(v) },
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) DeepBurgundy else MaterialTheme.colorScheme.outlineVariant
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${v.brand} ${v.model}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepBurgundy
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LicensePlateBadge(vehicleNumber = v.vehicleNumber)
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(DeepBurgundy),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = WarmCreamBeige,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Step 2: Select Service Package
 */
@Composable
fun Step2SelectService(
    services: List<ServiceDto>,
    selectedService: ServiceDto?,
    onSelectService: (ServiceDto) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Step 2: Select Service Package",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = DeepBurgundy
        )
        Text(
            text = "Pick the service that best suits your vehicle's requirement.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        val listState = rememberLazyListState()
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .drawLazyVerticalScrollbar(listState)
        ) {
            items(services, key = { it.getEffectiveId() }) { s ->
                val isSelected = selectedService?.getEffectiveId() == s.getEffectiveId()
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectService(s) },
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) DeepBurgundy else MaterialTheme.colorScheme.outlineVariant
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = s.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DeepBurgundy
                            )
                            if (!s.description.isNullOrBlank()) {
                                Text(
                                    text = s.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = "₹${s.price.toInt()}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = DeepBurgundy,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Step 3: Select Appointment Date
 */
@Composable
fun Step3SelectDate(
    formattedDate: String,
    onOpenDatePicker: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Step 3: Choose Service Date",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = DeepBurgundy
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Pick when you want to drop off your vehicle at the workshop.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(28.dp))

        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenDatePicker() },
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(2.dp, DeepBurgundy)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Appointment Date",
                        style = MaterialTheme.typography.labelMedium,
                        color = MutedSageGrey
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DeepBurgundy
                    )
                }

                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Pick Date",
                    tint = DeepBurgundy,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

/**
 * Step 4: Confirm Booking & Estimated Cost
 */
@Composable
fun Step4ConfirmBooking(
    vehicle: VehicleDto?,
    service: ServiceDto?,
    date: String,
    problemDescription: String,
    onProblemChange: (String) -> Unit
) {
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .drawLazyVerticalScrollbar(listState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Step 4: Review & Confirm",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DeepBurgundy
            )
        }

        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Booking Summary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepBurgundy
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Vehicle:", color = MutedSageGrey)
                        Text(
                            text = "${vehicle?.brand} ${vehicle?.model} (${vehicle?.vehicleNumber})",
                            fontWeight = FontWeight.Bold,
                            color = DeepBurgundy
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Service Package:", color = MutedSageGrey)
                        Text(
                            text = service?.name ?: "",
                            fontWeight = FontWeight.Bold,
                            color = DeepBurgundy
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Service Date:", color = MutedSageGrey)
                        Text(
                            text = date,
                            fontWeight = FontWeight.Bold,
                            color = DeepBurgundy
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = DeepBurgundy.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Estimated Cost",
                                fontWeight = FontWeight.Bold,
                                color = DeepBurgundy
                            )
                            Text(
                                text = "₹${service?.price?.toInt() ?: 0}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = DeepBurgundy
                            )
                        }
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = problemDescription,
                onValueChange = onProblemChange,
                label = { Text("Problem Description / Remarks (Optional)") },
                placeholder = { Text("e.g. Strange noise in front suspension or AC cooling slow") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DeepBurgundy,
                    focusedLabelColor = DeepBurgundy,
                    cursorColor = DeepBurgundy
                )
            )
        }
    }
}

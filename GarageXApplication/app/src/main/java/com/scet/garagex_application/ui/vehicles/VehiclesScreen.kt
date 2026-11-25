package com.scet.garagex_application.ui.vehicles

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scet.garagex_application.data.remote.dto.VehicleDto
import com.scet.garagex_application.ui.components.LicensePlateBadge
import com.scet.garagex_application.ui.components.drawLazyVerticalScrollbar
import com.scet.garagex_application.ui.theme.*

// =========================================================================
// Vehicles Screen Composable
//
// Mandated Components Used:
// 1. Scaffold: Provides the top app bar and floating action button anchor.
// 2. OutlinedCard: Clean, distinct cards for each car in the customer's fleet.
// 3. ExtendedFloatingActionButton: Expands/collapses based on LazyColumn scroll.
// 4. OutlinedTextField: Data entry in the Add Vehicle dialog.
// 5. LazyRow: Fuel type selection chips (PETROL, DIESEL, CNG, ELECTRIC, HYBRID).
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehiclesScreen(
    viewModel: VehicleViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddVehicle: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    // Determine if Extended FAB should be expanded: collapses when scrolling down
    val isFabExpanded by remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Vehicles",
                        color = DeepBurgundyPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DeepBurgundyPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadVehicles() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = DeepBurgundyPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WarmCreamSurface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddVehicle,
                expanded = isFabExpanded,
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                text = { Text("Add Vehicle", fontWeight = FontWeight.SemiBold) },
                containerColor = DeepBurgundyPrimary,
                contentColor = DeepBurgundyOnPrimary
            )
        },
        containerColor = WarmCreamBackground,
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = DeepBurgundyPrimary)
                    }
                }

                uiState.errorMessage != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = uiState.errorMessage ?: "Failed to load",
                                color = StatusErrorRed,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.loadVehicles() },
                                colors = ButtonDefaults.buttonColors(containerColor = DeepBurgundyPrimary)
                            ) {
                                Text("Try Again")
                            }
                        }
                    }
                }

                uiState.vehicles.isEmpty() -> {
                    // Empty state when user hasn't added any cars yet
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = "No vehicles",
                                modifier = Modifier.size(64.dp),
                                tint = MutedSageSecondary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No Vehicles Registered",
                                style = MaterialTheme.typography.headlineMedium,
                                color = DeepBurgundyPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Add your car or motorcycle to quickly book services and track repairs.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = onNavigateToAddVehicle,
                                colors = ButtonDefaults.buttonColors(containerColor = DeepBurgundyPrimary)
                            ) {
                                Text("Register First Vehicle")
                            }
                        }
                    }
                }

                else -> {
                    // Vehicle List using LazyColumn
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .drawLazyVerticalScrollbar(listState),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(uiState.vehicles, key = { it.getEffectiveId() }) { vehicle ->
                            val isActive = uiState.activeVehicleId == vehicle.getEffectiveId()
                            VehicleCardItem(
                                vehicle = vehicle,
                                isActive = isActive,
                                onSetActive = { viewModel.setActiveVehicle(vehicle.getEffectiveId()) },
                                onDelete = { viewModel.deleteVehicle(vehicle.getEffectiveId()) }
                            )
                        }
                    }
                }
            }
        }

        // Add Vehicle Dialog
        if (uiState.isAddDialogVisible) {
            AddVehicleDialog(
                uiState = uiState,
                viewModel = viewModel,
                onDismiss = { viewModel.closeAddDialog() }
            )
        }
    }
}

/**
 * Individual Vehicle Card inside the LazyColumn.
 * Uses OutlinedCard with our embossed LicensePlateBadge and Active Status.
 */
@Composable
fun VehicleCardItem(
    vehicle: VehicleDto,
    isActive: Boolean = false,
    onSetActive: () -> Unit = {},
    onDelete: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = WarmCreamSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(
            if (isActive) DeepBurgundyPrimary else WarmCreamOutline
        ))
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
                // Embossed Indian License Plate Badge
                LicensePlateBadge(vehicleNumber = vehicle.vehicleNumber)

                // Delete Icon Button
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Vehicle",
                        tint = StatusErrorRed.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Brand & Model
            Text(
                text = "${vehicle.brand} ${vehicle.model}",
                style = MaterialTheme.typography.titleLarge,
                color = DeepBurgundyPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Specs Row (Fuel Type Chip, Manufacturing Year, Kilometers)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Fuel Type Chip in Muted Sage Grey
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MutedSageContainer
                ) {
                    Text(
                        text = vehicle.fuelType,
                        color = MutedSageOnContainer,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                if (vehicle.manufacturingYear != null && vehicle.manufacturingYear > 0) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = WarmCreamSurfaceVariant
                    ) {
                        Text(
                            text = "Year ${vehicle.manufacturingYear}",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                if (vehicle.kilometers != null && vehicle.kilometers > 0) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = WarmCreamSurfaceVariant
                    ) {
                        Text(
                            text = "${vehicle.kilometers} km",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Active Vehicle Selector Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isActive) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DeepBurgundyPrimary
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Active",
                                tint = androidx.compose.ui.graphics.Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ACTIVE FLEET CAR",
                                color = androidx.compose.ui.graphics.Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = onSetActive,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepBurgundyPrimary)
                    ) {
                        Text(text = "Set as Active", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

/**
 * Add Vehicle Dialog with validation inputs
 */
@Composable
fun AddVehicleDialog(
    uiState: VehicleUiState,
    viewModel: VehicleViewModel,
    onDismiss: () -> Unit
) {
    val fuelTypes = listOf("PETROL", "DIESEL", "CNG", "ELECTRIC", "HYBRID")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add New Vehicle",
                color = DeepBurgundyPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                if (uiState.formError != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = StatusErrorRed.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = uiState.formError,
                            color = StatusErrorRed,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = uiState.newPlate,
                    onValueChange = { viewModel.onPlateChange(it) },
                    label = { Text("Registration Plate") },
                    placeholder = { Text("MH12AB1234", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = androidx.compose.ui.graphics.Color.White,
                        unfocusedContainerColor = androidx.compose.ui.graphics.Color.White,
                        focusedBorderColor = DeepBurgundyPrimary,
                        unfocusedBorderColor = WarmCreamOutline,
                        cursorColor = DeepBurgundyPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = uiState.newBrand,
                    onValueChange = { viewModel.onBrandChange(it) },
                    label = { Text("Brand / Make") },
                    placeholder = { Text("e.g. Maruti Suzuki, Hyundai, Tata", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = androidx.compose.ui.graphics.Color.White,
                        unfocusedContainerColor = androidx.compose.ui.graphics.Color.White,
                        focusedBorderColor = DeepBurgundyPrimary,
                        unfocusedBorderColor = WarmCreamOutline,
                        cursorColor = DeepBurgundyPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = uiState.newModel,
                    onValueChange = { viewModel.onModelChange(it) },
                    label = { Text("Model") },
                    placeholder = { Text("e.g. Swift, Nexon, Creta", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = androidx.compose.ui.graphics.Color.White,
                        unfocusedContainerColor = androidx.compose.ui.graphics.Color.White,
                        focusedBorderColor = DeepBurgundyPrimary,
                        unfocusedBorderColor = WarmCreamOutline,
                        cursorColor = DeepBurgundyPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Fuel Type",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Fuel Type Selection using LazyRow
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(fuelTypes) { type ->
                        val isSelected = uiState.newFuelType == type
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) DeepBurgundyPrimary else WarmCreamSurfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.onFuelTypeChange(type) }
                        ) {
                            Text(
                                text = type,
                                color = if (isSelected) DeepBurgundyOnPrimary else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.newYear,
                        onValueChange = { viewModel.onYearChange(it) },
                        label = { Text("Year") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = androidx.compose.ui.graphics.Color.White,
                            unfocusedContainerColor = androidx.compose.ui.graphics.Color.White,
                            focusedBorderColor = DeepBurgundyPrimary,
                            unfocusedBorderColor = WarmCreamOutline,
                            cursorColor = DeepBurgundyPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = uiState.newKm,
                        onValueChange = { viewModel.onKmChange(it) },
                        label = { Text("Odometer (km)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = androidx.compose.ui.graphics.Color.White,
                            unfocusedContainerColor = androidx.compose.ui.graphics.Color.White,
                            focusedBorderColor = DeepBurgundyPrimary,
                            unfocusedBorderColor = WarmCreamOutline,
                            cursorColor = DeepBurgundyPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { viewModel.submitNewVehicle() },
                enabled = !uiState.isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = DeepBurgundyPrimary)
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = DeepBurgundyOnPrimary)
                } else {
                    Text("Save Vehicle")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = WarmCreamSurface
    )
}

package com.scet.garagex_application.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.scet.garagex_application.data.remote.RetrofitClient
import com.scet.garagex_application.data.remote.dto.BrandDto
import com.scet.garagex_application.data.remote.dto.CreateVehicleRequest
import com.scet.garagex_application.data.remote.dto.UpdateActiveVehicleRequest
import com.scet.garagex_application.data.remote.dto.VehicleModelDto
import com.scet.garagex_application.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Screenshot 3: "Select your vehicle" Specs Screen
 * Features:
 * - Studio car render at top with model title
 * - Transmission Type selector: Manual vs Automatic (radio cards)
 * - Fuel Type selector: Petrol (Red), Diesel (Blue), CNG (Green) cards
 * - Registration number input
 * - Deep Burgundy button: "Save Vehicle to Fleet"
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleSpecsScreen(
    brand: BrandDto,
    model: VehicleModelDto,
    onNavigateBack: () -> Unit,
    onVehicleSaved: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedTransmission by remember { mutableStateOf("MANUAL") }
    var selectedFuelType by remember { mutableStateOf("PETROL") }
    var vehicleNumber by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Select your vehicle",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Studio Car Render Header matching Screenshot 3
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = model.imageUrl,
                    contentDescription = model.name,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(140.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Text(
                text = "${brand.name} ${model.name}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = WarmCreamSurfaceVariant, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Section 1: Transmission Selector matching Screenshot 3
            Text(
                text = "Car Transmission Type",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TransmissionOptionCard(
                    title = "Manual",
                    isSelected = selectedTransmission == "MANUAL",
                    modifier = Modifier.weight(1f),
                    onClick = { selectedTransmission = "MANUAL" }
                )
                TransmissionOptionCard(
                    title = "Automatic",
                    isSelected = selectedTransmission == "AUTOMATIC",
                    modifier = Modifier.weight(1f),
                    onClick = { selectedTransmission = "AUTOMATIC" }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section 2: Fuel Type Selector matching Screenshot 3
            Text(
                text = "Car Fuel Type",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FuelOptionCard(
                    title = "Petrol",
                    iconColor = Color(0xFFD32F2F), // Red fuel canister
                    isSelected = selectedFuelType == "PETROL",
                    modifier = Modifier.weight(1f),
                    onClick = { selectedFuelType = "PETROL" }
                )
                FuelOptionCard(
                    title = "Diesel",
                    iconColor = Color(0xFF1976D2), // Blue nozzle
                    isSelected = selectedFuelType == "DIESEL",
                    modifier = Modifier.weight(1f),
                    onClick = { selectedFuelType = "DIESEL" }
                )
                FuelOptionCard(
                    title = "CNG",
                    iconColor = Color(0xFF2E7D32), // Green diamond
                    isSelected = selectedFuelType == "CNG",
                    modifier = Modifier.weight(1f),
                    onClick = { selectedFuelType = "CNG" }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // License Plate Number input
            Text(
                text = "Vehicle License Plate",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = vehicleNumber,
                onValueChange = { vehicleNumber = it.uppercase() },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. MH12AB1234", color = TextSecondary) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DeepBurgundyPrimary,
                    unfocusedBorderColor = WarmCreamOutline
                ),
                singleLine = true
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Submit Button
            Button(
                onClick = {
                    if (vehicleNumber.isBlank()) {
                        errorMessage = "Please enter vehicle registration number."
                        return@Button
                    }
                    coroutineScope.launch {
                        try {
                            isSubmitting = true
                            errorMessage = null
                            val req = CreateVehicleRequest(
                                vehicleNumber = vehicleNumber.trim().uppercase(),
                                brand = brand.name,
                                model = model.name,
                                fuelType = selectedFuelType,
                                manufacturingYear = 2022,
                                kilometers = 12000,
                                transmission = selectedTransmission,
                                carImageUrl = model.imageUrl
                            )
                            val res = RetrofitClient.apiService.createVehicle(req)
                            if (res.isSuccessful && res.body()?.success == true) {
                                val savedCar = res.body()?.vehicle
                                if (savedCar != null) {
                                    RetrofitClient.apiService.updateActiveVehicle(
                                        UpdateActiveVehicleRequest(savedCar.getEffectiveId())
                                    )
                                }
                                onVehicleSaved()
                            } else {
                                errorMessage = res.body()?.message ?: "Failed to save vehicle"
                            }
                        } catch (e: Exception) {
                            errorMessage = e.localizedMessage ?: "Network error occurred"
                        } finally {
                            isSubmitting = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepBurgundyPrimary),
                enabled = !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Save Vehicle to Fleet",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TransmissionOptionCard(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(54.dp)
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) DeepBurgundyPrimary else WarmCreamOutline,
                shape = RoundedCornerShape(10.dp)
            ),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) DeepBurgundyContainer.copy(alpha = 0.35f) else Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = DeepBurgundyPrimary)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) DeepBurgundyPrimary else TextPrimary
            )
        }
    }
}

@Composable
fun FuelOptionCard(
    title: String,
    iconColor: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(84.dp)
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) DeepBurgundyPrimary else WarmCreamOutline,
                shape = RoundedCornerShape(10.dp)
            ),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) DeepBurgundyContainer.copy(alpha = 0.25f) else Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocalGasStation,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) DeepBurgundyPrimary else TextPrimary
            )
        }
    }
}

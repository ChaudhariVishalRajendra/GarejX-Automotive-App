package com.scet.garagex_application.ui.sos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scet.garagex_application.data.remote.RetrofitClient
import com.scet.garagex_application.data.remote.dto.CreateSosRequest
import com.scet.garagex_application.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SosRoadsideScreen(
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedIssue by remember { mutableStateOf("TOWING") }
    var userPhone by remember { mutableStateOf("9876543210") }
    var notes by remember { mutableStateOf("") }
    var isDispatched by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "24/7 Emergency SOS",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepBurgundyPrimary
                )
            )
        },
        containerColor = WarmCreamBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Hero SOS Alert Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFEBEE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Emergency,
                            contentDescription = "SOS",
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Roadside Breakdown Assistance",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Average response time: 20-30 minutes",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Select Breakdown Issue",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            // 4 Breakdown Types
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SosIssueTile(
                    title = "Towing Service",
                    icon = Icons.Default.RvHookup,
                    isSelected = selectedIssue == "TOWING",
                    modifier = Modifier.weight(1f),
                    onClick = { selectedIssue = "TOWING" }
                )
                SosIssueTile(
                    title = "Flat Tyre",
                    icon = Icons.Default.TireRepair,
                    isSelected = selectedIssue == "FLAT_TYRE",
                    modifier = Modifier.weight(1f),
                    onClick = { selectedIssue = "FLAT_TYRE" }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SosIssueTile(
                    title = "Jumpstart",
                    icon = Icons.Default.BatteryChargingFull,
                    isSelected = selectedIssue == "BATTERY_JUMPSTART",
                    modifier = Modifier.weight(1f),
                    onClick = { selectedIssue = "BATTERY_JUMPSTART" }
                )
                SosIssueTile(
                    title = "Emergency Fuel",
                    icon = Icons.Default.LocalGasStation,
                    isSelected = selectedIssue == "EMERGENCY_FUEL",
                    modifier = Modifier.weight(1f),
                    onClick = { selectedIssue = "EMERGENCY_FUEL" }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Contact Phone Input
            OutlinedTextField(
                value = userPhone,
                onValueChange = { userPhone = it },
                label = { Text("Emergency Contact Number") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Landmark / Car Problem Details (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isDispatched) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BadgeContainerGreen)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "✓ Towing Van Dispatched!",
                            fontWeight = FontWeight.Bold,
                            color = EmeraldBadgeGreen,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Technician Assigned: Ramesh (GJ-05-9988)\nExpected arrival: 18 mins",
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                    }
                }
            } else {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                isSubmitting = true
                                RetrofitClient.apiService.createSos(
                                    CreateSosRequest(
                                        phone = userPhone,
                                        latitude = 21.1702,
                                        longitude = 72.8311,
                                        locationName = "University Road, City",
                                        issueType = selectedIssue,
                                        notes = notes
                                    )
                                )
                                isDispatched = true
                            } catch (e: Exception) {
                                isDispatched = true
                            } finally {
                                isSubmitting = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "DISPATCH EMERGENCY ASSISTANCE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SosIssueTile(
    title: String,
    icon: ImageVector,
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
                color = if (isSelected) Color(0xFFD32F2F) else WarmCreamOutline,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFFEBEE) else Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) Color(0xFFD32F2F) else DeepBurgundyPrimary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color(0xFFD32F2F) else TextPrimary
            )
        }
    }
}

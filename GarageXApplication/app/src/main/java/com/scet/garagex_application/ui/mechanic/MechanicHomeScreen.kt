package com.scet.garagex_application.ui.mechanic

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import com.scet.garagex_application.ui.theme.TextPrimary
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scet.garagex_application.data.remote.dto.AppointmentDto
import com.scet.garagex_application.data.remote.dto.BillItemDto
import com.scet.garagex_application.ui.appointments.AppointmentStatusBadge
import com.scet.garagex_application.ui.components.LicensePlateBadge
import com.scet.garagex_application.ui.theme.DeepBurgundy
import com.scet.garagex_application.ui.theme.MutedSageGrey
import com.scet.garagex_application.ui.theme.StatusCompleted
import com.scet.garagex_application.ui.theme.StatusInProgress
import com.scet.garagex_application.ui.theme.StatusPending
import com.scet.garagex_application.ui.theme.WarmCreamBeige

// =========================================================================
// MechanicHomeScreen: Workshop Floor Bay Operations & Stat Cards
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MechanicHomeScreen(
    viewModel: MechanicViewModel,
    onLogoutClick: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var showCompleteDialog by remember { mutableStateOf<AppointmentDto?>(null) }
    var showBillDialog by remember { mutableStateOf<AppointmentDto?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadWorkshopData(context)
    }

    LaunchedEffect(uiState.actionSuccessMessage) {
        uiState.actionSuccessMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Workshop Bay",
                            fontWeight = FontWeight.Bold,
                            color = WarmCreamBeige
                        )
                        Text(
                            text = "Technician Console",
                            style = MaterialTheme.typography.labelSmall,
                            color = WarmCreamBeige.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onLogoutClick) {
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
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = DeepBurgundy)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. 4 Metric Stat Cards (2x2 Grid)
                item {
                    Text(
                        text = "Workshop Overview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepBurgundy
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatMetricCard(
                            title = "Pending",
                            count = uiState.stats.pending,
                            icon = Icons.Default.HourglassTop,
                            color = StatusPending,
                            modifier = Modifier.weight(1f)
                        )
                        StatMetricCard(
                            title = "Assigned",
                            count = uiState.stats.assigned,
                            icon = Icons.Default.Assignment,
                            color = Color(0xFF1976D2),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatMetricCard(
                            title = "In Progress",
                            count = uiState.stats.inProgress,
                            icon = Icons.Default.Build,
                            color = StatusInProgress,
                            modifier = Modifier.weight(1f)
                        )
                        StatMetricCard(
                            title = "Completed",
                            count = uiState.stats.completed,
                            icon = Icons.Default.CheckCircle,
                            color = StatusCompleted,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 2. Assigned Jobs Queue
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "My Assigned Vehicle Bay Queue (${uiState.jobs.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepBurgundy
                    )
                }

                if (uiState.jobs.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No active repair jobs assigned to you right now.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(uiState.jobs, key = { it.getEffectiveId() }) { job ->
                        MechanicJobItemCard(
                            job = job,
                            onStartJob = { viewModel.startServicing(context, job.getEffectiveId()) },
                            onOpenFinishDialog = { showCompleteDialog = job },
                            onOpenBillDialog = { showBillDialog = job }
                        )
                    }
                }
            }
        }
    }

    // Complete Job Dialog
    showCompleteDialog?.let { job ->
        CompleteJobDialog(
            job = job,
            onDismiss = { showCompleteDialog = null },
            onConfirm = { notes, cost ->
                viewModel.completeServicing(context, job.getEffectiveId(), notes, cost)
                showCompleteDialog = null
            }
        )
    }

    // Generate Bill Dialog
    showBillDialog?.let { job ->
        InBayBillDialog(
            job = job,
            onDismiss = { showBillDialog = null },
            onConfirm = { items, tax, discount ->
                viewModel.createInBayBill(context, job.getEffectiveId(), items, tax, discount) {
                    showBillDialog = null
                    Toast.makeText(context, "Bill created!", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}

/**
 * Metric Stat Card
 */
@Composable
fun StatMetricCard(
    title: String,
    count: Int,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedSageGrey,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "$count",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = DeepBurgundy
            )
        }
    }
}

/**
 * Individual Mechanic Job Card with Action CTAs
 */
@Composable
fun MechanicJobItemCard(
    job: AppointmentDto,
    onStartJob: () -> Unit,
    onOpenFinishDialog: () -> Unit,
    onOpenBillDialog: () -> Unit
) {
    val context = LocalContext.current

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
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
                Text(
                    text = "${job.vehicle?.brand ?: "Car"} ${job.vehicle?.model ?: ""}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepBurgundy
                )
                AppointmentStatusBadge(status = job.getEffectiveStatus())
            }

            Spacer(modifier = Modifier.height(8.dp))

            job.vehicle?.vehicleNumber?.let { plate ->
                LicensePlateBadge(vehicleNumber = plate)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Customer details & quick call
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Customer: ${job.customer?.name ?: "Customer"}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                if (!job.customer?.phone.isNullOrBlank()) {
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${job.customer.phone}"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Call Customer",
                            tint = DeepBurgundy
                        )
                    }
                }
            }

            if (!job.problemDescription.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Reported: ${job.problemDescription}",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons based on status
            when (job.getEffectiveStatus()) {
                "ASSIGNED" -> {
                    Button(
                        onClick = onStartJob,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DeepBurgundy,
                            contentColor = WarmCreamBeige
                        )
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Start Servicing in Bay", fontWeight = FontWeight.Bold)
                    }
                }

                "IN_PROGRESS" -> {
                    Button(
                        onClick = onOpenFinishDialog,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StatusCompleted,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Mark Repairs Completed", fontWeight = FontWeight.Bold)
                    }
                }

                "COMPLETED" -> {
                    OutlinedButton(
                        onClick = onOpenBillDialog,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Receipt, contentDescription = null, tint = DeepBurgundy)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Generate Itemized Bill", color = DeepBurgundy, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Dialog to input diagnostic inspection notes & final cost when finishing a job
 */
@Composable
fun CompleteJobDialog(
    job: AppointmentDto,
    onDismiss: () -> Unit,
    onConfirm: (notes: String, cost: Double) -> Unit
) {
    var notes by remember { mutableStateOf("") }
    var costText by remember { mutableStateOf("${job.estimatedCost?.toInt() ?: 2000}") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Complete Service Bay Job") },
        text = {
            Column {
                Text(
                    text = "Enter repair notes and final cost for ${job.vehicle?.brand} ${job.vehicle?.model}.",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Mechanic Diagnostic Notes") },
                    placeholder = { Text("e.g. Changed oil, replaced front brake pads") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = androidx.compose.ui.graphics.Color.White,
                        unfocusedContainerColor = androidx.compose.ui.graphics.Color.White,
                        focusedBorderColor = DeepBurgundy,
                        cursorColor = DeepBurgundy
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = costText,
                    onValueChange = { costText = it.filter { char -> char.isDigit() || char == '.' } },
                    label = { Text("Final Labor & Parts Cost (₹)") },
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cost = costText.toDoubleOrNull() ?: 0.0
                    onConfirm(notes, cost)
                },
                colors = ButtonDefaults.buttonColors(containerColor = DeepBurgundy)
            ) {
                Text("Submit & Complete", color = WarmCreamBeige)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MutedSageGrey)
            }
        }
    )
}

/**
 * Dialog to add spare parts line items and generate an itemized bill
 */
@Composable
fun InBayBillDialog(
    job: AppointmentDto,
    onDismiss: () -> Unit,
    onConfirm: (items: List<BillItemDto>, tax: Double, discount: Double) -> Unit
) {
    var itemName by remember { mutableStateOf("Engine Oil 5W30") }
    var itemQty by remember { mutableStateOf("1") }
    var itemPrice by remember { mutableStateOf("1500") }
    var laborPrice by remember { mutableStateOf("500") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Generate Customer Invoice") },
        text = {
            Column {
                Text(
                    text = "Add primary spare parts and labor for ${job.vehicle?.vehicleNumber}.",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(12.dp))

                val fieldColors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = androidx.compose.ui.graphics.Color.White,
                    unfocusedContainerColor = androidx.compose.ui.graphics.Color.White,
                    focusedBorderColor = DeepBurgundy,
                    cursorColor = DeepBurgundy
                )

                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Primary Spare Part") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = itemQty,
                        onValueChange = { itemQty = it.filter { c -> c.isDigit() } },
                        label = { Text("Qty") },
                        modifier = Modifier.weight(1f),
                        colors = fieldColors
                    )
                    OutlinedTextField(
                        value = itemPrice,
                        onValueChange = { itemPrice = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Part Price (₹)") },
                        modifier = Modifier.weight(1.5f),
                        colors = fieldColors
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = laborPrice,
                    onValueChange = { laborPrice = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Labor Charges (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val pQty = itemQty.toIntOrNull() ?: 1
                    val pPrice = itemPrice.toDoubleOrNull() ?: 0.0
                    val lPrice = laborPrice.toDoubleOrNull() ?: 0.0

                    val items = listOf(
                        BillItemDto(name = itemName.ifBlank { "Replacement Parts" }, quantity = pQty, price = pPrice),
                        BillItemDto(name = "Workshop Labor & Inspection", quantity = 1, price = lPrice)
                    )
                    onConfirm(items, 100.0, 50.0) // 100 tax, 50 discount
                },
                colors = ButtonDefaults.buttonColors(containerColor = DeepBurgundy)
            ) {
                Text("Generate Bill", color = WarmCreamBeige)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MutedSageGrey)
            }
        }
    )
}

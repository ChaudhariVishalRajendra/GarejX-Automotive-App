package com.scet.garagex_application.ui.bills

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scet.garagex_application.data.remote.dto.BillDto
import com.scet.garagex_application.ui.theme.DeepBurgundy
import com.scet.garagex_application.ui.theme.MutedSageGrey
import com.scet.garagex_application.ui.theme.StatusCompleted
import com.scet.garagex_application.ui.theme.StatusPending
import com.scet.garagex_application.ui.theme.WarmCreamBeige

// =========================================================================
// BillDetailScreen: Itemized Workshop Receipt with UPI Mobile Payment Intent
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillDetailScreen(
    billId: String,
    viewModel: BillsViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(billId) {
        viewModel.loadBillDetail(context, billId)
    }

    val bill = uiState.currentBill

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Itemized Receipt",
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
        if (uiState.isDetailLoading || bill == null) {
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
                item {
                    // Authentic Receipt Card
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            // Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Text(
                                        text = "GarejX Workshop",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = DeepBurgundy
                                    )
                                    Text(
                                        text = "Automotive Care & Diagnostics",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MutedSageGrey
                                    )
                                }

                                Surface(
                                    color = if (bill.isPaid()) StatusCompleted.copy(alpha = 0.15f) else StatusPending.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (bill.isPaid()) "PAID" else "PENDING",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (bill.isPaid()) StatusCompleted else StatusPending
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.4f))
                            Spacer(modifier = Modifier.height(12.dp))

                            // Invoice Metadata
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Invoice Number:", color = MutedSageGrey, style = MaterialTheme.typography.bodySmall)
                                Text(
                                    "#INV-${bill.getEffectiveId().takeLast(8).uppercase()}",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Date:", color = MutedSageGrey, style = MaterialTheme.typography.bodySmall)
                                Text(
                                    bill.createdAt?.take(10) ?: "Today",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.4f))
                            Spacer(modifier = Modifier.height(12.dp))

                            // Line Items Header
                            Text(
                                text = "Spare Parts & Labor Breakdown",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = DeepBurgundy
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Table Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Item", fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f), style = MaterialTheme.typography.bodySmall)
                                Text("Qty", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                                Text("Price", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                                Text("Total", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(modifier = Modifier.height(4.dp))

                            // Items List
                            bill.items?.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(item.name, modifier = Modifier.weight(2f), style = MaterialTheme.typography.bodySmall)
                                    Text("${item.quantity}", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                                    Text("₹${item.price.toInt()}", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                                    Text("₹${(item.total ?: (item.quantity * item.price)).toInt()}", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.4f))
                            Spacer(modifier = Modifier.height(12.dp))

                            // Subtotal, Tax, Discount
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Subtotal:", color = MutedSageGrey)
                                Text("₹${bill.subtotal?.toInt() ?: 0}", fontWeight = FontWeight.Medium)
                            }
                            if ((bill.tax ?: 0.0) > 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Tax (GST):", color = MutedSageGrey)
                                    Text("+₹${bill.tax?.toInt() ?: 0}", fontWeight = FontWeight.Medium)
                                }
                            }
                            if ((bill.discount ?: 0.0) > 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Discount:", color = MutedSageGrey)
                                    Text("-₹${bill.discount?.toInt() ?: 0}", color = StatusCompleted, fontWeight = FontWeight.Medium)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = DeepBurgundy, thickness = 2.dp)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Grand Total
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Grand Total",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepBurgundy
                                )
                                Text(
                                    text = "₹${bill.grandTotal?.toInt() ?: 0}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DeepBurgundy
                                )
                            }
                        }
                    }
                }

                // Payment Action Buttons
                if (!bill.isPaid()) {
                    item {
                        Button(
                            onClick = {
                                // Launch UPI Payment Intent (PhonePe, Google Pay, Paytm)
                                val amount = bill.grandTotal ?: 0.0
                                val upiUri = Uri.parse("upi://pay?pa=garejx@upi&pn=GarejX%20Workshop&mc=0000&tid=${bill.getEffectiveId()}&tr=${bill.getEffectiveId()}&tn=GarejX%20Bill%20Payment&am=$amount&cu=INR")
                                val upiIntent = Intent(Intent.ACTION_VIEW, upiUri)
                                val chooser = Intent.createChooser(upiIntent, "Pay with UPI App")
                                try {
                                    context.startActivity(chooser)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "No UPI app installed on device. You can simulate payment below.", Toast.LENGTH_LONG).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DeepBurgundy,
                                contentColor = WarmCreamBeige
                            )
                        ) {
                            Icon(imageVector = Icons.Default.Payment, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pay via UPI App (GPay / PhonePe)", fontWeight = FontWeight.Bold)
                        }
                    }

                    item {
                        OutlinedButton(
                            onClick = {
                                viewModel.markBillPaid(context, bill.getEffectiveId())
                                Toast.makeText(context, "Bill marked as PAID!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = StatusCompleted)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Confirm Cash / Direct Payment", color = DeepBurgundy)
                        }
                    }
                } else {
                    item {
                        Surface(
                            color = StatusCompleted.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = StatusCompleted
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "This bill has been fully settled.",
                                    color = StatusCompleted,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

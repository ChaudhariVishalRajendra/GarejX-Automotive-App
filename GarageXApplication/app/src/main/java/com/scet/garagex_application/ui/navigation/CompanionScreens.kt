package com.scet.garagex_application.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.scet.garagex_application.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & Support", fontWeight = FontWeight.Bold, color = DeepBurgundyPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = WarmCreamBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "24x7 Customer Helpline",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Call us for repair estimates, slot rescheduling, or warranty queries.",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {},
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = DeepBurgundyPrimary)
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Call +91 98765 43210")
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Frequently Asked Questions",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            val faqs = listOf(
                "How does the ₹1,500 GarejX Money work?" to "Your welcome credits can be applied at checkout for any service booking or spare parts purchase.",
                "Is there warranty on spare parts?" to "Yes, all OEM and OES spare parts fitted by GarejX workshops carry a 1-year / 10,000 km warranty.",
                "How do I track my vehicle live?" to "Once the mechanic begins work, the live stepped progress tracker updates your status in real-time."
            )

            items(faqs.size) { idx ->
                val (q, a) = faqs[idx]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = q, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = DeepBurgundyPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = a, fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}

data class StoreProduct(
    val name: String,
    val category: String,
    val price: String,
    val rating: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoStoreScreen() {
    val storeItems = listOf(
        StoreProduct("Castrol EDGE 5W-40 Synthetic Oil (4L)", "Engine Oil", "₹ 2,850", "4.8 ★", Icons.Default.Build),
        StoreProduct("Bosch Clear Advantage Wiper Set", "Wipers", "₹ 849", "4.7 ★", Icons.Default.DirectionsCar),
        StoreProduct("70mai Dash Cam 1S FHD 1080P", "Electronics", "₹ 3,999", "4.9 ★", Icons.Default.Videocam),
        StoreProduct("Qubo Smart Tyre Inflator (150 PSI)", "Emergency", "₹ 2,699", "4.8 ★", Icons.Default.Speed),
        StoreProduct("Ambi Pur Car Fragrance Gel (Aqua)", "Fragrance", "₹ 299", "4.6 ★", Icons.Default.Spa),
        StoreProduct("3M Microfiber Detailing Cloth (4-Pack)", "Car Care", "₹ 449", "4.9 ★", Icons.Default.CleaningServices),
        StoreProduct("Portronics 15W Car Mobile Mount", "Accessories", "₹ 1,199", "4.7 ★", Icons.Default.PhoneAndroid),
        StoreProduct("Wipro High-Power Car Vacuum Cleaner", "Car Care", "₹ 1,599", "4.6 ★", Icons.Default.ElectricalServices)
    )

    var addedItem by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("GoStore Car Accessories", fontWeight = FontWeight.Bold, color = DeepBurgundyPrimary, fontSize = 18.sp)
                        Text("100% Genuine Auto Parts & Care", fontSize = 11.sp, color = TextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.ui.graphics.Color.White)
            )
        },
        containerColor = WarmCreamBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(14.dp)
        ) {
            if (addedItem != null) {
                Surface(
                    color = DeepBurgundyPrimary,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "✓ Added \"$addedItem\" to garage cart!",
                        color = DeepBurgundyOnPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(storeItems) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Product Icon Banner
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(84.dp)
                                    .background(WarmCreamSurfaceVariant, shape = RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.name,
                                    tint = DeepBurgundyPrimary,
                                    modifier = Modifier.size(38.dp)
                                )
                                Surface(
                                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                ) {
                                    Text(
                                        text = item.rating,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = item.category.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 0.5.sp
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = item.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                maxLines = 2,
                                minLines = 2
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.price,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DeepBurgundyPrimary
                                )
                                Button(
                                    onClick = { addedItem = item.name },
                                    colors = ButtonDefaults.buttonColors(containerColor = DeepBurgundyPrimary),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(28.dp),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Add", fontSize = 11.sp, color = DeepBurgundyOnPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

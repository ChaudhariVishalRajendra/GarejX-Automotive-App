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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoStoreScreen() {
    val storeItems = listOf(
        Triple("Castrol EDGE 5W-40 Synthetic Oil", "₹ 2,499", "https://images.unsplash.com/photo-1615906655593-ad0386982a0f?w=400"),
        Triple("Bosch Eco Wiper Blades (Set of 2)", "₹ 749", "https://images.unsplash.com/photo-1583121274602-3e2820c69888?w=400"),
        Triple("Ambi Pur Car Fragrance Clip", "₹ 299", "https://images.unsplash.com/photo-1541899481282-d53bffe3c35d?w=400"),
        Triple("3M Microfiber Detailing Cloth (4-Pack)", "₹ 499", "https://images.unsplash.com/photo-1507089947368-19c1da9775ae?w=400")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GoStore — Car Accessories", fontWeight = FontWeight.Bold, color = DeepBurgundyPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
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
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(storeItems) { (name, price, img) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            AsyncImage(
                                model = img,
                                contentDescription = name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 2)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = price, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = DeepBurgundyPrimary)
                        }
                    }
                }
            }
        }
    }
}

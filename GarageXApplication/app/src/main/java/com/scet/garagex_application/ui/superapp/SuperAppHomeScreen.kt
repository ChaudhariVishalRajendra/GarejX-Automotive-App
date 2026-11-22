package com.scet.garagex_application.ui.superapp

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.scet.garagex_application.data.local.TokenManager
import com.scet.garagex_application.data.remote.RetrofitClient
import com.scet.garagex_application.data.remote.dto.*
import com.scet.garagex_application.ui.components.drawLazyVerticalScrollbar
import com.scet.garagex_application.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Screenshot 4: Automotive Super-App Home Dashboard
 * Features:
 * - Dynamic Active Vehicle Dropdown [ 🚗 HYUNDAI CRETA ▾ ] synced with DB (PATCH /api/auth/active-vehicle)
 * - Live Category Search Bar with instant filtering
 * - Hero Promo Carousel: "CHECK AND PAY CHALLAN ONLINE"
 * - 12-Category Super-Grid with emerald offer badges
 * - Instant Navigation to 3-step Vehicle Wizard and Service Booking
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperAppHomeScreen(
    onNavigateToSelectBrand: () -> Unit,
    onNavigateToBooking: (String) -> Unit,
    onNavigateToSos: () -> Unit,
    onNavigateToVehicles: () -> Unit,
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var banners by remember { mutableStateOf<List<BannerDto>>(emptyList()) }
    var categories by remember { mutableStateOf<List<ServiceCategoryDto>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }

    var myVehicles by remember { mutableStateOf<List<VehicleDto>>(emptyList()) }
    var activeVehicle by remember { mutableStateOf<VehicleDto?>(null) }
    var showCarPickerSheet by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    fun refreshDashboard() {
        coroutineScope.launch {
            try {
                isLoading = true
                // 1. Fetch banners
                val bannerRes = RetrofitClient.apiService.getBanners()
                if (bannerRes.isSuccessful) {
                    banners = bannerRes.body()?.banners ?: emptyList()
                }

                // 2. Fetch service categories
                val catRes = RetrofitClient.apiService.getCategories()
                if (catRes.isSuccessful) {
                    categories = catRes.body()?.categories ?: emptyList()
                }

                // 3. Fetch user profile for active vehicle ID
                val profileRes = RetrofitClient.apiService.getProfile()
                val activeVehicleId = profileRes.body()?.user?.activeVehicle

                // 4. Fetch user fleet
                val vehRes = RetrofitClient.apiService.getVehicles()
                if (vehRes.isSuccessful) {
                    val list = vehRes.body()?.vehicles ?: emptyList()
                    myVehicles = list
                    // Match active vehicle from DB or pick the latest vehicle added
                    activeVehicle = list.find { it.getEffectiveId() == activeVehicleId }
                        ?: list.lastOrNull()
                }
            } catch (e: Exception) {
                // Keep default state
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshDashboard()
    }

    Scaffold(
        topBar = {
            // Header matching Screenshot 4: Search bar on left, interactive dropdown pill on right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Search Input Box with real-time filtering
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmCreamSurfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search services, repairs...",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                            }
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                singleLine = true,
                                textStyle = TextStyle(fontSize = 13.sp, color = TextPrimary),
                                cursorBrush = SolidColor(DeepBurgundyPrimary),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { searchQuery = "" },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // Interactive Active Car Header Pill with Dropdown Arrow matching Screenshot 4
                Card(
                    modifier = Modifier
                        .clickable { showCarPickerSheet = true }
                        .border(1.dp, WarmCreamOutline, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = "Active Car",
                            tint = DeepBurgundyPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        val pillText = if (activeVehicle != null) {
                            "${activeVehicle!!.brand} ${activeVehicle!!.model}".uppercase()
                        } else {
                            "+ ADD CAR"
                        }
                        Text(
                            text = pillText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepBurgundyPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Switch Vehicle",
                            tint = DeepBurgundyPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Instant Header Logout Icon Button
                IconButton(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Log Out",
                        tint = Color(0xFFC62828),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        containerColor = WarmCreamBackground
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .drawLazyVerticalScrollbar(listState),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            // 1. Hero Promo Carousel matching Screenshot 4 (Only shown when not actively searching)
            if (searchQuery.isBlank()) {
                item {
                    PromoCarouselSection(
                        banners = banners,
                        onPayChallanClick = {
                            Toast.makeText(context, "Redirecting to GarejX Pro Online Challan Portal...", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            // 2. 12-Category Super-Grid (3 Columns x 4 Rows) matching Screenshot 4
            item {
                Spacer(modifier = Modifier.height(16.dp))

                val filteredCategories = if (searchQuery.isBlank()) {
                    categories
                } else {
                    categories.filter {
                        it.title.contains(searchQuery, ignoreCase = true) ||
                        it.code.contains(searchQuery, ignoreCase = true) ||
                        (it.badgeText?.contains(searchQuery, ignoreCase = true) == true)
                    }
                }

                CategorySuperGridSection(
                    categories = filteredCategories,
                    onCategoryClick = { catCode ->
                        if (catCode == "SOS_ROADSIDE") {
                            onNavigateToSos()
                        } else {
                            onNavigateToBooking(catCode)
                        }
                    }
                )
            }
        }
    }

    // Vehicle Switcher Modal BottomSheet Dropdown (Synced with DB)
    if (showCarPickerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCarPickerSheet = false },
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Select Active Vehicle",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Choose your vehicle for instant bookings & diagnostics",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (myVehicles.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = WarmCreamBackground)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No vehicles registered in your fleet yet.",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                    }
                } else {
                    myVehicles.forEach { veh ->
                        val isSelected = activeVehicle?.getEffectiveId() == veh.getEffectiveId()
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .clickable {
                                    activeVehicle = veh
                                    coroutineScope.launch {
                                        try {
                                            RetrofitClient.apiService.updateActiveVehicle(
                                                UpdateActiveVehicleRequest(veh.getEffectiveId())
                                            )
                                            Toast.makeText(
                                                context,
                                                "${veh.brand} ${veh.model} set as active car!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } catch (e: Exception) {
                                            // Handle network error
                                        }
                                    }
                                    showCarPickerSheet = false
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) DeepBurgundyContainer.copy(alpha = 0.45f) else WarmCreamBackground
                            ),
                            border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = SolidColor(DeepBurgundyPrimary)) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) DeepBurgundyPrimary else WarmCreamSurfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DirectionsCar,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else DeepBurgundyPrimary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "${veh.brand} ${veh.model}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = TextPrimary
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = veh.vehicleNumber,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = TextSecondary
                                            )
                                            Text(text = "•", fontSize = 12.sp, color = TextSecondary)
                                            Text(
                                                text = veh.fuelType,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = DeepBurgundyPrimary
                                            )
                                        }
                                    }
                                }

                                if (isSelected) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = DeepBurgundyPrimary
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Active",
                                                tint = Color.White,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "ACTIVE",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // + Add New Vehicle Wizard Button (Launches Screenshot 1 Brand Selector)
                Button(
                    onClick = {
                        showCarPickerSheet = false
                        onNavigateToSelectBrand()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepBurgundyPrimary)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "+ Add New Vehicle Wizard", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        showCarPickerSheet = false
                        onNavigateToVehicles()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Manage All Fleet Vehicles", color = TextSecondary)
                }
            }
        }
    }

    // Instant Log Out Confirmation Dialog
    if (showLogoutDialog) {
        val tokenManager = remember { TokenManager(context.applicationContext) }
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Log Out from GarejX?",
                    fontWeight = FontWeight.Bold,
                    color = DeepBurgundyPrimary
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to end your active session? You will need to log in again.",
                    color = TextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        coroutineScope.launch {
                            tokenManager.clearSession()
                            onLogout()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                ) {
                    Text("Log Out", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
fun PromoCarouselSection(
    banners: List<BannerDto>,
    onPayChallanClick: () -> Unit
) {
    val displayBanners = if (banners.isNotEmpty()) banners else listOf(
        BannerDto(
            id = "1",
            title = "CHECK AND PAY CHALLAN ONLINE",
            subtitle = "100% Paperless & Instant Verification",
            actionUrl = "",
            imageUrl = "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?w=800&auto=format&fit=crop&q=80",
            tag = "Pay Now >"
        ),
        BannerDto(
            id = "2",
            title = "Monsoon Shield Package",
            subtitle = "40-Point Free Checkup + Wiper Blades",
            actionUrl = "",
            imageUrl = "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=800&auto=format&fit=crop&q=80",
            tag = "Book Now >"
        )
    )

    val pagerState = rememberPagerState(pageCount = { displayBanners.size })

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(16.dp))
        ) { page ->
            val item = displayBanners[page]
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onPayChallanClick() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E212D))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alpha = 0.45f
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "GAREJX PRO",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = item.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                maxLines = 2
                            )
                            if (!item.subtitle.isNullOrBlank()) {
                                Text(
                                    text = item.subtitle,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        // Action Tag matching Screenshot 4
                        Card(
                            shape = RoundedCornerShape(6.dp),
                            colors = CardDefaults.cardColors(containerColor = DeepBurgundyPrimary)
                        ) {
                            Text(
                                text = item.tag,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Pager indicator dots
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(displayBanners.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) DeepBurgundyPrimary else WarmCreamOutline
                Box(
                    modifier = Modifier
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(if (pagerState.currentPage == iteration) 8.dp else 6.dp)
                )
            }
        }
    }
}

@Composable
fun CategorySuperGridSection(
    categories: List<ServiceCategoryDto>,
    onCategoryClick: (String) -> Unit
) {
    val displayCategories = if (categories.isNotEmpty()) categories else listOf(
        ServiceCategoryDto(id="1", code="CAR_SERVICES", title="Car Services", badgeText="Special Discount", iconName="car_repair"),
        ServiceCategoryDto(id="2", code="DENTING_PAINTING", title="Denting & Painting", badgeText="Grade-A Booth", iconName="format_paint"),
        ServiceCategoryDto(id="3", code="AC_SERVICE", title="AC Service & Repair", badgeText="Gas Refill ₹999", iconName="ac_unit"),
        ServiceCategoryDto(id="4", code="CAR_SPA", title="Car Spa & Cleaning", badgeText="Interior Clean", iconName="local_car_wash"),
        ServiceCategoryDto(id="5", code="DETAILING", title="Detailing Services", badgeText="Ceramic Coat", iconName="auto_awesome"),
        ServiceCategoryDto(id="6", code="BATTERIES", title="Batteries", badgeText="Free In-Bay Check", iconName="battery_charging_full"),
        ServiceCategoryDto(id="7", code="TYRES_WHEELS", title="Tyres & Wheel Care", badgeText="Wheel Alignment", iconName="tire_repair"),
        ServiceCategoryDto(id="8", code="INSPECTIONS", title="Car Inspections", badgeText="Used Car Audit", iconName="fact_check"),
        ServiceCategoryDto(id="9", code="WINDSHIELDS", title="Windshields & Lights", badgeText="OEM Glass", iconName="lightbulb"),
        ServiceCategoryDto(id="10", code="SUSPENSION", title="Suspension & Fitments", badgeText="Shock Absorbers", iconName="build"),
        ServiceCategoryDto(id="11", code="INSURANCE", title="Insurance Claims", badgeText="Cashless Support", iconName="shield"),
        ServiceCategoryDto(id="12", code="SOS_ROADSIDE", title="Emergency SOS", badgeText="24/7 Roadside", iconName="emergency")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        // Render 12 items as rows of 3 columns
        displayCategories.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { cat ->
                    Box(modifier = Modifier.weight(1f)) {
                        CategoryGridTile(category = cat, onClick = { onCategoryClick(cat.code) })
                    }
                }
                if (rowItems.size < 3) {
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryGridTile(
    category: ServiceCategoryDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(108.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Emerald Discount Badge matching Screenshot 4
            if (!category.badgeText.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = BadgeContainerGreen,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = category.badgeText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldBadgeGreen,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(vertical = 2.dp, horizontal = 2.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Category Icon
            Icon(
                imageVector = getCategoryVectorIcon(category.code),
                contentDescription = category.title,
                tint = DeepBurgundyPrimary,
                modifier = Modifier.size(28.dp)
            )

            // Category Title
            Text(
                text = category.title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                lineHeight = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

fun getCategoryVectorIcon(code: String): ImageVector {
    return when (code) {
        "CAR_SERVICES" -> Icons.Default.DirectionsCar
        "DENTING_PAINTING" -> Icons.Default.FormatPaint
        "AC_SERVICE" -> Icons.Default.AcUnit
        "CAR_SPA" -> Icons.Default.LocalCarWash
        "DETAILING" -> Icons.Default.AutoAwesome
        "BATTERIES" -> Icons.Default.BatteryChargingFull
        "TYRES_WHEELS" -> Icons.Default.TireRepair
        "INSPECTIONS" -> Icons.Default.FactCheck
        "WINDSHIELDS" -> Icons.Default.Lightbulb
        "SUSPENSION" -> Icons.Default.Build
        "INSURANCE" -> Icons.Default.Shield
        "SOS_ROADSIDE" -> Icons.Default.Emergency
        else -> Icons.Default.Handyman
    }
}

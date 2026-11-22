package com.scet.garagex_application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.scet.garagex_application.ui.appointments.AppointmentDetailScreen
import com.scet.garagex_application.ui.appointments.AppointmentsScreen
import com.scet.garagex_application.ui.appointments.AppointmentsViewModel
import com.scet.garagex_application.ui.auth.LoginScreen
import com.scet.garagex_application.ui.auth.LoginViewModel
import com.scet.garagex_application.ui.auth.RegisterScreen
import com.scet.garagex_application.ui.bills.BillDetailScreen
import com.scet.garagex_application.ui.bills.BillsScreen
import com.scet.garagex_application.ui.bills.BillsViewModel
import com.scet.garagex_application.ui.booking.BookingViewModel
import com.scet.garagex_application.ui.booking.BookingWizardScreen
import com.scet.garagex_application.ui.home.CustomerHomeScreen
import com.scet.garagex_application.ui.mechanic.MechanicHomeScreen
import com.scet.garagex_application.ui.mechanic.MechanicViewModel
import com.scet.garagex_application.ui.services.ServicesScreen
import com.scet.garagex_application.ui.services.ServicesViewModel
import com.scet.garagex_application.ui.splash.SplashScreen
import com.scet.garagex_application.ui.theme.GarejXTheme
import com.scet.garagex_application.ui.vehicles.VehicleViewModel
import com.scet.garagex_application.ui.vehicles.VehiclesScreen

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.scet.garagex_application.ui.navigation.MainAppShell
import com.scet.garagex_application.ui.catalog.SelectBrandScreen
import com.scet.garagex_application.ui.catalog.SelectModelBottomSheet
import com.scet.garagex_application.ui.catalog.VehicleSpecsScreen
import com.scet.garagex_application.ui.sos.SosRoadsideScreen
import com.scet.garagex_application.data.remote.dto.BrandDto
import com.scet.garagex_application.data.remote.dto.VehicleModelDto

/**
 * MainActivity is the single entry-point Activity for the GarejX application.
 *
 * Why Single-Activity Architecture:
 * In modern Jetpack Compose development, we use a single Activity and let
 * Navigation Compose swap the screens (Composables). This is faster, avoids
 * heavyweight Activity lifecycle bugs, and makes animations seamless.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Retrofit with Application context
        com.scet.garagex_application.data.remote.RetrofitClient.initialize(applicationContext)

        // Android 14/15 modern edge-to-edge system bar integration
        enableEdgeToEdge()

        setContent {
            GarejXTheme {
                GarejXAppNavigation()
            }
        }
    }
}

/**
 * GarejXAppNavigation defines all screen routes and connects them together.
 */
@Composable
fun GarejXAppNavigation() {
    val navController = rememberNavController()

    // Shared state across the brand -> model -> specs onboarding wizard flow
    var selectedBrand by remember { mutableStateOf<BrandDto?>(null) }
    var selectedModel by remember { mutableStateOf<VehicleModelDto?>(null) }
    var showModelBottomSheet by remember { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // --- 1. Splash Screen ---
        composable("splash") {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToCustomerHome = {
                    navController.navigate("customer_home") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateToMechanicHome = {
                    navController.navigate("mechanic_home") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // --- 2. Login Screen ---
        composable("login") {
            val loginViewModel: LoginViewModel = viewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onLoginSuccess = { role ->
                    val destination = if (role == "MECHANIC") "mechanic_home" else "customer_home"
                    navController.navigate(destination) {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // --- 3. Register Screen ---
        composable("register") {
            RegisterScreen(
                onNavigateBackToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate("customer_home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // --- 4. Super-App Customer 5-Tab Shell (Screenshots 4 & 5) ---
        composable("customer_home") {
            MainAppShell(
                onNavigateToSelectBrand = {
                    navController.navigate("select_brand")
                },
                onNavigateToBooking = { catCode ->
                    navController.navigate("booking?category=$catCode")
                },
                onNavigateToOrders = {
                    navController.navigate("appointments")
                },
                onNavigateToVehicles = {
                    navController.navigate("vehicles")
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // --- 4A. Brand Selection Screen (Screenshot 1) ---
        composable("select_brand") {
            SelectBrandScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onBrandSelected = { brand ->
                    selectedBrand = brand
                    showModelBottomSheet = true
                }
            )

            // Bottom sheet model picker (Screenshot 2)
            if (showModelBottomSheet && selectedBrand != null) {
                SelectModelBottomSheet(
                    brand = selectedBrand!!,
                    onDismiss = { showModelBottomSheet = false },
                    onModelSelected = { model ->
                        selectedModel = model
                        showModelBottomSheet = false
                        navController.navigate("vehicle_specs")
                    }
                )
            }
        }

        // --- 4B. Vehicle Transmission & Fuel Specs Screen (Screenshot 3) ---
        composable("vehicle_specs") {
            if (selectedBrand != null && selectedModel != null) {
                VehicleSpecsScreen(
                    brand = selectedBrand!!,
                    model = selectedModel!!,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onVehicleSaved = {
                        navController.navigate("customer_home") {
                            popUpTo("customer_home") { inclusive = true }
                        }
                    }
                )
            }
        }

        // --- 4C. Roadside Emergency SOS Screen ---
        composable("sos") {
            SosRoadsideScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // --- 5. Vehicles Fleet Screen ---
        composable("vehicles") {
            val vehicleViewModel: VehicleViewModel = viewModel()
            VehiclesScreen(
                viewModel = vehicleViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAddVehicle = {
                    navController.navigate("select_brand")
                }
            )
        }

        // --- 6. Service Catalog Screen ---
        composable("services") {
            val servicesViewModel: ServicesViewModel = viewModel()
            ServicesScreen(
                viewModel = servicesViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onBookService = { serviceId ->
                    navController.navigate("booking?serviceId=$serviceId")
                }
            )
        }

        // --- 7. 4-Step Booking Wizard Screen ---
        composable(
            route = "booking?serviceId={serviceId}&category={category}",
            arguments = listOf(
                navArgument("serviceId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("category") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val preselectedServiceId = backStackEntry.arguments?.getString("serviceId")
            val category = backStackEntry.arguments?.getString("category")
            val bookingViewModel: BookingViewModel = viewModel()
            BookingWizardScreen(
                viewModel = bookingViewModel,
                preselectedServiceId = preselectedServiceId,
                preselectedCategory = category,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToVehicles = {
                    navController.navigate("select_brand")
                },
                onBookingSuccess = { appointmentId ->
                    navController.navigate("appointment_detail/$appointmentId") {
                        popUpTo("customer_home") { inclusive = false }
                    }
                }
            )
        }

        // --- 8. Appointments List Screen ---
        composable("appointments") {
            val appointmentsViewModel: AppointmentsViewModel = viewModel()
            AppointmentsScreen(
                viewModel = appointmentsViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToBooking = {
                    navController.navigate("booking")
                },
                onSelectAppointment = { appointmentId ->
                    navController.navigate("appointment_detail/$appointmentId")
                }
            )
        }

        // --- 9. Appointment Detail & Status Stepper Screen ---
        composable(
            route = "appointment_detail/{appointmentId}",
            arguments = listOf(
                navArgument("appointmentId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getString("appointmentId") ?: ""
            val appointmentsViewModel: AppointmentsViewModel = viewModel()
            AppointmentDetailScreen(
                appointmentId = appointmentId,
                viewModel = appointmentsViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // --- 10. Invoices & Bills Screen ---
        composable("bills") {
            val billsViewModel: BillsViewModel = viewModel()
            BillsScreen(
                viewModel = billsViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSelectBill = { billId ->
                    navController.navigate("bill_detail/$billId")
                }
            )
        }

        // --- 11. Bill Receipt & UPI Payment Screen ---
        composable(
            route = "bill_detail/{billId}",
            arguments = listOf(
                navArgument("billId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val billId = backStackEntry.arguments?.getString("billId") ?: ""
            val billsViewModel: BillsViewModel = viewModel()
            BillDetailScreen(
                billId = billId,
                viewModel = billsViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // --- 12. Mechanic Workshop Bay Screen ---
        composable("mechanic_home") {
            val mechanicViewModel: MechanicViewModel = viewModel()
            MechanicHomeScreen(
                viewModel = mechanicViewModel,
                onLogoutClick = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
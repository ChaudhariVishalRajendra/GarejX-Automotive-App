package com.scet.garagex_application.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scet.garagex_application.data.local.TokenManager
import com.scet.garagex_application.ui.theme.DeepBurgundyOnPrimary
import com.scet.garagex_application.ui.theme.DeepBurgundyPrimary
import com.scet.garagex_application.ui.theme.TextSecondary
import com.scet.garagex_application.ui.theme.WarmCreamBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

// =========================================================================
// Splash Screen Composable
//
// What it does:
// 1. Shows the brand logo with a smooth subtle entrance pulse.
// 2. Checks TokenManager (DataStore) in the background.
// 3. Automatically routes:
//    - Logged in Customer -> Customer Home
//    - Logged in Mechanic -> Mechanic Workspace
//    - Not logged in     -> Login Screen
// =========================================================================

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToCustomerHome: () -> Unit,
    onNavigateToMechanicHome: () -> Unit
) {
    val context = LocalContext.current
    val tokenManager = TokenManager(context.applicationContext)

    // Animation scale for logo pulse
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale = infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // LaunchedEffect runs once when this Composable enters the composition
    LaunchedEffect(Unit) {
        // Minimum splash delay for brand presentation
        delay(1200)

        val savedToken = tokenManager.getToken().first()
        val savedRole = tokenManager.getUserRole().first()

        if (!savedToken.isNullOrBlank()) {
            if (savedRole == "MECHANIC") {
                onNavigateToMechanicHome()
            } else {
                onNavigateToCustomerHome()
            }
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCreamBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Branded Emblem Logo
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = com.scet.garagex_application.R.drawable.ic_garejx_logo),
                contentDescription = "GarejX Emblem",
                modifier = Modifier
                    .size(104.dp)
                    .scale(scale.value)
                    .clip(RoundedCornerShape(24.dp)),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "GarejX",
                style = MaterialTheme.typography.displayLarge,
                color = DeepBurgundyPrimary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Automotive Care & Precision",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(36.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                color = DeepBurgundyPrimary,
                strokeWidth = 2.5.dp
            )
        }
    }
}

package com.scet.garagex_application.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// =========================================================================
// Indian Embossed License Plate Component
//
// Designed to look like a realistic High-Security Registration Plate (HSRP):
// 1. Blue vertical strip on left with white "IND" text.
// 2. White reflective body with rounded black border.
// 3. Crisp, bold monospaced license plate text (e.g., "MH 12 AB 1234").
// =========================================================================

@Composable
fun LicensePlateBadge(
    vehicleNumber: String,
    modifier: Modifier = Modifier
) {
    // Format plate string (auto-upper-cased and trimmed)
    val formattedPlate = vehicleNumber.trim().uppercase()

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFFBFBFB))
            .border(width = 1.5.dp, color = Color(0xFF1E293B), shape = RoundedCornerShape(6.dp))
            .height(34.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Blue "IND" Strip
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(22.dp)
                .background(Color(0xFF0038A8)), // Official IND blue
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "IND",
                color = Color.White,
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        // Registration Number Text
        Box(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = formattedPlate,
                color = Color(0xFF0F172A),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }
    }
}

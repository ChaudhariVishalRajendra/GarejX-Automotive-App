package com.scet.garagex_application.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scet.garagex_application.ui.theme.*

// =========================================================================
// Login Screen Composable
//
// Uses the requested Material 3 Design Components:
// 1. Scaffold: Provides the top-level window insets and background structure.
// 2. ElevatedCard: Creates a distinct, elevated surface for the login card.
// 3. OutlinedTextField: Clean input fields with Deep Burgundy focus borders.
// 4. Atelier Automotive Palette: Deep Burgundy, Warm Cream Beige, Muted Sage Grey.
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: (role: String) -> Unit
) {
    // Collect UI state from ViewModel reactively
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    // Scaffold provides the screen container and applies our Warm Cream background
    Scaffold(
        containerColor = WarmCreamBackground,
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- Brand Header ---
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(DeepBurgundyPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "GX",
                        color = DeepBurgundyOnPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "GarejX Workshop",
                    style = MaterialTheme.typography.headlineMedium,
                    color = DeepBurgundyPrimary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Sign in to manage your vehicles & bookings",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                // --- Elevated Card for Login Form ---
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = WarmCreamSurface
                    ),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 6.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // Error Banner (displayed when validation or network fails)
                        if (uiState.errorMessage != null) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                shape = RoundedCornerShape(10.dp),
                                color = StatusErrorRed.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = uiState.errorMessage ?: "",
                                    color = StatusErrorRed,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        // --- Email Input Field ---
                        OutlinedTextField(
                            value = uiState.email,
                            onValueChange = { viewModel.onEmailChange(it) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Email Address") },
                            placeholder = { Text("customer@garejx.com", color = TextSecondary.copy(alpha = 0.5f)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email Icon",
                                    tint = DeepBurgundyPrimary
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next,
                                autoCorrect = false
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = DeepBurgundyPrimary,
                                focusedLabelColor = DeepBurgundyPrimary,
                                unfocusedBorderColor = WarmCreamOutline,
                                unfocusedLabelColor = TextSecondary,
                                focusedContainerColor = androidx.compose.ui.graphics.Color.White,
                                unfocusedContainerColor = androidx.compose.ui.graphics.Color.White,
                                cursorColor = DeepBurgundyPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // --- Password Input Field ---
                        OutlinedTextField(
                            value = uiState.password,
                            onValueChange = { viewModel.onPasswordChange(it) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Password") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Lock Icon",
                                    tint = DeepBurgundyPrimary
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                                    Icon(
                                        imageVector = if (uiState.isPasswordVisible) {
                                            Icons.Default.VisibilityOff
                                        } else {
                                            Icons.Default.Visibility
                                        },
                                        contentDescription = "Toggle Password Visibility",
                                        tint = DeepBurgundyPrimary
                                    )
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            visualTransformation = if (uiState.isPasswordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done,
                                autoCorrect = false
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    viewModel.performLogin(onLoginSuccess)
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = DeepBurgundyPrimary,
                                focusedLabelColor = DeepBurgundyPrimary,
                                unfocusedBorderColor = WarmCreamOutline,
                                unfocusedLabelColor = TextSecondary,
                                focusedContainerColor = androidx.compose.ui.graphics.Color.White,
                                unfocusedContainerColor = androidx.compose.ui.graphics.Color.White,
                                cursorColor = DeepBurgundyPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // --- Sign In Button ---
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.performLogin(onLoginSuccess)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            enabled = !uiState.isLoading,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DeepBurgundyPrimary,
                                contentColor = DeepBurgundyOnPrimary
                            )
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = DeepBurgundyOnPrimary,
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                Text(
                                    text = "Sign In",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- Register Link ---
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Don't have an account?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    TextButton(onClick = onNavigateToRegister) {
                        Text(
                            text = "Register",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = DeepBurgundyPrimary
                        )
                    }
                }
            }
        }
    }
}

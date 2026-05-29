package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AcademyViewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.R

@Composable
fun SplashScreen(viewModel: AcademyViewModel) {
    var startAnimation by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "gold_glowing")
    
    val goldPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gold_pulse"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        kotlinx.coroutines.delay(2000) // Splash duration
        _root_navigation_trigger(viewModel)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GeoBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Elegant Geometric Rounded Ring
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .border(
                        width = (2.5.dp).times(goldPulse),
                        color = GeoGold,
                        shape = RoundedCornerShape(32.dp)
                    )
                    .background(Color.White)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_app_icon),
                    contentDescription = "Academy logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Academy Titles (Geometric Theme typography pair)
            Text(
                text = "SHESHAN PERERA",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    color = GeoPrimary
                ),
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "ENGLISH ACADEMY",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    color = GeoGold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Learn English Smartly with Sri Lanka's Leading Educator",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray,
                    letterSpacing = 0.5.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Elegant Loading Indicator
            CircularProgressIndicator(
                color = GeoGold,
                strokeWidth = 3.dp,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Initializing LMS Portal...",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
            )
        }
    }
}

private suspend fun _root_navigation_trigger(viewModel: AcademyViewModel) {
    val profile = viewModel.studentProfile.value
    if (profile != null && profile.isLoggedIn) {
        viewModel.navigateTo("MAIN")
    } else {
        viewModel.navigateTo("LOGIN_REGISTER")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginRegisterScreen(viewModel: AcademyViewModel) {
    val profile by viewModel.studentProfile.collectAsState()
    var isRegisterTab by remember { mutableStateOf(false) }
    
    // Form Inputs
    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }

    LaunchedEffect(profile) {
        profile?.let { prof ->
            if (phoneInput.isBlank() && !prof.phone.isNullOrBlank() && prof.phone != "+94 77 000 0000" && prof.phone != "+94 77 456 7224") {
                phoneInput = prof.phone
            }
            if (nameInput.isBlank() && !prof.studentId.isNullOrBlank() && prof.studentId != "SP-2026-0941" && prof.studentId != "STAFF-7224") {
                nameInput = prof.studentId
            }
        }
    }
    var selectedCourse by remember { mutableStateOf("O/L English") }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var inlineErrorMessage by remember { mutableStateOf<String?>(null) }
    
    val coursesList = listOf("O/L English", "A/L English", "Spoken English", "Professional Communication Skills")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GeoBackground)
    ) {
        // Upper Geometric Blue Gradient Card accent
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(GeoPrimary, GeoPrimary.copy(alpha = 0.85f))
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_app_icon),
                    contentDescription = "Academy Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Sheshan Academy",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                )
            }

            Text(
                text = "Sri Lanka's Premium LMS Portal",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = GeoGold,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tab Selector Row styled with Geometric Balance geometry (pills)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .padding(4.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (!isRegisterTab) GeoGold else Color.Transparent)
                        .clickable { 
                            isRegisterTab = false 
                            emailInput = "" 
                            passwordInput = ""
                            inlineErrorMessage = null
                        }
                        .testTag("login_tab_btn")
                ) {
                    Text(
                        text = "Sign In",
                        fontFamily = MaterialTheme.typography.labelLarge.fontFamily,
                        fontWeight = FontWeight.Bold,
                        color = if (!isRegisterTab) GeoPrimary else Color.White
                    )
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isRegisterTab) GeoGold else Color.Transparent)
                        .clickable { 
                            isRegisterTab = true 
                            emailInput = "" 
                            passwordInput = ""
                            inlineErrorMessage = null
                        }
                        .testTag("register_tab_btn")
                ) {
                    Text(
                        text = "Academic Staff",
                        fontFamily = MaterialTheme.typography.labelLarge.fontFamily,
                        fontWeight = FontWeight.Bold,
                        color = if (isRegisterTab) GeoPrimary else Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Core Interactive Input Card (Tailwind style: white background, border-slate-100 shadow)
            Card(
                colors = CardDefaults.cardColors(containerColor = GeoSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, GeoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = if (!isRegisterTab) "Welcome Back, Student" else "Academic Staff Portal",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = GeoPrimary
                        )
                    )
                    Text(
                        text = if (!isRegisterTab) "Enter your registered Phone Number and Student ID together to sign in." else "Access the administration office suite to broadcast alerts, manage links, or post tutes.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray
                        ),
                        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                    )

                    if (isRegisterTab) {
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { 
                                emailInput = it 
                                inlineErrorMessage = null
                            },
                            label = { Text("Staff Username") },
                            leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = GeoPrimary) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = GeoPrimary,
                                unfocusedTextColor = GeoPrimary,
                                focusedBorderColor = GeoPrimary,
                                unfocusedBorderColor = GeoBorder,
                                focusedLabelColor = GeoPrimary,
                                unfocusedLabelColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp)
                                .testTag("email_input")
                        )

                        OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { 
                                passwordInput = it 
                                inlineErrorMessage = null
                            },
                            label = { Text("Staff Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GeoPrimary) },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = GeoPrimary,
                                unfocusedTextColor = GeoPrimary,
                                focusedBorderColor = GeoPrimary,
                                unfocusedBorderColor = GeoBorder,
                                focusedLabelColor = GeoPrimary,
                                unfocusedLabelColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .testTag("password_input")
                        )
                    } else {
                        // Student Login: Enter Phone & ID Together
                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { 
                                phoneInput = it 
                                inlineErrorMessage = null
                            },
                            label = { Text("Registered Phone Number (e.g., 0771234567)") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GeoPrimary) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = GeoPrimary,
                                unfocusedTextColor = GeoPrimary,
                                focusedBorderColor = GeoPrimary,
                                unfocusedBorderColor = GeoBorder,
                                focusedLabelColor = GeoPrimary,
                                unfocusedLabelColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp)
                                .testTag("phone_input")
                        )

                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { 
                                nameInput = it 
                                inlineErrorMessage = null
                            },
                            label = { Text("Student ID Number (e.g., ST-101)") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GeoPrimary) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = GeoPrimary,
                                unfocusedTextColor = GeoPrimary,
                                focusedBorderColor = GeoPrimary,
                                unfocusedBorderColor = GeoBorder,
                                focusedLabelColor = GeoPrimary,
                                unfocusedLabelColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .testTag("student_id_input")
                        )
                    }

                    // Helpful info cards (removed)

                    // Inline Error Warning Message block
                    AnimatedVisibility(visible = inlineErrorMessage != null) {
                        inlineErrorMessage?.let { msg ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Error Indicator",
                                        tint = AlertRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = msg,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = AlertRed,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary login action themed with GeoPrimary
                    Button(
                        onClick = {
                            if (isRegisterTab) {
                                val success = viewModel.loginAcademicStaff(emailInput, passwordInput)
                                if (!success) {
                                    inlineErrorMessage = "Incorrect Academic Staff username or password."
                                    viewModel.addSimulatedNotification(
                                        "⚠️ Access Denied",
                                        "Staff account login failed. Invalid username or passcode.",
                                        "ERR"
                                    )
                                } else {
                                    inlineErrorMessage = null
                                }
                            } else {
                                if (phoneInput.isNotBlank() && nameInput.isNotBlank()) {
                                    viewModel.checkStudentEnrollment(phoneInput, nameInput) { success ->
                                        if (!success) {
                                            inlineErrorMessage = "Wrong Phone number or ID. Please register under Staff details first."
                                        } else {
                                            inlineErrorMessage = null
                                        }
                                    }
                                } else {
                                    inlineErrorMessage = "Please enter both Phone Number and Student ID together to sign in."
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GeoPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("auth_action_btn")
                    ) {
                        Text(
                            text = if (isRegisterTab) "Teacher Login" else "Enter Classroom",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Approved by Sheshan Perera Academy Secretariat.",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Text(
                text = "©create by Jerome Himasha.\njeromehimasha@gmail.com\n0702602489",
                fontSize = 11.sp,
                color = Color.Gray.copy(alpha = 0.8f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 15.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}

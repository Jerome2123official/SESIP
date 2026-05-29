package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.AnnouncementEntity
import com.example.data.database.StudentProfileEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.AcademyViewModel
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.R
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items

@Composable
fun DashboardScreen(viewModel: AcademyViewModel) {
    val profile by viewModel.studentProfile.collectAsState()
    val announcements by viewModel.announcements.collectAsState()
    val isSinhala by viewModel.isSinhala.collectAsState()
    val isAdminVerified by viewModel.isAdminVerified.collectAsState()
    
    var showIdCardDialog by remember { mutableStateOf(false) }
    var showAvatarPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GeoBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .padding(bottom = 60.dp) // Clearance for Bottom Navigation
    ) {
        // Welcome and Status Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isSinhala) "ආයුබෝවන්, නැවත සාදරයෙන් පිළිගනිමු!" else "WELCOME BACK",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                )
                Text(
                    text = if (isAdminVerified) "Mr. Sheshan Perera (Staff)" else (profile?.fullName ?: "Student Account"),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = GeoPrimary
                    )
                )
            }

            // High contrast identity avatar with gold border (Tailwind border-2 border-[#D4AF37])
            Surface(
                onClick = { showIdCardDialog = true },
                shape = RoundedCornerShape(16.dp),
                color = GeoPrimary.copy(alpha = 0.1f),
                border = BorderStroke(2.dp, GeoGold),
                modifier = Modifier
                    .size(48.dp)
                    .testTag("avatar_btn")
            ) {
                if (!profile?.profileImageUri.isNullOrBlank()) {
                    coil.compose.AsyncImage(
                        model = profile?.profileImageUri,
                        contentDescription = "Student Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.img_profile_avatar),
                        contentDescription = "Student Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Live Class Hero Card (Tailwind: relative bg-[#1E3A8A] rounded-[32px] p-6 shadow-xl)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(GeoPrimary, GeoPrimary.copy(alpha = 0.9f))
                    )
                )
                .border(BorderStroke(1.dp, GeoGold.copy(alpha = 0.2f)), RoundedCornerShape(32.dp))
                .clickable { viewModel.selectTab("LIVE") }
        ) {
            // Gold abstract blurry accent background decoration
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 40.dp, y = (-40).dp)
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(GeoGold.copy(alpha = 0.15f))
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LIVE NOW",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GeoGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }

                Text(
                    text = "A/L General English",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    text = "Unit 08: Modal Verbs Mastery",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Overlapping avatars
                    Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                        listOf(Color(0xFF60A5FA), Color(0xFF93C5FD), GeoGold).forEachIndexed { index, color ->
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(1.5.dp, GeoPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (index == 2) {
                                    Text("+142", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { viewModel.selectTab("LIVE") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GeoGold,
                            contentColor = GeoPrimary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = if (isSinhala) "සම්බන්ධ වන්න" else "Join via Zoom",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Statistics Grid (Tailwind style)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // New Recordings Card
            StatBlockCard(
                title = if (isSinhala) "පාඩම් මාලාවන්" else "LESSONS",
                count = if (isAdminVerified) "Staff" else "${profile?.totalLessonsWatched ?: 12}",
                icon = Icons.Default.PlayCircle,
                tint = GeoPrimary,
                bgTint = GeoPrimary.copy(alpha = 0.08f),
                onClick = { viewModel.selectTab("RECORDINGS") },
                modifier = Modifier.weight(1f)
            )

            // PDF Tutes Card
            StatBlockCard(
                title = if (isSinhala) "නිබන්ධන සටහන්" else "PDF Tutes",
                count = "08",
                icon = Icons.Default.FileDownload,
                tint = GeoGold,
                bgTint = GeoGold.copy(alpha = 0.08f),
                onClick = { viewModel.selectTab("TUTES") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Learning Grammar Progress Card (Tailwind style)
        Card(
            colors = CardDefaults.cardColors(containerColor = GeoSurface),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, GeoBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isSinhala) "ව්‍යාකරණ ප්‍රගතිය" else "Grammar Progress",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GeoPrimary
                        )
                    )
                    Text(
                        text = "78%",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GeoGold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(GeoSlate)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.78f)
                            .fillMaxHeight()
                            .background(GeoPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "21 Lessons Done",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.LightGray
                    )
                    Text(
                        text = "06 Lessons Remaining",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.LightGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Announcements Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isSinhala) "නවතම නිවේදන" else "Latest Announcements",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = GeoPrimary
                )
            )
            Text(
                text = "See All",
                color = GeoGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { viewModel.selectTab("NOTIFICATIONS") }
            )
        }

        if (announcements.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = GeoSurface),
                border = BorderStroke(1.dp, GeoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No announcements at the moment.",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            announcements.take(2).forEach { alert ->
                DashboardNoticeCard(alert)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tutor's Motivating Message of the Week
        DashboardTutorMessage(isSinhala = isSinhala)
    }

    // Dynamic QR ID Admission Card Dialog
    if (showIdCardDialog) {
        AlertDialog(
            onDismissRequest = { showIdCardDialog = false },
            title = null,
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(GeoPrimary, GeoPrimary.copy(alpha = 0.8f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.School, contentDescription = null, tint = GeoGold, modifier = Modifier.size(36.dp))
                            Text("SHESHAN PERERA", style = MaterialTheme.typography.labelLarge.copy(color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 1.sp))
                            Text("ENGLISH ACADEMY", style = MaterialTheme.typography.labelSmall.copy(color = GeoGold, letterSpacing = 2.sp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .border(2.dp, GeoGold, CircleShape)
                            .clickable { showAvatarPicker = true },
                        contentAlignment = Alignment.Center
                    ) {
                        if (!profile?.profileImageUri.isNullOrBlank()) {
                            coil.compose.AsyncImage(
                                model = profile?.profileImageUri,
                                contentDescription = "Student Portrait",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.img_profile_avatar),
                                contentDescription = "Student Portrait",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        
                        // Edit overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.35f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Change Avatar",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isAdminVerified) "Mr. Sheshan Perera (Staff)" else (profile?.fullName ?: "Student Account"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = GeoPrimary
                    )
                    Text(
                        text = if (isAdminVerified) "Academic Director & Lecturer" else (profile?.courseType ?: "General English Student"),
                        fontSize = 13.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // QR representation
                    Card(
                        border = BorderStroke(1.dp, Color.LightGray),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.size(160.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = "Active Admission QR",
                                tint = GeoPrimary,
                                modifier = Modifier.size(130.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isAdminVerified) "STAFF ID: STAFF-7224" else "STUDENT ID: ${profile?.studentId ?: "SP-2026-0941"}",
                        fontFamily = MaterialTheme.typography.labelSmall.fontFamily,
                        fontWeight = FontWeight.Bold,
                        color = GeoPrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = if (isAdminVerified) "Scan physically at the Gampaha/Ja-Ela lecture halls to manage and open office facilities." else "Scan physically at the Gampaha/Ja-Ela lecture halls to record weekly attendance.",
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showIdCardDialog = false },
                    modifier = Modifier.testTag("id_close_btn")
                ) {
                    Text("Done", color = GeoPrimary, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = GeoSurface
        )
    }

    if (showAvatarPicker) {
        AvatarPickerDialog(
            onDismiss = { showAvatarPicker = false },
            onAvatarSelected = { uri ->
                viewModel.updateProfileImage(uri)
            }
        )
    }
}

@Composable
fun AvatarPickerDialog(
    onDismiss: () -> Unit,
    onAvatarSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onAvatarSelected(uri.toString())
            onDismiss()
        }
    }

    val presets = listOf(
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=256&q=80",
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=256&q=80",
        "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=256&q=80",
        "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?auto=format&fit=crop&w=256&q=80",
        "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=256&q=80",
        "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=256&q=80"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Profile Avatar", fontWeight = FontWeight.Bold, color = GeoPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Select from premium student avatars or choose a picture from your device gallery.", fontSize = 13.sp, color = Color.Gray)
                
                Text("Preset Avatars:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GeoPrimary)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(130.dp)
                ) {
                    items(presets) { url ->
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, GeoGold, CircleShape)
                                .clickable {
                                    onAvatarSelected(url)
                                    onDismiss()
                                }
                        ) {
                            coil.compose.AsyncImage(
                                model = url,
                                contentDescription = "preset avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = GeoSlate)
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Choose from Gallery", fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = AlertRed, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = GeoSurface,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun StatBlockCard(
    title: String,
    count: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    bgTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GeoSurface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, GeoBorder),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgTint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = count,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = GeoPrimary
            )
            Text(
                text = title,
                fontSize = 11.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun DashboardNoticeCard(alert: AnnouncementEntity) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (alert.isEmergency) Color(0xFFFFECEB) else GeoSurface
        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (alert.isEmergency) AlertRed.copy(alpha = 0.3f) else GeoBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (alert.isEmergency) AlertRed.copy(alpha = 0.15f) else GeoPrimary.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (alert.isEmergency) Icons.Default.Campaign else Icons.Default.Announcement,
                    contentDescription = null,
                    tint = if (alert.isEmergency) AlertRed else GeoPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (alert.isEmergency) "URGENT NOTICE" else "ACADEMY INFO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (alert.isEmergency) AlertRed else GeoPrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = alert.date,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = alert.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = GeoPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = alert.content,
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun DashboardTutorMessage(isSinhala: Boolean) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GeoPrimary),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(GeoGold),
                    contentAlignment = Alignment.Center
                ) {
                    Text("SP", fontWeight = FontWeight.Bold, color = GeoPrimary, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Mr. Sheshan Perera",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Tutor Message of the Week",
                        color = GeoGold,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "\"English mastery is not about memorizing tutes - it's about confidence and speaking daily. This week, ensure you finish looking at the passives rulebook! See you in Gampaha on Saturday.\"",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 18.sp
                )
            )
        }
    }
}

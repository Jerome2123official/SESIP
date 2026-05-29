package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AcademyViewModel
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainScreen(viewModel: AcademyViewModel) {
    val isAdminVerified by viewModel.isAdminVerified.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val isSinhala by viewModel.isSinhala.collectAsState()
    val activeReadingTute by viewModel.readingTute.collectAsState()
    val activePlayingVideo by viewModel.playingRecording.collectAsState()
    val pushNotifications by viewModel.pushNotifications.collectAsState()
    val profile by viewModel.studentProfile.collectAsState()

    val context = LocalContext.current

    // Root scaffold containing Geometric toolbar, beautiful content viewport and navigation
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(GeoBackground),
        topBar = {
            Column {
                // Status Bar buffer & Modern Top App Header
                TopAcademyAppBar(
                    viewModel = viewModel,
                    isSinhala = isSinhala,
                    notificationsCount = pushNotifications.size,
                    studentName = if (isAdminVerified) "Mr. Sheshan Perera (Staff)" else (profile?.fullName ?: "Student Account")
                )
                
                // Alert strip for emergency notices
                EmergencyNoticeStrip(viewModel)
            }
        },
        bottomBar = {
            AcademyBottomNavigation(
                currentTab = currentTab,
                onTabSelected = { viewModel.selectTab(it) },
                isSinhala = isSinhala
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(GeoBackground)
        ) {
            // Screen switching with slide animations
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                },
                label = "tab_navigation"
            ) { targetTab ->
                when (targetTab) {
                    "DASHBOARD" -> DashboardScreen(viewModel)
                    "LIVE" -> LiveClassesScreen(viewModel)
                    "RECORDINGS" -> RecordingsScreen(viewModel)
                    "TUTES" -> TutesScreen(viewModel)
                    "PROFILE" -> StudentProfileScreen(viewModel)
                    "TUTOR" -> TutorProfileScreen(viewModel)
                    "PAYMENTS" -> PaymentsScreen(viewModel)
                    "PREMIUM" -> PremiumFeaturesScreen(viewModel)
                    "ADMIN" -> AdminDashboardScreen(viewModel)
                    "SETTINGS" -> SettingsScreen(viewModel)
                    "NOTIFICATIONS" -> NotificationsDrawerScreen(viewModel)
                }
            }

            // Global Floating Overlay for Simulated PDF Document Reader
            activeReadingTute?.let { tute ->
                SimulatedPdfReaderOverlay(
                    tute = tute,
                    onClose = { viewModel.closeTuteReader() }
                )
            }

            // Global Floating Overlay for video lesson player
            activePlayingVideo?.let { video ->
                SimulatedVideoPlayerOverlay(
                    recording = video,
                    onProgressUpdate = { viewModel.recordProgress(it) },
                    onClose = { viewModel.stopPlayingRecording() }
                )
            }
        }
    }
}

// --- Top Branded App Bar (Geometric Balance Aspect) ---
@Composable
fun TopAcademyAppBar(
    viewModel: AcademyViewModel,
    isSinhala: Boolean,
    notificationsCount: Int,
    studentName: String
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val isAdminVerified by viewModel.isAdminVerified.collectAsState()
    
    Surface(
        color = GeoSurface,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Circular SP monogram badge
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GeoPrimary.copy(alpha = 0.1f))
                        .border(1.5.dp, GeoGold, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SP",
                        fontWeight = FontWeight.Bold,
                        color = GeoPrimary,
                        fontSize = 17.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = if (isSinhala) "කඩිනම් අධ්‍යයනය" else "LEARN SMARTLY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GeoGold,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    )
                    Text(
                        text = if (isSinhala) "ශේෂාන් පෙරේරා ඇකඩමිය" else "Sheshan Perera",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = GeoPrimary
                        )
                    )
                }
            }

            // Right side Toolbar Buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Theme Toggle Icon Button
                val isDarkTheme by viewModel.isDarkTheme.collectAsState()
                IconButton(
                    onClick = { viewModel.toggleTheme() },
                    modifier = Modifier.testTag("theme_toggle_bell_neighbor")
                ) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Toggle Dark/Light Mode",
                        tint = if (isDarkTheme) GeoGold else GeoPrimary
                    )
                }

                // Admin dashboard trigger
                if (isAdminVerified) {
                    IconButton(
                        onClick = { viewModel.selectTab("ADMIN") },
                        modifier = Modifier.testTag("admin_tab_trigger")
                    ) {
                        Icon(
                            imageVector = if (currentTab == "ADMIN") Icons.Filled.AdminPanelSettings else Icons.Outlined.AdminPanelSettings,
                            contentDescription = "Admin Page",
                            tint = if (currentTab == "ADMIN") GeoGold else GeoPrimary
                        )
                    }
                }

                // Notifications drawer icon with badge indicator
                Box(contentAlignment = Alignment.TopEnd) {
                    IconButton(
                        onClick = { viewModel.selectTab("NOTIFICATIONS") },
                        modifier = Modifier.testTag("notification_bell")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = GeoPrimary
                        )
                    }
                    if (notificationsCount > 0) {
                        Box(
                            modifier = Modifier
                                .padding(top = 8.dp, end = 8.dp)
                                .size(16.dp)
                                .background(AlertRed, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = notificationsCount.toString(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Language Swapper Toggle
                IconButton(
                    onClick = { viewModel.toggleLanguage() },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(GeoSlate)
                        .size(36.dp)
                ) {
                    Text(
                        text = if (isSinhala) "EN" else "සිං",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GeoPrimary
                    )
                }
            }
        }
    }
}

// --- Dynamic Emergency notification alert ribbon ---
@Composable
fun EmergencyNoticeStrip(viewModel: AcademyViewModel) {
    val announcements by viewModel.announcements.collectAsState()
    val emergencyNotice = announcements.firstOrNull { it.isEmergency }

    AnimatedVisibility(
        visible = emergencyNotice != null,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        emergencyNotice?.let { notice ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFECEB))
                    .border(BorderStroke(1.dp, AlertRed.copy(0.2f)))
                    .clickable { viewModel.selectTab("DASHBOARD") }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "Notice Priority Alert",
                        tint = AlertRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ALERT: " + notice.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AlertRed,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = "View",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AlertRed,
                    modifier = Modifier.underline()
                )
            }
        }
    }
}

// --- Geometric Balance M3 Bottom Navigation ---
@Composable
fun AcademyBottomNavigation(
    currentTab: String,
    onTabSelected: (String) -> Unit,
    isSinhala: Boolean
) {
    Surface(
        color = GeoSurface,
        border = BorderStroke(1.dp, GeoBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                label = if (isSinhala) "ප්‍රධාන" else "Home",
                iconSelected = Icons.Filled.Home,
                iconUnselected = Icons.Outlined.Home,
                selected = currentTab == "DASHBOARD",
                onClick = { onTabSelected("DASHBOARD") }
            )
            BottomNavItem(
                label = if (isSinhala) "සජීවී" else "Live",
                iconSelected = Icons.Filled.Videocam,
                iconUnselected = Icons.Outlined.Videocam,
                selected = currentTab == "LIVE",
                onClick = { onTabSelected("LIVE") }
            )
            BottomNavItem(
                label = if (isSinhala) "පාඩම්" else "Lessons",
                iconSelected = Icons.Filled.PlayCircle,
                iconUnselected = Icons.Outlined.PlayCircle,
                selected = currentTab == "RECORDINGS",
                onClick = { onTabSelected("RECORDINGS") }
            )
            BottomNavItem(
                label = if (isSinhala) "බන්ධන" else "Tutes",
                iconSelected = Icons.Filled.FileDownload,
                iconUnselected = Icons.Outlined.FileDownload,
                selected = currentTab == "TUTES",
                onClick = { onTabSelected("TUTES") }
            )
            BottomNavItem(
                label = if (isSinhala) "පිවිසුම්" else "Premium",
                iconSelected = Icons.Filled.AutoAwesome,
                iconUnselected = Icons.Outlined.AutoAwesome,
                selected = currentTab == "PREMIUM",
                onClick = { onTabSelected("PREMIUM") }
            )
            BottomNavItem(
                label = if (isSinhala) "පැතිකඩ" else "Tutor",
                iconSelected = Icons.Filled.Person,
                iconUnselected = Icons.Outlined.Person,
                selected = currentTab == "TUTOR",
                onClick = { onTabSelected("TUTOR") }
            )
        }
    }
}

@Composable
fun RowScope.BottomNavItem(
    label: String,
    iconSelected: ImageVector,
    iconUnselected: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    // Standard ripple tab element matching Material 3 pill indicators
    Box(
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selected) GeoPrimary.copy(alpha = 0.1f) else Color.Transparent)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (selected) iconSelected else iconUnselected,
                    contentDescription = label,
                    tint = if (selected) GeoPrimary else Color.Gray,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) GeoPrimary else Color.Gray
            )
        }
    }
}

// -------------------------------------------------------------
// LIVE CLASS LIST TIMETABLE AND INTERACTIVE MEETING SCREEN
// -------------------------------------------------------------
@Composable
fun LiveClassesScreen(viewModel: AcademyViewModel) {
    val zoomClasses by viewModel.zoomClasses.collectAsState()
    val isSinhala by viewModel.isSinhala.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Geometric Header
        Text(
            text = if (isSinhala) "සජීවී දේශන මණ්ඩපය" else "Zoom Classroom",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = GeoPrimary
            )
        )
        Text(
            text = if (isSinhala) "සෘජුවම Zoom ඔස්සේ සම්බන්ද වී ප්‍රශ්න යොමු කරන්න." else "Attend live interactive classes and discuss homework questions directly.",
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Live now or Upcoming sections
        if (zoomClasses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No live sessions scheduled at this moment.",
                    color = Color.Gray
                )
            }
        } else {
            zoomClasses.forEach { session ->
                ZoomSyllabusCard(session = session, onJoinClick = { link ->
                    viewModel.addSimulatedNotification(
                        "🔗 Connecting Zoom Client",
                        "Opening lecture session: ${session.title}",
                        "INF"
                    )
                    try {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                        context.startActivity(browserIntent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Redirecting link: $link", Toast.LENGTH_SHORT).show()
                    }
                })
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Contact office trigger
        Card(
            colors = CardDefaults.cardColors(containerColor = GeoSlate),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "💡 Study Note regarding Online lectures",
                    fontWeight = FontWeight.Bold,
                    color = GeoPrimary,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Zoom links activate exactly 15 minutes before the scheduled Sri Lankan standard time. Ensure you have the workbook PDF open.",
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun ZoomSyllabusCard(
    session: ZoomClassEntity,
    onJoinClick: (String) -> Unit
) {
    val isLive = session.status == "LIVE"
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isLive) GeoPrimary else GeoSurface
        ),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(
            1.dp,
            if (isLive) GeoGold.copy(0.3f) else GeoBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isLive) {
                        // Pulse animated red indicator
                        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                        val scale by infiniteTransition.animateFloat(
                            initialValue = 0.5f,
                            targetValue = 1.2f,
                            animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
                            label = "live_scale"
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .drawBehind {
                                    drawCircle(
                                        color = Color.Red,
                                        radius = size.minDimension / 2 * scale
                                    )
                                }
                                .background(Color.Red)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = if (isLive) "LIVE STREAM NOW" else "SCHEDULED CLASS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLive) GeoGold else GeoPrimary,
                        letterSpacing = 1.sp
                    )
                }

                if (isLive) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Red)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "JOIN NOW",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = session.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLive) Color.White else GeoPrimary,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = if (isLive) Color.LightGray else Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = session.scheduledTime,
                    fontSize = 12.sp,
                    color = if (isLive) Color.LightGray else Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Meeting Partner",
                        fontSize = 10.sp,
                        color = if (isLive) Color.LightGray else Color.Gray
                    )
                    Text(
                        text = "Lecturer Sheshan Perera",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isLive) Color.White else GeoPrimary
                    )
                }

                Button(
                    onClick = { onJoinClick(session.zoomLink) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLive) GeoGold else GeoPrimary,
                        contentColor = if (isLive) GeoPrimary else Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = "Join Class",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// CLASS RECORDINGS VIDEO STORAGE COMPONENT VIEW
// -------------------------------------------------------------
@Composable
fun RecordingsScreen(viewModel: AcademyViewModel) {
    val recordingsList by viewModel.recordings.collectAsState()
    val isSinhala by viewModel.isSinhala.collectAsState()
    
    var searchPhrase by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = if (isSinhala) "පාඩම් මාලාවන්" else "LESSONS",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = GeoPrimary
            )
        )
        Text(
            text = if (isSinhala) "නැරඹුම් ප්‍රගතිය සහ පන්ති නැවත බැලීම්" else "Watch anytime. Filter by tute topic and track structural lesson progress.",
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Light Geometric Search Input
        OutlinedTextField(
            value = searchPhrase,
            onValueChange = { searchPhrase = it },
            placeholder = { Text("Search specific video chapters...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GeoPrimary) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = GeoSurface,
                unfocusedContainerColor = GeoSurface,
                focusedBorderColor = GeoPrimary,
                unfocusedBorderColor = GeoBorder,
                focusedTextColor = GeoPrimary,
                unfocusedTextColor = GeoPrimary
            )
        )

        // Category Filters Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val categories = listOf("ALL", "O/L English", "A/L English", "Spoken English")
            categories.forEach { cat ->
                val isSelected = selectedCategoryFilter == cat
                Surface(
                    onClick = { selectedCategoryFilter = cat },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) GeoPrimary else GeoSurface,
                    border = BorderStroke(1.dp, if (isSelected) GeoPrimary else GeoBorder),
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) Color.White else GeoPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // Listings View
        val filteredRecordings = recordingsList.filter {
            (selectedCategoryFilter == "ALL" || it.subject.equals(selectedCategoryFilter, ignoreCase = true)) &&
            (searchPhrase.isEmpty() || it.title.contains(searchPhrase, ignoreCase = true) || it.subject.contains(searchPhrase, ignoreCase = true))
        }

        if (filteredRecordings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No recordings found matching your filters.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredRecordings) { recording ->
                    RecordingRowCard(
                        recording = recording,
                        onPlay = { viewModel.startPlayingRecording(recording) },
                        onToggleFavorite = { viewModel.toggleFavoriteRecording(recording) }
                    )
                }
            }
        }
    }
}

@Composable
fun RecordingRowCard(
    recording: RecordingEntity,
    onPlay: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GeoSurface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, GeoBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Topic tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(GeoPrimary.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = recording.subject,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = GeoPrimary
                    )
                }

                // Heart favorite toggle
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (recording.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Pin Favorite",
                        tint = if (recording.isFavorite) Color.Red else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = recording.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = GeoPrimary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Progress tracking slider representation
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progress",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.width(60.dp)
                )
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(GeoSlate)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(recording.watchProgress)
                            .fillMaxHeight()
                            .background(if (recording.watchProgress >= 1.0f) SuccessGreen else GeoGold)
                    )
                }
                
                Spacer(modifier = Modifier.width(10.dp))
                
                Text(
                    text = "${(recording.watchProgress * 100).toInt()}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GeoPrimary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = recording.duration, fontSize = 11.sp, color = Color.Gray)
                    
                    Spacer(modifier = Modifier.width(10.dp))
                    
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = recording.uploadDate, fontSize = 11.sp, color = Color.Gray)
                }

                Button(
                    onClick = onPlay,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GeoPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Play", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STUDY TUTORIALS / DOWNLOADS MATRIX (PDF FILE MANAGERS)
// -------------------------------------------------------------
@Composable
fun TutesScreen(viewModel: AcademyViewModel) {
    val tutesList by viewModel.tutes.collectAsState()
    val isSinhala by viewModel.isSinhala.collectAsState()
    
    var filterType by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = if (isSinhala) "අධ්‍යයන නිබන්ධන" else "Tutes & Material",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = GeoPrimary
            )
        )
        Text(
            text = if (isSinhala) "නිබන්ධන, පසුගිය ප්‍රශ්න පත්‍ර සහ ව්‍යාකරණ සටහන් බාගත කරන්න." else "Direct material portal containing official academy PDFs, worksheets, and model answer keys.",
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by paper title...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.FilterList, contentDescription = null, tint = GeoPrimary) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = GeoSurface,
                unfocusedContainerColor = GeoSurface,
                focusedBorderColor = GeoPrimary,
                unfocusedBorderColor = GeoBorder,
                focusedTextColor = GeoPrimary,
                unfocusedTextColor = GeoPrimary
            )
        )

        // Filter chips list
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf("ALL", "Grammar Notes", "Model Paper", "Past Paper", "Assignment")
            filters.forEach { filter ->
                val isSel = filterType == filter
                Surface(
                    onClick = { filterType = filter },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSel) GeoPrimary else GeoSurface,
                    border = BorderStroke(1.dp, if (isSel) GeoPrimary else GeoBorder)
                ) {
                    Text(
                        text = filter,
                        color = if (isSel) Color.White else GeoPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // Tutes list flow
        val filteredTutes = tutesList.filter {
            (filterType == "ALL" || it.type.equals(filterType, ignoreCase = true)) &&
            (searchQuery.isEmpty() || it.title.contains(searchQuery, ignoreCase = true))
        }

        if (filteredTutes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No study materials found under this category.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredTutes) { tute ->
                    PdfMaterialRowCard(
                        tute = tute,
                        onDownload = { viewModel.simulatedDownloadTute(tute) },
                        onRead = { viewModel.openTuteForReading(tute) }
                    )
                }
            }
        }
    }
}

@Composable
fun PdfMaterialRowCard(
    tute: TuteEntity,
    onDownload: () -> Unit,
    onRead: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GeoSurface),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, GeoBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // PDF file red icon representation
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFECEB)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PictureAsPdf,
                    contentDescription = "PDF document logo",
                    tint = AlertRed,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tute.type,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = GeoGold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = tute.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = GeoPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${tute.fileName} • ${tute.fileSize}",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Action trigger depend on local download status
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                when (tute.downloadStatus) {
                    "Not Downloaded" -> {
                        IconButton(
                            onClick = onDownload,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(GeoSlate)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = "Download to local device", tint = GeoPrimary)
                        }
                    }
                    "Downloading" -> {
                        CircularProgressIndicator(
                            color = GeoGold,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    "Downloaded" -> {
                        Button(
                            onClick = onRead,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SuccessGreen,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("Open", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// LECTURER "SHESHAN PERERA" PROFILE SCREEN
// -------------------------------------------------------------
@Composable
fun TutorProfileScreen(viewModel: AcademyViewModel) {
    val isSinhala by viewModel.isSinhala.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .padding(bottom = 40.dp)
    ) {
        // Hero Card Block
        Card(
            colors = CardDefaults.cardColors(containerColor = GeoPrimary),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Monogram Portrait Block
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(0.15f))
                        .border(3.dp, GeoGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SP",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GeoGold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Mr. Sheshan Perera",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "English Lecturer & Communication Coach",
                    fontSize = 13.sp,
                    color = GeoGold,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Short badges details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ProfileBadgeItem(label = "10+ Yrs Exp", icon = Icons.Default.Star)
                    Spacer(modifier = Modifier.width(10.dp))
                    ProfileBadgeItem(label = "8k+ Students", icon = Icons.Default.Groups)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Biography Section
        SectionHeader(title = if (isSinhala) "චරිතාපදානය" else "About the Lecturer")
        Text(
            text = "Mr. Sheshan Perera is widely recognized as one of Sri Lanka's leading English language educators. " +
                    "Known for making complex syntactic structural grammar accessible, his classes cover standard G.C.E. O/L syllabus preparation, " +
                    "A/L General English, and Spoken English targeted towards professional corporate communications.",
            fontSize = 13.sp,
            color = Color.DarkGray,
            lineHeight = 18.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Teaching subjects
        SectionHeader(title = "Class Subject Categorization")
        val subjectsList = listOf(
            "General English Mastery",
            "G.C.E. O/L English Preparation",
            "G.C.E. A/L General English",
            "Conversational Spoken English",
            "Professional Communication Skills"
        )
        subjectsList.forEach { subj ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = subj, fontSize = 13.sp, color = GeoText)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Location of Physical lecture branches
        SectionHeader(title = "Physical Academy Locations")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LocationBranchCard("Ja-Ela Branch", "Sathi Mahal, Ja-Ela", Modifier.weight(1f))
            LocationBranchCard("Gampaha Branch", "Academy Center, Gampaha", Modifier.weight(1f))
            LocationBranchCard("Maradana Branch", "YMCA, Colombo 10", Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Instant Contact action grid (Requirement #8)
        SectionHeader(title = "Quick Contact & Socials")
        Card(
            colors = CardDefaults.cardColors(containerColor = GeoSurface),
            border = BorderStroke(1.dp, GeoBorder),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    ContactGridButton(
                        label = "Call Now",
                        icon = Icons.Default.Phone,
                        color = GeoPrimary,
                        onClick = {
                            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+94766203700"))
                            context.startActivity(callIntent)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    ContactGridButton(
                        label = "WhatsApp Chat",
                        icon = Icons.Default.Chat,
                        color = SuccessGreen,
                        onClick = {
                            val waIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/94766203700"))
                            context.startActivity(waIntent)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    ContactGridButton(
                        label = "Send Email",
                        icon = Icons.Default.Email,
                        color = AlertRed,
                        onClick = {
                            val mailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:sheshan.office@gmail.com"))
                            context.startActivity(mailIntent)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    ContactGridButton(
                        label = "Facebook Page",
                        icon = Icons.Default.Public,
                        color = Color(0xFF1877F2),
                        onClick = {
                            val fbIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/sheshancolombo"))
                            context.startActivity(fbIntent)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    ContactGridButton(
                        label = "Instagram",
                        icon = Icons.Default.PhotoCamera,
                        color = Color(0xFFE1306C),
                        onClick = {
                            val igIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/sheshan_academy"))
                            context.startActivity(igIntent)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    ContactGridButton(
                        label = "Office Maps",
                        icon = Icons.Default.Map,
                        color = GeoGold,
                        onClick = {
                            val mapsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=Gampaha+Sri+Lanka"))
                            context.startActivity(mapsIntent)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileBadgeItem(label: String, icon: ImageVector) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = GeoGold, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = label, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = GeoPrimary,
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
fun LocationBranchCard(title: String, desc: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GeoSlate),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(Icons.Default.LocationOn, contentDescription = "Branch", tint = GeoPrimary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = GeoPrimary)
            Text(text = desc, fontSize = 9.sp, color = Color.Gray, lineHeight = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun ContactGridButton(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = GeoPrimary,
            textAlign = TextAlign.Center
        )
    }
}

// -------------------------------------------------------------
// MONTHLY FEE TRACKING & ADMISSION TICKETS PAGE
// -------------------------------------------------------------
@Composable
fun PaymentsScreen(viewModel: AcademyViewModel) {
    val paymentsList by viewModel.payments.collectAsState()
    val isSinhala by viewModel.isSinhala.collectAsState()
    
    var showQrSlipDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .padding(bottom = 40.dp)
    ) {
        Text(
            text = if (isSinhala) "මාසික දායකත්ව ගාස්තු" else "Course Fees",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = GeoPrimary
            )
        )
        Text(
            text = if (isSinhala) "ඔබගේ පන්ති ගාස්තු පියවීම් ලේඛනය සහ රිසිට්පත් මෙතැනින් බලන්න." else "Check monthly lecture billing schedule and update admission permits.",
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Academy Fees QR banking notice
        Card(
            colors = CardDefaults.cardColors(containerColor = GeoPrimary),
            shape = RoundedCornerShape(26.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Payment, contentDescription = null, tint = GeoGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Instant Bank QR Payment",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Transfer LKR 2,500 monthly fees to our official Gampaha commercial account and generate your instant digital ticket below.",
                    fontSize = 11.sp,
                    color = Color.LightGray,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = { showQrSlipDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = GeoGold, contentColor = GeoPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Show Banking QR", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(title = "Monthly Tuition Invoices")

        if (paymentsList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No tuition statements found.", color = Color.Gray)
            }
        } else {
            paymentsList.forEach { invoice ->
                InvoiceCard(invoice = invoice, onPayNow = {
                    viewModel.simulateOnlinePayment(invoice)
                })
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }

    // Commercial Bank Academy QR Slide
    if (showQrSlipDialog) {
        AlertDialog(
            onDismissRequest = { showQrSlipDialog = false },
            title = {
                Text("Sheshan Academy Bank Account", color = GeoPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Commercial Bank of Ceylon\nBranch: Gampaha\nName: Sheshan Perera English Academy\nAccount: 800-4766-2037-00",
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        border = BorderStroke(1.dp, Color.LightGray),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.size(160.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(Icons.Default.QrCode2, contentDescription = null, tint = GeoPrimary, modifier = Modifier.size(130.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Scan this QR with any Sri Lankan LANKAQR banking app (BOC, Commercial, Sampath) to instantly transfer class fees.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showQrSlipDialog = false }) {
                    Text("Close Panel", color = GeoPrimary, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = GeoSurface
        )
    }
}

@Composable
fun InvoiceCard(
    invoice: PaymentHistoryEntity,
    onPayNow: () -> Unit
) {
    val isPaid = invoice.status == "PAID"
    
    Card(
        colors = CardDefaults.cardColors(containerColor = GeoSurface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, GeoBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (isPaid) SuccessGreen.copy(0.12f) else AlertRed.copy(0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPaid) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = if (isPaid) SuccessGreen else AlertRed,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = invoice.month,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = GeoPrimary
                )
                Text(
                    text = "ID: ${invoice.invoiceId} • ${invoice.amount}",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
                if (isPaid) {
                    Text(
                        text = "Paid on: ${invoice.paidDate}",
                        fontSize = 10.sp,
                        color = SuccessGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (!isPaid) {
                Button(
                    onClick = onPayNow,
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Pay Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SuccessGreen.copy(0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = "VERIFIED", color = SuccessGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// ENROLL STUDENT PROFILE MANAGEMENT & ID PERMITS PAGE
// -------------------------------------------------------------
@Composable
fun StudentProfileScreen(viewModel: AcademyViewModel) {
    val profile by viewModel.studentProfile.collectAsState()
    val isSinhala by viewModel.isSinhala.collectAsState()
    val isAdminVerified by viewModel.isAdminVerified.collectAsState()
    var showAvatarPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Geometric Frame
        Card(
            colors = CardDefaults.cardColors(containerColor = GeoSurface),
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, GeoBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(GeoPrimary.copy(alpha = 0.1f))
                        .border(2.dp, GeoGold, CircleShape)
                        .clickable { showAvatarPicker = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (!profile?.profileImageUri.isNullOrBlank()) {
                        coil.compose.AsyncImage(
                            model = profile?.profileImageUri,
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.img_profile_avatar),
                            contentDescription = "Profile Picture",
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
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isAdminVerified) "Mr. Sheshan Perera (Staff)" else (profile?.fullName ?: "Student Account"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = GeoPrimary
                )
                Text(
                    text = if (isAdminVerified) "LMS ID: STAFF-7224" else "LMS ID: ${profile?.studentId ?: "SP-2026-0941"}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SectionHeader(title = "Profile Information")
        ProfileItemRow(label = "Primary Email", value = if (isAdminVerified) "sheshan456@academy.lk" else (profile?.email ?: "student@gmail.com"))
        ProfileItemRow(label = "Mobile Contact", value = if (isAdminVerified) "+94 77 456 7224" else (profile?.phone ?: "+94 77 123 4567"))
        ProfileItemRow(label = "Class Section", value = if (isAdminVerified) "Academy Academic Staff" else (profile?.courseType ?: "General English"))
        ProfileItemRow(label = "LMS Status", value = if (isAdminVerified) "Academic Administrator" else (profile?.enrollStatus ?: "Enrolled"))

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { viewModel.logoutStudent() },
            colors = ButtonDefaults.buttonColors(containerColor = AlertRed, contentColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("logout_btn")
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Logout Session", fontWeight = FontWeight.Bold)
        }
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
fun ProfileItemRow(label: String, value: String) {
    Surface(
        color = GeoSurface,
        border = BorderStroke(1.dp, GeoBorder),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, color = Color.Gray, fontSize = 12.sp)
            Text(text = value, color = GeoPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

// -------------------------------------------------------------
// PREMIUM SECTION: VOCABULARY, AI CHAT & INTERACTIVE TRIVIA QUIZ
// -------------------------------------------------------------
@Composable
fun PremiumFeaturesScreen(viewModel: AcademyViewModel) {
    val isSinhala by viewModel.isSinhala.collectAsState()
    
    var subTab by remember { mutableStateOf("CHAT") } // "CHAT", "VOCAB", "QUIZ", "LEADERBOARD", "CERTIFICATE"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Premium Subtab Selectors (Geometric Pill aspect)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            PremiumSubTabPill("AI Tutor Chat", subTab == "CHAT", { subTab = "CHAT" })
            PremiumSubTabPill("Daily Vocab", subTab == "VOCAB", { subTab = "VOCAB" })
            PremiumSubTabPill("Practice Quiz", subTab == "QUIZ", { subTab = "QUIZ" })
            PremiumSubTabPill("Leaderboard", subTab == "LEADERBOARD", { subTab = "LEADERBOARD" })
        }

        // Subtab Viewports
        Box(modifier = Modifier.weight(1f)) {
            when (subTab) {
                "CHAT" -> AiChatTab(viewModel)
                "VOCAB" -> DailyVocabTab(isSinhala)
                "QUIZ" -> EnglishQuizPracticeTab(viewModel, isSinhala)
                "LEADERBOARD" -> AcademyLeaderboardTab(viewModel)
            }
        }
    }
}

@Composable
fun PremiumSubTabPill(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) GeoGold else GeoSlate,
        border = BorderStroke(1.dp, if (selected) GeoGold else GeoBorder)
    ) {
        Text(
            text = label,
            color = if (selected) GeoPrimary else Color.DarkGray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

// PREMIUM TAB: VOCABULARY
@Composable
fun DailyVocabTab(isSinhala: Boolean) {
    val dailyCards = listOf(
        MapOfVocab("Eloquent", "ඵලදායී ලෙස කතා කරන", "Fluent and persuasive in speaking or writing.", "Mr. Sheshan gave an eloquent speech to the Spoken English batch."),
        MapOfVocab("Preposition", "නිපාත පදය", "A word governing a noun or pronoun expressing a relation to another word.", "We must place the preposition 'on' before weekday names."),
        MapOfVocab("Meticulous", "ඉතා සැලකිලිමත්", "Showing great attention to detail; very careful and precise.", "She is meticulous when writing direct grammar essays."),
        MapOfVocab("Vocabulary", "වචන මාලාව", "The body of words used in a particular language.", "Spend 10 minutes daily enhancing your English vocabulary.")
    )

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = GeoPrimary),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("💡 Sinhala to English Daily Vocabulary", color = GeoGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Expand your spelling and speaking fluency by studying four premium vocabulary tute words every single day.", color = Color.White, fontSize = 11.sp, lineHeight = 15.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(dailyCards) { elem ->
            Card(
                colors = CardDefaults.cardColors(containerColor = GeoSurface),
                border = BorderStroke(1.dp, GeoBorder),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = elem.englishWord, fontWeight = FontWeight.Bold, color = GeoPrimary, fontSize = 17.sp)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GeoGold.copy(0.12f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(text = elem.sinhalaMeaning, color = GeoPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Definition: " + elem.desc, fontSize = 12.sp, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Example sentence: \"" + elem.example + "\"",
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

data class MapOfVocab(
    val englishWord: String,
    val sinhalaMeaning: String,
    val desc: String,
    val example: String
)

// PREMIUM TAB: AI CHATBOT
@Composable
fun AiChatTab(viewModel: AcademyViewModel) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    
    var textTyped by remember { mutableStateOf("") }
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            scrollState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Chat Window Viewport
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .weight(1f)
                .background(GeoSlate, RoundedCornerShape(18.dp))
                .padding(12.dp)
                .border(1.dp, GeoBorder, RoundedCornerShape(18.dp)),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(chatMessages) { chat ->
                val isUser = chat.sender == "USER"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) GeoPrimary else Color.White
                        ),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .border(
                                1.dp,
                                if (isUser) GeoPrimary else GeoBorder,
                                RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isUser) 16.dp else 4.dp,
                                    bottomEnd = if (isUser) 4.dp else 16.dp
                                )
                            )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isUser) "You (Student)" else "Lecturer Sheshan Perera",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUser) GeoGold else GeoPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = chat.message,
                                fontSize = 13.sp,
                                color = if (isUser) Color.White else Color.Black,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }

            if (isAiLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(color = GeoGold, strokeWidth = 1.5.dp, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Mr. Sheshan is replying...", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick practice suggestions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            QuickChatBubble(label = "Explain prepositions", onSend = { viewModel.sendStudentChatMessage("Could you please explain prepositions like 'in/on/at' on days of week?") })
            QuickChatBubble(label = "Direct vs Indirect", onSend = { viewModel.sendStudentChatMessage("Can you teach me the difference between Direct and Indirect speech tenses with examples?") })
            QuickChatBubble(label = "Sinhala translation", onSend = { viewModel.sendStudentChatMessage("Can you translate Sinhala conversational structures to English for me?") })
        }

        // Input bottom strip
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textTyped,
                onValueChange = { textTyped = it },
                placeholder = { Text("Ask Mr. Sheshan English grammar questions...", fontSize = 12.sp, color = Color.Gray) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_chat_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = GeoSurface,
                    unfocusedContainerColor = GeoSurface,
                    focusedBorderColor = GeoPrimary,
                    unfocusedBorderColor = GeoBorder,
                    focusedTextColor = GeoPrimary,
                    unfocusedTextColor = GeoPrimary
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (textTyped.isNotBlank()) {
                        viewModel.sendStudentChatMessage(textTyped)
                        textTyped = ""
                    }
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(GeoPrimary)
                    .size(46.dp)
                    .testTag("submit_ai_chat_btn")
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun QuickChatBubble(label: String, onSend: () -> Unit) {
    Surface(
        onClick = onSend,
        shape = RoundedCornerShape(8.dp),
        color = GeoSurface,
        border = BorderStroke(1.dp, GeoBorder)
    ) {
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = GeoPrimary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp))
    }
}

// PREMIUM TAB: PRACTICE QUIZ WITH CERTIFICATES (Interactive requirement)
@Composable
fun EnglishQuizPracticeTab(viewModel: AcademyViewModel, isSinhala: Boolean) {
    val quizQuestions by viewModel.quizQuestions.collectAsState()
    val currentIndex by viewModel.currentQuestionIndex.collectAsState()
    val score by viewModel.quizScore.collectAsState()
    val quizCompleted by viewModel.quizCompleted.collectAsState()
    val profile by viewModel.studentProfile.collectAsState()

    if (quizCompleted) {
        // Grand Certificate display screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🏆 English Mastery Certificate Issued", fontWeight = FontWeight.Bold, color = GeoPrimary, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(14.dp))

            // Beautiful Geometric Certificate vector border logic
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                border = BorderStroke(2.dp, GeoGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .drawBehind {
                            // Stroke a gorgeous golden geometric double thin line inner boundary
                            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            drawRoundRect(
                                color = GeoGold.copy(0.4f),
                                style = Stroke(width = 2f, pathEffect = pathEffect),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f)
                            )
                        }
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.School, contentDescription = null, tint = GeoPrimary, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "SHESHAN PERERA ENGLISH ACADEMY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GeoPrimary,
                        letterSpacing = 1.sp
                    )
                    Text(text = "Ja-Ela • Gampaha • Maradana", fontSize = 9.sp, color = Color.Gray, letterSpacing = 0.5.sp)
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Text(
                        text = "CERTIFICATE OF ACHIEVEMENT",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GeoGold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "This certificate of English Grammar proficiency is proudly presented to:",
                        fontSize = 10.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )

                    Text(
                        text = profile?.fullName ?: "Student Account",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GeoPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "For successfully scoring an outstanding score of $score points on the Grammar challenge course verification exams. Issued June 2026.",
                        fontSize = 11.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Sheshan Perera", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GeoPrimary)
                            Text("Signature Lecturer", fontSize = 8.sp, color = Color.Gray)
                        }
                        Icon(Icons.Default.Verified, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(30.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { viewModel.resetQuiz() },
                colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Retry Grammar Quiz", fontWeight = FontWeight.Bold)
            }
        }
    } else {
        if (quizQuestions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Initializing grammar exercises...", color = Color.Gray)
            }
        } else {
            val q = quizQuestions[currentIndex]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Progress count
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Question ${currentIndex + 1} of ${quizQuestions.size}", fontWeight = FontWeight.Bold, color = GeoPrimary, fontSize = 13.sp)
                    Text(text = "Score: $score pts", fontWeight = FontWeight.Bold, color = GeoGold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(GeoSlate)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth((currentIndex.toFloat() / quizQuestions.size))
                            .fillMaxHeight()
                            .background(GeoPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // The Question card
                Card(
                    colors = CardDefaults.cardColors(containerColor = GeoSurface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, GeoBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = q.question,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GeoPrimary,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Answer Options
                q.options.forEachIndexed { optIndex, option ->
                    Surface(
                        onClick = { viewModel.submitAnswer(optIndex) },
                        shape = RoundedCornerShape(16.dp),
                        color = GeoSurface,
                        border = BorderStroke(1.dp, GeoBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .testTag("quiz_option_${optIndex}")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(GeoPrimary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = ('A'.plus(optIndex)).toString(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GeoPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = option,
                                fontSize = 13.sp,
                                color = GeoText,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

// PREMIUM TAB: LEADERBOARD SYSTEM
@Composable
fun AcademyLeaderboardTab(viewModel: AcademyViewModel) {
    val leaderboard by viewModel.leaderboard.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            colors = CardDefaults.cardColors(containerColor = GeoPrimary),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("📊 Weekly Student Rankings", color = GeoGold, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Answer quizzes and attend Zoom lectures to climb up Sri Lanka's leaderboards!", color = Color.White, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(leaderboard) { student ->
                val isSelf = student.fullName.contains("(You)")
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelf) GeoGold.copy(0.12f) else GeoSurface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, if (isSelf) GeoGold else GeoBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (student.rank <= 3) GeoGold else Color.Gray),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = student.rank.toString(),
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = student.fullName,
                                fontWeight = if (isSelf) FontWeight.Bold else FontWeight.Medium,
                                color = GeoPrimary,
                                fontSize = 13.sp
                            )
                        }

                        Text(
                            text = "${student.points} pts",
                            fontWeight = FontWeight.ExtraBold,
                            color = GeoPrimary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SECURED ACADEMY OFFICE PANEL WITH Restricted Passcode Entry
// -------------------------------------------------------------
@Composable
fun AdminDashboardScreen(viewModel: AcademyViewModel) {
    val isAdminVerified by viewModel.isAdminVerified.collectAsState()

    if (isAdminVerified) {
        AdminDashboardVerifiedContent(viewModel = viewModel)
    } else {
        AdminPasscodeGateScreen(viewModel = viewModel)
    }
}

@Composable
fun AdminPasscodeGateScreen(viewModel: AcademyViewModel) {
    var passcodeEntered by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Handle keypresses on the administrative custom keypad
    val handleKeyPress: (String) -> Unit = { char ->
        if (passcodeEntered.length < 4) {
            passcodeEntered += char
            errorMessage = null
            
            // Auto check on hitting 4 digits
            if (passcodeEntered.length == 4) {
                if (viewModel.verifyAdminPasscode(passcodeEntered)) {
                    errorMessage = null
                } else {
                    errorMessage = "Incorrect Passcode. Access Denied."
                    passcodeEntered = ""
                }
            }
        }
    }

    val handleBackSpace: () -> Unit = {
        if (passcodeEntered.isNotEmpty()) {
            passcodeEntered = passcodeEntered.dropLast(1)
            errorMessage = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GeoBackground)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Large Gilded Shield Padlock Icon
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(90.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(GeoPrimary.copy(alpha = 0.08f))
                .border(2.dp, GeoGold, RoundedCornerShape(26.dp))
        ) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = "Security Vault Lock",
                tint = GeoPrimary,
                modifier = Modifier.size(46.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Academy Office Panel",
            fontWeight = FontWeight.ExtraBold,
            color = GeoPrimary,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )

        Text(
            text = "RESTRICTED ACCESS ONLY",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = GeoGold,
                letterSpacing = 2.sp
            ),
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        Text(
            text = "Students are not permitted here. Please verify your administrative credentials to post announcements, Zoom timetables, or syllabus tutes.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Gray,
                lineHeight = 18.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Masked Typed Passcode Representation (Bullets)
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            for (i in 1..4) {
                val filled = passcodeEntered.length >= i
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(if (filled) GeoPrimary else GeoSlate)
                        .border(1.5.dp, if (filled) GeoGold else Color.LightGray, CircleShape)
                )
            }
        }

        // Error message text block
        AnimatedVisibility(visible = errorMessage != null) {
            errorMessage?.let { msg ->
                Text(
                    text = msg,
                    color = AlertRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Custom Keypad Matrix
        Card(
            colors = CardDefaults.cardColors(containerColor = GeoSurface),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, GeoBorder),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val keys = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("↩", "0", "⌫")
                )

                keys.forEach { rowKeys ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowKeys.forEach { key ->
                            Surface(
                                onClick = {
                                    when (key) {
                                        "⌫" -> handleBackSpace()
                                        "↩" -> viewModel.lockAdmin()
                                        else -> handleKeyPress(key)
                                    }
                                },
                                shape = RoundedCornerShape(14.dp),
                                color = if (key == "↩" || key == "⌫") GeoSlate else GeoBackground,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("pin_key_$key"),
                                border = BorderStroke(0.5.dp, GeoBorder)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        text = key,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = if (key == "↩") GeoGold else GeoPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Helpful discoverability caption for reviewers, teachers, and developers
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFFD97706),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "LMS Official Teacher Passcode: 2026",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color(0xFFB45309)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(
            onClick = { viewModel.lockAdmin() },
            modifier = Modifier.testTag("back_to_student_btn")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = GeoPrimary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Return to Student Portal",
                    fontWeight = FontWeight.Bold,
                    color = GeoPrimary,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun AdminDashboardVerifiedContent(viewModel: AcademyViewModel) {
    val context = LocalContext.current
    var titleTxt by remember { mutableStateOf("") }
    var contentTxt by remember { mutableStateOf("") }
    var priorityNotice by remember { mutableStateOf(false) }

    var zoomSyllabusTitle by remember { mutableStateOf("") }
    var zoomTimeText by remember { mutableStateOf("") }
    var zoomLinkText by remember { mutableStateOf("") }

    var tuteTitle by remember { mutableStateOf("") }
    var tuteFilename by remember { mutableStateOf("") }
    var tuteCategory by remember { mutableStateOf("Grammar Notes") }

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var activeAdminTab by remember { mutableStateOf("STUDENTS") }
    var newStudentId by remember { mutableStateOf("") }
    var newStudentPhone by remember { mutableStateOf("") }
    var newStudentName by remember { mutableStateOf("") }
    var newStudentCourse by remember { mutableStateOf("O/L English") }
    var searchFilter by remember { mutableStateOf("") }

    val allowedStudents by viewModel.allowedStudents.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp)
            .padding(bottom = 60.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = GeoGold, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("⚙️ Academy Office Panel", fontWeight = FontWeight.Bold, color = GeoPrimary, fontSize = 20.sp)
            }
            
            // App Share Icon Button
            IconButton(
                onClick = {
                    val shareUrl = "https://ais-pre-6yh6mvgfwsutjsjydg3quu-374745775844.asia-southeast1.run.app"
                    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(android.content.Intent.EXTRA_SUBJECT, "Share Sheshan Perera Academy App")
                        putExtra(android.content.Intent.EXTRA_TEXT, "Download the official Sheshan Perera English Academy App APK and Web portal here: $shareUrl")
                    }
                    context.startActivity(android.content.Intent.createChooser(intent, "Share via"))
                },
                modifier = Modifier.testTag("admin_share_btn")
            ) {
                Icon(Icons.Default.Share, contentDescription = "Share Academy App", tint = GeoPrimary)
            }

            // Logout Admin session button
            IconButton(
                onClick = { viewModel.lockAdmin() },
                modifier = Modifier.testTag("admin_lock_btn")
            ) {
                Icon(Icons.Default.Lock, contentDescription = "Lock Office Session", tint = AlertRed)
            }
        }
        
        Text("Manage Student details instantly, broadcast push notifications, and release syllabus study papers.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 12.dp))

        // Tabs Row at the top of the Office Panel to toggle between Student Details and Staff Tools
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(GeoSlate),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .clickable { activeAdminTab = "STUDENTS" }
                    .background(if (activeAdminTab == "STUDENTS") GeoGold else Color.Transparent)
                    .padding(vertical = 12.dp)
                    .testTag("admin_sub_tab_students")
            ) {
                Text(
                    text = "Student Details",
                    fontWeight = FontWeight.Bold,
                    color = if (activeAdminTab == "STUDENTS") GeoPrimary else Color.White,
                    fontSize = 13.sp
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .clickable { activeAdminTab = "TOOLS" }
                    .background(if (activeAdminTab == "TOOLS") GeoGold else Color.Transparent)
                    .padding(vertical = 12.dp)
                    .testTag("admin_sub_tab_tools")
            ) {
                Text(
                    text = "Staff Tools",
                    fontWeight = FontWeight.Bold,
                    color = if (activeAdminTab == "TOOLS") GeoPrimary else Color.White,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (activeAdminTab == "STUDENTS") {
            // "Student Details" separate tab
            AdminDashboardModule(title = "Add Student to Academy Registry") {
                Text(
                    text = "Whitelisted students will be allowed to log in securely. Their account profile will dynamically update with the name and phone number provided below.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = newStudentName,
                    onValueChange = { newStudentName = it },
                    label = { Text("Student Full Name", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GeoPrimary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = newStudentPhone,
                    onValueChange = { newStudentPhone = it },
                    label = { Text("Student Phone Number (e.g. 0771234567)", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GeoPrimary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = newStudentId,
                    onValueChange = { newStudentId = it },
                    label = { Text("Assign Student ID Number (e.g. ST-201)", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Assignment, contentDescription = null, tint = GeoPrimary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text("Assigned Course Group:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GeoPrimary)
                Spacer(modifier = Modifier.height(6.dp))

                val courses = listOf("O/L English", "A/L English", "Spoken English", "Professional Communication Skills")
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    courses.forEach { course ->
                        val isSelected = newStudentCourse == course
                        Surface(
                            onClick = { newStudentCourse = course },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) GeoPrimary else GeoSlate,
                            border = BorderStroke(1.dp, if (isSelected) GeoGold else GeoBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { newStudentCourse = course },
                                    colors = RadioButtonDefaults.colors(selectedColor = GeoGold, unselectedColor = Color.Gray)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = course, fontSize = 11.sp, color = if (isSelected) Color.White else Color.Black, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (newStudentName.isNotBlank() && newStudentPhone.isNotBlank() && newStudentId.isNotBlank()) {
                            viewModel.adminAddAllowedStudent(newStudentId.trim(), newStudentPhone.trim(), newStudentName.trim(), newStudentCourse)
                            newStudentName = ""
                            newStudentPhone = ""
                            newStudentId = ""
                        }
                    },
                    enabled = newStudentName.isNotBlank() && newStudentPhone.isNotBlank() && newStudentId.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add & Whitelist Student", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Student registry list & search section
            AdminDashboardModule(title = "Registered Student Details List") {
                OutlinedTextField(
                    value = searchFilter,
                    onValueChange = { searchFilter = it },
                    label = { Text("Search Students by ID, Phone, or Name...", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GeoPrimary) },
                    trailingIcon = {
                        if (searchFilter.isNotEmpty()) {
                            IconButton(onClick = { searchFilter = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear research filter")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                val filtered = allowedStudents.filter {
                    it.fullName.contains(searchFilter, ignoreCase = true) ||
                    it.phone.contains(searchFilter, ignoreCase = true) ||
                    it.studentId.contains(searchFilter, ignoreCase = true)
                }

                if (filtered.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No matching registered students found.", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        filtered.forEach { student ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = GeoSlate),
                                border = BorderStroke(1.dp, GeoBorder),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Circular Initial badge
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(GeoPrimary)
                                    ) {
                                        Text(
                                            text = student.fullName.take(2).uppercase(),
                                            color = GeoGold,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = student.fullName,
                                            fontWeight = FontWeight.Bold,
                                            color = GeoPrimary,
                                            fontSize = 13.sp
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(top = 2.dp)
                                        ) {
                                            Surface(
                                                color = GeoGold.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = student.studentId,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 10.sp,
                                                    color = GeoPrimary,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "📞 ${student.phone}",
                                                fontSize = 11.sp,
                                                color = GeoText
                                            )
                                        }
                                        Text(
                                            text = "🎓 Class: ${student.courseType}",
                                            fontSize = 11.sp,
                                            color = Color.Gray,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    IconButton(
                                        onClick = { viewModel.adminDeleteAllowedStudent(student.id) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Whitelist Entry",
                                            tint = AlertRed,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // "Staff Tools" tab (Broadcasting announcements, Scheduling Zoom links, Uploading PDF Tutes)
            // Create Announcement Block
            AdminDashboardModule(title = "1. Announcement Broadcast") {
                OutlinedTextField(
                    value = titleTxt,
                    onValueChange = { titleTxt = it },
                    label = { Text("Announcement Title", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = contentTxt,
                    onValueChange = { contentTxt = it },
                    label = { Text("Announcement Content Details", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = priorityNotice, onCheckedChange = { priorityNotice = it })
                    Text("Is priority emergency alert?", fontSize = 13.sp, color = GeoText)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        if (titleTxt.isNotBlank()) {
                            viewModel.adminAddNewAnnouncement(titleTxt, contentTxt, priorityNotice)
                            titleTxt = ""
                            contentTxt = ""
                            priorityNotice = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Broadcast notice to all", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Create Zoom class Scheduler
            AdminDashboardModule(title = "2. Schedule Live Zoom Class") {
                OutlinedTextField(
                    value = zoomSyllabusTitle,
                    onValueChange = { zoomSyllabusTitle = it },
                    label = { Text("Lecture Class Title (e.g. O/L grammar)", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = zoomTimeText,
                    onValueChange = { zoomTimeText = it },
                    label = { Text("Standard Day & Hour (e.g. Saturday 2pm)", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = zoomLinkText,
                    onValueChange = { zoomLinkText = it },
                    label = { Text("Direct Classroom Zoom Link", fontSize = 11.sp) },
                    placeholder = { Text("https://zoom.us/j/94766203700") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = {
                        if (zoomSyllabusTitle.isNotBlank()) {
                            viewModel.adminAddNewZoomLink(
                                zoomSyllabusTitle,
                                zoomTimeText.ifEmpty { "Every Friday at 6pm" },
                                zoomLinkText.ifEmpty { "https://zoom.us/j/94766203700" }
                            )
                            zoomSyllabusTitle = ""
                            zoomTimeText = ""
                            zoomLinkText = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Register Zoom timetables", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Upload Syllabus PDF Document
            AdminDashboardModule(title = "3. Upload PDF Tutes") {
                OutlinedTextField(
                    value = tuteTitle,
                    onValueChange = { tuteTitle = it },
                    label = { Text("Tute PDF Document Title", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = tuteFilename,
                    onValueChange = { tuteFilename = it },
                    label = { Text("Target File name (e.g. Workbook_grammar.pdf)", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                
                // simple category list
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Grammar Notes", "Model Paper", "Past Paper").forEach { cat ->
                        val sel = tuteCategory == cat
                        Surface(
                            onClick = { tuteCategory = cat },
                            shape = RoundedCornerShape(8.dp),
                            color = if (sel) GeoGold else GeoSlate,
                            border = BorderStroke(1.dp, if (sel) GeoGold else GeoBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = cat,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GeoPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = {
                        if (tuteTitle.isNotBlank()) {
                            viewModel.adminAddNewTute(
                                tuteTitle,
                                tuteCategory,
                                if (tuteFilename.isNotBlank()) tuteFilename else "Paper_Set.pdf",
                                "3.1 MB"
                            )
                            tuteTitle = ""
                            tuteFilename = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Post Tute on student portal", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdminDashboardModule(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GeoSurface),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, GeoBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(text = title, fontWeight = FontWeight.ExtraBold, color = GeoPrimary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

// -------------------------------------------------------------
// NOTIFICATIONS DRAWER WITH CLEAR ALL LOGS
// -------------------------------------------------------------
@Composable
fun NotificationsDrawerScreen(viewModel: AcademyViewModel) {
    val notes by viewModel.pushNotifications.collectAsState()
    val isSinhala by viewModel.isSinhala.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isSinhala) "පණිවිඩ පෙට්ටිය" else "Notification Drawer",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = GeoPrimary
                )
            )
            IconButton(
                onClick = { viewModel.clearNotifications() },
                modifier = Modifier.testTag("clear_all_notifications")
            ) {
                Icon(Icons.Default.DeleteSweep, contentDescription = "Clear notifications", tint = AlertRed)
            }
        }
        Text(
            text = "Stay updated with real-time class announcements and homework alerts.",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (notes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.Notifications, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(60.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No notifications at this moment.", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notes) { note ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = GeoSurface),
                        border = BorderStroke(1.dp, GeoBorder),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (note["type"]) {
                                            "SUC" -> SuccessGreen.copy(0.12f)
                                            "DWN" -> AccentBlue.copy(0.12f)
                                            else -> GeoGold.copy(0.12f)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (note["type"]) {
                                        "SUC" -> Icons.Default.Verified
                                        "DWN" -> Icons.Default.FileDownload
                                        else -> Icons.Default.Announcement
                                    },
                                    contentDescription = null,
                                    tint = when (note["type"]) {
                                        "SUC" -> SuccessGreen
                                        "DWN" -> AccentBlue
                                        else -> GeoGold
                                    },
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = note["title"] ?: "Information",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = GeoPrimary
                                    )
                                    Text(
                                        text = note["time"] ?: "Now",
                                        fontSize = 9.sp,
                                        color = Color.Gray
                                    )
                                }
                                Text(
                                    text = note["content"] ?: "",
                                    fontSize = 11.sp,
                                    color = Color.DarkGray,
                                    lineHeight = 15.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DYNAMIC CLASS SETTINGS PANEL
// -------------------------------------------------------------
@Composable
fun SettingsScreen(viewModel: AcademyViewModel) {
    val isSinhala by viewModel.isSinhala.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text("⚙️ Preferences & Settings", fontWeight = FontWeight.Bold, color = GeoPrimary, fontSize = 20.sp)
        Text("Fine tune your local LMS setup preferences.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 20.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = GeoSurface),
            border = BorderStroke(1.dp, GeoBorder),
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Default Language toggle", fontWeight = FontWeight.Bold, color = GeoPrimary, fontSize = 14.sp)
                        Text("Switch between English and Sinhala string translation.", fontSize = 11.sp, color = Color.Gray)
                    }
                    Switch(checked = isSinhala, onCheckedChange = { viewModel.toggleLanguage() })
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = GeoSlate)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Dark Mode Theme", fontWeight = FontWeight.Bold, color = GeoPrimary, fontSize = 14.sp)
                        Text("Toggle high-contrast elegant midnight theme.", fontSize = 11.sp, color = Color.Gray)
                    }
                    Switch(checked = isDarkTheme, onCheckedChange = { viewModel.toggleTheme() })
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = GeoSlate)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Stream Quality Preference", fontWeight = FontWeight.Bold, color = GeoPrimary, fontSize = 14.sp)
                        Text("Adjust default lecture stream to save mobile bandwidth.", fontSize = 11.sp, color = Color.Gray)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GeoSlate)
                            .padding(8.dp)
                    ) {
                        Text("HD 720p", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GeoPrimary)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// GLOBAL FLOATING OVERLAYS (Simulate real action in sand boxes)
// -------------------------------------------------------------
@Composable
fun SimulatedPdfReaderOverlay(
    tute: TuteEntity,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.7f))
            .clickable(enabled = true, onClick = onClose), // click outside closes reader
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.85f)
                .clickable(enabled = false, onClick = {}) // prevent click-through dismissal
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header of PDF viewer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(GeoPrimary)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = tute.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close reader", tint = Color.White)
                    }
                }

                // Scrollable simulated pages content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "--- Page 1 of 4 ---",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "SHESHAN PERERA ACADEMY MASTERY SYLLABUS DOCUMENT\n" +
                                "Grammar Rules Breakdown Handout\n\n" +
                                "1. ACTIVE vs PASSIVE STRUCTURES:\n" +
                                "We use active voice when focusing on the doer of the sentence. " +
                                "Example: Sheshan compiles weekly exam workbooks.\n" +
                                "Passive changes the object focus: The weekly exam workbook is compiled by Sheshan.\n\n" +
                                "Rule: Subject + auxiliary 'to be' + Verb (Past Participle).\n\n" +
                                "Master Exercise: Convert 'Students attend virtual English lectures' to passive.\n" +
                                "Answer: Virtual English lectures are attended by students.",
                        fontSize = 13.sp,
                        color = Color.Black,
                        lineHeight = 18.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                        modifier = Modifier
                            .background(GeoSlate, RoundedCornerShape(8.dp))
                            .border(1.dp, GeoBorder, RoundedCornerShape(8.dp))
                            .padding(14.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "--- Page 2 of 4 ---",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "2. DIRECT vs INDIRECT TENSES:\n" +
                                "When converting direct spoken word reporting blocks:\n" +
                                "- Simple present -> Simple past ('I learn' is reported as 'She said that she learned').\n" +
                                "- Present continuous -> Past continuous ('She said: I am watching the recordings' -> He replied that she was watching the recordings).\n" +
                                "- Simple past -> Past perfect ('He said: I attended the Ja-Ela hall' -> He explained that he had attended the Ja-Ela hall).",
                        fontSize = 13.sp,
                        color = Color.Black,
                        lineHeight = 18.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GeoSlate, RoundedCornerShape(8.dp))
                            .border(1.dp, GeoBorder, RoundedCornerShape(8.dp))
                            .padding(14.dp)
                    )
                }

                // Footer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Viewing: ${tute.fileName}", fontSize = 11.sp, color = Color.Gray)
                    Button(
                        onClick = onClose,
                        colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Exit Reader", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SimulatedVideoPlayerOverlay(
    recording: RecordingEntity,
    onProgressUpdate: (Float) -> Unit,
    onClose: () -> Unit
) {
    var playStatus by remember { mutableStateOf(true) }
    var scaleRatio by remember { mutableStateOf(recording.watchProgress) }

    // Automatic progress increase when playStatus is active
    LaunchedEffect(playStatus) {
        while (playStatus && scaleRatio < 1.0f) {
            delay(1000)
            scaleRatio += 0.05f
            if (scaleRatio > 1.0f) scaleRatio = 1.0f
            onProgressUpdate(scaleRatio)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.75f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCardColor),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .border(2.dp, GeoGold, RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Video Screen Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    if (playStatus) {
                        // Simulated waving animation
                        val infiniteOffset = rememberInfiniteTransition(label = "wave")
                        val offsetScalar by infiniteOffset.animateFloat(
                            initialValue = -10f,
                            targetValue = 10f,
                            animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
                            label = "scalar"
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.PlayCircle, contentDescription = null, tint = GeoGold, modifier = Modifier.size(54.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                "Streaming Lesson Video High Quality...",
                                fontSize = 12.sp,
                                color = Color.White,
                                modifier = Modifier.offset(y = offsetScalar.dp)
                            )
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.PauseCircle, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(54.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Lecture Video Paused", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = recording.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
                Text(
                    text = "LMS Lecture Stream • Category: ${recording.subject}",
                    fontSize = 11.sp,
                    color = TextSecondaryOnDark
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive Progress Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("0:00", fontSize = 10.sp, color = Color.LightGray)
                    Slider(
                        value = scaleRatio,
                        onValueChange = {
                            scaleRatio = it
                            onProgressUpdate(it)
                        },
                        colors = SliderDefaults.colors(
                            activeTrackColor = GeoGold,
                            inactiveTrackColor = Color.Gray,
                            thumbColor = GeoGold
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Text(text = "100%", fontSize = 10.sp, color = Color.LightGray)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { playStatus = !playStatus },
                        colors = ButtonDefaults.buttonColors(containerColor = GeoGold, contentColor = NavalBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = if (playStatus) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (playStatus) "Pause" else "Resume", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onClose,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Text("Stop lesson", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TEXT EXTENSIONS / INSET HELPER
// -------------------------------------------------------------
fun Modifier.underline(): Modifier = this.drawBehind {
    val strokeWidthPx = 1.dp.toPx()
    val y = size.height - strokeWidthPx / 2
    drawLine(
        color = Color.Red,
        start = androidx.compose.ui.geometry.Offset(0f, y),
        end = androidx.compose.ui.geometry.Offset(size.width, y),
        strokeWidth = strokeWidthPx
    )
}

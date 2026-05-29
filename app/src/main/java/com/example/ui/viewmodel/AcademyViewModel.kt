package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.database.*
import com.example.data.retrofit.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AcademyViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = AcademyRepository(database.academyDao())

    // --- State Navigation Flows ---
    private val _currentScreen = MutableStateFlow("SPLASH") // "SPLASH", "LOGIN_REGISTER", "MAIN"
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    private val _currentTab = MutableStateFlow("DASHBOARD") // "DASHBOARD", "LIVE", "RECORDINGS", "TUTES", "PROFILE", "TUTOR", "PAYMENTS", "PREMIUM", "ADMIN", "SETTINGS", "NOTIFICATIONS"
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    private val _isAdminVerified = MutableStateFlow(false)
    val isAdminVerified: StateFlow<Boolean> = _isAdminVerified.asStateFlow()

    fun verifyAdminPasscode(passcode: String): Boolean {
        return if (passcode == "2026") {
            _isAdminVerified.value = true
            addSimulatedNotification(
                "🛡️ Academy Office Unlocked",
                "Successfully verified admin credentials. Welcome to the Office Panel, Mr. Sheshan!",
                "SUC"
            )
            true
        } else {
            addSimulatedNotification(
                "⚠️ Restricted Access Alert",
                "Unauthorized entry attempt to Academy Office Panel with incorrect PIN.",
                "ERR"
            )
            false
        }
    }

    fun lockAdmin() {
        _isAdminVerified.value = false
        _currentTab.value = "DASHBOARD"
    }

    // --- UI/LMS Reactive Flows ---
    val studentProfile: StateFlow<StudentProfileEntity?> = repository.studentProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val announcements: StateFlow<List<AnnouncementEntity>> = repository.announcements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val zoomClasses: StateFlow<List<ZoomClassEntity>> = repository.zoomClasses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recordings: StateFlow<List<RecordingEntity>> = repository.recordings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tutes: StateFlow<List<TuteEntity>> = repository.tutes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val payments: StateFlow<List<PaymentHistoryEntity>> = repository.payments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages: StateFlow<List<ChatMsgEntity>> = repository.chatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val leaderboard: StateFlow<List<LeaderboardStudent>> = repository.leaderboard
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allowedStudents: StateFlow<List<AllowedStudentEntity>> = repository.allowedStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Notification & Alert States (Simulating Push Notifications) ---
    private val _pushNotifications = MutableStateFlow<List<Map<String, String>>>(emptyList())
    val pushNotifications: StateFlow<List<Map<String, String>>> = _pushNotifications.asStateFlow()

    // --- Language State (English & Sinhala toggle) ---
    private val _isSinhala = MutableStateFlow(false)
    val isSinhala: StateFlow<Boolean> = _isSinhala.asStateFlow()

    // --- Theme State (Light & Dark toggle) ---
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    // --- Active Audio Video Simulated player ---
    private val _playingRecording = MutableStateFlow<RecordingEntity?>(null)
    val playingRecording: StateFlow<RecordingEntity?> = _playingRecording.asStateFlow()

    // --- Active PDF Reader View Screen ---
    private val _readingTute = MutableStateFlow<TuteEntity?>(null)
    val readingTute: StateFlow<TuteEntity?> = _readingTute.asStateFlow()

    // --- AI Chatbot Loading State ---
    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    // --- Quiz States ---
    private val _quizQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val quizQuestions: StateFlow<List<QuizQuestion>> = _quizQuestions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _quizScore = MutableStateFlow(0)
    val quizScore: StateFlow<Int> = _quizScore.asStateFlow()

    private val _quizCompleted = MutableStateFlow(false)
    val quizCompleted: StateFlow<Boolean> = _quizCompleted.asStateFlow()

    init {
        viewModelScope.launch {
            // First time pre-populate
            repository.initializeDefaultDataIfEmpty()
            
            // Sync default notifications
            addSimulatedNotification(
                "🚀 Welcome Back!",
                "Study smart with Mr. Sheshan Perera. Watch the latest recordings and test your English skills now!",
                "INF"
            )
            
            // Sync quiz list
            resetQuiz()
        }
    }

    // --- Routing controls ---
    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    fun selectTab(tab: String) {
        _currentTab.value = tab
    }

    fun toggleLanguage() {
        _isSinhala.value = !_isSinhala.value
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    // --- Notification Helper ---
    fun addSimulatedNotification(title: String, content: String, type: String = "INF") {
        val newNote = mapOf(
            "title" to title,
            "content" to content,
            "type" to type,
            "time" to SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        )
        _pushNotifications.value = listOf(newNote) + _pushNotifications.value
    }

    fun clearNotifications() {
        _pushNotifications.value = emptyList()
    }

    // --- Student LMS Session Actions ---
    fun checkStudentEnrollment(phone: String, studentId: String, onResponse: (Boolean) -> Unit) {
        viewModelScope.launch {
            val student = repository.verifyAndGetAllowedStudent(phone, studentId)
            if (student != null) {
                val current = repository.getStudentProfileDirect()
                val updated = (current ?: StudentProfileEntity(id = 1)).copy(
                    isLoggedIn = true,
                    studentId = student.studentId,
                    fullName = student.fullName,
                    email = "${student.fullName.replace(" ", "").lowercase()}@academy.lk",
                    phone = student.phone,
                    courseType = student.courseType
                )
                repository.updateStudentProfile(updated)
                _isAdminVerified.value = false // student cannot access admin panel
                addSimulatedNotification(
                    "🔑 Student Signed In",
                    "Ayiubowan! Welcome back, ${student.fullName}!",
                    "SUC"
                )
                navigateTo("MAIN")
                onResponse(true)
            } else {
                addSimulatedNotification(
                    "⚠️ Access Denied",
                    "Wrong Phone number or ID. Please contact Mr. Sheshan Perera.",
                    "ERR"
                )
                onResponse(false)
            }
        }
    }

    fun adminAddAllowedStudent(studentId: String, phone: String, fullName: String, courseType: String) {
        viewModelScope.launch {
            repository.insertAllowedStudent(studentId, phone, fullName, courseType)
            addSimulatedNotification(
                "🎓 Whitelist Student Added",
                "Registered student $fullName (ID: $studentId, Phone: $phone) successfully.",
                "SUC"
            )
        }
    }

    fun adminDeleteAllowedStudent(id: Int) {
        viewModelScope.launch {
            repository.deleteAllowedStudent(id)
            addSimulatedNotification(
                "🗑️ Student Removed",
                "Deleted student from the Academy Office Whitelist registry.",
                "ERR"
            )
        }
    }

    fun loginAcademicStaff(username: String, passcode: String): Boolean {
        return if (username == "sheshan456" && passcode == "7224") {
            _isAdminVerified.value = true
            viewModelScope.launch {
                val current = repository.getStudentProfileDirect()
                val updated = (current ?: StudentProfileEntity(id = 1)).copy(
                    isLoggedIn = true
                )
                repository.updateStudentProfile(updated)
            }
            addSimulatedNotification(
                "🛡️ Academy Office Unlocked",
                "Successfully verified staff credentials. Welcome Mr. Sheshan!",
                "SUC"
            )
            _currentTab.value = "ADMIN"
            _currentScreen.value = "MAIN"
            true
        } else {
            false
        }
    }

    fun logoutStudent() {
        viewModelScope.launch {
            val current = repository.getStudentProfileDirect()
            if (current != null) {
                repository.updateStudentProfile(current.copy(isLoggedIn = false))
            }
            _isAdminVerified.value = false
            navigateTo("LOGIN_REGISTER")
        }
    }

    fun updateProfileImage(uri: String) {
        viewModelScope.launch {
            val current = repository.getStudentProfileDirect()
            if (current != null) {
                repository.updateStudentProfile(current.copy(profileImageUri = uri))
            } else {
                repository.updateStudentProfile(StudentProfileEntity(id = 1, profileImageUri = uri))
            }
            addSimulatedNotification(
                "📸 Profile Updated",
                "Your profile avatar has been successfully updated!",
                "SUC"
            )
        }
    }

    // --- PDF Tute downloads & reading simulations ---
    fun simulatedDownloadTute(tute: TuteEntity) {
        viewModelScope.launch {
            // cycle trigger
            repository.updateTute(tute.copy(downloadStatus = "Downloading"))
            addSimulatedNotification(
                "⬇️ Downloading Material",
                "Downloading paper: ${tute.fileName} in background...",
                "DWN"
            )
            
            // Simulating short delaying of download 완료
            kotlinx.coroutines.delay(1800)
            repository.updateTute(tute.copy(downloadStatus = "Downloaded"))
            addSimulatedNotification(
                "✅ Study Material Ready",
                "${tute.title} downloaded successfully. You can open and read it offline.",
                "SUC"
            )
        }
    }

    fun openTuteForReading(tute: TuteEntity) {
        _readingTute.value = tute
    }

    fun closeTuteReader() {
        _readingTute.value = null
    }

    // --- Video watch simulations ---
    fun startPlayingRecording(recording: RecordingEntity) {
        _playingRecording.value = recording
    }

    fun recordProgress(progress: Float) {
        val current = _playingRecording.value ?: return
        viewModelScope.launch {
            val updated = current.copy(watchProgress = progress)
            repository.updateRecording(updated)
            _playingRecording.value = updated
        }
    }

    fun stopPlayingRecording() {
        val active = _playingRecording.value
        if (active != null && active.watchProgress >= 0.95f) {
            // Increments lessons watched
            viewModelScope.launch {
                val profile = repository.getStudentProfileDirect()
                if (profile != null) {
                    val updatedProfile = profile.copy(
                        totalLessonsWatched = profile.totalLessonsWatched + 1
                    )
                    repository.updateStudentProfile(updatedProfile)
                    addSimulatedNotification(
                        "📺 Lesson Completed!",
                        "You fully watched: '${active.title}'! Keep learning to boost your skills.",
                        "SUC"
                    )
                }
            }
        }
        _playingRecording.value = null
    }

    fun toggleFavoriteRecording(recording: RecordingEntity) {
        viewModelScope.launch {
            repository.updateRecording(recording.copy(isFavorite = !recording.isFavorite))
        }
    }

    // --- Payments ---
    fun simulateOnlinePayment(payment: PaymentHistoryEntity) {
        viewModelScope.launch {
            val updated = payment.copy(
                status = "PAID",
                paidDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            )
            repository.updatePayment(updated)
            addSimulatedNotification(
                "💳 Payment Confirmed",
                "LKR 2,500 has been verified for ${payment.month}. Digital admission card issued.",
                "SUC"
            )
        }
    }

    // --- Admin board controls (syncing back real data!) ---
    fun adminAddNewAnnouncement(title: String, content: String, isEmergency: Boolean) {
        viewModelScope.launch {
            repository.insertAnnouncement(title, content, isEmergency)
            addSimulatedNotification(
                "📢 Broadcast Released",
                "New Announcement: '$title' was published to all students.",
                "SUC"
            )
        }
    }

    fun adminAddNewZoomLink(title: String, time: String, link: String) {
        viewModelScope.launch {
            repository.insertZoomClass(title, time, link, "UPCOMING")
            addSimulatedNotification(
                "📅 Zoom Live Class Scheduled",
                "New class: '$title' scheduled for $time.",
                "SUC"
            )
        }
    }

    fun adminAddNewTute(title: String, category: String, fileName: String, fileSize: String) {
        viewModelScope.launch {
            repository.insertTute(title, category, fileName, fileSize)
            addSimulatedNotification(
                "📚 New PDF Tute Uploaded",
                "PDF: $title posted under $category.",
                "SUC"
            )
        }
    }

    fun adminDeleteRecording(id: Int) {
        viewModelScope.launch {
            repository.deleteRecording(id)
        }
    }

    fun adminDeleteAnnouncement(id: Int) {
        viewModelScope.launch {
            repository.deleteAnnouncement(id)
        }
    }

    // --- Real-time AI Chatbot using direct Gemini REST ---
    fun sendStudentChatMessage(userText: String) {
        if (userText.trim().isEmpty()) return

        viewModelScope.launch {
            // 1. Insert student message
            repository.insertChatMessage("USER", userText)
            _isAiLoading.value = true

            // 2. Format discussion history for Gemini endpoint
            val currentHistory = chatMessages.value
            val contentsList = mutableListOf<Content>()
            
            // Limit history turns context size for better response speeds
            val cappedHistory = currentHistory.takeLast(10)
            cappedHistory.forEach { m ->
                contentsList.add(
                    Content(parts = listOf(Part(text = "${if (m.sender == "USER") "Student" else "Lecturer Sheshan"}: ${m.message}")))
                )
            }
            contentsList.add(Content(parts = listOf(Part(text = "Student: $userText"))))

            val systemPromptContent = Content(
                parts = listOf(
                    Part(
                        text = "You are Lecturer Sheshan Perera, an expert English Lecturer, Spoken English Coach, and Communication Skills Educator who runs the 'Sheshan Perera English Academy' in Sri Lanka (branches in Ja-Ela, Gampaha, Maradana). " +
                               "Conduct yourself as a highly supportive, friendly, motivating lecturer. Use some brief, warm phrases like 'Ayiubowan!' or 'Excellent work!' occasionally. " +
                               "Answer their English questions, teach grammar, help them with sentences, translate from Sinhala to English when asked. Speak in simple, accessible English. Keep replies clear and under 3 paragraphs."
                    )
                )
            )

            val requestBody = GeminiRequest(
                contents = contentsList,
                systemInstruction = systemPromptContent
            )

            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    // Fallback simulated intelligent tutor replies if apiKey is unchanged
                    kotlinx.coroutines.delay(1200)
                    val localReply = generateSimulatedResponse(userText)
                    repository.insertChatMessage("AI", localReply)
                } else {
                    val response = GeminiClient.service.generateContent(apiKey, requestBody)
                    val replyText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (replyText != null) {
                        repository.insertChatMessage("AI", replyText)
                    } else {
                        repository.insertChatMessage("AI", "I'm listening closely, student! Let's try restructuring that sentence. Ask me anything about grammar or spoken structures.")
                    }
                }
            } catch (e: Exception) {
                // Friendly error fallback
                repository.insertChatMessage("AI", "Oh, I had a momentary network gap! Let me tell you about prepositions instead. In Sri Lanka, we say 'I go to Gampaha by bus' - use 'by' for transport modes. What transports do you use? 😊")
            } finally {
                _isAiLoading.value = false
            }
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            repository.clearChatHistory()
            repository.insertChatMessage("AI", "Chat cleared! Let's start a fresh practice session. What grammar rules shall we master today?")
        }
    }

    private fun generateSimulatedResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("hello") || lower.contains("hi") || lower.contains("ayubowan") || lower.contains("ayiubowan") -> {
                "Ayiubowan! 🙏 Lovely to focus on your studies today. I'm Sheshan Perera, your AI tutor. Ask me how to frame conversational expressions or practice standard spelling!"
            }
            lower.contains("sing") || lower.contains("sinhala") || lower.contains("translate") -> {
                "Certainly! Translations are a superb way to learn English. For example, 'Sindu kiyannada?' translates to 'Shall I sing a song?', and 'Mata therenawd?' is 'Do you understand me?'. Ask me another!"
            }
            lower.contains("grammar") || lower.contains("rule") -> {
                "Excellent focus! For English grammar, remember that active sentences show *who does the action* (e.g., 'Lecturer Sheshan teaches English'), while passive sentences highlight *what receives the action* ('English is taught by Lecturer Sheshan'). Try making one passive sentence now!"
            }
            lower.contains("spoken") || lower.contains("fluent") || lower.contains("talk") -> {
                "To speak English fluently, stand in front of a mirror at our Gampaha/Ja-Ela classes and speak daily for 5 minutes. Try to think directly in English instead of translating sentence-by-sentence. You've got this!"
            }
            else -> {
                "That's a very smart question. In English, we structure this clearly with Subject + Verb + Object. Ask me to explain any word or grammar tense, and I'll break it down for you step-by-step!"
            }
        }
    }

    // --- Interactive Quiz system logic ---
    fun resetQuiz() {
        _quizQuestions.value = listOf(
            QuizQuestion(
                id = 1,
                question = "When reporting direct speech: 'I am taking AL English classes,' she said, it becomes: She said that she ______ AL English classes.",
                options = listOf("takes", "is taking", "was taking", "has taken"),
                correctAnswerIndex = 2,
                explanation = "Direct present continuous ('am taking') shifts back to past continuous ('was taking') in indirect speech."
            ),
            QuizQuestion(
                id = 2,
                question = "Select the correct active voice of: 'The grammar paper was compiled by Mr. Sheshan.'",
                options = listOf(
                    "Mr. Sheshan compiles the grammar paper.",
                    "Mr. Sheshan compiled the grammar paper.",
                    "Mr. Sheshan was compiling the grammar paper.",
                    "The grammar paper compiles Mr. Sheshan."
                ),
                correctAnswerIndex = 1,
                explanation = "The passive sentence is in the simple past ('was compiled'), so the active voice must use past simple form 'compiled'."
            ),
            QuizQuestion(
                id = 3,
                question = "Which preposition goes here? 'We hold our physical English classes ______ Gampaha ______ Saturday mornings.'",
                options = listOf("at / in", "in / on", "on / at", "in / at"),
                correctAnswerIndex = 1,
                explanation = "Use 'in' for cities/locations (in Gampaha) and 'on' for specific calendar days (on Saturday mornings)."
            ),
            QuizQuestion(
                id = 4,
                question = "Choose the most appropriate synonym for 'Eloquent' (which describes high-scoring speakers):",
                options = listOf("Quiet", "Fluent and persuasive", "Extremely loud", "Hesitant"),
                correctAnswerIndex = 1,
                explanation = "'Eloquent' means expressing oneself fluently, clearly, and powerfully."
            )
        )
        _currentQuestionIndex.value = 0
        _quizScore.value = 0
        _quizCompleted.value = false
    }

    fun submitAnswer(selectedOptionIndex: Int) {
        val questions = _quizQuestions.value
        if (_currentQuestionIndex.value < questions.size) {
            val q = questions[_currentQuestionIndex.value]
            if (selectedOptionIndex == q.correctAnswerIndex) {
                _quizScore.value += 30 // 30 points per correct answer
            }
            
            if (_currentQuestionIndex.value + 1 < questions.size) {
                _currentQuestionIndex.value += 1
            } else {
                // Quiz completed trigger
                _quizCompleted.value = true
                viewModelScope.launch {
                    val profile = repository.getStudentProfileDirect()
                    if (profile != null && _quizScore.value > profile.quizHighScore) {
                        repository.updateStudentProfile(profile.copy(quizHighScore = _quizScore.value))
                        // update custom leaderboard
                        repository.insertLeaderboardStudent(
                            LeaderboardStudent(id = 2, fullName = profile.fullName + " (You)", points = 245 + _quizScore.value, rank = 2)
                        )
                    }
                    addSimulatedNotification(
                        "🏆 English Quiz Finished!",
                        "Completed! You scored ${_quizScore.value} points. View your certificate in the Dashboard!",
                        "SUC"
                    )
                }
            }
        }
    }
}

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String
)

package com.example.data.database

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AcademyRepository(private val academyDao: AcademyDao) {

    // --- Flows ---
    val studentProfile: Flow<StudentProfileEntity?> = academyDao.getStudentProfileFlow()
    val allowedStudents: Flow<List<AllowedStudentEntity>> = academyDao.getAllAllowedStudentsFlow()
    val announcements: Flow<List<AnnouncementEntity>> = academyDao.getAllAnnouncementsFlow()
    val zoomClasses: Flow<List<ZoomClassEntity>> = academyDao.getAllZoomClassesFlow()
    val recordings: Flow<List<RecordingEntity>> = academyDao.getAllRecordingsFlow()
    val tutes: Flow<List<TuteEntity>> = academyDao.getAllTutesFlow()
    val payments: Flow<List<PaymentHistoryEntity>> = academyDao.getAllPaymentsFlow()
    val chatMessages: Flow<List<ChatMsgEntity>> = academyDao.getAllChatMessagesFlow()
    val leaderboard: Flow<List<LeaderboardStudent>> = academyDao.getLeaderboardFlow()

    // --- Suspends ---
    suspend fun getStudentProfileDirect(): StudentProfileEntity? = withContext(Dispatchers.IO) {
        academyDao.getStudentProfileDirect()
    }

    suspend fun updateStudentProfile(profile: StudentProfileEntity) = withContext(Dispatchers.IO) {
        academyDao.updateStudentProfile(profile)
    }

    suspend fun insertAllowedStudent(studentId: String, phone: String, fullName: String, courseType: String = "General English") = withContext(Dispatchers.IO) {
        academyDao.insertAllowedStudent(
            AllowedStudentEntity(
                studentId = studentId,
                phone = phone,
                fullName = fullName,
                courseType = courseType
            )
        )
    }

    suspend fun deleteAllowedStudent(id: Int) = withContext(Dispatchers.IO) {
        academyDao.deleteAllowedStudentById(id)
    }

    suspend fun verifyAndGetAllowedStudent(phone: String, studentId: String): AllowedStudentEntity? = withContext(Dispatchers.IO) {
        academyDao.verifyAndGetAllowedStudent(phone, studentId)
    }

    suspend fun insertAnnouncement(title: String, content: String, isEmergency: Boolean = false) = withContext(Dispatchers.IO) {
        val dateStr = "Today, " + java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
        academyDao.insertAnnouncement(
            AnnouncementEntity(
                title = title,
                content = content,
                date = dateStr,
                isEmergency = isEmergency
            )
        )
    }

    suspend fun deleteAnnouncement(id: Int) = withContext(Dispatchers.IO) {
        academyDao.deleteAnnouncementById(id)
    }

    suspend fun insertZoomClass(title: String, scheduledTime: String, zoomLink: String, status: String) = withContext(Dispatchers.IO) {
        academyDao.insertZoomClass(
            ZoomClassEntity(
                title = title,
                scheduledTime = scheduledTime,
                zoomLink = zoomLink,
                status = status
            )
        )
    }

    suspend fun updateZoomClass(zoomClass: ZoomClassEntity) = withContext(Dispatchers.IO) {
        academyDao.updateZoomClass(zoomClass)
    }

    suspend fun deleteZoomClass(id: Int) = withContext(Dispatchers.IO) {
        academyDao.deleteZoomClassById(id)
    }

    suspend fun insertRecording(title: String, subject: String, videoUrl: String, duration: String) = withContext(Dispatchers.IO) {
        val dateStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        academyDao.insertRecording(
            RecordingEntity(
                title = title,
                subject = subject,
                videoUrl = videoUrl,
                duration = duration,
                uploadDate = dateStr
            )
        )
    }

    suspend fun updateRecording(recording: RecordingEntity) = withContext(Dispatchers.IO) {
        academyDao.updateRecording(recording)
    }

    suspend fun deleteRecording(id: Int) = withContext(Dispatchers.IO) {
        academyDao.deleteRecordingById(id)
    }

    suspend fun insertTute(title: String, type: String, fileName: String, fileSize: String) = withContext(Dispatchers.IO) {
        academyDao.insertTute(
            TuteEntity(
                title = title,
                type = type,
                fileName = fileName,
                fileSize = fileSize
            )
        )
    }

    suspend fun updateTute(tute: TuteEntity) = withContext(Dispatchers.IO) {
        academyDao.updateTute(tute)
    }

    suspend fun deleteTute(id: Int) = withContext(Dispatchers.IO) {
        academyDao.deleteTuteById(id)
    }

    suspend fun insertPayment(invoiceId: String, month: String, amount: String, status: String, paidDate: String = "-") = withContext(Dispatchers.IO) {
        academyDao.insertPayment(
            PaymentHistoryEntity(
                invoiceId = invoiceId,
                month = month,
                amount = amount,
                status = status,
                paidDate = paidDate
            )
        )
    }

    suspend fun updatePayment(payment: PaymentHistoryEntity) = withContext(Dispatchers.IO) {
        academyDao.updatePayment(payment)
    }

    suspend fun insertChatMessage(sender: String, message: String) = withContext(Dispatchers.IO) {
        academyDao.insertChatMessage(ChatMsgEntity(sender = sender, message = message))
    }

    suspend fun clearChatHistory() = withContext(Dispatchers.IO) {
        academyDao.clearChatMessages()
    }

    suspend fun insertLeaderboardStudent(student: LeaderboardStudent) = withContext(Dispatchers.IO) {
        academyDao.insertLeaderboardStudent(student)
    }

    // --- Pre-population system ---
    suspend fun initializeDefaultDataIfEmpty() = withContext(Dispatchers.IO) {
        // 1. Profile
        val currentProfile = academyDao.getStudentProfileDirect()
        if (currentProfile == null) {
            academyDao.updateStudentProfile(StudentProfileEntity(isLoggedIn = false)) // starts logged out so landing tabs are visible
        }

        // 2. Announcements
        val anyAnnouncement = academyDao.getAllAnnouncementsFlow().firstOrNull()?.isNotEmpty() ?: false
        if (!anyAnnouncement) {
            academyDao.insertAnnouncement(
                AnnouncementEntity(
                    title = "💥 Finals Practice Exam Schedule",
                    content = "The O/L English preparation paper has been released. The solution workbook will be discussed in our satup Zoom Class. Download all papers from the study portal.",
                    date = "Today, 08:30 AM",
                    isEmergency = true
                )
            )
            academyDao.insertAnnouncement(
                AnnouncementEntity(
                    title = "📝 Direct Speech Grammar Tute Released",
                    content = "Please download 'Direct & Indirect Speech Rules.pdf' and work on the given grammar practice set before Friday's online session.",
                    date = "Yesterday",
                    isEmergency = false
                )
            )
            academyDao.insertAnnouncement(
                AnnouncementEntity(
                    title = "💡 Special Spoken English Challenge",
                    content = "All Spoken English class participants should upload their 2-minute introductory presentation audio or review script to the Academy office board.",
                    date = "2 days ago",
                    isEmergency = false
                )
            )
        }

        // 3. Zoom classes
        val anyZoom = academyDao.getAllZoomClassesFlow().firstOrNull()?.isNotEmpty() ?: false
        if (!anyZoom) {
            academyDao.insertZoomClass(
                ZoomClassEntity(
                    title = "O/L English - Intensive Grammar Mastery (Active vs Passive)",
                    scheduledTime = "Every Saturday at 2:00 PM (Sri Lankan Time)",
                    durationMinutes = 120,
                    startCountdownSeconds = 360, // almost started for simulation
                    zoomLink = "https://zoom.us/j/94766203700",
                    status = "LIVE"
                )
            )
            academyDao.insertZoomClass(
                ZoomClassEntity(
                    title = "A/L General English - Premium Essay Writing Rules",
                    scheduledTime = "Every Sunday at 10:30 AM (Sri Lankan Time)",
                    durationMinutes = 150,
                    startCountdownSeconds = 75600, // Upcoming
                    zoomLink = "https://zoom.us/j/94766203700",
                    status = "UPCOMING"
                )
            )
            academyDao.insertZoomClass(
                ZoomClassEntity(
                    title = "Spoken English & Communication Skills Foundation",
                    scheduledTime = "Every Tuesday at 6:30 PM (Sri Lankan Time)",
                    durationMinutes = 90,
                    startCountdownSeconds = 259200,
                    zoomLink = "https://zoom.us/j/94766203700",
                    status = "UPCOMING"
                )
            )
        }

        // 4. Recordings
        val anyRecording = academyDao.getAllRecordingsFlow().firstOrNull()?.isNotEmpty() ?: false
        if (!anyRecording) {
            academyDao.insertRecording(
                RecordingEntity(
                    title = "Direct & Indirect Speech Complete Concept",
                    subject = "O/L English",
                    duration = "52 mins",
                    uploadDate = "2026-05-24",
                    watchProgress = 0.85f,
                    isFavorite = true
                )
            )
            academyDao.insertRecording(
                RecordingEntity(
                    title = "Advanced Essay Openers & Structural Connectors",
                    subject = "A/L English",
                    duration = "1 hr 15 mins",
                    uploadDate = "2026-05-20",
                    watchProgress = 0.20f,
                    isFavorite = false
                )
            )
            academyDao.insertRecording(
                RecordingEntity(
                    title = "Sinhala Words to English Idioms: Speak with Confidence",
                    subject = "Spoken English",
                    duration = "45 mins",
                    uploadDate = "2026-05-18",
                    watchProgress = 1.0f,
                    isFavorite = true
                )
            )
            academyDao.insertRecording(
                RecordingEntity(
                    title = "Formal Letter Layouts vs E-mail Formats",
                    subject = "O/L English",
                    duration = "38 mins",
                    uploadDate = "2026-05-15",
                    watchProgress = 0.0f,
                    isFavorite = false
                )
            )
        }

        // 5. Tutes
        val anyTute = academyDao.getAllTutesFlow().firstOrNull()?.isNotEmpty() ?: false
        if (!anyTute) {
            academyDao.insertTute(
                TuteEntity(
                    title = "Direct & Indirect Speech Complete Rulebook",
                    type = "Grammar Notes",
                    fileName = "Direct_Indirect_Speech_Rules.pdf",
                    fileSize = "2.1 MB"
                )
            )
            academyDao.insertTute(
                TuteEntity(
                    title = "Academy O/L 2026 Ultimate English Model Syllabus Paper",
                    type = "Model Paper",
                    fileName = "OL_2026_English_Master_Paper.pdf",
                    fileSize = "3.8 MB"
                )
            )
            academyDao.insertTute(
                TuteEntity(
                    title = "A/L Reading Comprehension Assignments Set A",
                    type = "Assignment",
                    fileName = "AL_Comprehension_Assignment.pdf",
                    fileSize = "1.4 MB"
                )
            )
            academyDao.insertTute(
                TuteEntity(
                    title = "G.C.E O/L 2024 Past Paper with Academy Key Answer Guide",
                    type = "Past Paper",
                    fileName = "OL_2024_Past_Paper_Academy_Answers.pdf",
                    fileSize = "4.2 MB"
                )
            )
        }

        // 6. Payments
        val anyPayment = academyDao.getAllPaymentsFlow().firstOrNull()?.isNotEmpty() ?: false
        if (!anyPayment) {
            academyDao.insertPayment(
                PaymentHistoryEntity(
                    invoiceId = "INV-2026-8941",
                    month = "June 2026 Course Fee",
                    amount = "LKR 2,500",
                    status = "PENDING",
                    paidDate = "-"
                )
            )
            academyDao.insertPayment(
                PaymentHistoryEntity(
                    invoiceId = "INV-2026-7821",
                    month = "May 2026 Course Fee",
                    amount = "LKR 2,500",
                    status = "PAID",
                    paidDate = "2026-05-01"
                )
            )
            academyDao.insertPayment(
                PaymentHistoryEntity(
                    invoiceId = "INV-2026-5381",
                    month = "April 2026 Registration Fee",
                    amount = "LKR 1,500",
                    status = "PAID",
                    paidDate = "2026-04-12"
                )
            )
        }

        // 7. Chat messages
        val anyChat = academyDao.getAllChatMessagesFlow().firstOrNull()?.isNotEmpty() ?: false
        if (!anyChat) {
            academyDao.insertChatMessage(
                ChatMsgEntity(
                    sender = "AI",
                    message = "Ayiubowan! 🙏 Welcome to Sheshan Perera Academy AI Assistant. I can help you practice English grammar, teach you new vocabulary words, or translate words and sentences from Sinhala to English! How would you like to build your fluency today?"
                )
            )
        }

        // 8. Leaderboard
        val anyLeader = academyDao.getLeaderboardFlow().firstOrNull()?.isNotEmpty() ?: false
        if (!anyLeader) {
            academyDao.insertLeaderboardStudent(LeaderboardStudent(fullName = "Sanduni Dilshani", points = 340, rank = 1))
            academyDao.insertLeaderboardStudent(LeaderboardStudent(fullName = "Student Account (You)", points = 245, rank = 2))
            academyDao.insertLeaderboardStudent(LeaderboardStudent(fullName = "Kavishka Perera", points = 190, rank = 3))
            academyDao.insertLeaderboardStudent(LeaderboardStudent(fullName = "Nisansala Senanayake", points = 145, rank = 4))
            academyDao.insertLeaderboardStudent(LeaderboardStudent(fullName = "Pathum Madusanka", points = 110, rank = 5))
            academyDao.insertLeaderboardStudent(LeaderboardStudent(fullName = "Tharusha Mendis", points = 95, rank = 6))
        }

        // 9. Allowed Students (Default Whitelist)
        val anyAllowed = academyDao.getAllAllowedStudentsFlow().firstOrNull()?.isNotEmpty() ?: false
        if (!anyAllowed) {
            academyDao.insertAllowedStudent(AllowedStudentEntity(studentId = "ST-101", phone = "0771234567", fullName = "Kasun Jayasuriya", courseType = "O/L English"))
            academyDao.insertAllowedStudent(AllowedStudentEntity(studentId = "ST-102", phone = "0777654321", fullName = "Minoli Perera", courseType = "A/L English"))
        }
    }
}

package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_profile")
data class StudentProfileEntity(
    @PrimaryKey val id: Int = 1,
    val studentId: String = "SP-2026-0941",
    val fullName: String = "Student Account",
    val email: String = "student@gmail.com",
    val phone: String = "+94 77 000 0000",
    val courseType: String = "General English", // "O/L English", "A/L English", etc.
    val attendancePercentage: Int = 88,
    val totalLessonsWatched: Int = 5,
    val quizHighScore: Int = 120,
    val enrollStatus: String = "Enrolled",
    val isLoggedIn: Boolean = false,
    val profileImageUri: String? = null
)

@Entity(tableName = "allowed_students")
data class AllowedStudentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentId: String,
    val phone: String,
    val fullName: String,
    val courseType: String = "General English"
)

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val date: String,
    val isEmergency: Boolean = false
)

@Entity(tableName = "zoom_classes")
data class ZoomClassEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val scheduledTime: String,
    val durationMinutes: Int = 120,
    val startCountdownSeconds: Long = 3600, // Duration in seconds until class
    val zoomLink: String = "https://zoom.us/j/94766203700",
    val status: String = "UPCOMING" // "LIVE", "UPCOMING", "COMPLETED"
)

@Entity(tableName = "recordings")
data class RecordingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val subject: String, // "O/L English", "A/L English", "Spoken English", "Communicative English"
    val videoUrl: String = "https://www.w3schools.com/html/mov_bbb.mp4", // Test stream URL
    val duration: String = "45 mins",
    val uploadDate: String,
    val watchProgress: Float = 0.0f,
    val isFavorite: Boolean = false
)

@Entity(tableName = "tutes")
data class TuteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val type: String, // "Grammar Notes", "Model Paper", "Assignment", "Past Paper"
    val fileUrl: String = "https://journals.sagepub.com/pb-assets/cms/SAGE/pdf/Grammar-Guide.pdf",
    val downloadStatus: String = "Not Downloaded", // "Not Downloaded", "Downloading", "Downloaded"
    val fileName: String,
    val fileSize: String = "2.4 MB"
)

@Entity(tableName = "payment_history")
data class PaymentHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val invoiceId: String,
    val month: String,
    val amount: String = "LKR 2,500",
    val status: String, // "PAID", "PENDING", "OVERDUE"
    val paidDate: String = "-"
)

@Entity(tableName = "chat_messages")
data class ChatMsgEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String, // "USER" or "AI"
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "leaderboard")
data class LeaderboardStudent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val points: Int,
    val rank: Int
)

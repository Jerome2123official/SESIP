package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AcademyDao {

    // --- Student Profile ---
    @Query("SELECT * FROM student_profile WHERE id = 1 LIMIT 1")
    fun getStudentProfileFlow(): Flow<StudentProfileEntity?>

    @Query("SELECT * FROM student_profile WHERE id = 1 LIMIT 1")
    suspend fun getStudentProfileDirect(): StudentProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateStudentProfile(profile: StudentProfileEntity)

    // --- Announcements ---
    @Query("SELECT * FROM announcements ORDER BY id DESC")
    fun getAllAnnouncementsFlow(): Flow<List<AnnouncementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: AnnouncementEntity)

    @Query("DELETE FROM announcements WHERE id = :id")
    suspend fun deleteAnnouncementById(id: Int)

    // --- Zoom Classes ---
    @Query("SELECT * FROM zoom_classes ORDER BY id ASC")
    fun getAllZoomClassesFlow(): Flow<List<ZoomClassEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertZoomClass(zoomClass: ZoomClassEntity)

    @Update
    suspend fun updateZoomClass(zoomClass: ZoomClassEntity)

    @Query("DELETE FROM zoom_classes WHERE id = :id")
    suspend fun deleteZoomClassById(id: Int)

    // --- Recordings ---
    @Query("SELECT * FROM recordings ORDER BY id DESC")
    fun getAllRecordingsFlow(): Flow<List<RecordingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecording(recording: RecordingEntity)

    @Update
    suspend fun updateRecording(recording: RecordingEntity)

    @Query("DELETE FROM recordings WHERE id = :id")
    suspend fun deleteRecordingById(id: Int)

    // --- Tutes ---
    @Query("SELECT * FROM tutes ORDER BY id DESC")
    fun getAllTutesFlow(): Flow<List<TuteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTute(tute: TuteEntity)

    @Update
    suspend fun updateTute(tute: TuteEntity)

    @Query("DELETE FROM tutes WHERE id = :id")
    suspend fun deleteTuteById(id: Int)

    // --- Payment History ---
    @Query("SELECT * FROM payment_history ORDER BY id DESC")
    fun getAllPaymentsFlow(): Flow<List<PaymentHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentHistoryEntity)

    @Update
    suspend fun updatePayment(payment: PaymentHistoryEntity)

    // --- Chat Messages ---
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatMessagesFlow(): Flow<List<ChatMsgEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMsgEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatMessages()

    // --- Leaderboard ---
    @Query("SELECT * FROM leaderboard ORDER BY points DESC")
    fun getLeaderboardFlow(): Flow<List<LeaderboardStudent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboardStudent(student: LeaderboardStudent)

    @Query("DELETE FROM leaderboard")
    suspend fun clearLeaderboard()

    // --- Allowed Students (Academic Whitelist) ---
    @Query("SELECT * FROM allowed_students ORDER BY id DESC")
    fun getAllAllowedStudentsFlow(): Flow<List<AllowedStudentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllowedStudent(student: AllowedStudentEntity)

    @Query("DELETE FROM allowed_students WHERE id = :id")
    suspend fun deleteAllowedStudentById(id: Int)

    @Query("SELECT * FROM allowed_students WHERE phone = :phone AND studentId = :studentId LIMIT 1")
    suspend fun verifyAndGetAllowedStudent(phone: String, studentId: String): AllowedStudentEntity?
}

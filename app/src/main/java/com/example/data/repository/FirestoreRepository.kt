package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.*
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreRepository(private val context: Context) {

    private val dbId: String = context.getString(R.string.firestore_database_id)
    val db: FirebaseFirestore = FirebaseFirestore.getInstance(dbId)
    val auth = Firebase.auth

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    // --- Users & Profiles ---
    suspend fun getUserProfile(userId: String): UserProfile? {
        return try {
            val doc = db.collection("users").document(userId).get().await()
            doc.toObject(UserProfile::class.java)
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "Error getting user profile: ${e.message}")
            null
        }
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        val uid = profile.userId.ifBlank { auth.currentUser?.uid ?: "user_default" }
        val toSave = profile.copy(userId = uid)
        db.collection("users").document(uid).set(toSave).await()
    }

    // --- Colleges ---
    suspend fun getCollege(collegeId: String = "COLLEGE_APEX_2026"): College? {
        return try {
            val doc = db.collection("colleges").document(collegeId).get().await()
            doc.toObject(College::class.java)
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "Error getting college: ${e.message}")
            null
        }
    }

    suspend fun registerCollege(college: College) {
        val cid = college.id.ifBlank { "COLLEGE_APEX_2026" }
        db.collection("colleges").document(cid).set(college.copy(id = cid)).await()
    }

    // --- Attendance ---
    fun observeAttendance(studentId: String = ""): Flow<List<AttendanceRecord>> = callbackFlow {
        val ref = db.collection("attendance")
        val query = if (studentId.isNotBlank()) {
            ref.whereEqualTo("studentId", studentId)
        } else {
            ref.orderBy("createdAt", Query.Direction.DESCENDING).limit(100)
        }

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("FirestoreRepo", "Attendance listener error: ${error.message}")
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val records = snapshot.toObjects(AttendanceRecord::class.java)
                trySend(records)
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun markAttendance(record: AttendanceRecord) {
        val newDoc = db.collection("attendance").document()
        val data = record.copy(id = newDoc.id)
        newDoc.set(data).await()
    }

    // --- Academic Records ---
    fun observeAcademicRecords(studentId: String = ""): Flow<List<AcademicRecord>> = callbackFlow {
        val ref = db.collection("academicRecords")
        val query = if (studentId.isNotBlank()) {
            ref.whereEqualTo("studentId", studentId)
        } else {
            ref.limit(50)
        }
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("FirestoreRepo", "Academic records error: ${error.message}")
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val records = snapshot.toObjects(AcademicRecord::class.java)
                trySend(records)
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun addAcademicRecord(record: AcademicRecord) {
        val doc = db.collection("academicRecords").document()
        doc.set(record.copy(id = doc.id)).await()
    }

    // --- Placement Drives ---
    fun observePlacementDrives(): Flow<List<PlacementDrive>> = callbackFlow {
        val listener = db.collection("placementDrives")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("FirestoreRepo", "Drives listener error: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.toObjects(PlacementDrive::class.java)
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun createPlacementDrive(drive: PlacementDrive) {
        val doc = db.collection("placementDrives").document()
        doc.set(drive.copy(id = doc.id)).await()
    }

    // --- Placement Applications ---
    fun observeApplications(studentId: String = ""): Flow<List<PlacementApplication>> = callbackFlow {
        val ref = db.collection("placementApplications")
        val query = if (studentId.isNotBlank()) {
            ref.whereEqualTo("studentId", studentId)
        } else {
            ref.limit(100)
        }
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("FirestoreRepo", "Applications listener error: ${error.message}")
                return@addSnapshotListener
            }
            if (snapshot != null) {
                trySend(snapshot.toObjects(PlacementApplication::class.java))
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun submitApplication(app: PlacementApplication) {
        val doc = db.collection("placementApplications").document()
        doc.set(app.copy(id = doc.id)).await()
    }

    suspend fun updateApplicationStatus(applicationId: String, newStatus: String, notes: String = "") {
        val updates = mutableMapOf<String, Any>(
            "status" to newStatus,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (notes.isNotBlank()) updates["notes"] = notes
        db.collection("placementApplications").document(applicationId).update(updates).await()
    }

    // --- Announcements ---
    fun observeAnnouncements(): Flow<List<Announcement>> = callbackFlow {
        val listener = db.collection("announcements")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(30)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("FirestoreRepo", "Announcements error: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(snapshot.toObjects(Announcement::class.java))
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun postAnnouncement(announcement: Announcement) {
        val doc = db.collection("announcements").document()
        doc.set(announcement.copy(id = doc.id)).await()
    }

    // --- Chat Messages ---
    fun observeChatMessages(channelId: String = "CSE-FINAL-YEAR"): Flow<List<ChatMessage>> = callbackFlow {
        val listener = db.collection("messages")
            .whereEqualTo("channelId", channelId)
            .limit(100)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("FirestoreRepo", "Messages error: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val msgs = snapshot.toObjects(ChatMessage::class.java)
                        .sortedBy { it.createdAt?.seconds ?: 0L }
                    trySend(msgs)
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun sendMessage(message: ChatMessage) {
        val doc = db.collection("messages").document()
        doc.set(message.copy(id = doc.id)).await()
    }

    // --- Career Guidance ---
    suspend fun getCareerGuidance(studentId: String): CareerGuidance? {
        return try {
            val doc = db.collection("careerGuidance").document(studentId).get().await()
            doc.toObject(CareerGuidance::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveCareerGuidance(guidance: CareerGuidance) {
        db.collection("careerGuidance").document(guidance.studentId).set(guidance).await()
    }

    // --- Initial Seed Generator (if collections are empty) ---
    suspend fun seedInitialDataIfEmpty() {
        try {
            val collegeDoc = db.collection("colleges").document("COLLEGE_APEX_2026").get().await()
            if (!collegeDoc.exists()) {
                val college = College(
                    id = "COLLEGE_APEX_2026",
                    name = "Apex Institute of Science & Technology",
                    code = "APEX-2026",
                    principalId = "principal_101",
                    principalName = "Dr. Arthur Vance, Ph.D.",
                    email = "principal@apextech.edu",
                    phone = "+1 (555) 019-2831",
                    address = "Campus City Blvd, Tech Hub",
                    totalStudents = 1450,
                    totalFaculty = 98
                )
                db.collection("colleges").document(college.id).set(college).await()

                // Seed default drives
                val drives = listOf(
                    PlacementDrive(
                        id = "drive_google_cloud",
                        companyName = "Google Cloud",
                        role = "Associate Cloud Solutions Architect",
                        packageLpa = 18.5,
                        minCgpa = 7.5,
                        allowedDepartments = "CSE, IT, ECE",
                        maxBacklogs = 0,
                        deadline = "2026-10-25",
                        driveDate = "2026-11-02",
                        testLink = "https://eval.careerbridge.edu/google-cloud-prep",
                        status = "ACTIVE",
                        description = "Enterprise infrastructure deployment, Kubernetes container orchestration, and serverless architectures."
                    ),
                    PlacementDrive(
                        id = "drive_microsoft",
                        companyName = "Microsoft",
                        role = "Software Development Engineer (SDE-1)",
                        packageLpa = 21.0,
                        minCgpa = 8.0,
                        allowedDepartments = "CSE, IT",
                        maxBacklogs = 0,
                        deadline = "2026-10-28",
                        driveDate = "2026-11-08",
                        testLink = "https://eval.careerbridge.edu/microsoft-sde",
                        status = "ACTIVE",
                        description = "Core backend distributed services, Azure integrations, scalable data pipelines."
                    ),
                    PlacementDrive(
                        id = "drive_amazon",
                        companyName = "Amazon AWS",
                        role = "Cloud Support Associate",
                        packageLpa = 14.5,
                        minCgpa = 7.0,
                        allowedDepartments = "CSE, IT, ECE, MECH",
                        maxBacklogs = 1,
                        deadline = "2026-10-30",
                        driveDate = "2026-11-12",
                        testLink = "https://eval.careerbridge.edu/amazon-aws",
                        status = "ACTIVE",
                        description = "Troubleshooting cloud VPC network topologies, automated scaling, database performance."
                    ),
                    PlacementDrive(
                        id = "drive_infosys",
                        companyName = "Infosys Springboard",
                        role = "Specialist Programmer",
                        packageLpa = 9.5,
                        minCgpa = 6.5,
                        allowedDepartments = "ALL BRANCHES",
                        maxBacklogs = 1,
                        deadline = "2026-11-05",
                        driveDate = "2026-11-15",
                        testLink = "https://eval.careerbridge.edu/infosys-sp",
                        status = "ACTIVE",
                        description = "Full stack application modernization, microservices, cloud deployment."
                    )
                )
                for (drive in drives) {
                    db.collection("placementDrives").document(drive.id).set(drive).await()
                }

                // Seed sample announcements
                val announcements = listOf(
                    Announcement(
                        id = "ann_1",
                        title = "Campus Recruitment Season 2026 Flagoff",
                        content = "Top tech recruiters including Google Cloud, Microsoft, and Amazon AWS drives are now open for final-year students. Verify eligibility and apply before the cut-off dates.",
                        category = "PLACEMENT",
                        authorName = "Placement Cell Director",
                        authorRole = "PLACEMENT_OFFICER",
                        dateString = "Oct 10, 2026"
                    ),
                    Announcement(
                        id = "ann_2",
                        title = "Semester 7 Internal Assessment II Timetable",
                        content = "Internal examinations will commence from October 22nd. Attendance requirement of 75% is strictly mandatory to appear for examinations.",
                        category = "EXAM",
                        authorName = "Dr. Arthur Vance",
                        authorRole = "PRINCIPAL",
                        dateString = "Oct 08, 2026"
                    )
                )
                for (ann in announcements) {
                    db.collection("announcements").document(ann.id).set(ann).await()
                }

                // Seed sample chat messages
                val messages = listOf(
                    ChatMessage(
                        id = "msg_1",
                        channelId = "CSE-FINAL-YEAR",
                        senderId = "teacher_101",
                        senderName = "Prof. Robert Langdon",
                        senderRole = "TEACHER",
                        content = "Welcome students to the Capstone Project & Placement Portal. Please review your AI Career Guidance report.",
                        timeFormatted = "09:30 AM"
                    ),
                    ChatMessage(
                        id = "msg_2",
                        channelId = "CSE-FINAL-YEAR",
                        senderId = "student_alex",
                        senderName = "Alex Mercer",
                        senderRole = "STUDENT",
                        content = "Sir, are we required to submit the Cloud Architecture assignment before Friday?",
                        timeFormatted = "09:42 AM"
                    ),
                    ChatMessage(
                        id = "msg_3",
                        channelId = "CSE-FINAL-YEAR",
                        senderId = "teacher_101",
                        senderName = "Prof. Robert Langdon",
                        senderRole = "TEACHER",
                        content = "Yes Alex, all submissions are via the Assignment tab by 11:59 PM Thursday.",
                        timeFormatted = "09:45 AM"
                    )
                )
                for (msg in messages) {
                    db.collection("messages").document(msg.id).set(msg).await()
                }

                // Seed sample student applications
                val sampleApp = PlacementApplication(
                    id = "app_google_alex",
                    driveId = "drive_google_cloud",
                    companyName = "Google Cloud",
                    role = "Associate Cloud Solutions Architect",
                    studentId = "student_alex",
                    studentName = "Alex Mercer",
                    department = "Computer Science & Engineering",
                    cgpa = 8.65,
                    status = "TECH_INTERVIEW",
                    notes = "Cleared Coding Round with 96% score. Technical Interview scheduled.",
                    interviewSchedule = "Oct 18, 2026 at 10:00 AM"
                )
                db.collection("placementApplications").document(sampleApp.id).set(sampleApp).await()
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "Seeding error: ${e.message}")
        }
    }
}

package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class UserRole(val displayName: String) {
    STUDENT("Student"),
    TEACHER("Teacher"),
    PRINCIPAL("Principal"),
    HOD("Head of Department"),
    PARENT("Parent"),
    PLACEMENT_OFFICER("Placement Officer"),
    COMPANY("Company Recruiter")
}

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = UserRole.STUDENT.name,
    val collegeId: String = "COLLEGE_APEX_2026",
    val collegeName: String = "Apex Institute of Science & Technology",
    val department: String = "Computer Science & Engineering",
    val rollNumber: String = "CS-2023-042",
    val phone: String = "+1 (555) 234-5678",
    val linkedStudentId: String = "",
    val companyName: String = "",
    val cgpa: Double = 8.65,
    val backlogs: Int = 0,
    val skills: String = "Kotlin, Python, Java, Cloud Computing, SQL",
    val avatarUrl: String = "",
    val status: String = "ACTIVE"
)

@IgnoreExtraProperties
data class College(
    @DocumentId val id: String = "",
    val name: String = "Apex Institute of Science & Technology",
    val code: String = "APEX-2026",
    val principalId: String = "",
    val principalName: String = "Dr. Arthur Vance",
    val email: String = "principal@apextech.edu",
    val phone: String = "+1 (555) 019-2831",
    val address: String = "Academic City, Campus Drive 42",
    val totalStudents: Int = 1450,
    val totalFaculty: Int = 98,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class AttendanceRecord(
    @DocumentId val id: String = "",
    val collegeId: String = "COLLEGE_APEX_2026",
    val department: String = "Computer Science & Engineering",
    val studentId: String = "",
    val studentName: String = "",
    val subject: String = "Advanced Cloud Architectures",
    val date: String = "2026-10-10",
    val status: String = "PRESENT", // PRESENT, ABSENT, LATE
    val markedBy: String = "Prof. Robert Langdon",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class AcademicRecord(
    @DocumentId val id: String = "",
    val collegeId: String = "COLLEGE_APEX_2026",
    val studentId: String = "",
    val studentName: String = "",
    val semester: String = "Semester 7",
    val subject: String = "Distributed Systems",
    val internalMarks: Double = 28.0, // out of 30
    val externalMarks: Double = 64.0, // out of 70
    val totalMarks: Double = 92.0,
    val grade: String = "A+",
    val cgpa: Double = 8.8,
    val hasBacklog: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class PlacementDrive(
    @DocumentId val id: String = "",
    val collegeId: String = "COLLEGE_APEX_2026",
    val companyId: String = "",
    val companyName: String = "Google Cloud",
    val role: String = "Associate Cloud Solutions Architect",
    val packageLpa: Double = 18.5,
    val minCgpa: Double = 7.5,
    val allowedDepartments: String = "CSE, IT, ECE",
    val maxBacklogs: Int = 0,
    val deadline: String = "2026-10-25",
    val driveDate: String = "2026-11-02",
    val testLink: String = "https://eval.careerbridge.edu/test/gc-2026",
    val status: String = "ACTIVE",
    val description: String = "Designing resilient enterprise cloud architectures, API integration, and machine learning pipeline support.",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class PlacementApplication(
    @DocumentId val id: String = "",
    val driveId: String = "",
    val companyName: String = "",
    val role: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val department: String = "",
    val cgpa: Double = 0.0,
    val status: String = "APPLIED", // APPLIED, SHORTLISTED, EXAM_QUALIFIED, TECH_INTERVIEW, HR_INTERVIEW, SELECTED, REJECTED
    val notes: String = "Aptitude score cleared with 94%",
    val interviewSchedule: String = "Oct 18, 2026 at 10:00 AM",
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Announcement(
    @DocumentId val id: String = "",
    val collegeId: String = "COLLEGE_APEX_2026",
    val authorName: String = "Dean of Academics",
    val authorRole: String = "PRINCIPAL",
    val title: String = "Mid-Term Examination Schedule Released",
    val content: String = "The semester 7 mid-term timetable is published. Check individual subject schedules and assignment submission deadlines.",
    val category: String = "EXAM", // EXAM, PLACEMENT, EVENT, ACADEMIC
    val dateString: String = "Oct 10, 2026",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ChatMessage(
    @DocumentId val id: String = "",
    val channelId: String = "CSE-FINAL-YEAR",
    val senderId: String = "",
    val senderName: String = "",
    val senderRole: String = "TEACHER",
    val content: String = "Please review chapter 4 on consensus protocols before tomorrow's seminar.",
    val attachmentName: String = "",
    val timeFormatted: String = "10:45 AM",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CareerGuidance(
    @DocumentId val id: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val successScore: Int = 89,
    val riskLevel: String = "LOW", // LOW, MODERATE, HIGH
    val readinessPercentage: Int = 88,
    val suggestedRoles: String = "Cloud Engineer, Backend System Architect, Site Reliability Engineer",
    val skillGaps: String = "Docker & Kubernetes container orchestration, Distributed Caching (Redis), System Design mock interview",
    val actionablePlan: String = "1. Complete 15 medium LeetCode tree & graph problems.\n2. Containerize your Capstone project with Docker.\n3. Take 2 mock technical interviews on CareerBridge.",
    val rationale: String = "High CGPA (8.65) and 0 backlogs indicate strong academic core. Prior project in Kotlin and SQL aligns exceptionally with enterprise software roles.",
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class StudyMaterial(
    val id: String,
    val title: String,
    val subject: String,
    val author: String,
    val fileType: String = "PDF",
    val fileSize: String = "4.2 MB",
    val uploadDate: String = "Oct 08, 2026"
)

data class AssignmentItem(
    val id: String,
    val title: String,
    val subject: String,
    val dueDate: String,
    val status: String, // PENDING, SUBMITTED, GRADED
    val score: String = "Not Graded"
)

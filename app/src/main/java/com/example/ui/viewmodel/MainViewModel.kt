package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.CareerAiEngine
import com.example.data.model.*
import com.example.data.repository.FirestoreRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MainUiState(
    val currentRole: UserRole = UserRole.STUDENT,
    val userProfile: UserProfile = UserProfile(
        userId = "student_alex",
        name = "Alex Mercer",
        email = "alex.mercer@apextech.edu",
        role = UserRole.STUDENT.name,
        collegeId = "COLLEGE_APEX_2026",
        collegeName = "Apex Institute of Science & Technology",
        department = "Computer Science & Engineering",
        rollNumber = "CS-2023-042",
        cgpa = 8.65,
        backlogs = 0,
        skills = "Kotlin, Cloud Computing, Python, SQL, Docker",
        status = "ACTIVE"
    ),
    val college: College = College(),
    val attendanceList: List<AttendanceRecord> = emptyList(),
    val academicRecords: List<AcademicRecord> = emptyList(),
    val placementDrives: List<PlacementDrive> = emptyList(),
    val placementApplications: List<PlacementApplication> = emptyList(),
    val announcements: List<Announcement> = emptyList(),
    val chatMessages: List<ChatMessage> = emptyList(),
    val careerGuidance: CareerGuidance? = null,
    val isLoading: Boolean = false,
    val feedbackMessage: String? = null,
    val activeChannel: String = "CSE-FINAL-YEAR"
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repository = FirestoreRepository(application.applicationContext)
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        bootstrapData()
    }

    private fun bootstrapData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.seedInitialDataIfEmpty()

            // Fetch college
            val college = repository.getCollege()
            if (college != null) {
                _uiState.value = _uiState.value.copy(college = college)
            }

            // Generate initial career guidance
            val guidance = CareerAiEngine.generateGuidance(_uiState.value.userProfile)
            _uiState.value = _uiState.value.copy(careerGuidance = guidance)

            _uiState.value = _uiState.value.copy(isLoading = false)

            // Start listening to real-time collections
            observeLiveStreams()
        }
    }

    private fun observeLiveStreams() {
        viewModelScope.launch {
            repository.observeAttendance().collect { list ->
                _uiState.value = _uiState.value.copy(attendanceList = list)
            }
        }
        viewModelScope.launch {
            repository.observePlacementDrives().collect { drives ->
                _uiState.value = _uiState.value.copy(placementDrives = drives)
            }
        }
        viewModelScope.launch {
            repository.observeApplications().collect { apps ->
                _uiState.value = _uiState.value.copy(placementApplications = apps)
            }
        }
        viewModelScope.launch {
            repository.observeAnnouncements().collect { ann ->
                _uiState.value = _uiState.value.copy(announcements = ann)
            }
        }
        viewModelScope.launch {
            repository.observeChatMessages(_uiState.value.activeChannel).collect { msgs ->
                _uiState.value = _uiState.value.copy(chatMessages = msgs)
            }
        }
    }

    fun selectRole(role: UserRole) {
        val updatedProfile = when (role) {
            UserRole.STUDENT -> UserProfile(
                userId = "student_alex",
                name = "Alex Mercer",
                email = "alex.mercer@apextech.edu",
                role = UserRole.STUDENT.name,
                department = "Computer Science & Engineering",
                rollNumber = "CS-2023-042",
                cgpa = 8.65,
                backlogs = 0,
                skills = "Kotlin, Cloud Computing, Python, SQL, Docker"
            )
            UserRole.TEACHER -> UserProfile(
                userId = "teacher_langdon",
                name = "Prof. Robert Langdon",
                email = "robert.langdon@apextech.edu",
                role = UserRole.TEACHER.name,
                department = "Computer Science & Engineering",
                rollNumber = "FAC-CS-108",
                phone = "+1 (555) 987-6543"
            )
            UserRole.PRINCIPAL -> UserProfile(
                userId = "principal_vance",
                name = "Dr. Arthur Vance, Ph.D.",
                email = "principal@apextech.edu",
                role = UserRole.PRINCIPAL.name,
                department = "College Administration",
                rollNumber = "ADMIN-001"
            )
            UserRole.HOD -> UserProfile(
                userId = "hod_elena",
                name = "Dr. Elena Rostova",
                email = "elena.hod@apextech.edu",
                role = UserRole.HOD.name,
                department = "Computer Science & Engineering",
                rollNumber = "HOD-CSE-01"
            )
            UserRole.PARENT -> UserProfile(
                userId = "parent_mercer",
                name = "Thomas Mercer",
                email = "thomas.mercer@gmail.com",
                role = UserRole.PARENT.name,
                linkedStudentId = "student_alex",
                phone = "+1 (555) 432-1098"
            )
            UserRole.PLACEMENT_OFFICER -> UserProfile(
                userId = "placement_sharma",
                name = "David K. Sharma",
                email = "placement.director@apextech.edu",
                role = UserRole.PLACEMENT_OFFICER.name,
                department = "Corporate Relations & Placements",
                rollNumber = "TPO-01"
            )
            UserRole.COMPANY -> UserProfile(
                userId = "recruiter_sarah",
                name = "Sarah Jenkins",
                email = "sarah.jenkins@googlecloud.com",
                role = UserRole.COMPANY.name,
                companyName = "Google Cloud Campus Team"
            )
        }

        _uiState.value = _uiState.value.copy(
            currentRole = role,
            userProfile = updatedProfile,
            feedbackMessage = "Switched to ${role.displayName} portal"
        )
    }

    fun registerCollegeAndPrincipal(
        collegeName: String,
        collegeCode: String,
        principalName: String,
        email: String,
        phone: String
    ) {
        viewModelScope.launch {
            val newCollege = College(
                id = "COLLEGE_${collegeCode.uppercase()}",
                name = collegeName,
                code = collegeCode.uppercase(),
                principalName = principalName,
                email = email,
                phone = phone
            )
            repository.registerCollege(newCollege)
            _uiState.value = _uiState.value.copy(
                college = newCollege,
                feedbackMessage = "College $collegeName registered successfully!"
            )
            selectRole(UserRole.PRINCIPAL)
        }
    }

    fun markStudentAttendance(
        studentName: String,
        studentId: String,
        subject: String,
        status: String
    ) {
        viewModelScope.launch {
            val record = AttendanceRecord(
                studentId = studentId,
                studentName = studentName,
                subject = subject,
                status = status,
                markedBy = _uiState.value.userProfile.name
            )
            repository.markAttendance(record)
            _uiState.value = _uiState.value.copy(feedbackMessage = "Marked attendance: $studentName ($status)")
        }
    }

    fun applyForPlacementDrive(drive: PlacementDrive) {
        viewModelScope.launch {
            val profile = _uiState.value.userProfile
            // Check if already applied
            val existing = _uiState.value.placementApplications.any {
                it.driveId == drive.id && it.studentId == profile.userId
            }
            if (existing) {
                _uiState.value = _uiState.value.copy(feedbackMessage = "Already applied to ${drive.companyName}")
                return@launch
            }

            val app = PlacementApplication(
                driveId = drive.id,
                companyName = drive.companyName,
                role = drive.role,
                studentId = profile.userId,
                studentName = profile.name,
                department = profile.department,
                cgpa = profile.cgpa,
                status = "APPLIED",
                notes = "Eligible candidate application submitted with resume."
            )
            repository.submitApplication(app)
            _uiState.value = _uiState.value.copy(feedbackMessage = "Applied successfully to ${drive.companyName}!")
        }
    }

    fun advanceApplicationStatus(applicationId: String, newStage: String, notes: String = "") {
        viewModelScope.launch {
            repository.updateApplicationStatus(applicationId, newStage, notes)
            _uiState.value = _uiState.value.copy(feedbackMessage = "Updated candidate status to: $newStage")
        }
    }

    fun createNewPlacementDrive(
        companyName: String,
        role: String,
        packageLpa: Double,
        minCgpa: Double,
        departments: String,
        deadline: String,
        description: String
    ) {
        viewModelScope.launch {
            val drive = PlacementDrive(
                companyName = companyName,
                role = role,
                packageLpa = packageLpa,
                minCgpa = minCgpa,
                allowedDepartments = departments,
                deadline = deadline,
                description = description
            )
            repository.createPlacementDrive(drive)
            _uiState.value = _uiState.value.copy(feedbackMessage = "Published new drive: $companyName")
        }
    }

    fun postNewAnnouncement(title: String, content: String, category: String) {
        viewModelScope.launch {
            val ann = Announcement(
                title = title,
                content = content,
                category = category,
                authorName = _uiState.value.userProfile.name,
                authorRole = _uiState.value.currentRole.name
            )
            repository.postAnnouncement(ann)
            _uiState.value = _uiState.value.copy(feedbackMessage = "Announcement posted!")
        }
    }

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val profile = _uiState.value.userProfile
            val msg = ChatMessage(
                channelId = _uiState.value.activeChannel,
                senderId = profile.userId,
                senderName = profile.name,
                senderRole = profile.role,
                content = text.trim()
            )
            repository.sendMessage(msg)
        }
    }

    fun runAiCareerGuidance() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val guidance = CareerAiEngine.generateGuidance(_uiState.value.userProfile)
            repository.saveCareerGuidance(guidance)
            _uiState.value = _uiState.value.copy(
                careerGuidance = guidance,
                isLoading = false,
                feedbackMessage = "AI Career Guidance report refreshed!"
            )
        }
    }

    fun clearFeedback() {
        _uiState.value = _uiState.value.copy(feedbackMessage = null)
    }
}

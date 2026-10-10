package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.AttendanceRecord
import com.example.ui.components.MetricStatCard
import com.example.ui.components.StatusBadge
import com.example.ui.viewmodel.MainUiState
import com.example.ui.viewmodel.MainViewModel

@Composable
fun TeacherDashboard(
    uiState: MainUiState,
    viewModel: MainViewModel
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Class Roster & Attendance", "Enter Marks", "Post Announcement", "Class Chat")

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("teacher_tab_$index")
                )
            }
        }

        when (selectedTab) {
            0 -> TeacherAttendanceTab(uiState, viewModel)
            1 -> TeacherMarksTab(viewModel)
            2 -> TeacherAnnouncementTab(viewModel)
            3 -> TeacherChatTab(uiState, viewModel)
        }
    }
}

@Composable
private fun TeacherAttendanceTab(
    uiState: MainUiState,
    viewModel: MainViewModel
) {
    val sampleStudents = listOf(
        "CS-041" to "Emma Watson",
        "CS-042" to "Alex Mercer",
        "CS-043" to "Carlos Rivera",
        "CS-044" to "Priya Patel",
        "CS-045" to "David Kim",
        "CS-046" to "Sophia Chen"
    )

    var selectedSubject by remember { mutableStateOf("Advanced Cloud Architectures") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Attendance Management",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Department: CSE • Class: Semester 7 Section A • Subject: $selectedSubject",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Student List (Today's Lecture)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Date: Oct 10, 2026",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        items(sampleStudents) { (roll, name) ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Text(text = roll, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                viewModel.markStudentAttendance(name, roll, selectedSubject, "PRESENT")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                            modifier = Modifier.testTag("btn_present_$roll")
                        ) {
                            Text("Present", style = MaterialTheme.typography.labelSmall)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.markStudentAttendance(name, roll, selectedSubject, "ABSENT")
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                            modifier = Modifier.testTag("btn_absent_$roll")
                        ) {
                            Text("Absent", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        if (uiState.attendanceList.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Recently Logged to Firestore (${uiState.attendanceList.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            items(uiState.attendanceList.take(4)) { record ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${record.studentName} (${record.subject})",
                        style = MaterialTheme.typography.bodySmall
                    )
                    StatusBadge(status = record.status)
                }
            }
        }
    }
}

@Composable
private fun TeacherMarksTab(viewModel: MainViewModel) {
    var studentName by remember { mutableStateOf("Alex Mercer") }
    var semester by remember { mutableStateOf("Semester 7") }
    var subject by remember { mutableStateOf("Advanced Cloud Architectures") }
    var internalMarks by remember { mutableStateOf("28") }
    var externalMarks by remember { mutableStateOf("64") }
    var submitted by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Enter Internal & External Exam Marks",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Recorded marks automatically sync with Student, Parent, and HOD portals.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = studentName,
                        onValueChange = { studentName = it },
                        label = { Text("Student Name") },
                        modifier = Modifier.fillMaxWidth().testTag("input_marks_student_name")
                    )

                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Subject") },
                        modifier = Modifier.fillMaxWidth().testTag("input_marks_subject")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = internalMarks,
                            onValueChange = { internalMarks = it },
                            label = { Text("Internal Marks (out of 30)") },
                            modifier = Modifier.weight(1f).testTag("input_internal_marks")
                        )
                        OutlinedTextField(
                            value = externalMarks,
                            onValueChange = { externalMarks = it },
                            label = { Text("External Marks (out of 70)") },
                            modifier = Modifier.weight(1f).testTag("input_external_marks")
                        )
                    }

                    Button(
                        onClick = {
                            submitted = true
                        },
                        modifier = Modifier.fillMaxWidth().testTag("btn_save_marks")
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save & Publish Marks to Firestore")
                    }

                    if (submitted) {
                        Text(
                            text = "Marks for $studentName in $subject recorded and synchronized!",
                            color = Color(0xFF15803D),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TeacherAnnouncementTab(viewModel: MainViewModel) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("ACADEMIC") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Publish Department / Class Announcement",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Announcement Headline") },
            placeholder = { Text("e.g. Lab Project Submission Guidelines") },
            modifier = Modifier.fillMaxWidth().testTag("input_teacher_ann_title")
        )

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Announcement Details") },
            placeholder = { Text("Write full instructions for students...") },
            modifier = Modifier.fillMaxWidth().height(120.dp).testTag("input_teacher_ann_content")
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("ACADEMIC", "EXAM", "EVENT").forEach { cat ->
                FilterChip(
                    selected = category == cat,
                    onClick = { category = cat },
                    label = { Text(cat) }
                )
            }
        }

        Button(
            onClick = {
                if (title.isNotBlank() && content.isNotBlank()) {
                    viewModel.postNewAnnouncement(title, content, category)
                    title = ""
                    content = ""
                }
            },
            modifier = Modifier.fillMaxWidth().testTag("btn_teacher_publish_ann")
        ) {
            Icon(imageVector = Icons.Default.Campaign, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Broadcast Notice")
        }
    }
}

@Composable
private fun TeacherChatTab(
    uiState: MainUiState,
    viewModel: MainViewModel
) {
    StudentDiscussionTab(uiState = uiState, viewModel = viewModel)
}

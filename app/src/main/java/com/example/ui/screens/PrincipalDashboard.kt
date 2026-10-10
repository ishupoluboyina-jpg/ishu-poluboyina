package com.example.ui.screens

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
import com.example.ui.components.MetricStatCard
import com.example.ui.components.StatusBadge
import com.example.ui.viewmodel.MainUiState
import com.example.ui.viewmodel.MainViewModel

@Composable
fun PrincipalDashboard(
    uiState: MainUiState,
    viewModel: MainViewModel
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Institution Overview", "Departments", "Broadcast Notice", "Audit Logs")

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
                    modifier = Modifier.testTag("principal_tab_$index")
                )
            }
        }

        when (selectedTab) {
            0 -> PrincipalOverviewTab(uiState)
            1 -> PrincipalDepartmentsTab()
            2 -> PrincipalBroadcastTab(viewModel)
            3 -> PrincipalAuditLogsTab()
        }
    }
}

@Composable
private fun PrincipalOverviewTab(uiState: MainUiState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = uiState.college.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Code: ${uiState.college.code} • Principal: ${uiState.college.principalName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        StatusBadge(status = "ACTIVE")
                    }
                }
            }
        }

        // Executive Institutional Metrics
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricStatCard(
                        title = "Total Enrolled",
                        value = "${uiState.college.totalStudents}",
                        subtitle = "Across 6 Departments",
                        icon = Icons.Default.Groups,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "Faculty Strength",
                        value = "${uiState.college.totalFaculty}",
                        subtitle = "14:1 Student-Faculty Ratio",
                        icon = Icons.Default.School,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricStatCard(
                        title = "Placement Rate",
                        value = "87.4%",
                        subtitle = "Target: 90% by Dec",
                        icon = Icons.Default.Work,
                        containerColor = Color(0xFFDCFCE7),
                        contentColor = Color(0xFF15803D),
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "Campus Attendance",
                        value = "89.2%",
                        subtitle = "Healthy Compliance",
                        icon = Icons.Default.CheckCircle,
                        containerColor = Color(0xFFFEF3C7),
                        contentColor = Color(0xFFB45309),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Active Placement Drives Summary
        item {
            Text(
                text = "Live Campus Recruitment Drives (${uiState.placementDrives.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(uiState.placementDrives) { drive ->
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
                    Column {
                        Text(text = drive.companyName, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${drive.role} • CTC: ${drive.packageLpa} LPA",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    StatusBadge(status = drive.status)
                }
            }
        }
    }
}

@Composable
private fun PrincipalDepartmentsTab() {
    val depts = listOf(
        Triple("Computer Science & Engineering", "Dr. Elena Rostova", "420 Students • 94% Placed"),
        Triple("Information Technology", "Dr. Marcus Vance", "280 Students • 91% Placed"),
        Triple("Electronics & Communication", "Prof. Sunita Rao", "310 Students • 84% Placed"),
        Triple("Mechanical Engineering", "Dr. James MacIntyre", "240 Students • 79% Placed"),
        Triple("Civil Engineering", "Prof. Tariq Al-Mansoor", "200 Students • 76% Placed")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Academic Departments & Appointed HODs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(depts) { (name, hod, stats) ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Head of Department: $hod", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = stats, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}

@Composable
private fun PrincipalBroadcastTab(viewModel: MainViewModel) {
    TeacherAnnouncementTab(viewModel = viewModel)
}

@Composable
private fun PrincipalAuditLogsTab() {
    val sampleLogs = listOf(
        "Oct 10, 2026 10:14:02 AM" to "Placement Drive created: Google Cloud by TPO Sharma",
        "Oct 10, 2026 09:30:15 AM" to "Attendance marked: CSE-701 by Prof. Robert Langdon",
        "Oct 09, 2026 04:22:11 PM" to "HOD Elena Rostova generated Semester 7 Eligibility Report",
        "Oct 08, 2026 11:05:40 AM" to "Institutional announcement published: Mid-Term Examination Schedule"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Administrative Security & Audit Log",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tamper-evident record of privileged actions across all roles.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(sampleLogs) { (time, log) ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = log, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

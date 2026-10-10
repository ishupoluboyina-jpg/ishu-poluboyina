package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
fun HodDashboard(
    uiState: MainUiState,
    viewModel: MainViewModel
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Department Overview", "Faculty & Subjects", "At-Risk Support", "Department Notices")

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
                    modifier = Modifier.testTag("hod_tab_$index")
                )
            }
        }

        when (selectedTab) {
            0 -> HodOverviewTab(uiState)
            1 -> HodFacultyTab()
            2 -> HodAtRiskTab()
            3 -> TeacherAnnouncementTab(viewModel = viewModel)
        }
    }
}

@Composable
private fun HodOverviewTab(uiState: MainUiState) {
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
                    Text(
                        text = "Department of Computer Science & Engineering",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "HOD: Dr. Elena Rostova • College: ${uiState.college.code}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricStatCard(
                    title = "Students",
                    value = "420",
                    subtitle = "Across 4 Semesters",
                    icon = Icons.Default.People,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "Avg CGPA",
                    value = "8.42",
                    subtitle = "Rank 1 in College",
                    icon = Icons.Default.Grade,
                    containerColor = Color(0xFFDCFCE7),
                    contentColor = Color(0xFF15803D),
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "Attendance",
                    value = "92.4%",
                    subtitle = "Compliant",
                    icon = Icons.Default.CheckCircle,
                    containerColor = Color(0xFFFEF3C7),
                    contentColor = Color(0xFFB45309),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Text(
                text = "Placement Conversion in Department",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Final Year CSE Batch 2026: 120 Students", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• 104 students shortlisted across Google Cloud, Microsoft, Amazon AWS\n• Average Package: 14.8 LPA\n• Top Offered: 21.0 LPA (Microsoft)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun HodFacultyTab() {
    val sampleFaculty = listOf(
        Triple("Prof. Robert Langdon", "Advanced Cloud Architectures", "14 Yrs Exp • 4 Papers"),
        Triple("Dr. Sunita Rao", "Distributed Systems & Consensus", "11 Yrs Exp • 6 Papers"),
        Triple("Prof. Alan Turing Jr.", "Machine Learning & Big Data", "8 Yrs Exp • 2 Patents"),
        Triple("Dr. Grace Hopper-Smith", "Information Security & Cryptography", "16 Yrs Exp • IEEE Chair")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Assigned Department Faculty",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(sampleFaculty) { (name, subj, cred) ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = name, fontWeight = FontWeight.Bold)
                    Text(text = "Teaching: $subj", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    Text(text = cred, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun HodAtRiskTab() {
    val atRiskStudents = listOf(
        Triple("Jordan Baker (CS-088)", "Attendance: 68% (Below 75%)", "Action: Parent notified, counselor session booked"),
        Triple("Liam O'Connor (CS-112)", "2 Backlogs in Sem 5 Math", "Action: Remedial evening coaching assigned"),
        Triple("Zoe Saldana (CS-094)", "CGPA: 5.9 (Risk for placement)", "Action: AI Career guidance improvement plan initiated")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Students Requiring Academic Support (Intervention List)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(atRiskStudents) { (name, cause, action) ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        StatusBadge(status = "HIGH")
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = cause, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = action, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

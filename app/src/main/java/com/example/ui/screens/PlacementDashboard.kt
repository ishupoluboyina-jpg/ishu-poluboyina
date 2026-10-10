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
import com.example.data.model.PlacementApplication
import com.example.data.model.PlacementDrive
import com.example.ui.components.MetricStatCard
import com.example.ui.components.StatusBadge
import com.example.ui.viewmodel.MainUiState
import com.example.ui.viewmodel.MainViewModel

@Composable
fun PlacementDashboard(
    uiState: MainUiState,
    viewModel: MainViewModel
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Drives & Offers", "Candidate Pipeline", "Create New Drive", "Analytics")

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
                    modifier = Modifier.testTag("tpo_tab_$index")
                )
            }
        }

        when (selectedTab) {
            0 -> PlacementDrivesListTab(uiState)
            1 -> PlacementPipelineTab(uiState, viewModel)
            2 -> CreatePlacementDriveTab(viewModel)
            3 -> PlacementAnalyticsTab()
        }
    }
}

@Composable
private fun PlacementDrivesListTab(uiState: MainUiState) {
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
                        text = "Campus Placement Cell Overview",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${uiState.placementDrives.size} Active Recruitment Drives • 120 Final-Year Students",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        items(uiState.placementDrives) { drive ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = drive.companyName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "${drive.packageLpa} LPA",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = drive.role, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Min CGPA: ${drive.minCgpa} • Eligible: ${drive.allowedDepartments}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Drive Date: ${drive.driveDate} • Deadline: ${drive.deadline}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
private fun PlacementPipelineTab(
    uiState: MainUiState,
    viewModel: MainViewModel
) {
    val stages = listOf("APPLIED", "SHORTLISTED", "EXAM_QUALIFIED", "TECH_INTERVIEW", "HR_INTERVIEW", "SELECTED", "REJECTED")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Candidate Recruitment Pipeline (${uiState.placementApplications.size})",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Advance candidate stages or update recruiter interview notes.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(uiState.placementApplications) { app ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = app.studentName, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${app.companyName} • ${app.role}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        StatusBadge(status = app.status)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Advance Candidate Stage:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("TECH_INTERVIEW", "HR_INTERVIEW", "SELECTED").forEach { stage ->
                            OutlinedButton(
                                onClick = {
                                    viewModel.advanceApplicationStatus(app.id, stage, "Stage advanced by Placement Cell")
                                },
                                modifier = Modifier.weight(1f).testTag("btn_stage_${stage}_${app.id}")
                            ) {
                                Text(stage.replace("_", " "), style = MaterialTheme.typography.labelSmall, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CreatePlacementDriveTab(viewModel: MainViewModel) {
    var companyName by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var packageLpa by remember { mutableStateOf("15.0") }
    var minCgpa by remember { mutableStateOf("7.5") }
    var departments by remember { mutableStateOf("CSE, IT, ECE") }
    var deadline by remember { mutableStateOf("2026-11-10") }
    var description by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Publish Campus Placement Drive",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            OutlinedTextField(
                value = companyName,
                onValueChange = { companyName = it },
                label = { Text("Company Name") },
                placeholder = { Text("e.g. Cisco Systems") },
                modifier = Modifier.fillMaxWidth().testTag("input_drive_company")
            )
        }

        item {
            OutlinedTextField(
                value = role,
                onValueChange = { role = it },
                label = { Text("Job Designation / Role") },
                placeholder = { Text("e.g. Cloud Security Analyst") },
                modifier = Modifier.fillMaxWidth().testTag("input_drive_role")
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = packageLpa,
                    onValueChange = { packageLpa = it },
                    label = { Text("CTC (LPA)") },
                    modifier = Modifier.weight(1f).testTag("input_drive_ctc")
                )
                OutlinedTextField(
                    value = minCgpa,
                    onValueChange = { minCgpa = it },
                    label = { Text("Min CGPA") },
                    modifier = Modifier.weight(1f).testTag("input_drive_min_cgpa")
                )
            }
        }

        item {
            OutlinedTextField(
                value = departments,
                onValueChange = { departments = it },
                label = { Text("Allowed Departments") },
                modifier = Modifier.fillMaxWidth().testTag("input_drive_dept")
            )
        }

        item {
            OutlinedTextField(
                value = deadline,
                onValueChange = { deadline = it },
                label = { Text("Application Deadline (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth().testTag("input_drive_deadline")
            )
        }

        item {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Job Description & Requirements") },
                modifier = Modifier.fillMaxWidth().height(100.dp).testTag("input_drive_desc")
            )
        }

        item {
            Button(
                onClick = {
                    if (companyName.isNotBlank() && role.isNotBlank()) {
                        viewModel.createNewPlacementDrive(
                            companyName = companyName,
                            role = role,
                            packageLpa = packageLpa.toDoubleOrNull() ?: 12.0,
                            minCgpa = minCgpa.toDoubleOrNull() ?: 7.0,
                            departments = departments,
                            deadline = deadline,
                            description = description
                        )
                        companyName = ""
                        role = ""
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("btn_publish_drive")
            ) {
                Icon(imageVector = Icons.Default.Publish, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Publish Drive to Cloud Firestore")
            }
        }
    }
}

@Composable
private fun PlacementAnalyticsTab() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Placement Conversion Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricStatCard(
                    title = "Offers Released",
                    value = "104",
                    subtitle = "87.4% of eligible batch",
                    icon = Icons.Default.Work,
                    containerColor = Color(0xFFDCFCE7),
                    contentColor = Color(0xFF15803D),
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "Avg CTC",
                    value = "14.8 LPA",
                    subtitle = "Max: 21.0 LPA",
                    icon = Icons.Default.AttachMoney,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Top Recruiting Partners 2026", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. Microsoft — 18 Offers (21.0 LPA)\n2. Google Cloud — 24 Offers (18.5 LPA)\n3. Amazon AWS — 32 Offers (14.5 LPA)\n4. Infosys Springboard — 30 Offers (9.5 LPA)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

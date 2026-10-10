package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole

@Composable
fun AppTopBar(
    collegeName: String,
    currentRole: UserRole,
    userName: String,
    onSwitchRoleClick: () -> Unit,
    onRegisterCollegeClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "College Logo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "CareerBridge",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = collegeName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Role Pill button
                    FilterChip(
                        selected = true,
                        onClick = onSwitchRoleClick,
                        label = {
                            Text(
                                text = currentRole.displayName,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = when (currentRole) {
                                    UserRole.STUDENT -> Icons.Default.Person
                                    UserRole.TEACHER -> Icons.Default.AutoStories
                                    UserRole.PRINCIPAL -> Icons.Default.AccountBalance
                                    UserRole.HOD -> Icons.Default.SupervisorAccount
                                    UserRole.PARENT -> Icons.Default.FamilyRestroom
                                    UserRole.PLACEMENT_OFFICER -> Icons.Default.Work
                                    UserRole.COMPANY -> Icons.Default.Business
                                },
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Switch Role",
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.testTag("switch_role_chip")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onRegisterCollegeClick,
                        modifier = Modifier.testTag("btn_register_college")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AppRegistration,
                            contentDescription = "Register New College",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricStatCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor.copy(alpha = 0.8f)
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(contentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun CircularScoreGauge(
    score: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    val progress = (score / 100f).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "GaugeAnim"
    )

    val gaugeColor = when {
        score >= 80 -> Color(0xFF10B981) // Green
        score >= 65 -> Color(0xFFF59E0B) // Amber
        else -> Color(0xFFEF4444) // Red
    }

    Box(
        modifier = modifier.size(130.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 12.dp.toPx()
            // Background arc
            drawArc(
                color = Color.LightGray.copy(alpha = 0.3f),
                startAngle = 140f,
                sweepAngle = 260f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            // Progress arc
            drawArc(
                color = gaugeColor,
                startAngle = 140f,
                sweepAngle = 260f * animatedProgress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$score%",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, fgColor) = when (status.uppercase()) {
        "PRESENT", "SELECTED", "ACTIVE", "LOW" -> Color(0xFFDCFCE7) to Color(0xFF15803D)
        "SHORTLISTED", "EXAM_QUALIFIED", "MODERATE" -> Color(0xFFFEF3C7) to Color(0xFFB45309)
        "TECH_INTERVIEW", "HR_INTERVIEW", "APPLIED" -> Color(0xFFDBEAFE) to Color(0xFF1D4ED8)
        "ABSENT", "REJECTED", "HIGH" -> Color(0xFFFEE2E2) to Color(0xFFB91C1C)
        else -> Color(0xFFF1F5F9) to Color(0xFF475569)
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bgColor,
        modifier = modifier
    ) {
        Text(
            text = status.replace("_", " "),
            color = fgColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSwitcherDialog(
    currentRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Switch Role Portal", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Select a persona to test role-specific dashboards, permissions & workflows:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                UserRole.values().forEach { role ->
                    val isSelected = role == currentRole
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                onRoleSelected(role)
                                onDismiss()
                            }
                            .testTag("role_option_${role.name.lowercase()}"),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (role) {
                                    UserRole.STUDENT -> Icons.Default.Person
                                    UserRole.TEACHER -> Icons.Default.AutoStories
                                    UserRole.PRINCIPAL -> Icons.Default.AccountBalance
                                    UserRole.HOD -> Icons.Default.SupervisorAccount
                                    UserRole.PARENT -> Icons.Default.FamilyRestroom
                                    UserRole.PLACEMENT_OFFICER -> Icons.Default.Work
                                    UserRole.COMPANY -> Icons.Default.Business
                                },
                                contentDescription = null,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = role.displayName,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = when (role) {
                                        UserRole.STUDENT -> "Academic tracking, AI guidance, placements"
                                        UserRole.TEACHER -> "Mark attendance, internal marks, chat"
                                        UserRole.PRINCIPAL -> "Institution overview, departments, audits"
                                        UserRole.HOD -> "Department analytics, backlogs, faculty"
                                        UserRole.PARENT -> "Child progress, attendance, alerts"
                                        UserRole.PLACEMENT_OFFICER -> "Company drives, eligibility, offers"
                                        UserRole.COMPANY -> "Post drives, test links, shortlist"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun PrincipalRegistrationDialog(
    onDismiss: () -> Unit,
    onRegister: (name: String, code: String, principal: String, email: String, phone: String) -> Unit
) {
    var collegeName by remember { mutableStateOf("") }
    var collegeCode by remember { mutableStateOf("") }
    var principalName by remember { mutableStateOf("") }
    var officialEmail by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("First-Time College Registration", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Configure your institution profile. This establishes the college code and principal administrative role in Cloud Firestore.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = collegeName,
                    onValueChange = { collegeName = it },
                    label = { Text("College Name") },
                    placeholder = { Text("e.g. Stanford Tech Institute") },
                    modifier = Modifier.fillMaxWidth().testTag("input_college_name"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = collegeCode,
                    onValueChange = { collegeCode = it.uppercase() },
                    label = { Text("Unique College Code") },
                    placeholder = { Text("e.g. STI-2026") },
                    modifier = Modifier.fillMaxWidth().testTag("input_college_code"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = principalName,
                    onValueChange = { principalName = it },
                    label = { Text("Principal Full Name & Designation") },
                    placeholder = { Text("e.g. Dr. Arthur Vance, Ph.D.") },
                    modifier = Modifier.fillMaxWidth().testTag("input_principal_name"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = officialEmail,
                    onValueChange = { officialEmail = it },
                    label = { Text("Official Email Address") },
                    placeholder = { Text("principal@institution.edu") },
                    modifier = Modifier.fillMaxWidth().testTag("input_principal_email"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    placeholder = { Text("+1 (555) 019-2831") },
                    modifier = Modifier.fillMaxWidth().testTag("input_principal_phone"),
                    singleLine = true
                )

                if (errorText.isNotBlank()) {
                    Text(
                        text = errorText,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (collegeName.isBlank() || collegeCode.isBlank() || principalName.isBlank() || officialEmail.isBlank()) {
                        errorText = "Please fill in all mandatory fields"
                    } else {
                        onRegister(collegeName, collegeCode, principalName, officialEmail, phone)
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("btn_confirm_register_college")
            ) {
                Text("Register College")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

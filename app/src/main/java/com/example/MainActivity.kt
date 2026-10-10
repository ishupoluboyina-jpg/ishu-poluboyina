package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.UserRole
import com.example.ui.components.AppTopBar
import com.example.ui.components.PrincipalRegistrationDialog
import com.example.ui.components.RoleSwitcherDialog
import com.example.ui.screens.*
import com.example.ui.theme.CareerBridgeTheme
import com.example.ui.viewmodel.MainViewModel
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            Firebase.appCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
        } catch (_: Exception) {}

        setContent {
            CareerBridgeTheme {
                val viewModel: MainViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                var isAuthenticated by remember { mutableStateOf(Firebase.auth.currentUser != null) }
                var showRoleSwitcher by remember { mutableStateOf(false) }
                var showRegisterCollege by remember { mutableStateOf(false) }

                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(uiState.feedbackMessage) {
                    uiState.feedbackMessage?.let { msg ->
                        snackbarHostState.showSnackbar(msg)
                        viewModel.clearFeedback()
                    }
                }

                if (!isAuthenticated) {
                    AuthScreen(
                        onAuthSuccess = {
                            isAuthenticated = true
                        },
                        onRoleSelected = { role ->
                            viewModel.selectRole(role)
                        },
                        onRegisterCollege = { name, code, principal, email, phone ->
                            viewModel.registerCollegeAndPrincipal(name, code, principal, email, phone)
                        }
                    )
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            AppTopBar(
                                collegeName = uiState.college.name,
                                currentRole = uiState.currentRole,
                                userName = uiState.userProfile.name,
                                onSwitchRoleClick = { showRoleSwitcher = true },
                                onRegisterCollegeClick = { showRegisterCollege = true }
                            )
                        },
                        snackbarHost = { SnackbarHost(snackbarHostState) }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (uiState.currentRole) {
                                UserRole.STUDENT -> StudentDashboard(uiState, viewModel)
                                UserRole.TEACHER -> TeacherDashboard(uiState, viewModel)
                                UserRole.PRINCIPAL -> PrincipalDashboard(uiState, viewModel)
                                UserRole.HOD -> HodDashboard(uiState, viewModel)
                                UserRole.PARENT -> ParentDashboard(uiState, viewModel)
                                UserRole.PLACEMENT_OFFICER -> PlacementDashboard(uiState, viewModel)
                                UserRole.COMPANY -> CompanyDashboard(uiState, viewModel)
                            }
                        }

                        if (showRoleSwitcher) {
                            RoleSwitcherDialog(
                                currentRole = uiState.currentRole,
                                onRoleSelected = { newRole ->
                                    viewModel.selectRole(newRole)
                                },
                                onDismiss = { showRoleSwitcher = false }
                            )
                        }

                        if (showRegisterCollege) {
                            PrincipalRegistrationDialog(
                                onDismiss = { showRegisterCollege = false },
                                onRegister = { name, code, principal, email, phone ->
                                    viewModel.registerCollegeAndPrincipal(name, code, principal, email, phone)
                                    showRegisterCollege = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

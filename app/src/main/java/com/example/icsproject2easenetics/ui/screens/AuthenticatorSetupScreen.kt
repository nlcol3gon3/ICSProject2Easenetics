package com.example.icsproject2easenetics.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.icsproject2easenetics.ui.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticatorSetupScreen(
    onSetupComplete: () -> Unit,
    onBack: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var verificationCode by remember { mutableStateOf("") }
    val mfaSetupState by viewModel.mfaSetupState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    LaunchedEffect(mfaSetupState) {
        if (mfaSetupState is com.example.icsproject2easenetics.ui.viewmodels.MfaSetupState.Completed) {
            onSetupComplete()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Setup Authenticator App",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (mfaSetupState) {
                is com.example.icsproject2easenetics.ui.viewmodels.MfaSetupState.AuthenticatorReady -> {
                    val state = mfaSetupState as com.example.icsproject2easenetics.ui.viewmodels.MfaSetupState.AuthenticatorReady
                    AuthenticatorReadyContent(
                        secret = state.secret,
                        totpUri = state.totpUri,
                        verificationCode = verificationCode,
                        onVerificationCodeChange = { verificationCode = it },
                        onVerify = {
                            currentUser?.let { user ->
                                viewModel.verifyAuthenticatorSetup(user.uid, verificationCode)
                            }
                        },
                        isLoading = isLoading
                    )
                }
                is com.example.icsproject2easenetics.ui.viewmodels.MfaSetupState.Loading -> {
                    CircularProgressIndicator()
                }
                is com.example.icsproject2easenetics.ui.viewmodels.MfaSetupState.Error -> {
                    Text(
                        text = (mfaSetupState as com.example.icsproject2easenetics.ui.viewmodels.MfaSetupState.Error).message,
                        color = androidx.compose.ui.graphics.Color.Red
                    )
                }
                else -> {
                    // Start setup
                    LaunchedEffect(Unit) {
                        currentUser?.let { user ->
                            viewModel.setupAuthenticatorMfa(user.uid)
                        }
                    }
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun AuthenticatorReadyContent(
    secret: String,
    totpUri: String,
    verificationCode: String,
    onVerificationCodeChange: (String) -> Unit,
    onVerify: () -> Unit,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Filled.QrCode,
                contentDescription = "QR Code",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Setup Authenticator App",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "1. Open your authenticator app (Google Authenticator, Authy, etc.)\n" +
                        "2. Scan the QR code or enter the secret key manually\n" +
                        "3. Enter the 6-digit code generated by the app",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // In a real implementation, you'd generate and display a QR code here
            Text(
                text = "Manual Entry Secret:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = secret,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = verificationCode,
                onValueChange = {
                    if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                        onVerificationCodeChange(it)
                    }
                },
                label = { Text("Verification Code") },
                placeholder = { Text("000000") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onVerify,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading && verificationCode.length == 6
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Verify & Enable", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
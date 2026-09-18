package com.example.fitfusion.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitfusion.ui.theme.BeigeAccent
import com.example.fitfusion.ui.theme.FitFusionTheme
import com.example.fitfusion.ui.theme.NavyDeep
import com.example.fitfusion.ui.theme.TealPrimary
import com.example.fitfusion.ui.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel(),
    onAuthSuccess: () -> Unit,
    onVerificationRequired: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Error -> {
                snackbarHostState.showSnackbar(message = (authState as AuthState.Error).message)
            }
            is AuthState.Authenticated -> {
                onAuthSuccess()
            }
            is AuthState.VerificationRequired -> {
                onVerificationRequired()
            }
            else -> {}
        }
    }

    var isLoginMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPassword by remember { mutableStateOf("") }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var keepSignedIn by remember { mutableStateOf(true) }

    val emailError = getEmailError(email)
    val passwordError = getPasswordError(password)
    val confirmPasswordError = if (!isLoginMode && confirmPassword.isNotEmpty() && password != confirmPassword) "Passwords do not match" else null
    val isFormValid = if (isLoginMode) {
        email.isNotEmpty() && password.isNotEmpty()
    } else {
        email.isNotEmpty() && password.isNotEmpty() && emailError == null && passwordError == null && username.isNotEmpty() && confirmPasswordError == null
    }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize().background(BeigeAccent)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .background(NavyDeep, RectangleShape)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Checkroom, contentDescription = null, tint = BeigeAccent)
            }
            Text(
                text = "FITFUSION",
                style = Typography.headlineMedium,
                color = NavyDeep,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            Text(
                text = "Your digital wardrobe & daily personal\nstyle companion",
                textAlign = TextAlign.Center,
                color = NavyDeep,
                fontSize = 14.sp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = NavyDeep,
                    shape = RectangleShape
                )
                .background(Color(0xFFEAE4D9))
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isLoginMode) "Welcome back" else "Create Account",
                style = Typography.titleMedium,
                color = NavyDeep
            )

            Text(
                text = if (isLoginMode) "Sign in to organize your fits" else "Sign up to start organizing your fits",
                style = Typography.bodySmall,
                color = NavyDeep,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (!isLoginMode) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("USERNAME", fontSize = 12.sp, color = NavyDeep, fontWeight = FontWeight.Bold)
                }
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = { Text("John Doe", color = NavyDeep.copy(alpha = 0.5f)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Username",
                            tint = NavyDeep
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                Text("EMAIL ADDRESS", fontSize = 12.sp, color = NavyDeep, fontWeight = FontWeight.Bold)
            }
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                isError = !isLoginMode && emailError != null,
                placeholder = { Text("agent@fitfusion.com", color = NavyDeep.copy(alpha = 0.5f)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = NavyDeep
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )
            if (!isLoginMode && emailError != null) {
                Text(text = emailError, color = Color(0xFF8B0000), fontSize = 12.sp, modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 8.dp))
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("PASSWORD", fontSize = 12.sp, color = NavyDeep)
            }
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                isError = !isLoginMode && passwordError != null,
                placeholder = { Text("••••••••", color = NavyDeep.copy(alpha = 0.5f)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password",
                        tint = NavyDeep
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            passwordVisible = !passwordVisible
                        }
                    ) {
                        val icon = if (passwordVisible) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.Visibility
                        }

                        Icon(
                            imageVector = icon,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = NavyDeep
                        )
                    }
                },
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (!isLoginMode && passwordError != null) Text(text = passwordError, color = Color(0xFF8B0000), fontSize = 12.sp, modifier = Modifier.fillMaxWidth().padding(top = 4.dp))

            if (!isLoginMode) {
                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Text("CONFIRM PASSWORD", fontSize = 12.sp, color = NavyDeep, fontWeight = FontWeight.Bold)
                }
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    isError = confirmPasswordError != null,
                    placeholder = { Text("••••••••", color = NavyDeep.copy(alpha = 0.5f)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Confirm Password",
                            tint = NavyDeep
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                confirmPasswordVisible = !confirmPasswordVisible
                            }
                        ) {
                            val icon = if (confirmPasswordVisible) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            }

                            Icon(
                                imageVector = icon,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                tint = NavyDeep
                            )
                        }
                    },
                    visualTransformation = if (confirmPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                if (confirmPasswordError != null) Text(text = confirmPasswordError, color = Color(0xFF8B0000), fontSize = 12.sp, modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = keepSignedIn,
                    onCheckedChange = {
                        keepSignedIn = it
                    }
                )

                Text(
                    text = "Keep me signed in",
                    style = Typography.bodySmall,
                    color = NavyDeep
                )
                
                Spacer(Modifier.weight(1f))
            }

            Button(
                onClick = {
                    if (isLoginMode) {
                        viewModel.signIn(email, password, keepSignedIn)
                    } else {
                        viewModel.signUp(email, password, confirmPassword, username, keepSignedIn)
                    }
                },
                enabled = isFormValid,
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NavyDeep,
                    disabledContainerColor = NavyDeep.copy(alpha = 0.4f) // Stays dark industrial instead of washing out
                ),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isLoginMode) "LOG IN" else "SIGN UP",
                        style = Typography.labelLarge,
                        color = BeigeAccent
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = BeigeAccent
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { scope.launch{viewModel.signInWithGoogle()} },
                shape = RectangleShape,
                border = BorderStroke(2.dp, NavyDeep),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFEAE4D9)),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "G",
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF4285F4), // Google Blue accent
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Google",
                        fontWeight = FontWeight.Bold,
                        color = NavyDeep,
                        fontSize = 14.sp
                    )
                }
            }

            TextButton(
                onClick = { isLoginMode = !isLoginMode },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = if (isLoginMode) "Don't have an account? Sign up" else "Already have an account? Log in",
                    color = NavyDeep
                )
            }
        }
    }
}
}

@Preview(showBackground = true)
@Composable
fun ScreenTester() {
    FitFusionTheme {
        AuthScreen(onAuthSuccess = {}, onVerificationRequired = {})
    }
}

fun getPasswordError(password: String): String? {
    if (password.isEmpty()) return null
    if (password.length < 8) return "Requires at least 8 characters"
    if (!password.contains(Regex("[A-Z]"))) return "Requires 1 uppercase letter"
    if (!password.contains(Regex("[0-9]"))) return "Requires 1 number"
    if (!password.contains(Regex("[^A-Za-z0-9]"))) return "Requires 1 special character"
    return null
}

fun getEmailError(email: String): String? {
    if (email.isEmpty()) return null
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]+$".toRegex()
    return if (email.matches(emailRegex)) null else "Invalid email format"
}
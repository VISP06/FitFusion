package com.example.fitfusion.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitfusion.data.database.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

import io.github.jan.supabase.auth.providers.Google
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

@Serializable
data class UserMetadata(val username: String)

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun signUp(email: String, password: String, confirmPassword: String, username: String, keepSignedIn: Boolean) {
        if (password != confirmPassword) {
            _authState.value = AuthState.Error("Passwords do not match.")
            return
        }
        // TODO: Persist keepSignedIn boolean to DataStore to handle manual session wipe on next app launch
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                SupabaseClient.client.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                    // Add user metadata
                    val metadata = JsonObject(mapOf("username" to JsonPrimitive(username)))
                }
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "An error occurred during sign up")
            }
        }
    }

    fun signIn(email: String, password: String, keepSignedIn: Boolean) {
        // TODO: Persist keepSignedIn boolean to DataStore to handle manual session wipe on next app launch
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                SupabaseClient.client.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "An error occurred during sign in")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                SupabaseClient.client.auth.signOut()
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "An error occurred during sign out")
            }
        }
    }

    suspend fun signInWithGoogle() {
        _authState.value = AuthState.Loading
        try {
            SupabaseClient.client.auth.signInWith(Google)
            _authState.value = AuthState.Success
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "An error occurred during Google sign in")
        }
    }
}

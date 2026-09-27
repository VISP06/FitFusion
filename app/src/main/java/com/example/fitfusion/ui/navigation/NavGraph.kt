package com.example.fitfusion.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fitfusion.ui.outfits.OutfitsScreen
import com.example.fitfusion.ui.outfits.OutfitsViewModel
import com.example.fitfusion.ui.studio.StudioScreen
import com.example.fitfusion.ui.studio.StudioViewModel
import com.example.fitfusion.ui.wardrobe.WardrobeScreen
import com.example.fitfusion.ui.wardrobe.WardrobeViewModel

import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import com.example.fitfusion.ui.auth.AuthScreen
import com.example.fitfusion.ui.auth.AuthViewModel
import com.example.fitfusion.ui.auth.ManifestoGateScreen
import com.example.fitfusion.ui.auth.MannequinScreen
import com.example.fitfusion.ui.auth.ProfileScreen
import com.example.fitfusion.ui.auth.VerificationScreen
import com.example.fitfusion.data.database.SupabaseClient
import io.github.jan.supabase.auth.auth
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.key
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonPrimitive

sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    object Auth : Screen("auth", Icons.Default.Lock, "AUTH")
    object Verification : Screen("verification", Icons.Default.Lock, "VERIFICATION")
    object Closet : Screen("closet", Icons.Default.Checkroom, "CLOSET")
    object Studio : Screen("studio", Icons.Default.ColorLens, "STUDIO")
    object Outfits : Screen("outfits", Icons.Default.Style, "OUTFITS")
    object Profile : Screen("profile", Icons.Default.Person, "PROFILE")
}

@Composable
fun FitFusionNavGraph(
    navController: NavHostController,
    wardrobeViewModel: WardrobeViewModel,
    studioViewModel: StudioViewModel,
    outfitsViewModel: OutfitsViewModel,
    startDestination: String
) {
    key(startDestination) {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
        composable(Screen.Auth.route) {
            val authViewModel: AuthViewModel = viewModel()
            AuthScreen(
                viewModel = authViewModel,
                onAuthSuccess = {
                    val user = SupabaseClient.client.auth.currentUserOrNull()
                    val hasAccepted = user?.userMetadata?.get("manifesto_accepted")?.jsonPrimitive?.booleanOrNull ?: false
                    val targetRoute = if (hasAccepted) Screen.Closet.route else "manifesto"
                    navController.navigate(targetRoute) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                },
                onVerificationRequired = {
                    navController.navigate(Screen.Verification.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Verification.route) {
            VerificationScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Closet.route) {
            WardrobeScreen(viewModel = wardrobeViewModel)
        }
        composable("home") {
            WardrobeScreen(viewModel = wardrobeViewModel)
        }
        composable("manifesto") {
            val authViewModel: AuthViewModel = viewModel()
            ManifestoGateScreen(
                viewModel = authViewModel,
                onNavigateToApp = {
                    navController.navigate("home") { popUpTo("manifesto") { inclusive = true } }
                }
            )
        }
        composable(Screen.Studio.route) {
            StudioScreen(studioViewModel)
        }
        composable(Screen.Outfits.route) {
            OutfitsScreen(outfitsViewModel)
        }
        composable(Screen.Profile.route) {
            val authViewModel: AuthViewModel = viewModel()
            ProfileScreen(
                userEmail = SupabaseClient.client.auth.currentUserOrNull()?.email ?: "Unknown User",
                onNavigateBack = { navController.popBackStack() },
                onSignedOut = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToPrivacy = { navController.navigate("privacy") },
                onNavigateToMannequin = { navController.navigate("mannequin") },
                viewModel = authViewModel
            )
        }
        composable("privacy") {
            com.example.fitfusion.ui.auth.PrivacyPolicyScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("mannequin") {
            MannequinScreen(onNavigateBack = { navController.popBackStack() })
        }
        }
    }
}

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
import com.example.fitfusion.ui.auth.ProfileScreen
import com.example.fitfusion.ui.auth.VerificationScreen
import com.example.fitfusion.data.database.SupabaseClient
import io.github.jan.supabase.auth.auth
import androidx.lifecycle.viewmodel.compose.viewModel

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
    outfitsViewModel: OutfitsViewModel
) {
    val session = SupabaseClient.client.auth.currentSessionOrNull()
    val startDest = if (session != null) Screen.Closet.route else Screen.Auth.route

    NavHost(
        navController = navController,
        startDestination = startDest
    ) {
        composable(Screen.Auth.route) {
            val authViewModel: AuthViewModel = viewModel()
            AuthScreen(
                viewModel = authViewModel,
                onAuthSuccess = {
                    navController.navigate(Screen.Closet.route) {
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
            WardrobeScreen(
                viewModel = wardrobeViewModel,
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
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
                viewModel = authViewModel,
                onSignedOut = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

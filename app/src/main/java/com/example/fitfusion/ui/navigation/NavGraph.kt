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
import com.example.fitfusion.ui.auth.AuthScreen
import com.example.fitfusion.data.database.SupabaseClient
import io.github.jan.supabase.auth.auth

sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    object Auth : Screen("auth", Icons.Default.Lock, "AUTH")
    object Closet : Screen("closet", Icons.Default.Checkroom, "CLOSET")
    object Studio : Screen("studio", Icons.Default.ColorLens, "STUDIO")
    object Outfits : Screen("outfits", Icons.Default.Style, "OUTFITS")
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
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Screen.Closet.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Closet.route) {
            WardrobeScreen(wardrobeViewModel)
        }
        composable(Screen.Studio.route) {
            StudioScreen(studioViewModel)
        }
        composable(Screen.Outfits.route) {
            OutfitsScreen(outfitsViewModel)
        }
    }
}

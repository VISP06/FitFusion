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

sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
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
    NavHost(
        navController = navController,
        startDestination = Screen.Closet.route
    ) {
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

package com.example.fitfusion.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fitfusion.ui.studio.StudioScreen
import com.example.fitfusion.ui.studio.StudioViewModel
import com.example.fitfusion.ui.theme.NavyDeep
import com.example.fitfusion.ui.wardrobe.WardrobeScreen
import com.example.fitfusion.ui.wardrobe.WardrobeViewModel

sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    object Closet : Screen("closet", Icons.Default.Checkroom, "CLOSET")
    object Studio : Screen("studio", Icons.Default.ColorLens, "STUDIO")
    object Outfits : Screen("outfits", Icons.Default.Style, "OUTFITS")
}

@Composable
fun FitFusionNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Closet.route
    ) {
        composable(Screen.Closet.route) {
            val wardrobeViewModel: WardrobeViewModel = viewModel()
            WardrobeScreen(wardrobeViewModel)
        }
        composable(Screen.Studio.route) {
            val studioViewModel: StudioViewModel = viewModel()
            StudioScreen(studioViewModel)
        }
        composable(Screen.Outfits.route) {
            PlaceholderScreen("OUTFITS")
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$title COMING SOON",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = NavyDeep
        )
    }
}

package com.example.fitfusion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fitfusion.data.database.WardrobeDatabase
import com.example.fitfusion.ui.navigation.FitFusionNavGraph
import com.example.fitfusion.ui.navigation.Screen
import com.example.fitfusion.ui.outfits.OutfitsViewModel
import com.example.fitfusion.ui.studio.StudioViewModel
import com.example.fitfusion.ui.theme.BeigeAccent
import com.example.fitfusion.ui.theme.FitFusionTheme
import com.example.fitfusion.ui.theme.NavyDeep
import com.example.fitfusion.ui.wardrobe.WardrobeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = WardrobeDatabase.getDatabase(applicationContext)
        val dao = database.wardrobeDao()

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return when {
                    modelClass.isAssignableFrom(WardrobeViewModel::class.java) -> WardrobeViewModel(dao) as T
                    modelClass.isAssignableFrom(StudioViewModel::class.java) -> StudioViewModel(dao) as T
                    modelClass.isAssignableFrom(OutfitsViewModel::class.java) -> OutfitsViewModel(dao) as T
                    else -> throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }

        val wardrobeViewModel = ViewModelProvider(this, factory)[WardrobeViewModel::class.java]
        val studioViewModel = ViewModelProvider(this, factory)[StudioViewModel::class.java]
        val outfitsViewModel = ViewModelProvider(this, factory)[OutfitsViewModel::class.java]

        setContent {
            FitFusionTheme {
                MainScreen(wardrobeViewModel, studioViewModel, outfitsViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    wardrobeViewModel: WardrobeViewModel,
    studioViewModel: StudioViewModel,
    outfitsViewModel: OutfitsViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val screens = listOf(
        Screen.Closet,
        Screen.Studio,
        Screen.Outfits
    )

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.bottomBorder(2.dp, NavyDeep)
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "FITFUSION",
                            style = MaterialTheme.typography.headlineLarge,
                            letterSpacing = 4.sp,
                            color = BeigeAccent
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = NavyDeep
                    )
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.border(
                    width = 2.dp,
                    color = NavyDeep
                )
            ) {
                screens.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label, fontWeight = FontWeight.Bold) },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyDeep,
                            unselectedIconColor = NavyDeep.copy(alpha = 0.5f),
                            indicatorColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            FitFusionNavGraph(
                navController = navController,
                wardrobeViewModel = wardrobeViewModel,
                studioViewModel = studioViewModel,
                outfitsViewModel = outfitsViewModel
            )
        }
    }
}

private fun Modifier.bottomBorder(strokeWidth: androidx.compose.ui.unit.Dp, color: androidx.compose.ui.graphics.Color): Modifier = drawBehind {
    val width = strokeWidth.toPx()
    val y = size.height - width / 2
    drawLine(
        color = color,
        start = Offset(0f, y),
        end = Offset(size.width, y),
        strokeWidth = width
    )
}

package com.example.fitfusion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fitfusion.ui.navigation.FitFusionNavGraph
import com.example.fitfusion.ui.navigation.Screen
import com.example.fitfusion.ui.theme.FitFusionTheme
import com.example.fitfusion.ui.theme.NavyDeep
import com.example.fitfusion.ui.theme.BeigeAccent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitFusionTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
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
            FitFusionNavGraph(navController = navController)
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

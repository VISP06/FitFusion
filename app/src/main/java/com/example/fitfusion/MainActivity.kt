package com.example.fitfusion

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import com.example.fitfusion.data.database.SyncRepository
import com.example.fitfusion.ui.splash.AnimatedSplashScreen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.fitfusion.data.database.SupabaseClient
import com.example.fitfusion.data.database.WardrobeDatabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import com.example.fitfusion.ui.navigation.FitFusionNavGraph
import com.example.fitfusion.ui.navigation.Screen
import com.example.fitfusion.ui.outfits.OutfitsViewModel
import com.example.fitfusion.ui.studio.StudioViewModel
import com.example.fitfusion.ui.theme.BeigeAccent
import com.example.fitfusion.ui.theme.FitFusionTheme
import com.example.fitfusion.ui.theme.NavyDeep
import com.example.fitfusion.ui.wardrobe.WardrobeViewModel
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonPrimitive

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = WardrobeDatabase.getDatabase(applicationContext)
        val dao = database.wardrobeDao()
        val supabaseClient = SupabaseClient.client
        val syncRepository = SyncRepository(dao, supabaseClient)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return when {
                    modelClass.isAssignableFrom(WardrobeViewModel::class.java) -> WardrobeViewModel(dao, supabaseClient, syncRepository) as T
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
    val supabase = SupabaseClient.client
    val sessionStatus by supabase.auth.sessionStatus.collectAsState()
    val context = LocalContext.current

    if (sessionStatus is SessionStatus.Initializing) {
        AnimatedSplashScreen()
        return
    }

    val keepSignedIn = context.getSharedPreferences("FitFusionPrefs", Context.MODE_PRIVATE)
        .getBoolean("KEEP_SIGNED_IN", true)

    LaunchedEffect(sessionStatus) {
        if (sessionStatus is SessionStatus.Authenticated && !keepSignedIn) {
            supabase.auth.signOut()
        }
    }

    val startDest = if (sessionStatus is SessionStatus.Authenticated && keepSignedIn) {
        val user = supabase.auth.currentUserOrNull()
        val hasAccepted = user?.userMetadata?.get("manifesto_accepted")?.jsonPrimitive?.booleanOrNull ?: false
        if (hasAccepted) Screen.Closet.route else "manifesto"
    } else {
        Screen.Auth.route
    }

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
            if (currentDestination?.route != Screen.Auth.route &&
                currentDestination?.route != Screen.Profile.route &&
                currentDestination?.route != "manifesto" &&
                currentDestination?.route != "mannequin"
            ) {
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
                        actions = {
                            IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = "Menu",
                                    tint = Color(0xFFEAE4D9)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = NavyDeep
                        )
                    )
                }
            }
        },
        bottomBar = {
            if (currentDestination?.route != Screen.Auth.route &&
                currentDestination?.route != Screen.Profile.route &&
                currentDestination?.route != "manifesto" &&
                currentDestination?.route != "mannequin"
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.border(
                        width = 2.dp,
                        color = NavyDeep
                    )
                ) {
                    screens.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route || (screen == Screen.Closet && it.route == "home") } == true
                        
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
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            FitFusionNavGraph(
                navController = navController,
                wardrobeViewModel = wardrobeViewModel,
                studioViewModel = studioViewModel,
                outfitsViewModel = outfitsViewModel,
                startDestination = startDest
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

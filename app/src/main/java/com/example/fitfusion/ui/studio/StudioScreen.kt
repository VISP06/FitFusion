package com.example.fitfusion.ui.studio

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.fitfusion.data.entity.ClothingItem
import com.example.fitfusion.data.entity.Outfit
import com.example.fitfusion.ui.theme.CoralMuted
import com.example.fitfusion.ui.theme.NavyDeep
import com.example.fitfusion.ui.wardrobe.GridBackground
import kotlinx.coroutines.launch

@Composable
fun StudioScreen(viewModel: StudioViewModel) {
    val availableClothes by viewModel.availableClothes.collectAsState()
    val selectedClothes by viewModel.selectedClothes.collectAsState()
    val generatedOutfits by viewModel.generatedOutfits.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            GridBackground()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "STUDIO",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = NavyDeep
                )
                
                Text(
                    text = "SELECT ITEMS TO MIX",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = NavyDeep.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth().height(120.dp)
                ) {
                    items(availableClothes) { item ->
                        val isSelected = selectedClothes.contains(item.id)
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(MaterialTheme.colorScheme.surface)
                                .border(
                                    width = if (isSelected) 4.dp else 2.dp,
                                    color = if (isSelected) CoralMuted else NavyDeep,
                                    shape = RectangleShape
                                )
                                .clickable { viewModel.toggleSelection(item.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (item.imageUri != null) {
                                AsyncImage(
                                    model = item.imageUri,
                                    contentDescription = item.category,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                    alpha = if (isSelected) 1f else 0.7f
                                )
                            }
                            
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.6f))
                                    .padding(4.dp)
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    item.category.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyDeep
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.generateCombinations() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .border(2.dp, NavyDeep, RectangleShape),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NavyDeep,
                        contentColor = Color.White
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = CoralMuted,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 3.dp
                        )
                    } else {
                        Text(
                            "GENERATE COMBINATIONS",
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (generatedOutfits.isNotEmpty()) {
                    Text(
                        text = "GENERATED LOOKS",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = NavyDeep,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(generatedOutfits) { outfit ->
                            OutfitResultCard(
                                outfit = outfit,
                                onSave = {
                                    viewModel.saveOutfit(outfit)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Outfit saved to your collection!")
                                    }
                                }
                            )
                        }
                    }
                } else if (!isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "NO LOOKS GENERATED YET",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = NavyDeep.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OutfitResultCard(outfit: Outfit, onSave: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, NavyDeep, RectangleShape),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = outfit.name,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = NavyDeep
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(outfit.items) { item ->
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .border(1.dp, NavyDeep, RectangleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (item.imageUri != null) {
                                AsyncImage(
                                    model = item.imageUri,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = NavyDeep
                                )
                            }
                        }
                    }
                }
            }
            
            IconButton(
                onClick = onSave,
                modifier = Modifier.border(2.dp, NavyDeep, RectangleShape)
            ) {
                Icon(Icons.Default.BookmarkBorder, contentDescription = "Save", tint = NavyDeep)
            }
        }
    }
}

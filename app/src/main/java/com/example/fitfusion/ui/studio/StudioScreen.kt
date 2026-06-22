package com.example.fitfusion.ui.studio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
    val isSelectionValid by viewModel.isSelectionValid.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Group items into categories
    val tops = availableClothes.filter { viewModel.getMacroCategory(it) == MacroCategory.TOPS }
    val bottoms = availableClothes.filter { viewModel.getMacroCategory(it) == MacroCategory.BOTTOMS }
    val footwear = availableClothes.filter { viewModel.getMacroCategory(it) == MacroCategory.FOOTWEAR }
    val accessories = availableClothes.filter { viewModel.getMacroCategory(it) == MacroCategory.ACCESSORIES }

    Box(modifier = Modifier.fillMaxSize()) {
        GridBackground()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "STUDIO",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        color = NavyDeep
                    )
                    
                    Text(
                        text = "SELECT ITEMS TO MIX & MATCH",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = NavyDeep.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

            // Tops section
            item {
                CategorySection(
                    title = "TOPS (Shirts, T-shirts, Hoodies)",
                    items = tops,
                    selectedIds = selectedClothes,
                    onItemClick = { viewModel.toggleSelection(it) }
                )
            }

            // Bottoms section
            item {
                CategorySection(
                    title = "BOTTOMS (Pants, Shorts, Jeans)",
                    items = bottoms,
                    selectedIds = selectedClothes,
                    onItemClick = { viewModel.toggleSelection(it) }
                )
            }

            // Footwear section
            item {
                CategorySection(
                    title = "FOOTWEAR (Shoes, Sneakers, Boots - Optional)",
                    items = footwear,
                    selectedIds = selectedClothes,
                    onItemClick = { viewModel.toggleSelection(it) }
                )
            }

            // Accessories section
            item {
                CategorySection(
                    title = "ACCESSORIES (Watches, Chains, Hats - Optional)",
                    items = accessories,
                    selectedIds = selectedClothes,
                    onItemClick = { viewModel.toggleSelection(it) }
                )
            }

            // Action Button
            item {
                Button(
                    onClick = { viewModel.generateOutfitsWithAI(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .border(2.dp, NavyDeep, RectangleShape),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NavyDeep,
                        contentColor = Color.White,
                        disabledContainerColor = NavyDeep.copy(alpha = 0.1f),
                        disabledContentColor = NavyDeep.copy(alpha = 0.5f)
                    ),
                    enabled = isSelectionValid && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = CoralMuted,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 3.dp
                        )
                    } else {
                        Text(
                            text = if (isSelectionValid) "GENERATE COMBINATIONS" else "SELECT AT LEAST 2 TOPS & 2 BOTTOMS TO UNLOCK STUDIO",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )
                    }
                }
            }

            // Results Section
            if (generatedOutfits.isNotEmpty()) {
                item {
                    Text(
                        text = "GENERATED LOOKS",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = NavyDeep,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(generatedOutfits) { outfit ->
                    OutfitResultCard(
                        outfit = outfit,
                        viewModel = viewModel,
                        onSavedMessage = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Outfit saved to your collection!")
                            }
                        }
                    )
                }
            } else if (!isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "NO LOOKS GENERATED YET",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = NavyDeep.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
fun CategorySection(
    title: String,
    items: List<ClothingItem>,
    selectedIds: Set<Int>,
    onItemClick: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Black,
            color = NavyDeep,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .border(1.dp, NavyDeep.copy(alpha = 0.2f), RectangleShape)
                    .background(Color.White.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "NO ITEMS IN THIS CATEGORY",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = NavyDeep.copy(alpha = 0.4f),
                    fontSize = 11.sp
                )
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth().height(100.dp)
            ) {
                items(items) { item ->
                    val isSelected = selectedIds.contains(item.id)
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(
                                width = if (isSelected) 4.dp else 2.dp,
                                color = if (isSelected) CoralMuted else NavyDeep,
                                shape = RectangleShape
                            )
                            .clickable { onItemClick(item.id) },
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
                        } else {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = NavyDeep.copy(alpha = 0.3f)
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.85f))
                                .padding(vertical = 3.dp)
                        ) {
                            Text(
                                text = "${item.color.uppercase()} ${item.material.uppercase()}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = NavyDeep,
                                fontSize = 8.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OutfitResultCard(
    outfit: Outfit, 
    viewModel: StudioViewModel,
    onSavedMessage: () -> Unit
) {
    var isSaved by remember { mutableStateOf(false) }
    var showNotes by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, NavyDeep, RectangleShape),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = outfit.name.uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = NavyDeep,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(outfit.items) { item ->
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
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
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(2.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = NavyDeep
                                        )
                                        Text(
                                            text = item.category.replace("Recommended ", ""),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 6.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NavyDeep,
                                            maxLines = 1,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = item.color,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 6.sp,
                                            color = NavyDeep,
                                            maxLines = 1,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                IconButton(
                    onClick = {
                        if (!isSaved) {
                            viewModel.saveOutfit(outfit)
                            isSaved = true
                            onSavedMessage()
                        }
                    },
                    modifier = Modifier.border(2.dp, NavyDeep, RectangleShape)
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save",
                        tint = NavyDeep
                    )
                }
            }

            OutlinedButton(
                onClick = { showNotes = !showNotes },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .height(48.dp)
                    .border(2.dp, NavyDeep, RectangleShape),
                shape = RectangleShape,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = NavyDeep
                )
            ) {
                Text(
                    text = if (showNotes) "- HIDE STYLIST NOTES" else "+ WHY THIS WORKS",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    maxLines = 1
                )
            }

            AnimatedVisibility(visible = showNotes) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .border(width = 1.dp, color = NavyDeep.copy(alpha = 0.5f), shape = RectangleShape)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "AI STYLIST: ${outfit.reasoning}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = NavyDeep
                    )
                }
            }
        }
    }
}

package com.example.fitfusion.ui.outfits

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Style
import com.example.fitfusion.ui.wardrobe.GridBackground
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
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
import com.example.fitfusion.ui.theme.BeigeAccent
import kotlinx.coroutines.launch

@Composable
fun OutfitsScreen(viewModel: OutfitsViewModel) {
    val outfits by viewModel.savedOutfits.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        GridBackground()

        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "OUTFITS",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(16.dp),
                color = NavyDeep
            )

            if (outfits.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val lineColor = NavyDeep.copy(alpha = 0.05f)
                        val strokeWidth = 1.dp.toPx()

                        // Diagonal flowing blueprint lines
                        for (i in -size.height.toInt()..size.width.toInt() step 40.dp.toPx().toInt()) {
                            drawLine(
                                color = lineColor,
                                start = Offset(i.toFloat(), 0f),
                                end = Offset(i.toFloat() + size.height, size.height),
                                strokeWidth = strokeWidth
                            )
                        }

                        // Abstract geometric shape (circle + crosshairs)
                        drawCircle(
                            color = lineColor,
                            radius = size.minDimension / 3f,
                            center = center,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Style,
                            contentDescription = null,
                            tint = NavyDeep,
                            modifier = Modifier
                                .size(72.dp)
                                .border(2.dp, NavyDeep, RectangleShape)
                                .padding(16.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "THE ARCHIVE IS EMPTY.",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NavyDeep,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "DESIGN YOUR FIRST LOOK IN THE STUDIO TO SAVE IT HERE.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = NavyDeep.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    items(outfits) { outfit ->
                        OutfitCard(
                            outfit = outfit,
                            onDelete = {
                                viewModel.deleteOutfit(outfit)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Outfit deleted")
                                }
                            },
                            onPreviewClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Coming Soon: Mannequin Preview for ${outfit.name}",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
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
fun OutfitCard(
    outfit: Outfit,
    onDelete: () -> Unit,
    onPreviewClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, NavyDeep, RectangleShape),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CoralMuted)
                    .padding(12.dp)
                    .drawBehind {
                        val strokeWidth = 2.dp.toPx()
                        val y = size.height - strokeWidth / 2
                        drawLine(
                            color = NavyDeep,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = strokeWidth
                        )
                    }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = outfit.name.uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = BeigeAccent,
                        letterSpacing = 2.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BeigeAccent)
                    }
                }
            }

            // Items Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(outfit.items) { item ->
                    ClothingItemThumbnail(item)
                }
            }

            // Stylist Notes Block
            if (outfit.reasoning.isNotEmpty()) {
                var showNotes by remember { mutableStateOf(false) }

                OutlinedButton(
                    onClick = { showNotes = !showNotes },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp)
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

                AnimatedVisibility(
                    visible = showNotes,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp)
                ) {
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

            // Action Button
            Button(
                onClick = onPreviewClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
                    .border(2.dp, NavyDeep, RectangleShape),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NavyDeep,
                    contentColor = BeigeAccent
                ),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text(
                    text = "PREVIEW ON MANNEQUIN",
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun ClothingItemThumbnail(item: ClothingItem) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .border(1.dp, NavyDeep, RectangleShape)
            .background(BeigeAccent.copy(alpha = 0.5f))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(BeigeAccent)
                .border(1.dp, NavyDeep),
            contentAlignment = Alignment.Center
        ) {
            if (item.imageUri != null) {
                AsyncImage(
                    model = item.imageUri,
                    contentDescription = item.category,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = item.category.take(1).uppercase(),
                    fontWeight = FontWeight.Black,
                    color = NavyDeep
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.category.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = NavyDeep,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

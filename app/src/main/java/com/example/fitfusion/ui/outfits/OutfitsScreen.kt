package com.example.fitfusion.ui.outfits

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            items(outfits) { outfit ->
                OutfitCard(
                    outfit = outfit,
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

@Composable
fun OutfitCard(
    outfit: Outfit,
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
                Text(
                    text = outfit.name,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = BeigeAccent,
                    letterSpacing = 2.sp
                )
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
                    letterSpacing = 1.sp
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
            Text(
                text = item.category.take(1).uppercase(),
                fontWeight = FontWeight.Black,
                color = NavyDeep
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.category,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = NavyDeep,
            maxLines = 1
        )
    }
}

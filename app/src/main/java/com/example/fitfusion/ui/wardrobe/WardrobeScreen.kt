package com.example.fitfusion.ui.wardrobe

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.fitfusion.data.entity.ClothingItem
import com.example.fitfusion.ui.theme.NavyDeep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardrobeScreen(viewModel: WardrobeViewModel) {
    val items by viewModel.clothingItems.collectAsState()
    val isSheetOpen by viewModel.isSheetOpen.collectAsState()
    val currentPhotoUri by viewModel.currentPhotoUri.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current

    // CAMERA BUG FIX: use rememberSaveable for temporary URI
    var tempCameraUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    // Launchers
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        viewModel.setPhotoUri(uri)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success) {
            viewModel.clearPhotoUri()
            tempCameraUri = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GridBackground()

        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.openSheet() },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    shape = RectangleShape,
                    modifier = Modifier.border(2.dp, NavyDeep, RectangleShape)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Item")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                Text(
                    text = "CLOSET",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(16.dp),
                    color = NavyDeep
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items) { item ->
                        WardrobeItemCard(
                            item = item,
                            onDelete = { viewModel.deleteItem(item) }
                        )
                    }
                }
            }
        }

        if (isSheetOpen) {
            AddItemBottomSheet(
                onDismiss = { viewModel.closeSheet() },
                sheetState = sheetState,
                currentPhotoUri = currentPhotoUri,
                onGalleryClick = {
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onCameraClick = {
                    val uri = context.createTempPictureUri()
                    tempCameraUri = uri
                    viewModel.setPhotoUri(uri)
                    cameraLauncher.launch(uri)
                },
                onRetakeClick = {
                    viewModel.clearPhotoUri()
                },
                viewModel = viewModel
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WardrobeItemCard(item: ClothingItem, onDelete: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .border(2.dp, NavyDeep, RectangleShape)
            .combinedClickable(
                onClick = { },
                onLongClick = { showDeleteDialog = true }
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RectangleShape
    ) {
        Column {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer),
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
                        imageVector = Icons.Default.Camera,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            HorizontalDivider(color = NavyDeep, thickness = 2.dp)
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = item.category.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = NavyDeep,
                    maxLines = 1
                )
                Text(
                    text = "${item.color} / ${item.material}",
                    style = MaterialTheme.typography.bodySmall,
                    color = NavyDeep.copy(alpha = 0.7f),
                    maxLines = 1
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "DELETE ITEM?",
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = NavyDeep
                )
            },
            text = {
                Text(
                    text = "THIS WILL PERMANENTLY REMOVE THIS ITEM FROM YOUR CLOSET.",
                    fontWeight = FontWeight.Bold,
                    color = NavyDeep.copy(alpha = 0.7f)
                )
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RectangleShape,
            modifier = Modifier.border(2.dp, NavyDeep, RectangleShape),
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .border(2.dp, NavyDeep, RectangleShape),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NavyDeep,
                        contentColor = Color.White
                    )
                ) {
                    Text("YES, DELETE", fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(
                        "CANCEL",
                        fontWeight = FontWeight.Bold,
                        color = NavyDeep
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemBottomSheet(
    onDismiss: () -> Unit,
    sheetState: SheetState,
    currentPhotoUri: Uri?,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onRetakeClick: () -> Unit,
    viewModel: WardrobeViewModel
) {
    val aiCategory by viewModel.aiCategory.collectAsState()
    val aiColor by viewModel.aiColor.collectAsState()
    val aiMaterial by viewModel.aiMaterial.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RectangleShape,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle(color = NavyDeep) }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "ADD NEW ITEM",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = NavyDeep
            )

            if (currentPhotoUri != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AsyncImage(
                        model = currentPhotoUri,
                        contentDescription = "Selected Photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .border(2.dp, NavyDeep, RectangleShape),
                        contentScale = ContentScale.Crop
                    )
                    
                    if (isAiLoading) {
                        CircularProgressIndicator(color = NavyDeep)
                    } else {
                        Button(
                            onClick = { viewModel.analyzeImageWithAI(context) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .border(2.dp, NavyDeep, RectangleShape),
                            shape = RectangleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NavyDeep,
                                contentColor = Color.White
                            )
                        ) {
                            Text("ANALYZE WITH AI", fontWeight = FontWeight.Black)
                        }
                    }

                    TextButton(onClick = onRetakeClick) {
                        Text(
                            text = "RETAKE / RESELECT",
                            color = NavyDeep,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCameraClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp)
                            .border(2.dp, NavyDeep, RectangleShape),
                        shape = RectangleShape,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyDeep)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Camera, contentDescription = null)
                            Text(
                                text = "CAMERA", 
                                fontSize = 14.sp, 
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                    OutlinedButton(
                        onClick = onGalleryClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp)
                            .border(2.dp, NavyDeep, RectangleShape),
                        shape = RectangleShape,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyDeep)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Photo, contentDescription = null)
                            Text(
                                text = "GALLERY", 
                                fontSize = 14.sp, 
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            CategoryDropdown(
                selectedCategory = aiCategory,
                onCategorySelected = { viewModel.aiCategory.value = it }
            )

            OutlinedTextField(
                value = aiColor,
                onValueChange = {},
                label = { Text("COLOR (AI AUTO)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape,
                readOnly = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NavyDeep,
                    unfocusedBorderColor = NavyDeep,
                    focusedLabelColor = NavyDeep,
                    cursorColor = NavyDeep,
                    focusedTextColor = NavyDeep,
                    unfocusedTextColor = NavyDeep
                )
            )

            OutlinedTextField(
                value = aiMaterial,
                onValueChange = {},
                label = { Text("MATERIAL (AI AUTO)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape,
                readOnly = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NavyDeep,
                    unfocusedBorderColor = NavyDeep,
                    focusedLabelColor = NavyDeep,
                    cursorColor = NavyDeep,
                    focusedTextColor = NavyDeep,
                    unfocusedTextColor = NavyDeep
                )
            )

            Button(
                onClick = {
                    viewModel.saveItem(aiCategory, aiColor, aiMaterial)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(2.dp, NavyDeep, RectangleShape),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NavyDeep,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "SAVE ITEM", 
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf(
        "T-shirt", "Shirt/Blouse", "Hoodie/Sweater", "Jacket/Coat",
        "Trousers/Jeans", "Shorts", "Skirt", "Dress", "Shoes", "Accessories"
    )
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedCategory,
            onValueChange = {},
            readOnly = true,
            label = { Text("CATEGORY") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            shape = RectangleShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NavyDeep,
                unfocusedBorderColor = NavyDeep,
                focusedLabelColor = NavyDeep,
                focusedTextColor = NavyDeep,
                unfocusedTextColor = NavyDeep
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .border(2.dp, NavyDeep, RectangleShape)
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category, fontWeight = FontWeight.Medium) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun GridBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 1.dp.toPx()
        val step = 40.dp.toPx()
        val color = NavyDeep.copy(alpha = 0.05f)

        for (x in 0..size.width.toInt() step step.toInt()) {
            drawLine(
                color = color,
                start = Offset(x.toFloat(), 0f),
                end = Offset(x.toFloat(), size.height),
                strokeWidth = strokeWidth
            )
        }
        for (y in 0..size.height.toInt() step step.toInt()) {
            drawLine(
                color = color,
                start = Offset(0f, y.toFloat()),
                end = Offset(size.width, y.toFloat()),
                strokeWidth = strokeWidth
            )
        }
    }
}

package com.example.fitfusion.ui.wardrobe

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitfusion.BuildConfig
import com.example.fitfusion.data.database.WardrobeDao
import com.example.fitfusion.data.entity.ClothingItem
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WardrobeViewModel(private val dao: WardrobeDao) : ViewModel() {

    val clothingItems: StateFlow<List<ClothingItem>> = dao.getAllClothingItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isSheetOpen = MutableStateFlow(false)
    val isSheetOpen: StateFlow<Boolean> = _isSheetOpen.asStateFlow()

    val currentPhotoUri = MutableStateFlow<Uri?>(null)

    // AI States
    val aiCategory = MutableStateFlow("T-shirt")
    val aiColor = MutableStateFlow("")
    val aiMaterial = MutableStateFlow("")
    val isAiLoading = MutableStateFlow(false)

    fun setPhotoUri(uri: Uri?) {
        currentPhotoUri.value = uri
    }

    fun clearPhotoUri() {
        currentPhotoUri.value = null
        aiCategory.value = "T-shirt"
        aiColor.value = ""
        aiMaterial.value = ""
    }

    fun openSheet() {
        _isSheetOpen.value = true
    }

    fun closeSheet() {
        _isSheetOpen.value = false
        clearPhotoUri()
    }

    fun analyzeImageWithAI(context: Context) {
        val uri = currentPhotoUri.value ?: return

        viewModelScope.launch {
            isAiLoading.value = true
            try {
                // 1. Use the working 2.5 model
                val model = GenerativeModel(
                    modelName = "gemini-2.5-flash",
                    apiKey = BuildConfig.GEMINI_API_KEY
                )

                // 2. Decode the image
                val bitmap = withContext(Dispatchers.IO) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val source = ImageDecoder.createSource(context.contentResolver, uri)
                        ImageDecoder.decodeBitmap(source)
                    } else {
                        @Suppress("DEPRECATION")
                        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    }
                }

                // 3. Shrink the massive camera photo so Google doesn't reject the payload
                val maxDimension = 800
                val scale = maxDimension.toFloat() / maxOf(bitmap.width, bitmap.height)
                val scaledBitmap = if (scale < 1.0f) {
                    Bitmap.createScaledBitmap(
                        bitmap,
                        (bitmap.width * scale).toInt(),
                        (bitmap.height * scale).toInt(),
                        true
                    )
                } else {
                    bitmap
                }

                // 4. Ask for the JSON
                val prompt = """
                    Analyze this image. Identify the single main piece of clothing or accessory. You MUST classify the item into ONLY one of these exact categories: [T-shirt, Shirt, Hoodie, Jacket, Pants, Jeans, Shorts, Sneakers, Shoes, Boots, Watch, Hat, Chain, Bag]. Do NOT invent new categories. Return a JSON object with 'is_clear', 'category', 'color', 'material'.
                """.trimIndent()

                val response = model.generateContent(
                    content {
                        image(scaledBitmap)
                        text(prompt)
                    }
                )

                val rawText = response.text ?: throw Exception("Empty response from AI")

                // 5. Slice out the JSON manually to bypass markdown formatting bugs
                val startIndex = rawText.indexOf('{')
                val endIndex = rawText.lastIndexOf('}')

                if (startIndex == -1 || endIndex == -1) {
                    throw Exception("No JSON structure found in text.")
                }

                val cleanJson = rawText.substring(startIndex, endIndex + 1)
                val jsonObject = org.json.JSONObject(cleanJson)

                // 6. Safely update the text fields on the screen
                val isClear = jsonObject.optBoolean("is_clear", true)

                if (isClear) {
                    aiCategory.value = jsonObject.optString("category", "T-shirt")
                    aiColor.value = jsonObject.optString("color", "Unknown Color")
                    aiMaterial.value = jsonObject.optString("material", "Unknown Material")
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "IMAGE TOO BLURRY", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "ANALYSIS FAILED: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                Log.e("FitFusion_AI", "Error parsing AI response", e)
            } finally {
                isAiLoading.value = false
            }
        }
    }
    fun saveItem(context: Context, category: String, color: String, material: String) {
        val uri = currentPhotoUri.value
        if (uri != null) {
            viewModelScope.launch {
                val internalPath = withContext(Dispatchers.IO) {
                    saveImageToInternalStorage(context, uri)
                }
                if (internalPath != null) {
                    val newItem = ClothingItem(
                        imageUri = internalPath,
                        category = category,
                        color = color,
                        material = material
                    )
                    dao.insertClothingItem(newItem)
                    closeSheet()
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "ERROR SAVING IMAGE", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val fileName = "wardrobe_${System.currentTimeMillis()}.jpg"
            val file = java.io.File(context.filesDir, fileName)
            val outputStream = java.io.FileOutputStream(file)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            Log.e("FitFusion_SaveImage", "Failed to save image to internal storage", e)
            null
        }
    }

    fun deleteItem(item: ClothingItem) {
        viewModelScope.launch {
            dao.deleteClothingItem(item)
            item.imageUri?.let { path ->
                withContext(Dispatchers.IO) {
                    try {
                        val file = java.io.File(path)
                        if (file.exists()) {
                            file.delete()
                        }
                    } catch (e: Exception) {
                        Log.e("FitFusion_DeleteImage", "Failed to delete image file", e)
                    }
                }
            }
        }
    }
}

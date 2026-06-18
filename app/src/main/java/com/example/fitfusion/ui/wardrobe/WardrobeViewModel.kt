package com.example.fitfusion.ui.wardrobe

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitfusion.BuildConfig
import com.example.fitfusion.data.database.WardrobeDao
import com.example.fitfusion.data.entity.ClothingItem
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

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
                val model = GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = BuildConfig.GEMINI_API_KEY
                )

                val bitmap = withContext(Dispatchers.IO) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val source = ImageDecoder.createSource(context.contentResolver, uri)
                        ImageDecoder.decodeBitmap(source)
                    } else {
                        @Suppress("DEPRECATION")
                        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    }
                }

                val prompt = """
                    Analyze this image of a clothing item. 
                    Return ONLY a raw JSON object with the following keys:
                    "is_clear": (boolean, set to false if the image is too blurry, too dark, or does not contain clothing),
                    "category": (string, pick the closest from: T-shirt, Shirt/Blouse, Hoodie/Sweater, Jacket/Coat, Trousers/Jeans, Shorts, Skirt, Dress, Shoes, Accessories),
                    "color": (string, primary color),
                    "material": (string, e.g. Cotton, Denim, Wool, Leather).
                    Do not include any markdown formatting or extra text.
                """.trimIndent()

                val response = model.generateContent(
                    content {
                        image(bitmap)
                        text(prompt)
                    }
                )

                val resultText = response.text?.trim() ?: ""
                val jsonResult = JSONObject(resultText)

                if (jsonResult.getBoolean("is_clear")) {
                    aiCategory.value = jsonResult.getString("category")
                    aiColor.value = jsonResult.getString("color")
                    aiMaterial.value = jsonResult.getString("material")
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "IMAGE TOO BLURRY OR NO CLOTHING DETECTED", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "AI ANALYSIS FAILED: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                isAiLoading.value = false
            }
        }
    }

    fun saveItem(category: String, color: String, material: String) {
        val uri = currentPhotoUri.value
        if (uri != null) {
            viewModelScope.launch {
                val newItem = ClothingItem(
                    imageUri = uri.toString(),
                    category = category,
                    color = color,
                    material = material
                )
                dao.insertClothingItem(newItem)
                closeSheet()
            }
        }
    }

    fun deleteItem(item: ClothingItem) {
        viewModelScope.launch {
            dao.deleteClothingItem(item)
        }
    }
}

package com.example.fitfusion.ui.studio

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitfusion.BuildConfig
import com.example.fitfusion.data.database.WardrobeDao
import com.example.fitfusion.data.entity.ClothingItem
import com.example.fitfusion.data.entity.Outfit
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class MacroCategory {
    TOPS, BOTTOMS, FOOTWEAR, ACCESSORIES, UNKNOWN
}

class StudioViewModel(private val dao: WardrobeDao) : ViewModel() {

    val availableClothes: StateFlow<List<ClothingItem>> = dao.getAllClothingItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedClothes = MutableStateFlow<Set<Int>>(emptySet())
    val selectedClothes: StateFlow<Set<Int>> = _selectedClothes.asStateFlow()

    private val _generatedOutfits = MutableStateFlow<List<Outfit>>(emptyList())
    val generatedOutfits: StateFlow<List<Outfit>> = _generatedOutfits.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun getMacroCategory(item: ClothingItem): MacroCategory {
        val categoryLower = item.category.lowercase()
        return when {
            categoryLower.contains("t-shirt") || 
            categoryLower.contains("shirt") || 
            categoryLower.contains("hoodie") || 
            categoryLower.contains("sweater") || 
            categoryLower.contains("jacket") || 
            categoryLower.contains("coat") || 
            categoryLower.contains("blouse") ||
            categoryLower.contains("top") -> MacroCategory.TOPS
            
            categoryLower.contains("trouser") || 
            categoryLower.contains("jeans") || 
            categoryLower.contains("pants") || 
            categoryLower.contains("shorts") || 
            categoryLower.contains("skirt") || 
            categoryLower.contains("dress") ||
            categoryLower.contains("bottom") -> MacroCategory.BOTTOMS
            
            categoryLower.contains("shoe") || 
            categoryLower.contains("sneaker") || 
            categoryLower.contains("boot") || 
            categoryLower.contains("footwear") -> MacroCategory.FOOTWEAR
            
            categoryLower.contains("accessories") || 
            categoryLower.contains("accessory") || 
            categoryLower.contains("watch") || 
            categoryLower.contains("chain") || 
            categoryLower.contains("hat") -> MacroCategory.ACCESSORIES
            
            else -> MacroCategory.UNKNOWN
        }
    }

    val isSelectionValid: StateFlow<Boolean> = combine(
        availableClothes,
        _selectedClothes
    ) { clothes, selectedIds ->
        val selectedItems = clothes.filter { selectedIds.contains(it.id) }
        val topsCount = selectedItems.count { getMacroCategory(it) == MacroCategory.TOPS }
        val bottomsCount = selectedItems.count { getMacroCategory(it) == MacroCategory.BOTTOMS }
        topsCount >= 2 && bottomsCount >= 2
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun toggleSelection(itemId: Int) {
        _selectedClothes.value = if (_selectedClothes.value.contains(itemId)) {
            _selectedClothes.value - itemId
        } else {
            _selectedClothes.value + itemId
        }
    }

    fun generateOutfitsWithAI(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _generatedOutfits.value = emptyList()
            try {
                val model = GenerativeModel(
                    modelName = "gemini-2.5-flash",
                    apiKey = BuildConfig.GEMINI_API_KEY
                )

                val allItems = availableClothes.value
                val selectedIds = _selectedClothes.value
                val selectedItems = allItems.filter { selectedIds.contains(it.id) }

                val descriptionString = selectedItems.joinToString(separator = "\n") { item ->
                    "- ID: ${item.id}, Category: ${item.category}, Color: ${item.color}, Material: ${item.material} (${getMacroCategory(item)})"
                }

                val prompt = """
                    You are an expert Fashion Stylist and Architect.
                    Your task is to generate exactly 3 distinct, stylish outfits using a subset of the clothing items provided by the user.
                    Each outfit must be a combination of items matching current fashion trends.
                    
                    CRITICAL INSTRUCTIONS:
                    1. You must return ONLY a clean JSON array containing exactly 3 outfit objects. Do not include markdown code block syntax (like ```json ... ```). Return raw JSON text only.
                    2. Every outfit object inside the JSON array MUST contain the following keys exactly:
                       - "name": A stylish name for the outfit vibe (e.g., "Urban Explorer", "Minimalist Chic", "Retro Classic").
                       - "top": The exact ID (integer) of the selected top used in this outfit from the provided list.
                       - "bottom": The exact ID (integer) of the selected bottom used in this outfit from the provided list.
                       - "footwear": If a footwear item (from the provided list) is selected and used in this outfit, return its ID (integer). If no footwear was selected or if you recommend a better fit, return a recommended shoe choice as a descriptive string (e.g., "White leather sneakers").
                       - "accessories": If an accessory item (from the provided list) is selected and used in this outfit, return its ID (integer). If none is used or if you want to recommend a specific accessory, return a string describing the accessory, or null if no accessory is needed.
                       - "reasoning": A comprehensive, highly detailed paragraph explaining the specific fashion trend matching, color theory compatibility, and why these specific items complement each other perfectly.
                    
                    Here are the user's selected clothing items:
                    $descriptionString
                """.trimIndent()

                val response = withContext(Dispatchers.IO) {
                    model.generateContent(prompt)
                }

                val rawText = response.text ?: throw Exception("Empty response from Gemini")

                val startIndex = rawText.indexOf('[')
                val endIndex = rawText.lastIndexOf(']')

                if (startIndex == -1 || endIndex == -1) {
                    throw Exception("No JSON array structure found in text: $rawText")
                }

                val cleanJson = rawText.substring(startIndex, endIndex + 1)
                val jsonArray = org.json.JSONArray(cleanJson)

                val outfitsList = mutableListOf<Outfit>()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val name = obj.optString("name", "Generated Outfit ${i + 1}")
                    val reasoning = obj.optString("reasoning", "")

                    val itemsInOutfit = mutableListOf<ClothingItem>()

                    // Parse top
                    val topId = obj.optInt("top", -1)
                    selectedItems.find { it.id == topId }?.let { itemsInOutfit.add(it) }

                    // Parse bottom
                    val bottomId = obj.optInt("bottom", -1)
                    selectedItems.find { it.id == bottomId }?.let { itemsInOutfit.add(it) }

                    // Parse footwear
                    if (obj.has("footwear") && !obj.isNull("footwear")) {
                        val footwearVal = obj.get("footwear")
                        if (footwearVal is Number) {
                            selectedItems.find { it.id == footwearVal.toInt() }?.let { itemsInOutfit.add(it) }
                        } else if (footwearVal is String && footwearVal.isNotEmpty()) {
                            // Recommended footwear
                            itemsInOutfit.add(
                                ClothingItem(
                                    id = -1,
                                    imageUri = null,
                                    category = "Recommended Footwear",
                                    color = footwearVal,
                                    material = "N/A"
                                )
                            )
                        }
                    }

                    // Parse accessories
                    if (obj.has("accessories") && !obj.isNull("accessories")) {
                        val accessoriesVal = obj.get("accessories")
                        if (accessoriesVal is Number) {
                            selectedItems.find { it.id == accessoriesVal.toInt() }?.let { itemsInOutfit.add(it) }
                        } else if (accessoriesVal is String && accessoriesVal.isNotEmpty()) {
                            // Recommended accessory
                            itemsInOutfit.add(
                                ClothingItem(
                                    id = -2,
                                    imageUri = null,
                                    category = "Recommended Accessory",
                                    color = accessoriesVal,
                                    material = "N/A"
                                )
                            )
                        }
                    }

                    outfitsList.add(Outfit(name = name, items = itemsInOutfit, reasoning = reasoning))
                }

                _generatedOutfits.value = outfitsList

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "GENERATION FAILED: ${e.message}", Toast.LENGTH_LONG).show()
                }
                Log.e("FitFusion_Studio", "Error generating outfits", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveOutfit(outfit: Outfit) {
        viewModelScope.launch {
            val actualItems = outfit.items.filter { it.id > 0 }
            val dbOutfit = outfit.copy(items = actualItems)
            dao.insertOutfit(dbOutfit)
        }
    }
}

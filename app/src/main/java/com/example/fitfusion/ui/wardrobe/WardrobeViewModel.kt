package com.example.fitfusion.ui.wardrobe

import androidx.lifecycle.ViewModel
import com.example.fitfusion.data.entity.ClothingItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WardrobeViewModel : ViewModel() {

    private val _clothingItems = MutableStateFlow<List<ClothingItem>>(
        listOf(
            ClothingItem(1, null, "T-shirt", "Teal", "Cotton"),
            ClothingItem(2, null, "Jacket", "Navy", "Wool"),
            ClothingItem(3, null, "Shorts", "Coral", "Linen"),
            ClothingItem(4, null, "Shoes", "Beige", "Leather"),
            ClothingItem(5, null, "Hoodie", "Gray", "Fleece"),
            ClothingItem(6, null, "Accessories", "Gold", "Metal")
        )
    )
    val clothingItems: StateFlow<List<ClothingItem>> = _clothingItems.asStateFlow()

    private val _isSheetOpen = MutableStateFlow(false)
    val isSheetOpen: StateFlow<Boolean> = _isSheetOpen.asStateFlow()

    fun openSheet() {
        _isSheetOpen.value = true
    }

    fun closeSheet() {
        _isSheetOpen.value = false
    }
}

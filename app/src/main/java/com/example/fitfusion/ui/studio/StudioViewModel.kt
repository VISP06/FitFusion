package com.example.fitfusion.ui.studio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitfusion.data.entity.ClothingItem
import com.example.fitfusion.data.entity.Outfit
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudioViewModel : ViewModel() {

    private val _availableClothes = MutableStateFlow<List<ClothingItem>>(
        listOf(
            ClothingItem(1, null, "T-shirt", "Teal", "Cotton"),
            ClothingItem(2, null, "Jacket", "Navy", "Wool"),
            ClothingItem(3, null, "Shorts", "Coral", "Linen"),
            ClothingItem(4, null, "Shoes", "Beige", "Leather"),
            ClothingItem(5, null, "Hoodie", "Gray", "Fleece"),
            ClothingItem(6, null, "Accessories", "Gold", "Metal")
        )
    )
    val availableClothes: StateFlow<List<ClothingItem>> = _availableClothes.asStateFlow()

    private val _selectedClothes = MutableStateFlow<Set<Int>>(emptySet())
    val selectedClothes: StateFlow<Set<Int>> = _selectedClothes.asStateFlow()

    private val _generatedOutfits = MutableStateFlow<List<Outfit>>(emptyList())
    val generatedOutfits: StateFlow<List<Outfit>> = _generatedOutfits.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun toggleSelection(itemId: Int) {
        _selectedClothes.value = if (_selectedClothes.value.contains(itemId)) {
            _selectedClothes.value - itemId
        } else {
            _selectedClothes.value + itemId
        }
    }

    fun generateCombinations() {
        viewModelScope.launch {
            _isLoading.value = true
            _generatedOutfits.value = emptyList()
            
            // Simulate AI Processing
            delay(2000)

            val selectedItems = _availableClothes.value.filter { _selectedClothes.value.contains(it.id) }
            val pool = if (selectedItems.isEmpty()) _availableClothes.value else selectedItems

            val results = (1..3).map { index ->
                Outfit(
                    id = index,
                    name = "COMBO 0$index",
                    items = pool.shuffled().take((2..4).random())
                )
            }

            _generatedOutfits.value = results
            _isLoading.value = false
        }
    }
}

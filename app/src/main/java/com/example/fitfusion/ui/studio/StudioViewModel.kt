package com.example.fitfusion.ui.studio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitfusion.data.database.WardrobeDao
import com.example.fitfusion.data.entity.ClothingItem
import com.example.fitfusion.data.entity.Outfit
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

            val allItems = availableClothes.value
            val selectedIds = _selectedClothes.value
            val pool = if (selectedIds.isEmpty()) allItems else allItems.filter { selectedIds.contains(it.id) }

            if (pool.isNotEmpty()) {
                val results = (1..3).map { index ->
                    Outfit(
                        name = "COMBO 0$index",
                        items = pool.shuffled().take((2..4).random().coerceAtMost(pool.size))                    )
                }
                _generatedOutfits.value = results
            }

            _isLoading.value = false
        }
    }

    fun saveOutfit(outfit: Outfit) {
        viewModelScope.launch {
            dao.insertOutfit(outfit)
        }
    }
}

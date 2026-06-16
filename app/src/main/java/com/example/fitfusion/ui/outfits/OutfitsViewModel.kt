package com.example.fitfusion.ui.outfits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitfusion.data.database.WardrobeDao
import com.example.fitfusion.data.entity.Outfit
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OutfitsViewModel(private val dao: WardrobeDao) : ViewModel() {
    
    val savedOutfits: StateFlow<List<Outfit>> = dao.getAllOutfits()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteOutfit(outfit: Outfit) {
        viewModelScope.launch {
            dao.deleteOutfit(outfit)
        }
    }
}

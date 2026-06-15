package com.example.fitfusion.ui.outfits

import androidx.lifecycle.ViewModel
import com.example.fitfusion.data.entity.ClothingItem
import com.example.fitfusion.data.entity.Outfit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OutfitsViewModel : ViewModel() {
    private val _savedOutfits = MutableStateFlow<List<Outfit>>(emptyList())
    val savedOutfits: StateFlow<List<Outfit>> = _savedOutfits.asStateFlow()

    init {
        loadMockOutfits()
    }

    private fun loadMockOutfits() {
        _savedOutfits.value = listOf(
            Outfit(
                id = 1,
                name = "STREETWEAR 01",
                items = listOf(
                    ClothingItem(1, null, "Oversized Tee", "Off-White", "Cotton"),
                    ClothingItem(2, null, "Cargo Pants", "Black", "Nylon"),
                    ClothingItem(3, null, "Sneakers", "White/Grey", "Leather")
                )
            ),
            Outfit(
                id = 2,
                name = "FORMAL 02",
                items = listOf(
                    ClothingItem(4, null, "Blazer", "Navy", "Wool"),
                    ClothingItem(5, null, "Dress Shirt", "Light Blue", "Cotton"),
                    ClothingItem(6, null, "Chinos", "Beige", "Cotton"),
                    ClothingItem(7, null, "Loafers", "Brown", "Leather")
                )
            ),
            Outfit(
                id = 3,
                name = "MINIMALIST 03",
                items = listOf(
                    ClothingItem(8, null, "Turtle Neck", "Black", "Wool"),
                    ClothingItem(9, null, "Slacks", "Dark Grey", "Synthetic")
                )
            )
        )
    }
}

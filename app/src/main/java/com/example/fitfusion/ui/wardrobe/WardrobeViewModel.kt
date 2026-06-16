package com.example.fitfusion.ui.wardrobe

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitfusion.data.database.WardrobeDao
import com.example.fitfusion.data.entity.ClothingItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

    fun setPhotoUri(uri: Uri?) {
        currentPhotoUri.value = uri
    }

    fun clearPhotoUri() {
        currentPhotoUri.value = null
    }

    fun openSheet() {
        _isSheetOpen.value = true
    }

    fun closeSheet() {
        _isSheetOpen.value = false
        clearPhotoUri()
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

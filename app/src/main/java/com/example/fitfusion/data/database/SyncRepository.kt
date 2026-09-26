package com.example.fitfusion.data.database

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import com.example.fitfusion.data.entity.ClothingItem

@Serializable
data class NetworkClothingItem(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("image_uri") val imageUri: String?,
    val category: String,
    val color: String,
    val material: String,
    @SerialName("is_synced") val isSynced: Boolean
)

class SyncRepository(private val dao: WardrobeDao, private val supabase: SupabaseClient) {
    suspend fun pushUnsyncedItems() {
        try {
            val unsynced = dao.getUnsyncedClothingItems()
            for (item in unsynced) {
                val networkItem = NetworkClothingItem(item.id, item.userId, item.imageUri, item.category, item.color, item.material, true)
                supabase.postgrest["clothing_items"].insert(networkItem)
                dao.insertClothingItem(item.copy(isSynced = true))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

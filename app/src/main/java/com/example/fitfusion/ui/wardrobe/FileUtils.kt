package com.example.fitfusion.ui.wardrobe

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

/**
 * Creates a temporary image file in the app's cache directory and returns its [Uri]
 * using the defined [FileProvider].
 */
fun Context.createTempPictureUri(): Uri {
    val tempFile = File.createTempFile(
        "picture_${System.currentTimeMillis()}",
        ".jpg",
        cacheDir
    ).apply {
        createNewFile()
        deleteOnExit()
    }

    return FileProvider.getUriForFile(
        this,
        "com.example.fitfusion.fileprovider",
        tempFile
    )
}

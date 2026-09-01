package com.example.fitfusion.data.database

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import com.example.fitfusion.BuildConfig // Replace with your actual package name
import io.github.jan.supabase.auth.Auth

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest)
    }
}
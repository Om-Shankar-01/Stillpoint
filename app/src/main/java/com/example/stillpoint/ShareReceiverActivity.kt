package com.example.stillpoint

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.stillpoint.data.ContentRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ShareReceiverActivity : ComponentActivity() {

    @Inject
    lateinit var repository: ContentRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent?.action == Intent.ACTION_SEND) {
            // Check if the shared data is plain text
            if ("text/plain" == intent.type) {
                // Get the shared text (the URL)
                intent.getStringExtra(Intent.EXTRA_TEXT)?.let { url ->
                    lifecycleScope.launch {
                        Log.d("ShareReceiver", "Attempting to save URL: $url")
                        val result = repository.saveContentFromUrl(url)
                        val message =
                            if (result.isSuccess) "Item saved successfully" else "Error saving URL"
                        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        finish()
    }
}
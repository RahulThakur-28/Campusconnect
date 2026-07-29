package com.rahul.campusconnect

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.rahul.campusconnect.domain.model.AppTheme
import com.rahul.campusconnect.navigation.AppNavGraph
import com.rahul.campusconnect.ui.theme.CampusconnectTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d("STARTUP", "1. onCreate started")



        super.onCreate(savedInstanceState)

        Log.d("STARTUP", "2. super completed")

        enableEdgeToEdge()

        setContent {

            Log.d("STARTUP", "3. setContent called")

            val theme by viewModel.theme.collectAsState()

            val isDarkTheme = when (theme) {
                AppTheme.SYSTEM -> isSystemInDarkTheme()
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
            }

            CampusconnectTheme(darkTheme = isDarkTheme) {
                AppNavGraph()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    CampusconnectTheme {
        AppNavGraph()
    }
}
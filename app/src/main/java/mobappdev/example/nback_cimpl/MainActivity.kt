package mobappdev.example.nback_cimpl

import EndGameScreen
import android.content.res.Configuration
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mobappdev.example.nback_cimpl.ui.screens.AudioScreen
import mobappdev.example.nback_cimpl.ui.screens.HomeScreen
import mobappdev.example.nback_cimpl.ui.screens.VisualScreen
import mobappdev.example.nback_cimpl.ui.theme.NBack_CImplTheme
import mobappdev.example.nback_cimpl.ui.viewmodels.GameVM
import java.util.Locale

//import mobappdev.example.nback_cimpl.ui.screens.VisualScreenActivity

/**
 * This is the MainActivity of the application
 *
 * Your navigation between the two (or more) screens should be handled here
 * For this application you need at least a homescreen (a start is already made for you)
 * and a gamescreen (you will have to make yourself, but you can use the same viewmodel)
 *
 * Date: 25-08-2023
 * Version: Version 1.0
 * Author: Yeetivity
 *
 */


class MainActivity : ComponentActivity() {
    private lateinit var textToSpeech: TextToSpeech
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NBack_CImplTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Instantiate the viewmodel
                    val gameViewModel: GameVM = viewModel(
                        factory = GameVM.Factory
                    )


                    textToSpeech = TextToSpeech(this){status->
                        if(status == TextToSpeech.SUCCESS){
                            val result = textToSpeech.setLanguage(Locale.getDefault())

                            if(result == TextToSpeech.LANG_MISSING_DATA
                                || result == TextToSpeech.LANG_NOT_SUPPORTED){
                                Toast.makeText(this, "language is not supported", Toast.LENGTH_LONG).show()
                            }
                            gameViewModel.setTextToSpeech(textToSpeech)
                        }
                    }

                    val navController = rememberNavController()
                   
                    val configuration = LocalConfiguration.current

                    LaunchedEffect(configuration) {
                        snapshotFlow { configuration.orientation }
                            .collect { orientation ->
                                when (orientation) {
                                    Configuration.ORIENTATION_LANDSCAPE -> {
                                        Log.d("GameVM","This is Landscape")
                                    }
                                    else -> {
                                        Log.d("GameVM","This is Portrait")
                                    }
                                }
                            }
                    }



                    NavHost(navController = navController, startDestination = "HomeScreen") {
                        composable("HomeScreen") {
                            HomeScreen(vm = gameViewModel, navController = navController)
                        }
                        composable("VisualScreen") {
                            VisualScreen(vm = gameViewModel, navController = navController)
                        }
                        composable("EndGameScreen") {
                            EndGameScreen(vm = gameViewModel, navController = navController)
                        }
                        composable("AudioScreen") {
                            AudioScreen(vm = gameViewModel, navController = navController)
                        }
                    }
                }
            }
        }
    }
}
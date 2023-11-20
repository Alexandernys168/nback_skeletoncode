package mobappdev.example.nback_cimpl.ui.screens

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import mobappdev.example.nback_cimpl.R
import mobappdev.example.nback_cimpl.ui.viewmodels.GameType
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel

@Composable
fun AudioScreen(
    vm: GameViewModel,
    navController: NavController
) {
    var wrongAnswer by remember { mutableStateOf(false) }

    var orientation by remember { mutableStateOf(Configuration.ORIENTATION_PORTRAIT) }

    val configuration = LocalConfiguration.current

    // If our configuration changes then this will launch a new coroutine scope for it
    LaunchedEffect(configuration) {
        // Save any changes to the orientation value on the configuration object
        snapshotFlow { configuration.orientation }
            .collect { orientation = it }
    }

    LaunchedEffect(vm.wrongAnswer) {
        if (vm.wrongAnswer != 0) {
            wrongAnswer = true
            delay(1200) // Adjust the duration as needed (2000ms = 2 seconds)
            wrongAnswer = false
            vm.clearWrongAnswer()
            delay(100)
        }
    }



    LaunchedEffect(vm) {
        vm.setGameType(GameType.Audio)
        vm.startGame()

    }


    val gameState by vm.gameState.collectAsState()  // Collect the game state
    val score by vm.score.collectAsState()  // Collect the score state

    // Use LaunchedEffect to handle navigation
    LaunchedEffect(gameState.eventValue) {
        if (gameState.eventValue == -555) {
            navController.navigate("HomeScreen")
            Log.d("HomeVm", "I am home now")
        }
    }

    val speakerImage = if (gameState.eventValue > 0) {
        painterResource(id = R.drawable.sound_on) // Replace with your speaker image resource
    } else {
        null // No speaker image
    }


    val blueColor = Color(0xFF86B9EE)
    val gray_blueColor = Color(0xffbed8f1)
    val greenColor = Color(0xff7dd69e)

    when (orientation){
        Configuration.ORIENTATION_LANDSCAPE -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(blueColor)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top
                ){
                    Text(
                        text = "Score: $score",
                        modifier = Modifier.padding(8.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.displayMedium
                    )



                    Text(
                        text = "Current Round: ${vm.getCurrentRound()}",
                        modifier = Modifier.padding(8.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                    // Big Picture of speaker
                    speakerImage?.let { image ->
                        Image(
                            painter = image,
                            contentDescription = "Speaker Image",
                            modifier = Modifier
                                .size(125.dp) // Adjust the size as needed
                                .align(Alignment.CenterHorizontally)
                                .padding(16.dp)
                        )
                    }
                }


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(if (wrongAnswer) Color.Red else greenColor)
                        .align(Alignment.BottomCenter),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "AUDIO",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .clickable {
                                vm.checkMatch()
                            }
                            .padding(16.dp)
                    )
                }
            }
        }
        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(blueColor)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = "Score: $score",
                        modifier = Modifier.padding(16.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.displayMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Current Round: ${vm.getCurrentRound()}",
                        modifier = Modifier.padding(8.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Big Picture of speaker
                    speakerImage?.let { image ->
                        Image(
                            painter = image,
                            contentDescription = "Speaker Image",
                            modifier = Modifier
                                .size(200.dp) // Adjust the size as needed
                                .align(Alignment.CenterHorizontally)
                                .padding(16.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(if (wrongAnswer) Color.Red else greenColor)
                        .align(Alignment.BottomCenter),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "AUDIO",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .clickable {
                                vm.checkMatch()
                            }
                            .padding(16.dp)
                    )
                }
            }
        }
    }


}
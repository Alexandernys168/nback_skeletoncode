package mobappdev.example.nback_cimpl.ui.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import mobappdev.example.nback_cimpl.R
import mobappdev.example.nback_cimpl.ui.viewmodels.GameType
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel

@Composable
fun AudioScreen(
    vm: GameViewModel,
    navController: NavController
) {



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


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(blueColor)
    ) {

        Text(
            text = "Score: $score",
            modifier = Modifier.padding(16.dp),
            color = Color.White,
            style = MaterialTheme.typography.displayMedium
        )


        Spacer(modifier = Modifier.height(64.dp))
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
                    .size(200.dp) // Adjust the size as needed
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        }

        // Linear layout at the bottom with green background and text
        Row(
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
                .background(greenColor)
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
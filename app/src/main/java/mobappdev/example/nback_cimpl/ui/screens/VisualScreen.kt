package mobappdev.example.nback_cimpl.ui.screens

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import mobappdev.example.nback_cimpl.ui.viewmodels.GameVM
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel
import androidx.compose.ui.res.colorResource
import kotlinx.coroutines.delay
import mobappdev.example.nback_cimpl.ui.viewmodels.GameType

/*
class VisualScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Instantiate the viewmodel
            val gameViewModel: GameVM = viewModel(
                factory = GameVM.Factory
            )

        }
    }


}
*/

@Composable
fun VisualScreen(
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

    LaunchedEffect(vm) {
        vm.setGameType(GameType.Visual)
        vm.startGame()
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


    val gameState by vm.gameState.collectAsState()  // Collect the game state
    val score by vm.score.collectAsState()  // Collect the score state

    // Use LaunchedEffect to handle navigation
    LaunchedEffect(gameState.eventValue) {
        if (gameState.eventValue == -555) {
            navController.navigate("HomeScreen")
            Log.d("HomeVm", "I am home now")
        }
    }

    val blueColor = Color(0xFF86B9EE)
    val gray_blueColor = Color(0xffbed8f1)
    val greenColor = Color(0xff7dd69e)


    when (orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(blueColor)
            ) {

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
                // Grid layout for blue squares
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(3) { row ->
                        Column(modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.SpaceEvenly) {
                            repeat(3) { col ->

                                val index = row * 3 + col
                                val isLit =
                                    gameState.eventValue == index  // Check if the square should be lit

                                Spacer(
                                    modifier = Modifier
                                        //.size(50.dp)
                                        .weight(1f)
                                        .aspectRatio(9f / 2f)
                                        .padding(4.dp)
                                        .background(if (isLit) Color.Yellow else gray_blueColor)
                                )
                            }
                        }
                    }
                }
                // Linear layout at the bottom with green background and text
                Row(
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth()
                        .background(if (wrongAnswer) Color.Red else greenColor),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "POSITION",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
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
            Column(
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
                // Grid layout for blue squares
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(32.dp)
                ) {
                    items(3) { row ->
                        Row(Modifier.fillMaxWidth()) {
                            repeat(3) { col ->

                                val index = row * 3 + col
                                val isLit =
                                    gameState.eventValue == index  // Check if the square should be lit

                                Spacer(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .weight(1f)
                                        .padding(4.dp)
                                        .background(if (isLit) Color.Yellow else gray_blueColor)
                                )
                            }
                        }
                    }
                }
                // Linear layout at the bottom with green background and text
                Row(
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .background(if (wrongAnswer) Color.Red else greenColor),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "POSITION",
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
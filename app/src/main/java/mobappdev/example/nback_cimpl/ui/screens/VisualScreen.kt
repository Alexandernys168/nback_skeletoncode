package mobappdev.example.nback_cimpl.ui.screens

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import mobappdev.example.nback_cimpl.ui.viewmodels.GameVM
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel
import androidx.compose.ui.res.colorResource

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

    LaunchedEffect(vm) {
        vm.startGame()
    }

    val gameState by vm.gameState.collectAsState()  // Collect the game state
    val score by vm.score.collectAsState()  // Collect the score state

    val blueColor = Color(0xFF86B9EE)
    val gray_blueColor = Color(0xffbed8f1)
    val greenColor = Color(0xff7dd69e)


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
                .background(greenColor),
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
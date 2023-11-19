import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel




@Composable
fun EndGameScreen(

    vm: GameViewModel,
    navController: NavController


) {
    val highScore by vm.highscore.collectAsState()

    val blueColor = Color(0xFF86B9EE)
    val gray_blueColor = Color(0xffbed8f1)
    val darkBlueColor = Color(0xFF336699)
    val greenColor = Color(0xFF7DD69E)
    val textColor = Color.White

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(blueColor)
            .padding(top = 100.dp)
            .padding(16.dp)

    ) {
        Text(text = "Game Completed", color = darkBlueColor,  modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp))
        // Dark blue square displaying total score and information
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(darkBlueColor)

                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()

            ) {
                Text(text = "Total Score: $highScore", color = textColor)
                Spacer(modifier = Modifier.height(16.dp))
                InfoRectangle(title = "N = ", value = "x", color = greenColor)
                Spacer(modifier = Modifier.height(8.dp))
                InfoRectangle(title = "Playtime", value = "x", color = greenColor)
                Spacer(modifier = Modifier.height(8.dp))
                InfoRectangle(title = "Interval", value = "x", color = greenColor)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { navController.navigate("VisualScreen") }) {
                Text(text = "Play again")
            }
            Button(onClick = { navController.navigate("HomeScreen") }) {
                Text(text = "Return Home")
            }
        }
    }
}

@Composable
fun InfoRectangle(title: String, value: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color)
            .padding(8.dp)
            .width(120.dp)
            .height(60.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = Color.White, textAlign = TextAlign.Center)
        }
    }
}

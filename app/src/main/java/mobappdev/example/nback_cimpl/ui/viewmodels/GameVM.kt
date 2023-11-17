package mobappdev.example.nback_cimpl.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mobappdev.example.nback_cimpl.GameApplication
import mobappdev.example.nback_cimpl.NBackHelper
import mobappdev.example.nback_cimpl.data.UserPreferencesRepository

/**
 * This is the GameViewModel.
 *
 * It is good practice to first make an interface, which acts as the blueprint
 * for your implementation. With this interface we can create fake versions
 * of the viewmodel, which we can use to test other parts of our app that depend on the VM.
 *
 * Our viewmodel itself has functions to start a game, to specify a gametype,
 * and to check if we are having a match
 *
 * Date: 25-08-2023
 * Version: Version 1.0
 * Author: Yeetivity
 *
 */


interface GameViewModel {
    val gameState: StateFlow<GameState>
    val score: StateFlow<Int>
    val highscore: StateFlow<Int>
    val nBack: Int

    fun setGameType(gameType: GameType)
    fun startGame()

    fun checkMatch()
}

class GameVM(
    private val userPreferencesRepository: UserPreferencesRepository
) : GameViewModel, ViewModel() {
    private val _gameState = MutableStateFlow(GameState())
    override val gameState: StateFlow<GameState>
        get() = _gameState.asStateFlow()

    private val _score = MutableStateFlow(0)
    override val score: StateFlow<Int>
        get() = _score

    private val _highscore = MutableStateFlow(0)
    override val highscore: StateFlow<Int>
        get() = _highscore

    // nBack is currently hardcoded
    override val nBack: Int = 2

    private var job: Job? = null  // coroutine job for the game event
    private val eventInterval: Long = 2000L  // 2000 ms (2s)

    private val nBackHelper = NBackHelper()  // Helper that generate the event array
    private var events = emptyArray<Int>()  // Array with all events

    private var _passedEvents = mutableListOf<Int>() // mutableListOf to store passed events

    private var matchedEventsRounds =
        HashSet<Pair<Int, Int>>() // Stores matched events and their rounds

    private val _invalidValueChanges = mutableSetOf<Int>() // Track changes made by invalidValue

    private val invalidValue = -999 // Define your invalid value here

    private var currentRound = 0 // Variable to track the current round

    private var lastEventValue: Int = -1


    override fun setGameType(gameType: GameType) {
        // update the gametype in the gamestate
        _gameState.value = _gameState.value.copy(gameType = gameType)
    }

    override fun startGame() {
        job?.cancel()  // Cancel any existing game loop

        // Get the events from our C-model (returns IntArray, so we need to convert to Array<Int>)
        events = nBackHelper.generateNBackString(10, 9, 30, nBack).toList()
            .toTypedArray()  // Todo Higher Grade: currently the size etc. are hardcoded, make these based on user input
        Log.d("GameVM", "The following sequence was generated: ${events.contentToString()}")

        job = viewModelScope.launch {
            when (gameState.value.gameType) {
                GameType.Audio -> runAudioGame()
                GameType.AudioVisual -> runAudioVisualGame()
                GameType.Visual -> runVisualGame(events)
            }
            // Todo: update the highscore
        }
    }

    override fun checkMatch() {
        /**
         * Todo: This function should check if there is a match when the user presses a match button
         * Make sure the user can only register a match once for each event.
         */
        val currentEvent = _gameState.value.eventValue

        // Ensure there is a valid event value and there are enough previous events for comparison
        if (currentEvent != -1 && events.size > nBack) {
            // Check if the current event value is different from the last one
            if (currentEvent != lastEventValue && currentEvent != invalidValue) {

                val nBackEvent = events[events.size - nBack - 2]
                val currentRound = calculateCurrentRound() // Calculate the current round
                Log.d(
                    "GameVM",
                    "currentEvent: $currentEvent, lastEventValue: $lastEventValue, nBackEvent: $nBackEvent"
                )
                val previouslyMatched =
                    matchedEventsRounds.any { it.first == currentEvent && it.second != currentRound }

                if (lastEventValue == currentEvent && !previouslyMatched) {
                    _score.value += 1
                    matchedEventsRounds.add(
                        Pair(
                            currentEvent,
                            currentRound
                        )
                    ) // Add the event and its round to the matched set
                    Log.d("GameVM", "Match Found!")
                } else {
                    Log.d("GameVM", "No Match Found or Already Matched")
                    // No match or already matched
                }




                // Check if the event was previously matched in a different round

                /*
                                // Compare the current event with the one nBack positions ago and check if it wasn't previously matched
                                if (!previouslyMatched) {
                                    _score.value += 1  // Increase the score
                                    matchedEventsRounds.add(Pair(currentEvent, currentRound)) // Add the event and its round to the matched set
                                    Log.d("GameVM", "Match Found!")
                                } else {
                                    Log.d("GameVM", "No Match Found or Already Matched")
                                    // No match or already matched
                                }

                 */
            }
        }

    }

    private fun runAudioGame() {
        // Todo: Make work for Basic grade
    }

    private suspend fun runVisualGame(events: Array<Int>) {

        // Todo: Replace this code for actual game code
        for (value in events) {
            currentRound++

            _gameState.value = _gameState.value.copy(eventValue = value)

            delay(eventInterval)

            _invalidValueChanges.add(invalidValue)
            _gameState.value = _gameState.value.copy(eventValue = invalidValue)
            delay(1000L)

        }

    }

    private fun runAudioVisualGame() {
        // Todo: Make work for Higher grade
    }

    private fun calculateCurrentRound(): Int {
        return currentRound // Return the current round value
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GameApplication)
                GameVM(application.userPreferencesRespository)
            }
        }
    }

    init {
        // Code that runs during creation of the vm
        viewModelScope.launch {
            userPreferencesRepository.highscore.collect {
                _highscore.value = it
            }
        }
    }
}

// Class with the different game types
enum class GameType {
    Audio,
    Visual,
    AudioVisual
}

data class GameState(
    // You can use this state to push values from the VM to your UI.
    val gameType: GameType = GameType.Visual,  // Type of the game
    val eventValue: Int = -1  // The value of the array string
)

class FakeVM : GameViewModel {
    override val gameState: StateFlow<GameState>
        get() = MutableStateFlow(GameState()).asStateFlow()
    override val score: StateFlow<Int>
        get() = MutableStateFlow(2).asStateFlow()
    override val highscore: StateFlow<Int>
        get() = MutableStateFlow(42).asStateFlow()
    override val nBack: Int
        get() = 2

    override fun setGameType(gameType: GameType) {
    }

    override fun startGame() {
    }

    override fun checkMatch() {
    }
}
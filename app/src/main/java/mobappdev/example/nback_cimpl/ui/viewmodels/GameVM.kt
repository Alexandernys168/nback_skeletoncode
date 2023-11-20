package mobappdev.example.nback_cimpl.ui.viewmodels

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mobappdev.example.nback_cimpl.GameApplication
import mobappdev.example.nback_cimpl.MainActivity
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
    val numberOfEvents: Int
    val totalTimeBetweenIntervals: Long
    val wrongAnswer: Int

    fun clearWrongAnswer()
    fun getCurrentRound(): Int

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
    private val hiddenEventInterval: Long = 1000L // 1000 ms (1s)
    override val totalTimeBetweenIntervals: Long = eventInterval + hiddenEventInterval
    override val numberOfEvents: Int = 10

    private val nBackHelper = NBackHelper()  // Helper that generate the event array
    private var events = emptyArray<Int>()  // Array with all events

    private var _givenPointThisRound: Int = 0;

    override var wrongAnswer: Int =0

    override fun clearWrongAnswer() {
        wrongAnswer =0
    }



    private var _passedEvents = mutableListOf<Int>() // mutableListOf to store passed events

    private var matchedEventsRounds =
        HashSet<Pair<Int, Int>>() // Stores matched events and their rounds

    private val _invalidValueChanges = mutableSetOf<Int>() // Track changes made by invalidValue

    private val invalidValue = -999 // Define your invalid value here
    private val gameHasEnded = -555 // Sets the aeventValue which indicates that the game is finished

    private var currentRound = 0 // Variable to track the current round

    private var _currentEventValue: Int = -1

    private lateinit var textToSpeech: TextToSpeech

    fun setTextToSpeech(textToSpeech: TextToSpeech) {
        this.textToSpeech = textToSpeech
    }

    private val valueToLetterMap = mapOf(
        1 to "A",
        2 to "B",
        3 to "C",
        4 to "D",
        5 to "E",
        6 to "F",
        7 to "G",
        8 to "H",
        9 to "I"
    )

    private val letterToValueMap = mapOf(
        "A" to 1,
        "B" to 2,
        "C" to 3,
        "D" to 4,
        "E" to 5,
        "F" to 6,
        "G" to 7,
        "H" to 8,
        "I" to 9
    )


    private val eventsToSpeak = mutableListOf<String>()





    override fun setGameType(gameType: GameType) {
        // update the gametype in the gamestate
        _gameState.value = _gameState.value.copy(gameType = gameType)

    }

    override fun startGame() {
        job?.cancel()  // Cancel any existing game loop
        _gameState.value = _gameState.value.copy(eventValue = 0)
        currentRound=0
        _score.value=0

        // Get the events from our C-model (returns IntArray, so we need to convert to Array<Int>)
        events = nBackHelper.generateNBackString(numberOfEvents, 9, 30, nBack).toList()
            .toTypedArray()  // Todo Higher Grade: currently the size etc. are hardcoded, make these based on user input
        Log.d("GameVM", "The following sequence was generated: ${events.contentToString()}")

        job = viewModelScope.launch {
            when (gameState.value.gameType) {
                GameType.Audio -> runAudioGame(events)
                GameType.AudioVisual -> runAudioVisualGame()
                GameType.Visual -> runVisualGame(events)
            }

            //_highscore.value = _score.value;
            if( _score.value > _highscore.value){
                saveHighScore(_score.value)
            }

        }

    }

    override fun checkMatch() {
        /**
         * Todo: This function should check if there is a match when the user presses a match button
         * Make sure the user can only register a match once for each event.
         */
        val currentEvent = _gameState.value.eventValue
        Log.d(
            "GameVM",
            "currentEvent: $_currentEventValue, PassedEvent: ${_passedEvents.firstOrNull()}"
        )
        if(_passedEvents.isNotEmpty()){
            if (_currentEventValue != -1 && _currentEventValue != invalidValue && _currentEventValue == _passedEvents[0] && currentRound>1 && _givenPointThisRound!= currentRound) {
                // Check if the current event value is different from the last one

                _score.value += 1
                Log.d("GameVM", "Match Found!")
                _givenPointThisRound = currentRound

            } else {
                Log.d("GameVM", "No Match Found or Already Matched")
                wrongAnswer++
                // No match or already matched
            }
        }




    }

    private suspend fun runAudioGame(events: Array<Int>) {
        // Todo: Make work for Basic grade
        Log.d("GameVM", "EventValue: ${_gameState.value.eventValue}")

        for (value in events) {

            currentRound++
            if (currentRound > 2) {
                _passedEvents.removeAt(0)
            }
            val letter = valueToLetterMap[value]

            textToSpeech.speak(letter, TextToSpeech.QUEUE_ADD, null, null)
            Log.d("GameVM", "The letter $letter should have been heard.")
            // Get the numeric value corresponding to the letter

            val numericValue =
                letterToValueMap[letter] // Assuming you have a map for letters to numeric values


            // Set the game state's eventValue with the numeric value of the letter
            if (numericValue != null) {
                _gameState.value = _gameState.value.copy(eventValue = numericValue)

                _currentEventValue = _gameState.value.copy(eventValue = numericValue).eventValue

                _passedEvents.add(_gameState.value.copy(eventValue = numericValue).eventValue)
            }
            delay(eventInterval)
            _gameState.value = _gameState.value.copy(eventValue = invalidValue)
            delay(hiddenEventInterval)

            val isLastEvent = numericValue == events.last()
            if (isLastEvent) {
                _gameState.value = _gameState.value.copy(eventValue = gameHasEnded)

            }




        }
        _currentEventValue = -1
        _passedEvents.clear()

        Log.d("GameVM", "EventValue: ${_gameState.value.eventValue}")


    }

    private suspend fun runVisualGame(events: Array<Int>) {



        for (value in events) {

            currentRound++
            if(currentRound >2){
                _passedEvents.removeAt(0)
            }

            _gameState.value = _gameState.value.copy(eventValue = value)

            _currentEventValue= _gameState.value.copy(eventValue = value).eventValue

            _passedEvents.add(_gameState.value.copy(eventValue = value).eventValue)


            delay(eventInterval)

            _gameState.value = _gameState.value.copy(eventValue = invalidValue)
            delay(hiddenEventInterval)

            val isLastEvent = value == events.last()
            if(isLastEvent){
                _gameState.value = _gameState.value.copy(eventValue = gameHasEnded)
            }

        }
        Log.d("GameVM", "EventValue: ${_gameState.value.eventValue}")
        _currentEventValue = -1
        _passedEvents.clear()

    }

    private fun runAudioVisualGame() {
        // Todo: Make work for Higher grade
    }

    override fun getCurrentRound(): Int {
        return currentRound // Return the current round value
    }


     fun saveHighScore(score: Int) {
        viewModelScope.launch {
            userPreferencesRepository.saveHighScore(score)
        }
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
/*
class FakeVM : GameViewModel {
    override val gameState: StateFlow<GameState>
        get() = MutableStateFlow(GameState()).asStateFlow()
    override val score: StateFlow<Int>
        get() = MutableStateFlow(2).asStateFlow()
    override val highscore: StateFlow<Int>
        get() = MutableStateFlow(42).asStateFlow()
    override val nBack: Int
        get() = 2

    override val numberOfEvents: Int
        get() = 10

    override val totalTimeBetweenIntervals: Long
        get() = 3000L

    override fun getCurrentRound(): Int {
        TODO("Not yet implemented")
    }

    override fun setGameType(gameType: GameType) {
    }

    override fun startGame() {
    }

    override fun checkMatch() {
    }
}

 */
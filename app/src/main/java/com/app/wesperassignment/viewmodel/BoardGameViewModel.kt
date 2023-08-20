package com.app.wesperassignment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.wesperassignment.model.Game
import com.app.wesperassignment.model.StandardGame
import com.app.wesperassignment.utils.ROUND_LOAD_TIME
import com.app.wesperassignment.view.OnCellTapListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BoardGameViewModel: ViewModel(), OnCellTapListener {

    private val gameLiveData = MutableLiveData<BoardGameUIState>().apply {
        value = BoardGameUIState()
    }

    private var currentProcess: Job? = null

    fun getGameData(): LiveData<BoardGameUIState> = gameLiveData

    override fun onCellTapped(position: Int) {
        if (getGame().gameStarted) {
            if (getGame().isPartialSeriesPressed(position)){
                updateFlashes()
            } else {
                currentProcess?.cancel()
                if (getGame().areCellsPressed(position)) {
                    increasePoints()
                } else {
                    reducePoints()
                }
                finishRound()
                if (getGame().gameShouldContinue()) {
                    startRound()
                }
            }
        }
    }

    private fun updateFlashes(){
        gameLiveData.value = getState().copy(
            game = getGame()
        )
    }

    fun startGame(newGame: Game) {
        newGame.apply {
            gameStarted = true
        }
        gameLiveData.value = getState().copy(
            game = newGame
        )
        startRound()
        startTimer()
    }

    private fun startTimer(){
        viewModelScope.launch {
            while (getGame().gameStarted){
                delay(1000)
                increaseSeconds()
                updateTopIfRequired()
            }
        }
    }

    private fun updateTopIfRequired(){
        val top = getState().topScore
        val current = getGame().timeElapsed
        if (current > top){
            gameLiveData.value = getState().copy(
                topScore = current
            )
        }
    }

    private fun startRound(){
        currentProcess = viewModelScope.launch {
            // Giving user a natural wait time between rounds
            delay(ROUND_LOAD_TIME)
            flashRandomCell()
            delay(getGame().roundTime)
            onNothingPressed()
        }
    }

    private fun onNothingPressed(){
        reducePoints()
        finishRound()
        if (getGame().gameShouldContinue()){
            startRound()
        }
    }

    private fun finishRound(){
        unflashBoard()
        increaseRound()
        recalculateRoundTime()
    }

    private fun recalculateRoundTime(){
        if (getGame().numberOfRounds % 5 == 0){
            getGame().roundTime = (getGame().roundTime * 0.9).toLong()
        }
    }

    private fun reducePoints(){
        val newPoints = getGame().points - 1
        val game = getGame().apply {
            points = newPoints
        }
        gameLiveData.value = getState().copy(
            game = game
        )
    }

    private fun increasePoints(){
        val newPoints = getGame().points + 1
        val game = getGame().apply {
            points = newPoints
        }
        gameLiveData.value = getState().copy(
            game = game
        )
    }

    private fun increaseSeconds(){
        val time = getGame().timeElapsed + 1
        val game = getGame().apply {
            timeElapsed = time
        }
        gameLiveData.value = getState().copy(
            game = game
        )
    }

    private fun increaseRound(){
        getGame().numberOfRounds++
    }

    private fun unflashBoard(){
        if (getGame().areCellsFlashed()){
            val game = getGame().apply {
                unflashBoard()
            }
            val state = getState().copy(
                game = game
            )
            gameLiveData.value = state
        }
    }

    private fun flashRandomCell() {
        val game = getGame().apply {
            flashRandomCell()
        }
        val state = getState().copy(
            game = game
        )
        gameLiveData.value = state
    }

    fun restartGame() {
        val top = getState().topScore
        val game = getGame().apply {
            restartGame()
        }
        gameLiveData.value = BoardGameUIState(
            topScore = top,
            game = game
        )
        currentProcess = null
    }

    private fun getState() = gameLiveData.value!!

    private fun getGame() = getState().game!!
}

data class BoardGameUIState(
    var game: Game? = StandardGame(),
    var topScore: Int = 0
)
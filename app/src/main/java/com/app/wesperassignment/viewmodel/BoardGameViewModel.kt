package com.app.wesperassignment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.wesperassignment.model.BoardCell
import com.app.wesperassignment.model.getDefaultListOfCells
import com.app.wesperassignment.utils.BOARD_SIZE
import com.app.wesperassignment.utils.ROUND_LOAD_TIME
import com.app.wesperassignment.utils.square
import com.app.wesperassignment.view.OnCellTapListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class BoardGameViewModel: ViewModel(), OnCellTapListener {

    private val gameLiveData = MutableLiveData<BoardGameUIState>().apply {
        value = BoardGameUIState()
    }

    private var roundTime = 3000L
    private var numberOfRounds = 1
    private var flashedCell = -1
    private var currentProcess: Job? = null

    fun getGameData(): LiveData<BoardGameUIState> = gameLiveData

    override fun onCellTapped(position: Int) {
        if (getState().gameStarted) {
            currentProcess?.cancel()
            if (position == flashedCell) {
                increasePoints()
            } else {
                reducePoints()
            }
            finishRound()
            if (gameShouldContinue()) {
                startRound()
            }
        }
    }

    private fun gameShouldContinue() =
        getState().points > 0 && getState().gameStarted

    fun startGame() {
        gameLiveData.value = getState().copy(
            gameStarted = true
        )
        startRound()
        startTimer()
    }

    private fun startTimer(){
        viewModelScope.launch {
            while (getState().gameStarted){
                delay(1000)
                increaseSeconds()
                updateTopIfRequired()
            }
        }
    }

    private fun updateTopIfRequired(){
        val top = getState().topScore
        val current = getState().timeElapsed
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
            delay(roundTime)
            onNothingPressed()
        }
    }

    private fun onNothingPressed(){
        reducePoints()
        finishRound()
        if (gameShouldContinue()){
            startRound()
        }
    }

    private fun finishRound(){
        unflashBoard()
        increaseRound()
        recalculateRoundTime()
    }

    private fun recalculateRoundTime(){
        if (numberOfRounds % 5 == 0){
            roundTime = (roundTime * 0.9).toLong()
        }
    }

    private fun reducePoints(){
        val points = getState().points - 1
        gameLiveData.value = getState().copy(
            points = points
        )
    }

    private fun increasePoints(){
        val points = getState().points + 1
        gameLiveData.value = getState().copy(
            points = points
        )
    }

    private fun increaseSeconds(){
        val time = getState().timeElapsed + 1
        gameLiveData.value = getState().copy(
            timeElapsed = time
        )
    }

    private fun increaseRound(){
        numberOfRounds++
    }

    private fun unflashBoard(){
        if (flashedCell > -1){
            val newList = getState().cells
            newList[flashedCell].flashed = false
            val state = getState().copy(
                cells = newList
            )
            gameLiveData.value = state
        }
    }

    private fun flashRandomCell(): Int {
        val random = Random.nextInt(0, BOARD_SIZE.square())
        val newList = getState().cells
        newList[random].flashed = true
        val state = getState().copy(
            cells = newList
        )
        gameLiveData.value = state
        flashedCell = random
        return random
    }

    fun restartGame() {
        val top = getState().topScore
        gameLiveData.value = BoardGameUIState(
            topScore = top
        )
        roundTime = 3000L
        numberOfRounds = 1
        flashedCell = -1
        currentProcess = null
    }

    private fun getState() = gameLiveData.value!!

}

data class BoardGameUIState(
    var gameStarted: Boolean = false,
    var timeElapsed: Int = 0,
    var points: Int = 1,
    var cells: List<BoardCell> = getDefaultListOfCells(BOARD_SIZE),
    var topScore: Int = 0
)
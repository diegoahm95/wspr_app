package com.app.wesperassignment.model

import com.app.wesperassignment.utils.BOARD_SIZE
import com.app.wesperassignment.utils.square
import kotlin.random.Random

class StandardGame(
    private var flashedCell: Int = -1,
) : Game() {

    override fun restartGame() {
        roundTime = 3000L
        numberOfRounds = 1
        flashedCell = -1
        points = 1
        cells = getDefaultListOfCells(BOARD_SIZE)
        timeElapsed = 0
        gameStarted = false
    }

    override fun areCellsFlashed(): Boolean = flashedCell > -1

    override fun unflashBoard() {
        if (flashedCell > -1){
            cells[flashedCell].flashed = false
        }
    }

    override fun flashRandomCell() {
        val random = Random.nextInt(0, BOARD_SIZE.square())
        cells[random].flashed = true
        flashedCell = random
    }

    override fun gameShouldContinue() = points > 0 && gameStarted

    override fun getDefaultGame(): Game = StandardGame()

    override fun areCellsPressed(position: Int) = (position == flashedCell)

    override fun isPartialSeriesPressed(position: Int): Boolean = false
}
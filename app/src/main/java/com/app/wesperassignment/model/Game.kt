package com.app.wesperassignment.model

import com.app.wesperassignment.utils.BOARD_SIZE

abstract class Game {
    var gameStarted: Boolean = false
    var timeElapsed: Int = 0
    var points: Int = 1
    var cells: List<BoardCell> = getDefaultListOfCells(BOARD_SIZE)
    var roundTime: Long = 3000
    var numberOfRounds: Int = 1

    abstract fun gameShouldContinue(): Boolean
    abstract fun restartGame()
    abstract fun getDefaultGame(): Game
    abstract fun areCellsPressed(position: Int): Boolean
    abstract fun unflashBoard()
    abstract fun flashRandomCell()
    abstract fun areCellsFlashed(): Boolean
    abstract fun isPartialSeriesPressed(position: Int): Boolean
}
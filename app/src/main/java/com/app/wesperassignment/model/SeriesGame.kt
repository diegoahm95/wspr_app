package com.app.wesperassignment.model

import com.app.wesperassignment.utils.BOARD_SIZE
import com.app.wesperassignment.utils.SERIES_SIZE
import com.app.wesperassignment.utils.square
import kotlin.random.Random

class SeriesGame(
    private var flashedCells: MutableList<Int> = mutableListOf(),
) : Game() {

    override fun restartGame() {
        roundTime = 3500L
        numberOfRounds = 1
        flashedCells = mutableListOf()
        points = 1
        cells = getDefaultListOfCells(BOARD_SIZE)
        timeElapsed = 0
        gameStarted = false
    }

    override fun areCellsFlashed(): Boolean = flashedCells.isNotEmpty()

    override fun unflashBoard() {
        flashedCells.forEach {
            cells[it].flashed = false

        }
        flashedCells = mutableListOf()
    }

    override fun flashRandomCell() {
        do {
            val random = Random.nextInt(0, BOARD_SIZE.square())
            if (!flashedCells.contains(random)) {
                flashCell(random)
            }
        } while (flashedCells.size < SERIES_SIZE)
    }

    private fun flashCell(position: Int){
        cells[position].flashed = true
        flashedCells.add(position)
    }

    override fun gameShouldContinue() = points > 0 && gameStarted

    override fun getDefaultGame(): Game = SeriesGame()

    override fun areCellsPressed(position: Int): Boolean {
        return if (flashedCells.size == 1 && flashedCells[0] == position){
            removeFlashedCell(position)
            true
        } else {
            false
        }
    }

    override fun isPartialSeriesPressed(position: Int): Boolean {
        return if (flashedCells.size > 1 && flashedCells.contains(position)){
            removeFlashedCell(position)
            true
        } else {
            false
        }
    }

    private fun removeFlashedCell(position: Int){
        flashedCells.remove(position)
        cells[position].flashed = false
    }
}
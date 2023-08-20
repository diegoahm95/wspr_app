package com.app.wesperassignment.view

import android.os.Bundle
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.wesperassignment.R
import com.app.wesperassignment.model.BoardCell
import com.app.wesperassignment.model.SeriesGame
import com.app.wesperassignment.model.StandardGame
import com.app.wesperassignment.model.getDefaultListOfCells
import com.app.wesperassignment.utils.BOARD_SIZE
import com.app.wesperassignment.viewmodel.BoardGameUIState
import com.app.wesperassignment.viewmodel.BoardGameViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: BoardGameViewModel by viewModels()
    private lateinit var board: GridView
    private lateinit var points: TextView
    private lateinit var time: TextView
    private lateinit var top: TextView
    private lateinit var start: Button
    private lateinit var startSeries: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getViews()
        createGrid()
        setupButtons()
        subscribeToData()
    }

    private fun createGrid(){
        board.numColumns = BOARD_SIZE
        board.columnWidth = GridView.AUTO_FIT
        board.stretchMode = GridView.STRETCH_COLUMN_WIDTH
        board.horizontalSpacing = 5
        board.verticalSpacing = 5
        board.adapter = GameGridAdapter(this, getDefaultListOfCells(BOARD_SIZE), viewModel)
    }

    private fun getViews(){
        board = findViewById(R.id.board)
        points = findViewById(R.id.points)
        time = findViewById(R.id.time)
        start = findViewById(R.id.start)
        top = findViewById(R.id.top_time)
        startSeries = findViewById(R.id.start_series)
    }

    private fun subscribeToData(){
        viewModel.getGameData().observe(this) {
            handleUIState(it)
        }
    }

    private fun handleUIState(state: BoardGameUIState) {
        with (state){
            if (game!!.points < 1){
                restartGame()
            } else {
                if (game!!.gameStarted){
                    processGame(this)
                } else {
                    setIdleState()
                }
            }
            updateTop(topScore)
        }
    }

    private fun processGame(state: BoardGameUIState){
        if (start.isEnabled){
            toggleButton(false)
        }
        with(state.game!!){
            updatePoints(points)
            updateTime(timeElapsed)
            updateList(cells)
        }
    }

    private fun updateList(cells: List<BoardCell>){
        (board.adapter as GameGridAdapter).updateItems(cells)
    }

    private fun setIdleState(){
        toggleButton(true)
        updatePoints(0)
        updateTime(0)
        updateList(getDefaultListOfCells(BOARD_SIZE))
    }

    private fun restartGame(){
        Toast.makeText(applicationContext, "Game finished!", Toast.LENGTH_SHORT).show()
        toggleButton(true)
        viewModel.restartGame()
    }

    private fun setupButtons(){
        start.setOnClickListener {
            viewModel.startGame(StandardGame())
        }
        startSeries.setOnClickListener {
            viewModel.startGame(SeriesGame())
        }
    }

    private fun toggleButton(enabled: Boolean){
        start.isEnabled = enabled
        startSeries.isEnabled = enabled
    }

    private fun updatePoints(pts: Int){
        points.text = getString(R.string.points, pts)
    }

    private fun updateTime(seconds: Int){
        time.text = getString(R.string.time, seconds)
    }

    private fun updateTop(seconds: Int){
        top.text = getString(R.string.top, seconds)
    }
}

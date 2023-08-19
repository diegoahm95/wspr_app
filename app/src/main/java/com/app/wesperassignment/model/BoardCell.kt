package com.app.wesperassignment.model

import com.app.wesperassignment.utils.square

data class BoardCell (
    val id: Int,
    var flashed: Boolean
)

fun getDefaultListOfCells(size: Int): List<BoardCell> {
    val list = mutableListOf<BoardCell>()
    for (i in 1..size.square()){
        list.add(BoardCell(i, false))
    }
    return list
}
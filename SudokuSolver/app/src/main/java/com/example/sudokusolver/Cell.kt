package com.example.sudokusolver

data class Cell(
    val row: Int,
    val col: Int,
    var value: Int,
    var isStartingCell: Boolean = false,
    var collidesWithStartingCell: Boolean = false,
    var notes: MutableSet<Int> = mutableSetOf()
) {

}
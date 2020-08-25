package com.example.sudokusolver

import android.util.Log
import androidx.lifecycle.MutableLiveData

class SudokuGame {

    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var cellsLiveData = MutableLiveData<List<Cell>>()
    val isTakingNotesLiveData = MutableLiveData<Boolean>()
    val highlightedKeysLiveData = MutableLiveData<Set<Int>>()

    private var selectedRow = -1
    private var selectedColumn = -1
    private var isTakingNotes = false

    private val board: Board


    init {
        val cells = List(9*9) {i -> Cell(i/9, i%9, 0)}

        cells[1].value = 3
        cells[1].isStartingCell = true
        cells[12].value = 1
        cells[12].isStartingCell = true
        cells[13].value = 9
        cells[13].isStartingCell = true
        cells[14].value = 5
        cells[14].isStartingCell = true
        cells[20].value = 8
        cells[20].isStartingCell = true
        cells[25].value = 6
        cells[25].isStartingCell = true
        cells[27].value = 8
        cells[27].isStartingCell = true
        cells[31].value = 6
        cells[31].isStartingCell = true
        cells[36].value = 4
        cells[36].isStartingCell = true
        cells[39].value = 8
        cells[39].isStartingCell = true
        cells[44].value = 1
        cells[44].isStartingCell = true
        cells[49].value = 2
        cells[49].isStartingCell = true
        cells[55].value = 6
        cells[55].isStartingCell = true
        cells[60].value = 2
        cells[60].isStartingCell = true
        cells[61].value = 8
        cells[61].isStartingCell = true
        cells[66].value = 4
        cells[66].isStartingCell = true
        cells[67].value = 1
        cells[67].isStartingCell = true
        cells[68].value = 9
        cells[68].isStartingCell = true
        cells[71].value = 5
        cells[71].isStartingCell = true
        cells[79].value = 7
        cells[79].isStartingCell = true

        board = Board(9, cells)

        selectedCellLiveData.postValue(Pair(selectedRow, selectedColumn))
        cellsLiveData.postValue(board.cells)
        isTakingNotesLiveData.postValue(isTakingNotes)
        Log.d(TAG, "Initialized SudokuGame")
    }

    fun handleInput(number: Int){
        if (selectedRow == -1 || selectedColumn == -1) return
        val cell = board.getCell(selectedRow, selectedColumn)
        if (board.getCell(selectedRow, selectedColumn).isStartingCell) return

        if(isTakingNotes) {
            if(cell.notes.contains(number)){
                cell.notes.remove(number)
            }else{
                cell.notes.add(number)
            }
            highlightedKeysLiveData.postValue(cell.notes)
        }
        else{
            cell.value = number
        }
        cellsLiveData.postValue(board.cells)
    }

    fun updateSelectedCell(row: Int, col: Int){
        Log.d(TAG, "updateSelectedCell happens")
        val cell = board.getCell(row, col)
        Log.d(TAG, cell.isStartingCell.toString())
        if (!cell.isStartingCell) {
            selectedRow = row
            selectedColumn = col
            selectedCellLiveData.postValue(Pair(row, col))

            if (isTakingNotes) {
                highlightedKeysLiveData.postValue(cell.notes)
            }

        }
    }

    fun changeNoteTakingState() {
        isTakingNotes = !isTakingNotes
        isTakingNotesLiveData.postValue(isTakingNotes)

        val curNotes = if (isTakingNotes) {
            board.getCell(selectedRow, selectedColumn).notes
        } else {
            setOf<Int>()
        }
        highlightedKeysLiveData.postValue(curNotes)
    }

    fun delete() {
        val cell = board.getCell(selectedRow, selectedColumn)
        if(isTakingNotes) {
            cell.notes.clear()
            highlightedKeysLiveData.postValue((setOf()))
        } else {
            cell.value = 0
        }
        cellsLiveData.postValue(board.cells)
    }

    companion object{
        val TAG = "SudokuGame"
    }
}
package com.example.sudokusolver

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar

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
    /*
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
    */
        /*
        Solution:
        4 3 5 2 6 9 7 8 1
        6 8 2 5 7 1 4 9 3
        1 9 7 8 3 4 5 6 2
        8 2 6 1 9 5 3 4 7
        3 7 4 6 8 2 9 1 5
        9 5 1 7 4 3 6 2 8
        5 1 9 3 2 6 8 7 4
        2 4 8 9 5 7 1 3 6
        7 6 3 4 1 8 2 5 9
         */
        cells[3].value = 2
        cells[3].isStartingCell = true
        cells[4].value = 6
        cells[4].isStartingCell = true
        cells[6].value = 7
        cells[6].isStartingCell = true
        cells[8].value = 1
        cells[8].isStartingCell = true
        cells[9].value = 6
        cells[9].isStartingCell = true
        cells[10].value = 8
        cells[10].isStartingCell = true
        cells[13].value = 7
        cells[13].isStartingCell = true
        cells[16].value = 9
        cells[16].isStartingCell = true
        cells[18].value = 1
        cells[18].isStartingCell = true
        cells[19].value = 9
        cells[19].isStartingCell = true
        cells[23].value = 4
        cells[23].isStartingCell = true
        cells[24].value = 5
        cells[24].isStartingCell = true
        cells[27].value = 8
        cells[27].isStartingCell = true
        cells[28].value = 2
        cells[28].isStartingCell = true
        cells[30].value = 1
        cells[30].isStartingCell = true
        cells[34].value = 4
        cells[34].isStartingCell = true
        cells[38].value = 4
        cells[38].isStartingCell = true
        cells[39].value = 6
        cells[39].isStartingCell = true
        cells[41].value = 2
        cells[41].isStartingCell = true
        cells[42].value = 9
        cells[42].isStartingCell = true
        cells[46].value = 5
        cells[46].isStartingCell = true
        cells[50].value = 3
        cells[50].isStartingCell = true
        cells[52].value = 2
        cells[52].isStartingCell = true
        cells[53].value = 8
        cells[53].isStartingCell = true
        cells[56].value = 9
        cells[56].isStartingCell = true
        cells[57].value = 3
        cells[57].isStartingCell = true
        cells[61].value = 7
        cells[61].isStartingCell = true
        cells[62].value = 4
        cells[62].isStartingCell = true
        cells[64].value = 4
        cells[64].isStartingCell = true
        cells[67].value = 5
        cells[67].isStartingCell = true
        cells[70].value = 3
        cells[70].isStartingCell = true
        cells[71].value = 6
        cells[71].isStartingCell = true
        cells[72].value = 7
        cells[72].isStartingCell = true
        cells[74].value = 3
        cells[74].isStartingCell = true
        cells[76].value = 1
        cells[76].isStartingCell = true
        cells[77].value = 8
        cells[77].isStartingCell = true
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

    fun solveGrid(){
        Log.d(TAG, "started solving grid")
        var grid: MutableList<Int> = mutableListOf()
        board.cells?.forEach { cell -> grid.add(cell.value) } //copy contents into list
        val solvedGrid: SolvedGrid = SolvedGrid(grid)
        solvedGrid.fillAt(0)
        grid = solvedGrid.getGrid()
        board.cells?.forEachIndexed {i, cell -> cell.value = grid[i]}
        cellsLiveData.postValue(board.cells)
        Log.d(TAG, "finished solving grid. $grid")
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

class SolvedGrid(gridToSolve: MutableList<Int>) {
    /****
     *
     * Functions needed for Sudoku Solver algorithm. The board must be converted into a list of integers, and is taken as an input for accessing the cells, as well as being modified instead of the live data.
     *
     ****/
    private var grid: MutableList<Int> = mutableListOf()
    private var highest: Int = 0;
    init {
        grid.addAll(gridToSolve)
    }


    private fun getCellValue(row: Int, col: Int): Int{
        return grid[row*9 + col]
    }

    fun getIndices(index: Int): List<Int> {
        val indices: MutableList<Int> = mutableListOf(index/9, index%9); //0, 9, ... are the first (0) elements of the grid.
        if (indices[0] > 8) Log.d(TAG, "Probably invalid index (not in [0, 80])")
        return indices
    }

    fun setCellValue(row: Int, column: Int, value: Int){
        grid[row*9 + column] = value
    }

    fun getSegment(vertical_index: Int, horizontal_index: Int): List<Int> {
        /*
        Get the 3x3 subgrid. getSegment(0, 0) returns the top left subgrid, getSegment(0, 2) the top right subgrid. In general:
        (0,0) (0,1) (0,2)
        (1,0) (1,1) (1,2)
        (2,0) (2,1) (2,2)
         */
        val segment_vals: MutableList<Int> = mutableListOf()
        for(row in (vertical_index*3)..(vertical_index*3 + 2)) { // x..y is inclusive range!
            for(col in (horizontal_index*3)..(horizontal_index*3 + 2)) {
                segment_vals.add(getCellValue(row, col))
            }
        }
        return segment_vals
    }

    fun getRow(row: Int): List<Int> {
        val row_vals: MutableList<Int> = mutableListOf()
        for(col in 0..8){
            row_vals.add(getCellValue(row, col))
        }
        return row_vals
    }

    fun getColumn(col: Int): List<Int> {
        val col_vals: MutableList<Int> = mutableListOf()
        for(row in 0..8){
            col_vals.add(getCellValue(row, col))
        }
        return col_vals
    }

    fun whichSegment(row: Int, column: Int): List<Int> {
        /*
        Returns the list [vertical_index, horizontal_index] of the segment the cell is located in.
         */
        return listOf((row - row%3)/3, (column - column%3)/3)
    }

    fun isProtected(row: Int, column: Int): Boolean{//TODO implement feature
        return false
    }

    private fun getSegmentOfCell(row: Int, column: Int): List<Int>{
        /*
        returns the 3x3 segment in which the cell at row and column is located
        */
        val segmentList: List<Int> = whichSegment(row, column)
        return getSegment(segmentList[0], segmentList[1])
    }

    fun isValidEntry(row: Int, column: Int, entry: Int): Boolean{
        val segment: List<Int> = getSegmentOfCell(row, column)
        if (entry!= 0){
            when (entry) {
                in getRow(row) -> return false
                in getColumn(column) -> return false
                in segment -> return false
            }
        }
        return true
    }

    fun nextEmptyInRow(row: Int, column: Int): Int{
        if(column>=8){//Reached end of row. Should return 9 to trigger
            return END_OF_ROW;
        }
        var index = row*9 + column
        var row_index = column
        if (grid[index] == 0) return index
        while(grid[index] != 0 && row_index < 9){
            ++index
            ++row_index
        }
        return row_index
    }

    fun fillCell(row: Int, column: Int): Boolean{
        /*
        fills single cell correctly; keeps iterating until meets irresolvable situation in row, or end of row. Should be only called on empty cell!
        */
        //Log.d("fillCell", "Filling cell $column")
        if(column==END_OF_ROW) return true //Start chain-reaction of returning true's once reached column 8 where fillCell(row, 9) is called.
        if (getCellValue(row, column) != 0){//Check if nextEmptyInRow() accidentally finds non-empty cell.
            Log.d(SudokuGame.TAG, "Error! Non-empty cell written to!")
        }
        for(guess in 1..9){
            if(isValidEntry(row, column, guess)){
                setCellValue(row, column, guess)
                if(fillCell(row, nextEmptyInRow(row, column+1))) return true
            }
        }
        setCellValue(row, column, 0) //set back to empty
        return false
    }
    fun fillRow(row: Int): Boolean{
        Log.d("fillRow", "Filling row $row")
        Log.d("nextEmpty", "Found next empty at ${nextEmptyInRow(row, 0)}")
        if (row == END_OF_COL){
            return true
        }
        if (fillCell(row, nextEmptyInRow(row, 0))){
            if (fillRow(row+1)) return true
        }
        return false
    }

    fun getGrid(): MutableList<Int> {
        return grid
    }


    fun nextEmpty(index: Int): Int{/*
    Finds next empty cell to the right of cell with index (excluding cell at index itself).
    */
        if(index > 81){//Reached end of grid and somehow still increased index.
            Log.d(TAG, "nextEmpty reached index > 81!")
            return 81;
        }
        var next_empty = index + 1;
        /*
        To include indexed cell itself:
        var next_empty = index;
         */
        while(next_empty < 81 && grid[next_empty] != 0){
            ++next_empty
        }
        if(next_empty > 79) {
            Log.d(TAG, "Next empty is $next_empty")
        }
        return next_empty
    }

    fun fillAt(index: Int): Boolean{
        /*
       fills single cell correctly; keeps iterating until meets irresolvable situation.
       */
        if(index==81) return true //Start chain-reaction of returning true's once reached column 8 where fillCell(row, 9) is called.
        val (row, column) = getIndices(index);
        if (getCellValue(row, column) != 0){//Check if nextEmptyInRow() accidentally finds non-empty cell.
            Log.d(SudokuGame.TAG, "Error! Non-empty cell written to!")
        }
        for(guess in 1..9){
            if(isValidEntry(row, column, guess)){
                setCellValue(row, column, guess)
                //TODO this could be the buggy part
                if(fillAt(nextEmpty(index))) return true
            }
        }
        setCellValue(row, column, 0) //set back to empty
        return false
    }

    companion object {
        val END_OF_ROW = 9;
        val END_OF_COL = 9;
        val END_OF_GRID = 81;
        val TAG = "SudokuGame_SolvedGrid"
    }
    /****
     *
     *
     *
     ****/
}
package com.example.sudokusolver

//TODO rename local variables with camelCase


class SudokuGrid(initial_values: Array<IntArray>){
    /*
    The indexing convention is as follows: the top-left element on the real sudoku board has index (0, 0). The one below it (1, 0), i.e. the row index
    corresponds to the rows on the board.
    There are 9 3x3 segments: these are indexed similarly to the elements, i.e. (0, 0) is the (vertical, horizontal) pair of indices corresponding to
    the top left 3x3 segment.
     */
    private var _grid: Array<IntArray> = initial_values.clone()
    private val _segment_map: Map<Int, List<Int>> = mapOf(0 to listOf(0, 1, 2), 1 to listOf(3, 4, 5), 2 to listOf(6, 7, 8)) // mapping from segment description to index range
    private fun setField(row: Int, column: Int, new_value: Int){
        _grid[row][column] = new_value
    }
    fun cellAt(row: Int, column: Int): Int {
        return _grid[row][column]
    }
    fun getSegment(vertical_segment: Int, horizontal_segment: Int): Array<IntArray>{
        var segment: Array<IntArray> = arrayOf(IntArray(3), IntArray(3), IntArray(3))
        for(row: Int in _segment_map[vertical_segment]!!){
            for(column: Int in _segment_map[horizontal_segment]!!){
                segment[row][column] = cellAt(row, column)
            }
        }
        return segment
    }

    fun nextEmptyInRow(row: Int, column: Int): Int{
        /*
        Find next empty position in row starting with (inclusive) (row, col) location. Return index if found one, return 9 if none found.
        */
        var x: Int = column
        while((x < 9) && (_grid[row][x] != 0)){
            ++x
        }
        return x
    }
    fun getRow(row: Int): IntArray{
        return _grid[row]
    }
    fun getColumn(column: Int): IntArray{
        var column_values: IntArray = IntArray(9)
        for(i in 0..8){
            column_values[i] = _grid[i][column]
        }
        return column_values
    }
    fun whichSegment(row: Int, column: Int): IntArray{
        val horizontal_segment: Int = (column - column%3)/3
        val vertical_segment: Int = (row - row%3)/3
        val return_array: IntArray = intArrayOf(vertical_segment, horizontal_segment)
        return return_array
    }
    fun isProtected(row: Int, column: Int): Boolean{//TODO implement feature using a constant mask
        return false
    }

    private fun getSegmentOfCell(row: Int, column: Int): Array<IntArray>{
        /*
        returns the 3x3 segment in which the cell at row and column is located
        */
        val segment_list: IntArray = whichSegment(row, column)
        return getSegment(segment_list[0], segment_list[1])
    }

    private fun valueIsInSegment(value: Int, segment: Array<IntArray> ): Boolean {
        /*
        given a 3x3 segment of the sudoku grid, e.g. from getSegment(), check if the value is already in there.
         */
        for(row in segment){
            for(cell_value in row){
                if(value == cell_value) return true
            }
        }
        return false

    }

    fun isValid(row: Int, column: Int, entry: Int): Boolean{
        val segment: Array<IntArray> = getSegmentOfCell(row, column)
        if (entry!= 0){
            when {
                entry in getRow(row) -> return false
                entry in getColumn(column) -> return false
                valueIsInSegment(entry, segment) -> return false
            }
        }
        return true
    }
    fun writeCell(row: Int, column: Int, entry: Int){
        /*
        Use this function to overwrite element in cell (row, column) if it is not write-protected.
         */
        if(!isProtected(row, column)){
           setField(row, column, entry)
        }
    }
    fun drawBoard(){
        for (row in _grid){
            for (cell in row){
                print("$cell ")
            }
            println()
        }
    }
}
/*
example grid:
val grid: Array<IntArray> = arrayOf(intArrayOf(0, 0, 0, 2, 6, 0, 7, 0, 1),
                 intArrayOf(6, 8, 0, 0, 7, 0, 0, 9, 0),
                 intArrayOf(1, 9, 0, 0, 0, 4, 5, 0, 0),
                 intArrayOf(8, 2, 0, 1, 0, 0, 0, 4, 0),
                 intArrayOf(0, 0, 4, 6, 0, 2, 9, 0, 0),
                 intArrayOf(0, 5, 0, 0, 0, 3, 0, 2, 8),
                 intArrayOf(0, 0, 9, 3, 0, 0, 0, 7, 4),
                 intArrayOf(0, 4, 0, 0, 5, 0, 0, 3, 6),
                 intArrayOf(7, 0, 3, 0, 1, 8, 0, 0, 0))

solution:

 */


class Solver(grid: SudokuGrid) {
    val initial_grid: SudokuGrid = grid


}
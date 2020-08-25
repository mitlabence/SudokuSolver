package com.example.sudokusolver

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.min


class SudokuGridView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) { //take a list (set) of attributes along with the context, return the grid as view.
    private var squareSize = 3 //this many cells make up one side of a sudoku square (default is 3x3 squares for 9x9 sudoku grid)
    private var gridSize = 9

    //these are set in onDraw
    private var cellSizePixels = 0F
    private var noteSizePixels = 0F

    private var listener: SudokuGridView.OnTouchListener? = null

    private var selectedRow = -1
    private var selectedColumn = -1

    private var cells: List<Cell>? = null

    private val thickLinePaint = Paint().apply{
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 4F
    }
    private val thinLinePaint = Paint().apply{
        style = Paint.Style.STROKE
        color = Color.DKGRAY
        strokeWidth = 2F
    }

    private val selectedCellPaint = Paint().apply{
        style = Paint.Style.FILL_AND_STROKE
        color = resources.getColor(R.color.colorSecondaryLightest)
    }
    private val highlightedCellPaint = Paint().apply{ //same row and same column or same square as selected cell should be highlighted
        style = Paint.Style.FILL_AND_STROKE
        color = resources.getColor(R.color.colorTertiaryLightest)
    }
    private val startingCellPaint = Paint().apply{
        style = Paint.Style.FILL_AND_STROKE
        color = resources.getColor(R.color.colorPrimaryLightest)
    }

    private val noteTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK

    }

    private val textPaint = Paint().apply{
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
        textSize = 24F
    }

    private val startingCellTextPaint = Paint().apply{
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
        textSize = 32F
        typeface = Typeface.DEFAULT_BOLD
    }

    override fun onDraw(canvas: Canvas) {
        updateMeasurements(width)
        cellSizePixels = (width / gridSize).toFloat()
        fillCells(canvas)
        drawLines(canvas)
        drawText(canvas)
    }

    private fun updateMeasurements(width: Int){
        cellSizePixels = (width / gridSize).toFloat()
        noteSizePixels = cellSizePixels / squareSize.toFloat()
        noteTextPaint.textSize = cellSizePixels / squareSize.toFloat()
        textPaint.textSize = cellSizePixels / 1.5F
        startingCellTextPaint.textSize = cellSizePixels / 1.5F
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        /*make the board square by taking the minimum of the width and height dimensions that match parents and set as side size
        */
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
         val squareSize = min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(squareSize, squareSize)
    }

    private fun drawLines(canvas: Canvas) {
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), thickLinePaint)
        //draw horizontal and vertical lines
        for (i in 1 until gridSize) {
            val paintToUse = when (i % squareSize){
                0 -> thickLinePaint
                else -> thinLinePaint
            }
            canvas.drawLine( //vertical line
                i*cellSizePixels,
                0F,
                i*cellSizePixels,
                height.toFloat(),
                paintToUse
            )
            canvas.drawLine( //horizontal line
                0F,
                i*cellSizePixels,
                width.toFloat(),
                i*cellSizePixels,
                paintToUse
            )
        }
    }

    private fun drawText(canvas: Canvas){
        cells?.forEach{cell ->
            val value = cell.value
            val textBounds = Rect()


            if(value == 0) {
                //draw notes
                cell.notes.forEach {note ->
                    val valueString = note.toString()
                    val rowInCell = (note - 1) / squareSize
                    val colInCell = (note - 1) % squareSize
                    noteTextPaint.getTextBounds(valueString, 0, valueString.length, textBounds)
                    val textWidth = noteTextPaint.measureText(valueString)
                    val textHeight = textBounds.height()

                    canvas.drawText(
                        valueString, (cell.col * cellSizePixels) + (colInCell * noteSizePixels) + noteSizePixels / 2 - textWidth / 2f,
                        (cell.row * cellSizePixels) + (rowInCell * noteSizePixels) + noteSizePixels / 2 + textWidth / 2f,
                        noteTextPaint
                    )

                }
            } else {
                val row = cell.row
                val col = cell.col
                val valueString = cell.value.toString()

                val paintToUse = if (cell.isStartingCell) startingCellTextPaint else textPaint
                val textBounds = Rect()
                paintToUse.getTextBounds(valueString, 0, valueString.length, textBounds)

                val textWidth = paintToUse.measureText(valueString)
                val textHeight = textBounds.height()

                canvas.drawText(valueString,
                    (col*cellSizePixels) + cellSizePixels / 2 - textWidth / 2,
                    (row * cellSizePixels) + cellSizePixels / 2 + textHeight /2,
                    textPaint)
            }


        }
    }

    private fun fillCells(canvas: Canvas){
        // fill the selected cell with light gray (if modifiable) or reddish light gray (if unmodifiable)
        if(selectedColumn == -1 || selectedColumn == -1){
            cells?.forEach{
                val row= it.row
                val column = it.col
                if(it.isStartingCell) {
                    fillCell(canvas, row, column, startingCellPaint)
                }
            }
        return
        }
        cells?.forEach{
            val row= it.row
            val column = it.col
            if(it.isStartingCell) {
                fillCell(canvas, row, column, startingCellPaint)
            }
            else if (row == selectedRow && column == selectedColumn) {
                fillCell(canvas, row, column, selectedCellPaint)
            }
            else if (row == selectedRow || column == selectedColumn) {//same row or same column, but not both -> highlight cell
                fillCell(canvas, row, column, highlightedCellPaint)
            }
            else if (row/squareSize == selectedRow/squareSize && column/squareSize == selectedColumn/squareSize) { //same square as cell
                fillCell(canvas, row, column, highlightedCellPaint)
            }
        }
    }

    private fun fillCell(canvas: Canvas, row: Int, column: Int, paint: Paint){
        canvas.drawRect(column*cellSizePixels, row*cellSizePixels, (column+1)*cellSizePixels, (row+1)*cellSizePixels, paint)
    }

    fun updateCells(cells: List<Cell>){
        this.cells = cells
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
    return when (event.action) {
        MotionEvent.ACTION_DOWN -> {
            handleTouchEvent(event.x, event.y)
            true
        }
        else -> false
    }
    }

    private fun handleTouchEvent(x: Float, y: Float){
        val possibleSelectedRow = (y / cellSizePixels).toInt()
        val possibleSelectedColumn = (x / cellSizePixels).toInt()
        listener?.onCellTouched(possibleSelectedRow, possibleSelectedColumn)
        Log.d(TAG, "handleTouchEvent happened")
    }

    fun updateSelectedCellUI(row: Int, col: Int){
        selectedRow = row
        selectedColumn = col
        invalidate() //invalidate view -> re-render
    }

    fun registerListener(listener: SudokuGridView.OnTouchListener){
        this.listener = listener
    }

    interface OnTouchListener {
        fun onCellTouched(row: Int, col: Int)
    }
    companion object{
        val TAG = "SudokuGridView"
    }
}
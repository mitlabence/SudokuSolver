package com.example.sudokusolver

import com.example.sudokusolver.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


class SudokuGridView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) { //take a list (set) of attributes along with the context, return the grid as view.
    private var squareSize = 3 //this many cells make up one side of a sudoku square (default is 3x3 squares for 9x9 sudoku grid)
    private var gridSize = 9
    private var cellSizePixels = 0F

    private var selectedRow = -1
    private var selectedColumn = -1

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

    override fun onDraw(canvas: Canvas) {
        cellSizePixels = (width / gridSize).toFloat()
        fillCells(canvas)
        drawLines(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        /*make the board square by taking the minimum of the width and height dimensions that match parents and set as side size
        */
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
         val squareSize = Math.min(widthMeasureSpec, heightMeasureSpec)
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
    private fun fillCells(canvas: Canvas){
        // fill the selected cell with light gray (if modifiable) or reddish light gray (if unmodifiable)
        if (selectedRow == -1 || selectedColumn == -1) return //there is no selected cell

        for (row in 0..gridSize) {
            for (column in 0..gridSize){
                if (row == selectedRow && column == selectedColumn) {
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
    }

    private fun fillCell(canvas: Canvas, row: Int, column: Int, paint: Paint){
        canvas.drawRect(column*cellSizePixels, row*cellSizePixels, (column+1)*cellSizePixels, (row+1)*cellSizePixels, paint)
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
        selectedRow = (y / cellSizePixels).toInt()
        selectedColumn = (x / cellSizePixels).toInt()
        invalidate() //invalidate view -> re-render
    }
}
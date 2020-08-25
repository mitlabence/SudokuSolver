package com.example.sudokusolver.grid

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.sudokusolver.R
import com.example.sudokusolver.SudokuGridView
import com.example.sudokusolver.SudokuGridViewModel
import kotlinx.android.synthetic.main.fragment_grid.*

class GridActivity : AppCompatActivity(), SudokuGridView.OnTouchListener {
    private lateinit var viewModel: SudokuGridViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Created GridActivity")
        setContentView(R.layout.fragment_grid)

        sudokuGrid.registerListener(this)

        viewModel = ViewModelProviders.of(this).get(SudokuGridViewModel::class.java)
        viewModel.sudokuGrid.selectedCellLiveData.observe(this, Observer { updateSelectedCellUI(it) })
    }
    private fun updateSelectedCellUI(cell: Pair<Int, Int>?) = cell?.let{
        sudokuGrid.updateSelectedCellUI(cell.first, cell.second)
    }

    override fun onCellTouched(row: Int, col: Int){
        Log.d(TAG, "Cell row $row col $col touched.")
        viewModel.sudokuGrid.updateSelectedCell(row, col)
    }
    companion object {
        val TAG = "GridActivity"
    }
}
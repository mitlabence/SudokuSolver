package com.example.sudokusolver.grid

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.sudokusolver.Cell
import com.example.sudokusolver.R
import com.example.sudokusolver.SudokuGridView
import com.example.sudokusolver.SudokuGridViewModel
import com.example.sudokusolver.databinding.FragmentGridBinding
import kotlinx.android.synthetic.main.fragment_grid.*

class GridFragment : Fragment(), SudokuGridView.OnTouchListener {
    private lateinit var binding: FragmentGridBinding
    private lateinit var viewModel: SudokuGridViewModel
    private lateinit var numberButtons: List<Button>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(GridActivity.TAG, "Created GridFragment")
    }

    private fun updateSelectedCellUI(cell: Pair<Int, Int>?) = cell?.let{
        sudokuGrid.updateSelectedCellUI(cell.first, cell.second)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sudokuGrid.registerListener(this) //GridFragment
        Log.d(TAG, "onActivityCreated done")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "Initialized GridFragment")
        binding = FragmentGridBinding.inflate(inflater, container, false)
        /*
        binding.addPlant.setOnClickListener {
            navigateToPlantListPage()
        }
        */


        viewModel = ViewModelProviders.of(this).get(SudokuGridViewModel::class.java)
        viewModel.sudokuGrid.selectedCellLiveData.observe(viewLifecycleOwner, Observer { updateSelectedCellUI(it) })
        viewModel.sudokuGrid.cellsLiveData.observe(viewLifecycleOwner, Observer { updateCells(it) })
        viewModel.sudokuGrid.isTakingNotesLiveData.observe(viewLifecycleOwner, Observer {updateNoteTakingUI(it)})
        viewModel.sudokuGrid.highlightedKeysLiveData.observe(viewLifecycleOwner, Observer {updateHighlightedKeys(it)})




        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        numberButtons = listOf(oneButton, twoButton, threeButton, fourButton, fiveButton, sixButton, sevenButton, eightButton, nineButton)

        numberButtons.forEachIndexed{ index, button ->
            button.setOnClickListener {
                viewModel.sudokuGrid.handleInput(index + 1)
            }
        }
        notesButton.setOnClickListener { viewModel.sudokuGrid.changeNoteTakingState() }
        deleteButton.setOnClickListener { viewModel.sudokuGrid.delete() }
        solveButton.setOnClickListener {viewModel.sudokuGrid.solveGrid()}

    }


    private fun updateCells(cells: List<Cell>?) = cells?.let{
        sudokuGrid.updateCells(cells)
    }

    override fun onCellTouched(row: Int, col: Int){
        viewModel.sudokuGrid.updateSelectedCell(row, col)
    }

    private fun updateNoteTakingUI(isNoteTaking: Boolean?) = isNoteTaking?.let {
        val color = if (it) this.context?.let { it1 -> ContextCompat.getColor(it1, R.color.colorPrimary) } else Color.LTGRAY
        if (color != null) {
            notesButton.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        }
    }

    private fun updateHighlightedKeys(set: Set<Int>?) = set?.let{
        numberButtons.forEachIndexed{index, button ->
            val color = if (set.contains(index+1)) context?.let { it1 -> ContextCompat.getColor(it1, R.color.colorPrimary) } else Color.LTGRAY
            if (color != null) {
                button.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
            }
    }
    }

    companion object {
        val TAG = "GridFragment"
    }
}
package com.example.sudokusolver.grid

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sudokusolver.R

//https://www.raywenderlich.com/1560485-android-recyclerview-tutorial-with-kotlin

//https://stackoverflow.com/questions/40587168/simple-android-grid-example-using-recyclerview-with-gridlayoutmanager-like-the

//GridLayout with RecycleView: https://codelabs.developers.google.com/codelabs/kotlin-android-training-grid-layout/#0

class GridAdapter(private val dataSet: Array<String>): RecyclerView.Adapter<GridAdapter.ViewHolder>() {
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val textView: TextView
        init {
            //Define click listener for the ViewHolder's view
            v.setOnClickListener{ Log.d(TAG, "Clicked on TextView at $adapterPosition")}
            textView = v.findViewById(R.id.cell_text)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v  = LayoutInflater.from(parent.context).inflate(R.layout.sudoku_cell, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "Element $position set")
        holder.textView.text = dataSet[position]
    }

    override fun getItemCount(): Int = dataSet.size

    companion object {
        private const val TAG = "ClickListenerTextView"
    }
}
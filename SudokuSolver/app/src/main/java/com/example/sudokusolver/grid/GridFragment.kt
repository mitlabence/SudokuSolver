package com.example.sudokusolver.grid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sudokusolver.databinding.FragmentGridBinding

class GridFragment : Fragment() {
    private lateinit var binding: FragmentGridBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGridBinding.inflate(inflater, container, false)
        /*
        binding.addPlant.setOnClickListener {
            navigateToPlantListPage()
        }
        */
        return binding.root
    }
}
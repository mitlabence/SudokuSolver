package com.example.sudokusolver


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.sudokusolver.databinding.FragmentMenuBinding


class MenuFragment : Fragment() {
    private lateinit var binding: FragmentMenuBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        binding.gridButton.setOnClickListener {view: View ->
            Navigation.findNavController(view).navigate(R.id.action_menuFragment_to_gridFragment)
        }
        binding.cameraButton.setOnClickListener {view: View ->
            Navigation.findNavController(view).navigate(R.id.action_menuFragment_to_cameraFragment)
        }
        return binding.root
    }

}
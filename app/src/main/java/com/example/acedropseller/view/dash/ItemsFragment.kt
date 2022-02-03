package com.example.acedropseller.view.dash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.acedropseller.R
import com.example.acedropseller.databinding.FragmentItemsBinding
import com.example.acedropseller.databinding.FragmentLandingBinding

class ItemsFragment : Fragment() {
    private var _binding: FragmentItemsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemsBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.fabBtn.setOnClickListener{
            findNavController().navigate(R.id.action_itemsFragment_to_uploadProductFragment)
        }

        return view
    }

}
package com.example.acedropseller.view.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.acedropseller.R
import com.example.acedropseller.databinding.FragmentAadharBinding
import com.example.acedropseller.databinding.FragmentPersonalDetailsBinding

class AadharFragment : Fragment() {

    private var _binding: FragmentAadharBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAadharBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.uploadBtn.setOnClickListener{
            findNavController().navigate(R.id.action_aadharFragment_to_sellerPhotoFragment)
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.acedropseller.view.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.acedropseller.R
import com.example.acedropseller.databinding.FragmentPersonalDetailsBinding
import com.example.acedropseller.databinding.FragmentSellerPhotoBinding

class SellerPhotoFragment : Fragment() {
    private var _binding: FragmentSellerPhotoBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSellerPhotoBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.nextBtn.setOnClickListener{
            findNavController().navigate(R.id.action_sellerPhotoFragment_to_dashboardActivity)
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
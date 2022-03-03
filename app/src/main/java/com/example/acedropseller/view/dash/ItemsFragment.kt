package com.example.acedropseller.view.dash

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.acedropseller.R
import com.example.acedropseller.adapter.ProductAdapter
import com.example.acedropseller.databinding.FragmentItemsBinding
import com.example.acedropseller.network.ApiResponse
import com.example.acedropseller.viewmodel.ProductViewModel

class ItemsFragment : Fragment() {
    private var _binding: FragmentItemsBinding? = null
    private val binding get() = _binding!!
    private var productAdapter = ProductAdapter()
    private val productViewModel: ProductViewModel by activityViewModels()
    private var mLastClickTime = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabBtn.setOnClickListener {
            findNavController().navigate(R.id.action_itemsFragment_to_uploadProductFragment)
        }

        getProductList()

        productAdapter.setOnItemClickListener(object : ProductAdapter.onItemClickListener {
            override fun Update(position: Int) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                    return
                } else {
                    val bundle = bundleOf("ProductDetails" to productAdapter.productList[position])
                    findNavController().navigate(
                        R.id.action_itemsFragment_to_uploadProductFragment,
                        bundle
                    )
                }

            }
        })
    }

    private fun getProductList() {
        productViewModel.getProductList(requireContext())?.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponse.Success -> {
                    binding.progressBar.visibility = View.GONE
                    it.data?.let { it1 -> productAdapter.updateProductList(it1) }
                    binding.itemsRv.adapter = productAdapter
                }
                is ApiResponse.Loading -> binding.progressBar.visibility = View.VISIBLE
                is ApiResponse.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
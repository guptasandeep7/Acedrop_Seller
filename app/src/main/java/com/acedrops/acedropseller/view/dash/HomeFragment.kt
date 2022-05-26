package com.acedrops.acedropseller.view.dash

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.acedrops.acedropseller.R
import com.acedrops.acedropseller.adapter.HomeAdapter
import com.acedrops.acedropseller.databinding.FragmentHomeBinding
import com.acedrops.acedropseller.model.home.HomeItem
import com.acedrops.acedropseller.network.ApiResponse
import com.acedrops.acedropseller.repository.Datastore
import com.acedrops.acedropseller.repository.Datastore.Companion.ACCESS_TOKEN_KEY
import com.acedrops.acedropseller.repository.Datastore.Companion.NAME_KEY
import com.acedrops.acedropseller.repository.Datastore.Companion.REF_TOKEN_KEY
import com.acedrops.acedropseller.utill.ProgressDialog
import com.acedrops.acedropseller.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()
    private var homeAdapter = HomeAdapter()
    lateinit var dialog: Dialog
    private var mLastClickTime = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        val datastore = activity?.let { Datastore(it) }

        lifecycleScope.launch {
            binding.textviewName.let {
                it.text = "Hello, ${datastore?.getUserDetails(NAME_KEY)}"
            }
            Log.w("HOME FRAGMENT", "ACCESS TOKEN : ${datastore?.getUserDetails(ACCESS_TOKEN_KEY)}")
            Log.w("HOME FRAGMENT", "refresh token : ${datastore?.getUserDetails(REF_TOKEN_KEY)}")
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog = ProgressDialog.progressDialog(requireContext())

        getHomeData()

        homeAdapter.setOnItemClickListener(object : HomeAdapter.onItemClickListener {
            override fun acceptOrder(position: Int) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                    return
                } else {
                    acceptDialog(homeAdapter.orderList[position].orders[0].order_item.id)
                }
            }

            override fun cancelOrder(position: Int) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
                    return
                } else {
                    rejectDialog(homeAdapter.orderList[position].orders[0].order_item.id)
                }
            }
        })
    }

    private fun acceptDialog(id: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Accept Order")
            .setMessage("Are you sure you want to accept this order?")
            .setPositiveButton("Accept") { _, _ ->
                acceptOrder(id)
            }
            .setNeutralButton("Cancel") { dialog, _ -> dialog.dismiss() }
        val exit = builder.create()
        exit.show()
    }

    private fun rejectDialog(id: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Reject Order")
            .setMessage("Are you sure you want to reject this order?")
            .setPositiveButton("Reject") { _, _ ->
                rejectOrder(id)
            }
            .setNeutralButton("Back") { dialog, _ -> dialog.dismiss() }
        val exit = builder.create()
        exit.show()
    }

    private fun acceptOrder(id: Int) {
        homeViewModel.acceptOrder(id, requireContext()).observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponse.Success -> {
                    dialog.cancel()
                    resultDialog(R.layout.accepted_dialog)
                }
                is ApiResponse.Loading -> {
                    dialog.show()
                }
                is ApiResponse.Error -> {
                    dialog.cancel()
                    Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun rejectOrder(id: Int) {
        homeViewModel.rejectOrder(id, requireContext()).observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponse.Success -> {
                    dialog.cancel()
                    resultDialog(R.layout.rejected_dialog)
                }
                is ApiResponse.Loading -> {
                    dialog.show()
                }
                is ApiResponse.Error -> {
                    dialog.cancel()
                    Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun resultDialog(layout: Int) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(layout)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        dialog.show()

        Handler().postDelayed(
            {
                dialog.dismiss()
            }, 4000
        )
    }

    private fun getHomeData() {
        homeViewModel.getHomeData(requireContext()).observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponse.Success -> {
                    binding.progressBar.visibility = View.GONE
                    it.data?.let { it1 -> homeAdapter.updateOrderList(changeOrderList(it1)) }
                    binding.homeRv.adapter = homeAdapter
                    binding.orderRev.text = homeAdapter.orderList.size.toString()
                }
                is ApiResponse.Loading -> binding.progressBar.visibility = View.VISIBLE
                is ApiResponse.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun changeOrderList(homeItems: List<HomeItem>): MutableList<HomeItem> {
        val orderList = mutableListOf<HomeItem>()
        homeItems.forEach {
            it.orders.forEach { it2 ->
                orderList.add(
                    HomeItem(
                        it.basePrice,
                        it.description,
                        it.discountedPrice,
                        it.id,
                        it.imgUrls,
                        it.offers,
                        listOf(it2),
                        it.shopId,
                        it.shortDescription,
                        it.stock,
                        it.title
                    )
                )
            }
        }
        return orderList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
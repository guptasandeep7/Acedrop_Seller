package com.example.acedropseller.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.acedropseller.R
import com.example.acedropseller.databinding.HomeItemBinding
import com.example.acedropseller.model.home.HomeItem

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    var orderList = mutableListOf<HomeItem>()
    fun updateOrderList(homeItems: List<HomeItem>) {
        orderList = homeItems.toMutableList()
        notifyDataSetChanged()
    }

    private var mlistner: onItemClickListener? = null

    interface onItemClickListener {
        fun acceptOrder(position: Int)
        fun cancelOrder(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mlistner = listener
    }

    class ViewHolder(val binding: HomeItemBinding, listener: onItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(homeItem: HomeItem) {
            binding.homeItem = homeItem
            binding.productBasePrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

        init {
            binding.cancelBtn.setOnClickListener {
                listener.cancelOrder(adapterPosition)
            }
            binding.acceptBtn.setOnClickListener {
                listener.acceptOrder(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: HomeItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.home_item, parent, false
        )
        return ViewHolder(binding, mlistner!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(orderList[position])
    }

    override fun getItemCount(): Int {
        return orderList.size
    }
}
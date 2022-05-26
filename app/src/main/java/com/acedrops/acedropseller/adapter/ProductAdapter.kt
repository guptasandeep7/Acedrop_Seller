package com.acedrops.acedropseller.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.acedrops.acedropseller.R
import com.acedrops.acedropseller.databinding.ProductItemBinding
import com.acedrops.acedropseller.model.ProductData

class ProductAdapter : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    var productList = mutableListOf<ProductData>()
    fun updateProductList(productList: List<ProductData>) {
        this.productList = productList.toMutableList()
        notifyDataSetChanged()
    }

    private var mlistner: onItemClickListener? = null

    interface onItemClickListener {
        fun Update(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mlistner = listener
    }

    class ViewHolder(val binding: ProductItemBinding, listener: onItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductData) {
            binding.product = product
            binding.productBasePrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

        init {
            binding.updateBtn.setOnClickListener {
                listener.Update(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ProductItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.product_item, parent, false
        )
        return ViewHolder(binding, mlistner!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}
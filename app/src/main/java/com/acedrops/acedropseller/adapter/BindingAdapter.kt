package com.acedrops.acedropseller.adapter

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.load
import com.acedrops.acedropseller.R
import com.acedrops.acedropseller.model.home.ImgUrl

@BindingAdapter("imageFromUrl")
fun ImageView.imageFromUrl(url: String?) {
    if (url != null){
        this.load(url) {
            placeholder(R.drawable.ic_placeholder)
            crossfade(true)
        }
    }
    else{
        this.setBackgroundResource(R.drawable.ic_placeholder)
    }
}

@BindingAdapter("imageCheck")
fun ImageView.imageCheck(imgUrl: List<ImgUrl>) {
    try{
        this.load(imgUrl[0].imageUrl) {
            placeholder(R.drawable.ic_placeholder)
            crossfade(true)
        }
    }catch (e:Exception){
        this.setBackgroundResource(R.drawable.ic_placeholder)
    }
}

@BindingAdapter("toStringText")
fun TextView.toStringText(long: Long) {
    this.text = "${resources.getString(R.string.Rs)}$long"
}



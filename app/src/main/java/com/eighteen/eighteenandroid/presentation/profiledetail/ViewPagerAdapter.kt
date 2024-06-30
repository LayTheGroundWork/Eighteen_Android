package com.eighteen.eighteenandroid.presentation.profiledetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.eighteen.eighteenandroid.R
import com.eighteen.eighteenandroid.presentation.common.imageloader.ImageLoader

class ViewPagerAdapter(private val items: List<String>) : RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.page_item, parent, false)
        return ViewPagerViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ViewPagerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.ImageView)

        fun bind(url: String) {
            ImageLoader.get().loadUrl(imageView = imageView, url = url)
        }
    }
}
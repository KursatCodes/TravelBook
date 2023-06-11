package com.muhammedkursatgokgun.mymaps.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.Placeholder
import androidx.recyclerview.widget.RecyclerView
import com.muhammedkursatgokgun.mymaps.MapsActivity
import com.muhammedkursatgokgun.mymaps.databinding.RecyclerRowBinding
import com.muhammedkursatgokgun.mymaps.model.Place

class PlaceAdapter(val placeList: List<Place>) : RecyclerView.Adapter<PlaceAdapter.Placeholder>() {

    class Placeholder(val recyclerRowBinding: RecyclerRowBinding) : RecyclerView.ViewHolder(recyclerRowBinding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Placeholder {
        val recyclerRowBinding= RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Placeholder(recyclerRowBinding)
    }

    override fun getItemCount(): Int {
        return placeList.size
    }

    override fun onBindViewHolder(holder: Placeholder, position: Int) {
        holder.recyclerRowBinding.recycleViewTextView.setText(placeList[position].name)
        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context,MapsActivity::class.java)
            intent.putExtra("place",placeList[position])
            intent.putExtra("info","old")
            holder.itemView.context.startActivity(intent)
        }
    }

}
package com.example.artbookwithnavigationkt.Adapter


import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.artbookwithnavigationkt.RoomDataBase.Art
import com.example.artbookwithnavigationkt.View.GalleryFragmentDirections
import com.example.artbookwithnavigationkt.databinding.RecyclerRowBinding



class ArtBookAdapter(val artList: List<Art>) : RecyclerView.Adapter<ArtBookAdapter.ArtHolder>(){
    private lateinit var recyclerRowBinding: RecyclerRowBinding

    class ArtHolder(val recyclerRowBinding: RecyclerRowBinding ) : RecyclerView.ViewHolder(recyclerRowBinding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtHolder {

        recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ArtHolder(recyclerRowBinding)
    }

    override fun getItemCount(): Int {
        return artList.size
    }

    override fun onBindViewHolder(holder: ArtHolder, position: Int) {
        val byteArray = artList[position].artImage
        if(byteArray != null) {
            val bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
            holder.recyclerRowBinding.recyclerViewImage.setImageBitmap(bitmap)
        } else {
            holder.recyclerRowBinding.recyclerViewImage.setImageBitmap(null)
        }
        holder.recyclerRowBinding.recyclerViewTextView.text = artList[position].artName

        holder.itemView.setOnClickListener{

            val action = GalleryFragmentDirections.actionGalleryFragmentToSavedArtGallery("old",artList[position].id)

            Navigation.findNavController(it).navigate(action)

        }
    }
}
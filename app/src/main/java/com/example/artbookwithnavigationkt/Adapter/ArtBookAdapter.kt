package com.example.artbookwithnavigationkt.Adapter


import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.artbookwithnavigationkt.RoomDataBase.Art
import com.example.artbookwithnavigationkt.RoomDataBase.ArtDAO
import com.example.artbookwithnavigationkt.RoomDataBase.ArtDatabase
import com.example.artbookwithnavigationkt.View.GalleryFragmentDirections
import com.example.artbookwithnavigationkt.databinding.RecyclerRowBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class ArtBookAdapter(val artList: List<Art>) : RecyclerView.Adapter<ArtBookAdapter.ArtHolder>(){
    private lateinit var recyclerRowBinding: RecyclerRowBinding
    private lateinit var artDao: ArtDAO
    private var artFromMain: Art? = null
    private val compositeDisposable = CompositeDisposable()
    private lateinit var artDatabase: ArtDatabase
    class ArtHolder(val recyclerRowBinding: RecyclerRowBinding ) : RecyclerView.ViewHolder(recyclerRowBinding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtHolder {
        recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        artDatabase = Room.databaseBuilder(parent.context, ArtDatabase::class.java, "ArtDB").build()
        artDao = artDatabase.artDAO()

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

        holder.itemView.setOnLongClickListener{
            artFromMain = artList[position]
            
            val alert = AlertDialog.Builder(holder.itemView.context)
            alert.setTitle("Art Book")
            alert.setMessage("This content will be delete. \nAre you sure?")
            alert.setPositiveButton("Yes"){dialog,which ->

                artFromMain?.let {
                    compositeDisposable.add(artDao.delete(it)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe())
                }
            }
            alert.setNegativeButton("No"){dialog,which ->
            }
            alert.show()
            return@setOnLongClickListener true
        }

        holder.itemView.setOnClickListener{

            val action = GalleryFragmentDirections.actionGalleryFragmentToSavedArtGallery(artList[position].id)

            Navigation.findNavController(it).navigate(action)

        }
    }
}
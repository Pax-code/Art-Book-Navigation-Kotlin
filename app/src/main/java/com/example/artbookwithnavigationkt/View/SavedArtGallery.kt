package com.example.artbookwithnavigationkt.View

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.artbookwithnavigationkt.Adapter.ArtBookAdapter
import com.example.artbookwithnavigationkt.R
import com.example.artbookwithnavigationkt.RoomDataBase.Art
import com.example.artbookwithnavigationkt.RoomDataBase.ArtDAO
import com.example.artbookwithnavigationkt.RoomDataBase.ArtDatabase
import com.example.artbookwithnavigationkt.databinding.FragmentGalleryBinding
import com.example.artbookwithnavigationkt.databinding.FragmentSavedArtGalleryBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class SavedArtGallery : Fragment() {
    private var _binding: FragmentSavedArtGalleryBinding? = null
    private val binding get() = _binding!!
    private val compositeDisposable = CompositeDisposable()
    private lateinit var artDao: ArtDAO
    private lateinit var artDatabase: ArtDatabase
    private var artFromMain: Art? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        artDatabase = Room.databaseBuilder(requireContext(), ArtDatabase::class.java, "ArtDB").build()
        artDao = artDatabase.artDAO()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSavedArtGalleryBinding.inflate(layoutInflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val selectedId = SavedArtGalleryArgs.fromBundle(it).id
            compositeDisposable.add(artDao.getArtById(selectedId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse))
        }
        binding.deleteButton.setOnClickListener{
            artFromMain?.let {
                compositeDisposable.add(artDao.delete(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponseDelete))
            }
        }
    }
    private fun handleResponseDelete(){
        val action = SavedArtGalleryDirections.actionSavedArtGalleryToGalleryFragment()
        Navigation.findNavController(requireView()).navigate(action)
    }
    private fun handleResponse(art: Art) {
        artFromMain = art
        binding.artDate.setText(art.artDate)
        binding.artistName.setText("${art.artistName}")
        binding.artName.setText(art.artName)
        art.artImage?.let {
            println("çalıştı")
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            binding.savedArtView.setImageBitmap(bitmap)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
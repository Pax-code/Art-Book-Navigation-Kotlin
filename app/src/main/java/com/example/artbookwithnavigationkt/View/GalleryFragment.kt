package com.example.artbookwithnavigationkt.View

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.artbookwithnavigationkt.Adapter.ArtBookAdapter
import com.example.artbookwithnavigationkt.R
import com.example.artbookwithnavigationkt.RoomDataBase.Art
import com.example.artbookwithnavigationkt.RoomDataBase.ArtDAO
import com.example.artbookwithnavigationkt.RoomDataBase.ArtDatabase
import com.example.artbookwithnavigationkt.databinding.FragmentGalleryBinding
import com.example.artbookwithnavigationkt.startAnimation
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class GalleryFragment : Fragment() {
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() =  _binding!!
    private val compositeDisposable = CompositeDisposable()
    private lateinit var artDao : ArtDAO
    private lateinit var artDatabase : ArtDatabase


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
        _binding = FragmentGalleryBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fabAnimation()
        gettingDataForRecyclerView()
    }
    private fun fabAnimation(){
        val animation = AnimationUtils.loadAnimation(requireContext(),
            R.anim.circle_expanding_animation
        ).apply {
            duration = 700
            interpolator = AccelerateDecelerateInterpolator()
        }

        binding.fab.setOnClickListener{
            try {
        Handler().postDelayed({
            val action = GalleryFragmentDirections.actionGalleryFragmentToAddArtFragment()
            Navigation.findNavController(it).navigate(action)
        },430)
            }catch (e: Exception){
                e.printStackTrace()
            }
            binding.circle.startAnimation(animation){
                binding.fab.isVisible = false
                binding.circle.isVisible = true
                binding.circle.isVisible = false
                binding.fab.isVisible = false
            }
        }
    }

    private fun gettingDataForRecyclerView(){
        compositeDisposable.add(artDao.getArtWithNameAndIdAndImage()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponse, Throwable::printStackTrace))

    }
    private fun handleResponse(artList: List<Art>) {
        binding.recyclerArtView.layoutManager = LinearLayoutManager(requireContext())
        val artAdapter = ArtBookAdapter(artList)
        binding.recyclerArtView.adapter = artAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
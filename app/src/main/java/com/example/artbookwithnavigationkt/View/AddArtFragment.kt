package com.example.artbookwithnavigationkt.View

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.room.Room
import com.example.artbookwithnavigationkt.R
import com.example.artbookwithnavigationkt.RoomDataBase.Art
import com.example.artbookwithnavigationkt.RoomDataBase.ArtDAO
import com.example.artbookwithnavigationkt.RoomDataBase.ArtDatabase
import com.example.artbookwithnavigationkt.databinding.FragmentAddArtBinding
import com.example.artbookwithnavigationkt.startAnimation
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream


class AddArtFragment : Fragment() {
    private  var _binding: FragmentAddArtBinding? = null
    private val binding get() = _binding!!
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var selectedBitmap : Bitmap? = null
    private lateinit var artDatabase : ArtDatabase
    private lateinit var artDao : ArtDAO
    private val compositeDisposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        artDatabase = Room.databaseBuilder(requireContext(),ArtDatabase::class.java,"ArtDB").build()
        artDao = artDatabase.artDAO()
        registerLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        _binding = FragmentAddArtBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.button.setOnClickListener { gettingDataForDataBase(view) }
        fabAnimation()
        permissionListener()
        binding.button.isVisible = true
        //Save Button
    }
    private fun permissionListener() {
        binding.imageView.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ){
                //Android 33+ READ_MEDIA_IMAGES
                if (ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                    //permission denied
                    if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),android.Manifest.permission.READ_MEDIA_IMAGES)){
                        //rationale
                        Snackbar.make(it,"Permission needed",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",View.OnClickListener {
                            //request permission
                            permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                        }).show()
                    }else{
                        //request permission
                        permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                    }
                }else{
                    val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                }

            }else {
                //api 32- READ_EXTERNAL_STORAGE
                if (ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    //permission denied
                    if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                        //rationale
                        Snackbar.make(it,"Permission needed",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",View.OnClickListener {
                            //request permission
                            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        }).show()
                    }else{
                        //request permission
                        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }else{
                    val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                }
            }

        }
    }

    private fun fabAnimation(){
        val animation = AnimationUtils.loadAnimation(requireContext(),
            R.anim.circle_expanding_animation
        ).apply {
            duration = 700
            interpolator = AccelerateDecelerateInterpolator()
        }

        binding.fab2.setOnClickListener{
                Handler().postDelayed({
                    binding.button.isVisible = false
                },200)
            try {
                Handler().postDelayed({
                    val action = AddArtFragmentDirections.actionAddArtFragmentToGalleryFragment()
                    Navigation.findNavController(it).navigate(action)
                },430)
            }catch (e: Exception){
                e.printStackTrace()
            }

            binding.fab2.isVisible = false

            binding.circleHome.startAnimation(animation){
                binding.circleHome.isVisible = true
                binding.circleHome.isVisible = false
                binding.fab2.isVisible = false
            }
        }
    }

    private fun registerLauncher(){
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK){
                val intentFromResult = result.data
                if (intentFromResult != null){
                    val imageData = intentFromResult.data
                    if (imageData != null) {
                        try {
                            if (Build.VERSION.SDK_INT >= 28) {
                                val source = ImageDecoder.createSource(requireContext().contentResolver, imageData)

                                selectedBitmap = ImageDecoder.decodeBitmap(source)
                                binding.imageView.setImageBitmap(selectedBitmap)
                            }else{
                                selectedBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,imageData)
                                binding.imageView.setImageBitmap(selectedBitmap)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){result ->
            if (result){
                //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }else{
                //permission denied
                Toast.makeText(requireContext(), "Permission needed", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun makeSmallerBitmap(image: Bitmap, maximumSize: Int) : Bitmap{

        var width = image.width
        var height = image.height

        val bitmapRatio : Double = width.toDouble() / height.toDouble()
        if (bitmapRatio > 1){
            //landscape
            width = maximumSize
            val scaledHight = width / bitmapRatio
            height = scaledHight.toInt()
        }else{
            //portrait or square
            height = maximumSize
            val scaleWidth = height * bitmapRatio
            width = scaleWidth.toInt()
        }
        return Bitmap.createScaledBitmap(image,width,height,true)
    }

   private fun gettingDataForDataBase(view: View){
        val artName = binding.artNameText.text.toString().trim()
        val artistName = binding.artistText.text.toString().trim()
        val artDate = binding.artDateText.text.toString().trim()

        if (selectedBitmap != null){
            val smallBitmap = makeSmallerBitmap(selectedBitmap!!,300)

            val outputStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteArray = outputStream.toByteArray()

            val art = Art(artName,artistName,artDate,byteArray)

            compositeDisposable.add(artDao.insert(art)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
            )


        }
       val errorMsg = StringBuilder()

       //this part is checking texts and imageView is empty or not but i want to see all of them in the same time. i used a lot of if because of this issue
       if (selectedBitmap == null) {
           errorMsg.append("You should choose also an image.\n")
       }
       if (TextUtils.isEmpty(artName)) {
           binding.artNameText.error = "Art name can not be empty"
           errorMsg.append(" ")
       }
       if (TextUtils.isEmpty(artistName)) {
           binding.artistText.error = "Artist name can not be empty"
           errorMsg.append(" ")
       }
       if (TextUtils.isEmpty(artDate)) {
           binding.artDateText.error = "Art made date can not be empty"
           errorMsg.append(" ")
       }

       if (errorMsg.isNotEmpty()) {
           Toast.makeText(requireContext(), errorMsg.toString().trim(), Toast.LENGTH_SHORT).show()
       }
    }

    private fun handleResponse(){
        val action = AddArtFragmentDirections.actionAddArtFragmentToGalleryFragment()
        Navigation.findNavController(requireView()).navigate(action)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
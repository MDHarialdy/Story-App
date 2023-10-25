package com.mdh.storyapp.newstory

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.mdh.storyapp.ViewModelFactory
import com.mdh.storyapp.databinding.ActivityNewStoryBinding
import com.mdh.storyapp.main.MainActivity
import com.mdh.storyapp.newstory.CameraActivity.Companion.CAMERAX_RESULT
import com.mdh.storyapp.newstory.CameraActivity.Companion.EXTRA_CAMERAX_IMAGE
import com.mdh.storyapp.utils.reduceFileImage
import com.mdh.storyapp.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class NewStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewStoryBinding
    private var currentImageUri: Uri? = null
    private val newStoryViewModel by viewModels<NewStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

        }

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.apply {
            btnGallery.setOnClickListener{ startGallery() }
            btnCamera.setOnClickListener{ startCameraX() }
            btnUpload.setOnClickListener{ uploadImage() }
        }
    }

    //launcher cameraX
    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }

    //launcher gallery
    private fun startGallery() {
        launchGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launchGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    //fungsi check&request permission
    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    //showimage
    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image Uri", "showImage: $it")
            binding.imgAdd.setImageURI(it)
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")

            val description = binding.edtDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )
            newStoryViewModel.uploadFile(multipartBody, description)
            newStoryViewModel.uploadResponse.observe(this){ response ->
                if (response != null) {
                    if(!response.error){
                        Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    //companion object
    companion object{
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        const val MAXIMAL_SIZE = 1 * 1024 * 1024
    }
}
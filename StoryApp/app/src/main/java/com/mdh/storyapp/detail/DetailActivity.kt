package com.mdh.storyapp.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import coil.load
import com.mdh.storyapp.ViewModelFactory
import com.mdh.storyapp.databinding.ActivityDetailBinding


class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpViewModel()
    }

    private fun setUpViewModel(){
        val username = intent.getStringExtra(USER_ID) ?: ""
        detailViewModel.getDetail(username)
        detailViewModel.Story.observe(this){
            binding.imgDetail.load(it.photoUrl)
            binding.tvTitle.text = it.name
            binding.tvDescription.text = it.description
        }
    }
    companion object{
        const val USER_ID = "user_id"
    }
}
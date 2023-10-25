package com.mdh.storyapp.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mdh.storyapp.R
import com.mdh.storyapp.ViewModelFactory
import com.mdh.storyapp.databinding.ActivityMainBinding
import com.mdh.storyapp.newstory.NewStoryActivity
import com.mdh.storyapp.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabButton.setOnClickListener{
            val moveIntent = Intent(this, NewStoryActivity::class.java)
            startActivity(moveIntent)
        }
        setUpViewModel()
        setUpAction()
    }

    private fun setUpAction() {
        mainViewModel.fetchStories()
    }

    private fun setUpViewModel(){
        mainViewModel.getSession().observe(this){
            Log.d("MainActivity", "user: ${it.name}")
            Log.d("MainActivity", "isLogin: ${it.isLogin}")
            if (it.isLogin) {
                Log.d("MainActivity", "try to login")
                mainViewModel.fetchStories()
                binding.root.visibility = View.VISIBLE
            } else {
                Log.d("MainActivity", "Tidak Ada User")
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        mainViewModel.listStory.observe(this){
            setListStory(it)
        }
    }

    private fun setListStory(items: List<ListStoryItem>) {
        val listUserAdapter = UserAdapter(items)
        binding.apply {
            rvItemUser.layoutManager = LinearLayoutManager(this@MainActivity)
            rvItemUser.adapter = listUserAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                mainViewModel.logout()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
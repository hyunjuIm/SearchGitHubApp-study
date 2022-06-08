package com.example.searchgithubapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isGone
import com.example.searchgithubapp.data.entity.GithubRepoEntity
import com.example.searchgithubapp.databinding.ActivitySearchBinding
import com.example.searchgithubapp.utillity.RetrofitUtil
import com.example.searchgithubapp.view.RepositoryRecyclerAdapter
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SearchActivity : AppCompatActivity(), CoroutineScope {

    lateinit var binding: ActivitySearchBinding
    lateinit var adapter: RepositoryRecyclerAdapter

    private val job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdapter()
        initViews()
        bindViews()
    }

    private fun initAdapter() = with(binding) {
        adapter = RepositoryRecyclerAdapter()
    }

    private fun initViews() = with(binding) {
        emptyResultTextView.isGone = true
        recyclerView.adapter = adapter
    }

    private fun bindViews() = with(binding) {
        searchButton.setOnClickListener {
            searchKeyword(searchBarInputView.text.toString())
        }
    }

    private fun searchKeyword(keywordString: String) = launch {
        withContext(Dispatchers.IO) {
            val response = RetrofitUtil.githubApiService.searchRepositories(keywordString)
            if (response.isSuccessful) {
                val body = response.body()
                withContext(Dispatchers.Main) {
                    Log.d("response", body.toString())
                    setData(body?.items.orEmpty())
                }
            }
        }
    }

    private fun setData(items: List<GithubRepoEntity>) {
        adapter.setRepositoryList(items) {
            Toast.makeText(this, "entity : $it", Toast.LENGTH_SHORT).show()
        }
    }
}
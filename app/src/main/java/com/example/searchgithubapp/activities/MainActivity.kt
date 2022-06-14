package com.example.searchgithubapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isGone
import com.example.searchgithubapp.data.database.DataBaseProvider
import com.example.searchgithubapp.data.entity.GithubRepoEntity
import com.example.searchgithubapp.databinding.ActivityMainBinding
import com.example.searchgithubapp.view.RepositoryRecyclerAdapter
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var binding: ActivityMainBinding

    private val job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val repositoryDao by lazy {
        DataBaseProvider.provideDB(applicationContext).repositoryDao()
    }

    private lateinit var repositoryRecyclerAdapter: RepositoryRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdapter()
        initViews()
    }

    private fun initAdapter() {
        repositoryRecyclerAdapter = RepositoryRecyclerAdapter()
    }

    private fun initViews() = with(binding) {
        recyclerView.adapter = repositoryRecyclerAdapter

        searchButton.setOnClickListener {
            startActivity(
                Intent(this@MainActivity, SearchActivity::class.java)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        launch(coroutineContext) {
            loadLikedRepositoryList()
        }
    }

    private suspend fun loadLikedRepositoryList() = withContext(Dispatchers.IO) {
        val repoList = repositoryDao.getHistory()
        withContext(Dispatchers.Main) {
            setData(repoList)
        }
    }

    private fun setData(githubRepositoryList: List<GithubRepoEntity>) = with(binding) {
        if (githubRepositoryList.isEmpty()) {
            emptyResultTextView.isGone = false
            recyclerView.isGone = true
        } else {
            emptyResultTextView.isGone = true
            recyclerView.isGone = false
            repositoryRecyclerAdapter.setRepositoryList(githubRepositoryList) {
                startActivity(
                    Intent(this@MainActivity, RepositoryActivity::class.java).apply {
                        putExtra(RepositoryActivity.REPOSITORY_OWNER_KEY, it.owner.login)
                        putExtra(RepositoryActivity.REPOSITORY_NAME_KEY, it.name)
                    }
                )
            }
        }
    }
}
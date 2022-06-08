package com.example.searchgithubapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isGone
import com.example.searchgithubapp.data.entity.GithubRepoEntity
import com.example.searchgithubapp.databinding.ActivityRepositoryBinding
import com.example.searchgithubapp.extensions.loadCenterInside
import com.example.searchgithubapp.utillity.RetrofitUtil
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class RepositoryActivity : AppCompatActivity(), CoroutineScope {

    lateinit var binding: ActivityRepositoryBinding

    private val job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    companion object {
        const val REPOSITORY_OWNER_KEY = "REPOSITORY_OWNER_KEY"
        const val REPOSITORY_NAME_KEY = "REPOSITORY_NAME_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepositoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repositoryOwner = intent.getStringExtra(REPOSITORY_OWNER_KEY) ?: kotlin.run {
            Toast.makeText(this, "Repository Owner 이름이 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val repositoryName = intent.getStringExtra(REPOSITORY_NAME_KEY) ?: kotlin.run {
            Toast.makeText(this, "Repository 이름이 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        launch {
            loadRepository(repositoryOwner, repositoryName)?.let {
                setData(it)
            } ?: run {
                Toast.makeText(this@RepositoryActivity, "Repository 정보가 없습니다.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        showLoading(true)
    }

    private suspend fun loadRepository(
        repositoryOwner: String,
        repositoryName: String
    ): GithubRepoEntity? = withContext(coroutineContext) {
        var repositoryEntity: GithubRepoEntity? = null
        withContext(Dispatchers.IO) {
            val response = RetrofitUtil.githubApiService.getRepository(
                ownerLogin = repositoryOwner,
                repoName = repositoryName
            )
            if (response.isSuccessful) {
                val body = response.body()
                withContext(Dispatchers.Main) {
                    body?.let { repo ->
                        repositoryEntity = repo
                    }
                }
            }
        }
        repositoryEntity
    }

    @SuppressLint("SetTextI18n")
    private fun setData(githubRepoEntity: GithubRepoEntity) = with(binding) {
        showLoading(false)

        ownerProfileImageView.loadCenterInside(githubRepoEntity.owner.avatarUrl, 42f)
        ownerNameAndRepoNameTextView.text =
            "${githubRepoEntity.owner.login}/${githubRepoEntity.name}"
        stargazersCountText.text = githubRepoEntity.stargazersCount.toString()
        githubRepoEntity.language?.let { language ->
            languageText.isGone = false
            languageText.text = language
        } ?: kotlin.run {
            languageText.isGone = true
            languageText.text = ""
        }
        descriptionTextView.text = githubRepoEntity.description
        updateTimeTextView.text = githubRepoEntity.updatedAt
    }

    private fun showLoading(isShown: Boolean) = with(binding) {
        progressBar.isGone = isShown.not()
    }
}
package com.example.searchgithubapp.data.response

import com.example.searchgithubapp.data.entity.GithubRepoEntity

data class GithubRepoSearchResponse(
    val totalCount: Int,
    val items: List<GithubRepoEntity>
)
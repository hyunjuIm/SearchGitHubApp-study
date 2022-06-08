package com.example.searchgithubapp.utillity

import com.example.searchgithubapp.data.entity.GithubRepoEntity
import com.example.searchgithubapp.data.response.GithubRepoSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApiService {

    @GET("search/repositories")
    suspend fun searchRepositories(@Query("q") query: String): Response<GithubRepoSearchResponse>

    @GET("repos/{owner}/{name}")
    suspend fun getRepository(
        @Path("owner") owner: String,
        @Path("name") repoName: String
    ): GithubRepoEntity
}
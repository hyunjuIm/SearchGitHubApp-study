package com.example.searchgithubapp.utillity

import com.example.searchgithubapp.data.response.GithubAccessTokenResponse
import retrofit2.Response
import retrofit2.http.*

interface AuthApiService {
    @FormUrlEncoded
    @POST("login/oauth/access_token")
    @Headers("Accept: application/json")
    suspend fun getAccessToken(
        @Field("client_id") client: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String,
    ): Response<GithubAccessTokenResponse>
}
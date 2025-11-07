package com.example.nu.urlshortener.data.service

import com.example.nu.urlshortener.data.model.ReadShortenedUrlResponse
import com.example.nu.urlshortener.data.model.UrlShortenRequest
import com.example.nu.urlshortener.data.model.UrlShortenResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UrlShortenerService {

    @POST("/api/alias")
    suspend fun postShortenUrl(
        @Body request: UrlShortenRequest
    ): UrlShortenResponse

    @GET("/api/alias/{id}")
    suspend fun getShortenUrl(
        @Path("id") id: String
    ): ReadShortenedUrlResponse
}
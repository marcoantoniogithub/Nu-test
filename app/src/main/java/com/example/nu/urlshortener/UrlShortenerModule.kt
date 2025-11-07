package com.example.nu.urlshortener

import com.example.nu.urlshortener.data.repository.UrlRepositoryImpl
import com.example.nu.urlshortener.data.service.UrlShortenerService
import com.example.nu.urlshortener.domain.repository.UrlRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUrlRepository(
        repositoryImpl: UrlRepositoryImpl
    ): UrlRepository


    companion object {
        @Provides
        @Singleton
        fun provideUrlShortenerService(retrofit: Retrofit): UrlShortenerService {
            return retrofit.create(UrlShortenerService::class.java)
        }
    }
}

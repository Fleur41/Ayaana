package com.sam.ayaana.data.remote.interceptor


import android.util.Log
import com.sam.ayaana.datastore.DatastoreRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val datastoreRepository: DatastoreRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Try to get token from DataStore
        val token = runBlocking {
            datastoreRepository.getAuthToken()
        }

        // If we have a token, add it to the request
        val requestBuilder = originalRequest.newBuilder()
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
            Log.d("AuthInterceptor", "Adding auth token to request")
        }

        val request = requestBuilder.build()
        val response = chain.proceed(request)

        // Handle token expiration (401 Unauthorized)
        if (response.code == 401) {
            Log.d("AuthInterceptor", "Token expired, attempting refresh")
            // TODO: Implement token refresh logic when you have refresh tokens
            response.close()
        }

        return response
    }
}
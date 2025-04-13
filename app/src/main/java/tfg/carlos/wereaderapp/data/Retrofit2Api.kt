package tfg.carlos.wereaderapp.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tfg.carlos.wereaderapp.data.network.AuthService

object Retrofit2Api  {
    private const val BASE_URL = "http://10.0.2.2:3000/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }
}

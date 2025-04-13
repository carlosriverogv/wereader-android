package tfg.carlos.wereaderapp.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.data.network.AuthInterceptor
import tfg.carlos.wereaderapp.data.network.AuthService
import tfg.carlos.wereaderapp.data.network.SessionManager
import tfg.carlos.wereaderapp.data.network.UserService

object Retrofit2Api  {
    private const val BASE_URL = "http://10.0.2.2:3000/"

    private val sessionManager: SessionManager = WeReaderApplication.sessionManager

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor { sessionManager.getToken() })
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }

    val userApi: UserService by lazy {
        retrofit.create(UserService::class.java)
    }
}

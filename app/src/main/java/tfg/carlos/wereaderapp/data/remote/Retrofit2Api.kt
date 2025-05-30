package tfg.carlos.wereaderapp.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tfg.carlos.wereaderapp.WeReaderApplication
import tfg.carlos.wereaderapp.data.remote.api.AuthService
import tfg.carlos.wereaderapp.data.local.SessionManager
import tfg.carlos.wereaderapp.data.remote.api.BookService
import tfg.carlos.wereaderapp.data.remote.api.FriendshipService
import tfg.carlos.wereaderapp.data.remote.api.LibraryService
import tfg.carlos.wereaderapp.data.remote.api.SharedLibraryService
import tfg.carlos.wereaderapp.data.remote.api.UserService

object Retrofit2Api  {
    private const val BASE_URL = "http://192.168.1.158:3000/"

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

    val friendshipApi: FriendshipService by lazy {
        retrofit.create(FriendshipService::class.java)
    }

    val libraryApi: LibraryService by lazy {
        retrofit.create(LibraryService::class.java)
    }

    val bookApi: BookService by lazy {
        retrofit.create(BookService::class.java)
    }

    val sharedLibraryApi: SharedLibraryService by lazy {
        retrofit.create(SharedLibraryService::class.java)
    }
}

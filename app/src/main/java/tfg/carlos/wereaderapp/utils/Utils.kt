package tfg.carlos.wereaderapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService
import com.auth0.android.jwt.JWT

// Función que comprueba si el dispositivo tiene conexión a internet
fun checkConnection(context: Context): Boolean {
    // Obtenemos el servicio de conectividad
    //val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val cm = context.getSystemService<ConnectivityManager>()
    // Obtenemos la red activa
    val networkInfo = cm!!.activeNetwork

    if (networkInfo != null) {
        // Obtenemos las capacidades de la red
        val activeNetwork = cm.getNetworkCapabilities(networkInfo)

        // Comprobamos si la red tiene conexión a internet mediante wifi o datos móviles
        if (activeNetwork != null) {
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        }
    }
    return false
}

fun isTokenValid(token: String?): Boolean {
    if (token.isNullOrEmpty()) return false

    return try {
        val jwt = JWT(token)

        // Comprueba si ha expirado
        !jwt.isExpired(10) // 10 = margen de tolerancia en segundos

    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
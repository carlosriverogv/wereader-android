package edu.carlosrivero.demo5.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService

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
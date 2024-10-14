package com.example.news_compose_c40.ui.screens.news

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.news_compose_c40.data.connectivity.NetworkHandler
import javax.inject.Inject

open class ConnectivityViewModel @Inject constructor(val networkHandler: NetworkHandler):ViewModel() {

    private val _isConnected= mutableStateOf(false)
    val isConnected:Boolean get()=_isConnected.value

    var wasDisconnected=false
    fun updateConnectivity(){
        _isConnected.value = networkHandler.isNetworkAvailable()
        if (!isConnected){
            wasDisconnected = true
        }
        _showBackOnlineMessage.value = wasDisconnected  && _isConnected.value
    }

    private val _showBackOnlineMessage= mutableStateOf(false)
    val showBackOnlineMessage:Boolean get()=_showBackOnlineMessage.value

    fun hideBackOnlineMessage(){
        _showBackOnlineMessage.value = false
    }
}
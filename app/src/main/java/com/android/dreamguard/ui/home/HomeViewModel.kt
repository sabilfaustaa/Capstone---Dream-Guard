package com.android.dreamguard.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.dreamguard.data.remote.api.ApiConfig
import com.android.dreamguard.data.remote.models.HomePageResponse
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _homePageData = MutableLiveData<HomePageResponse?>()
    val homePageData: LiveData<HomePageResponse?> = _homePageData

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun fetchHomePageData() {
        viewModelScope.launch {
            try {
                val apiService = ApiConfig.getApiService(getApplication())
                val response = apiService.getHomePageData()
                if (response.isSuccessful) {
                    _homePageData.value = response.body()
                } else {
                    _errorMessage.value = response.message()
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            }
        }
    }
}

package com.miapp.xanostorekotlin.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Este es el fragmento del Panel"
    }
    val text: LiveData<String> = _text
}
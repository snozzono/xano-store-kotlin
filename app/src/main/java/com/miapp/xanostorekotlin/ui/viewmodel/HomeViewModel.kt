package com.miapp.xanostorekotlin.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Este es el fragmento de Inicio"
    }
    val text: LiveData<String> = _text
}
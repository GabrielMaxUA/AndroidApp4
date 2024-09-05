package com.trios2024aa.itunes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ITunesViewModelFactory(private val repository: ITunesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ITunesViewModel::class.java)) {
            return ITunesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



package com.trios2024aa.itunes

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Response

class ITunesViewModel(private val repository: ITunesRepository) : ViewModel() {

    // State to hold the list of search results
    private val _items = mutableStateOf<List<TuneItem>>(emptyList())
    val items: State<List<TuneItem>> = _items

    // State to check if a search was performed
    private val _searched = mutableStateOf(false)
    val searched: State<Boolean> = _searched

    // State to track loading status
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // State to handle any error messages
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    // Function to search for tunes by query
    fun searchTunes(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response: Response<ITunesResponse> = repository.searchTunes(query)
                if (response.isSuccessful) {
                    _items.value = response.body()?.results ?: emptyList()
                } else {
                    _errorMessage.value = "Error: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                _items.value = emptyList()
            } finally {
                _isLoading.value = false
                _searched.value = true
            }
        }
    }
}

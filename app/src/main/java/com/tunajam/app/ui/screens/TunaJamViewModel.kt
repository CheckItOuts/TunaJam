package com.tunajam.app.ui.screens


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.tunajam.app.TunaJamApplication
import com.tunajam.app.R
import com.tunajam.app.data.TunaJamPhotoRepository
import com.tunajam.app.model.TunaJamPhoto
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * UI state for the Home screen
 */
sealed interface TunaJamUiState {
    data class Success(val photos: List<TunaJamPhoto>) : TunaJamUiState
    object Error : TunaJamUiState
    object Loading : TunaJamUiState
}

class TunaJamViewModel(private val tunaJamPhotoRepository: TunaJamPhotoRepository) : ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var tunaJamUiState: TunaJamUiState by mutableStateOf(TunaJamUiState.Loading)
        private set

    /**
     * Call getMarsPhotos() on init so we can display status immediately.
     */
    init {
        getTunaJamPhotos()
    }

    /**
     * Gets Mars photos information from the Mars API Retrofit service and updates the
     * [MarsPhoto] [List] [MutableList].
     */
    fun getTunaJamPhotos() {
        viewModelScope.launch {
            tunaJamUiState = TunaJamUiState.Loading
            tunaJamUiState = try {
                TunaJamUiState.Success(tunaJamPhotoRepository.getTunaJamPhotos())

            } catch (e: IOException) {
                TunaJamUiState.Error
            } catch (e: HttpException) {
                TunaJamUiState.Error
            }
        }
    }

    /**
     * Factory for [TunaJamViewModel] that takes [TunaJamPhotoRepository] as a dependency
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TunaJamApplication)
                val tunaJamPhotoRepository = application.container.tunaJamRepository
                TunaJamViewModel(tunaJamPhotoRepository = tunaJamPhotoRepository)
            }
        }
    }


}
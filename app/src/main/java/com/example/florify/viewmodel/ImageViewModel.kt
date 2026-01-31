package com.example.florify.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.example.florify.model.Classification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class ClassificationState {
    object Idle : ClassificationState()
    object Loading : ClassificationState()
    data class Success(val results: List<Classification>) : ClassificationState()
    data class Error(val message: String) : ClassificationState()
}

class ImageViewModel : ViewModel() {

    private var _image = MutableStateFlow<Bitmap?>(null)
    val image = _image.asStateFlow()

    private val _classificationState = MutableStateFlow<ClassificationState>(ClassificationState.Idle)
    val classificationState = _classificationState.asStateFlow()

    fun storeImage(image: Bitmap?) {
        _image.value = image
    }

    fun setClassificationState(state: ClassificationState) {
        _classificationState.value = state
    }

}
package com.sielee.browsemymedia.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sielee.browsemymedia.data.model.PhotoModel
import com.sielee.browsemymedia.data.repo.MainRepo

class FoldersViewModel(context: Context) : ViewModel() {
    private val mainRepo = MainRepo(context.applicationContext)
    val folders = mainRepo.getAllImagesPaths()

    fun getPhotos(photosPath: String): MutableList<PhotoModel> {
        return mainRepo.getAllFolderImages(photosPath)
    }
}
class FoldersViewModelFactory(private val context: Context):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoldersViewModel::class.java)){
           return FoldersViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }

}
package com.sielee.browsemymedia.viewmodels

import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sielee.browsemymedia.data.model.FolderModel
import com.sielee.browsemymedia.data.model.PhotoModel
import com.sielee.browsemymedia.data.repo.MainRepo
import java.io.File
import java.util.*

class SharedViewModel(context: Context) : ViewModel() {
    private val mainRepo = MainRepo(context.applicationContext)
    private val _folders  = MutableLiveData<List<FolderModel>>()
    val folderList:LiveData<List<FolderModel>> = _folders

    private val _photos  = MutableLiveData<List<PhotoModel>>()
    private val photoList:LiveData<List<PhotoModel>> = _photos

    private val _photo  = MutableLiveData<PhotoModel>()
    val photo:LiveData<PhotoModel> = _photo

    private val _selectedFolderPhotos  = MutableLiveData<Array<PhotoModel>>()
    val selectedFolderPhotos:LiveData<Array<PhotoModel>> = _selectedFolderPhotos

    init {
        _folders.value = mainRepo.getAllImagesPaths()
    }

    fun setPhotoToEdit(photo: PhotoModel) {
        _photo.value = photo
    }
    fun getPhotos(photosPath: String):LiveData<List<PhotoModel>> {
        _photos.value = mainRepo.getAllFolderImages(photosPath)
      return  photoList
    }
    fun folderSearchFilter(folders:List<FolderModel>, searchString:String): List<FolderModel> {
        return folders.filter { folder ->
            folder.folderName?.lowercase(Locale.getDefault())?.contains(searchString.lowercase(Locale.getDefault()))!!
        }
    }
    fun photoSearchFilter(photos:List<PhotoModel>, searchString:String): List<PhotoModel> {
        return photos.filter { photo ->
            photo.name?.lowercase(Locale.getDefault())?.contains(searchString.lowercase(Locale.getDefault()))!!
        }
    }
    fun sortFoldersByName(folders: List<FolderModel>): List<FolderModel> {
         return folders.sortedWith(compareBy {folder ->
                folder.folderName
            })


    }
    fun sortPhotosByName(photos:List<PhotoModel>): List<PhotoModel> {
        return photos.sortedWith(compareBy {
            it.name
        })
    }
    fun sortPhotosByDateModified(photos:List<PhotoModel>): List<PhotoModel> {
        return photos.sortedWith(compareBy {
            it.last_modified
        })
    }
    fun sortPhotosBySize(photos:List<PhotoModel>): List<PhotoModel> {
        return photos.sortedWith(compareBy {
            it.size
        })
    }
    fun hiddenPhotosExist(photos: List<PhotoModel>): Boolean {
        return photos.map { photo ->
            photo.name!!.startsWith(".")
            true
        }.isEmpty()
    }
    fun deletePhoto(context: Context, filePath:String){
        val fileToDelete = File(filePath)
        val uri:Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        Log.d("PhotosDetails", "deleteCurrentPhoto: ${fileToDelete.name}")
        if (fileToDelete.exists()){
            context.contentResolver.delete(uri,MediaStore.Images.Media.DATA + "=?", arrayOf(fileToDelete.absolutePath))
            //fileToDelete.delete()
            Log.d("PhotosDetails", "deleteCurrentPhoto:${fileToDelete.delete()} Exist:${fileToDelete.exists()} ")
            MediaScannerConnection.scanFile(context, arrayOf(fileToDelete.toString()),null,null)
            //context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(fileToDelete)))
        }
    }
    fun setUpCurrentFolderPhotos(folderPhotos: Array<PhotoModel>) {
        _selectedFolderPhotos.value = folderPhotos
    }
}
class FoldersViewModelFactory(private val context: Context):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)){
           return SharedViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }

}
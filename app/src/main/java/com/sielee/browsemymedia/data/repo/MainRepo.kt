package com.sielee.browsemymedia.data.repo

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.sielee.browsemymedia.data.model.FolderModel
import com.sielee.browsemymedia.data.model.PhotoModel


const val TAG = "MainRepo"
class MainRepo(private val context: Context) {

    fun getAllImagesPaths(): List<FolderModel> {
        val foldersList = mutableListOf<FolderModel>()
        val photosPathList = mutableListOf<String>()

        val imgUrls = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.DATA,
        )

        val BUCKET_GROUP_BY = "1) GROUP BY 1,(2"
        val BUCKET_ORDER_BY = "MAX(datetaken) DESC"
        val cursor: Cursor? = context.contentResolver.query(
            imgUrls,
            projection,
            BUCKET_GROUP_BY,
            null,
            BUCKET_ORDER_BY
        )
        try {
            if(cursor?.moveToFirst()==true)
                do {
                val folders = FolderModel()
                val bucketName =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                val dataPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA))
                val folderPaths = dataPath?.substring(0, dataPath.lastIndexOf("$bucketName/"))+"$bucketName/"

                if (!photosPathList.contains(folderPaths)) {
                    photosPathList.add(folderPaths)
                    folders.apply {
                        path = folderPaths
                        photoFirst = dataPath
                        folderName = bucketName
                    }
                    foldersList.add(folders)
                }else{
                    for(fold in foldersList){
                        if (fold.path.equals(folderPaths)){
                            folders.apply {
                                photoFirst = dataPath
                            }
                        }
                    }
                }

                } while (cursor.moveToNext())
            cursor?.close()
        } catch (error: Exception) {
            error.printStackTrace()
        }
        foldersList.forEach {
            Log.d(TAG, "Folder:${it.folderName} Path: ${it.path} Photos: ${it.photosCount} ")
        }
        Log.d(TAG, "size: ${foldersList.size} ")
        return foldersList
    }

    fun getAllFolderImages(folderPath: String): MutableList<PhotoModel> {
        var photosList = mutableListOf<PhotoModel>()
        val folderPhotosUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_MODIFIED
        )
        val cursor: Cursor? = context.contentResolver.query(
            folderPhotosUri,
            projection,
            MediaStore.Images.Media.DATA + " like ? ",
            arrayOf("%$folderPath%"),
            MediaStore.Images.Media.DATE_MODIFIED + " DESC"
        )
        try {
            cursor?.moveToFirst()

            do {
                val photoModel = PhotoModel()
                photoModel.apply {
                    name =
                        (cursor?.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)))
                    path =
                        (cursor?.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)))
                    size =
                        (cursor?.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)))
                    last_modified =
                        (cursor?.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)))
                }
                photosList.add(photoModel)
            } while (cursor!!.moveToNext())
            cursor.close()

            val reselection = mutableListOf<PhotoModel>()
            photosList.forEach {
                reselection.add(it)
            }
            photosList = reselection
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return photosList
    }

}


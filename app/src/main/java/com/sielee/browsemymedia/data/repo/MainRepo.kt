package com.sielee.browsemymedia.data.repo

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.sielee.browsemymedia.data.model.FolderModel
import com.sielee.browsemymedia.data.model.PhotoModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class MainRepo(private val context: Context) {

    @Suppress("DEPRECATION")
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
            MediaStore.Images.Media.DATE_MODIFIED,

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
            if (cursor?.moveToFirst() == true)
                do {
                    val folder = FolderModel()
                    val bucketName =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                    val dataPath =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA))
                    val folderPaths = dataPath?.substring(
                        0,
                        dataPath.lastIndexOf("$bucketName/")
                    ) + "$bucketName/"
                    val dateModified =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED))

                    if (!photosPathList.contains(folderPaths)) {
                        photosPathList.add(folderPaths)
                        folder.apply {
                            path = folderPaths
                            photoFirst = dataPath
                            folderName = bucketName
                            photosCount = getAllFolderImages(folderPaths).size
                        }
                        foldersList.add(folder)
                    } else {
                        for (fold in foldersList) {
                            if (fold.path.equals(folderPaths)) {
                                folder.apply {
                                    photoFirst = dataPath
                                    addPhotos()
                                    photosCount = getAllFolderImages(fold.path!!).size
                                }
                            }
                        }
                    }

                } while (cursor.moveToNext())
            cursor?.close()
        } catch (error: Exception) {
            error.printStackTrace()
        }
        return foldersList
    }
@Suppress("DEPRECATION")
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
            val selection = MediaStore.Images.Media.DATA + " like ?"
        val cursor: Cursor? = context.contentResolver.query(
            folderPhotosUri,
            projection,
            selection,
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
                    size = formatSize(
                        (cursor?.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)))!!)
                    last_modified = formatDates(
                        (cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)))
                    )
                    id = (cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)))
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

    @SuppressLint("SimpleDateFormat")
    fun formatDates(date:Long): String? {
      return  if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            DateTimeFormatter.ofPattern("dd.MM.yyyy").format(
                Instant.ofEpochMilli(date*1000)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            )
        }else{
        SimpleDateFormat("dd.MM.yyyy").format(Date(date*1000))
        }
    }
    private fun formatSize(size: Int): String {
        val formattedSize:Double = (size/1024.0) //to kbs
        return if (formattedSize/1024.0>1){
             String.format("%.1f", formattedSize/1024.0) + " MB"
        }else{
             String.format("%.1f", formattedSize) + " KB"
        }
    }

}


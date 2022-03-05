package com.sielee.browsemymedia.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FolderModel(
    var path:String?="",
    var folderName:String?="",
    var photoFirst: String?="",
    var photosCount:Int=0
):Parcelable

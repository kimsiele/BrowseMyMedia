package com.sielee.browsemymedia.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoModel (
    var name:String?="",
    var path:String?="",
    var size:String?="",
    var last_modified:String?=""
):Parcelable
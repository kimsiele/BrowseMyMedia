package com.sielee.browsemymedia.data.model

import android.graphics.Bitmap
import com.zomato.photofilters.imageprocessors.Filter

data class FilterItem(
    var bitmap: Bitmap,
    var filter: Filter
)

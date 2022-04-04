package com.sielee.browsemymedia.utils

import android.graphics.Bitmap
import com.sielee.browsemymedia.data.model.FilterItem

class ThumbnailManager {
    private var filterThumbsNails = ArrayList<FilterItem>(10)
    private var processedThumbsNails = ArrayList<FilterItem>(10)

    fun addThumbs(filterItem: FilterItem) {
        filterThumbsNails.add(filterItem)
    }

    fun processThumbs():ArrayList<FilterItem> {
        filterThumbsNails.forEach {filterItem ->
            filterItem.bitmap = filterItem.filter.processFilter(Bitmap.createBitmap(filterItem.bitmap))
            processedThumbsNails.add(filterItem)
        }
        return processedThumbsNails
    }

    fun clearThumbs() {
        filterThumbsNails = ArrayList()
        processedThumbsNails = ArrayList()
    }
}
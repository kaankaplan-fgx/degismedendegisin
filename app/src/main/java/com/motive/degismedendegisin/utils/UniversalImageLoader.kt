package com.motive.degismedendegisin.utils

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.motive.degismedendegisin.R
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener

class UniversalImageLoader(val mContext: Context) {

    val config : ImageLoaderConfiguration
        get(){
            val defaultOptions = DisplayImageOptions.Builder()
                .showImageOnLoading(defaultImage)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .cacheOnDisk(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(FadeInBitmapDisplayer(400)).build()
            return ImageLoaderConfiguration.Builder(mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(WeakMemoryCache())
                .diskCacheSize(100*1024*1024).build()
        }

    companion object{
        private val defaultImage = R.drawable.ic_baseline_person_24

        fun setImage(imgURL:String, imageView:ImageView, mProgressBar: ProgressBar?, ilkKisim: String?){

            //imgURL:facebook.com/images/logo.jpeg
            //ilk kısım = http://
            val imageLoader=ImageLoader.getInstance()
            imageLoader.displayImage(ilkKisim+imgURL,imageView,object  : ImageLoadingListener{
                override fun onLoadingStarted(imageUri: String?, view: View?) {
                    if(mProgressBar != null){
                        mProgressBar.visibility = View.VISIBLE
                    }
                }

                override fun onLoadingFailed(
                    imageUri: String?,
                    view: View?,
                    failReason: FailReason?
                ) {
                    if(mProgressBar != null){
                        mProgressBar.visibility = View.GONE
                    }
                }

                override fun onLoadingComplete(
                    imageUri: String?,
                    view: View?,
                    loadedImage: Bitmap?
                ) {
                    if(mProgressBar != null){
                        mProgressBar.visibility = View.GONE
                    }
                }

                override fun onLoadingCancelled(imageUri: String?, view: View?) {
                    if(mProgressBar != null){
                        mProgressBar.visibility = View.GONE
                    }
                }

            })
        }
    }
}
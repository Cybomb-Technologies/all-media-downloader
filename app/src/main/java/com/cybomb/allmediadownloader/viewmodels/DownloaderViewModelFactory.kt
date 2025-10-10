package com.cybomb.allmediadownloader.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cybomb.allmediadownloader.datamodels.DownloaderViewModel
import com.cybomb.allmediadownloader.fetcher.FacebookFetcher
import com.cybomb.allmediadownloader.fetcher.InstagramFetcher
import com.cybomb.allmediadownloader.fetcher.PinterestFetcher
import com.cybomb.allmediadownloader.fetcher.RedditFetcher
import com.cybomb.allmediadownloader.fetcher.TwitterFetcher
import com.cybomb.allmediadownloader.fetcher.YouTubeFetcher

class DownloaderViewModelFactory(
    private val instagramFetcher: InstagramFetcher,
    private val youTubeFetcher: YouTubeFetcher,
    private val pinterestFetcher: PinterestFetcher,
    private val facebookFetcher: FacebookFetcher,
    private val redditFetcher: RedditFetcher,
    private val twitterFetcher: TwitterFetcher,
    private val getCookiesForUrl: (String) -> String?,
    private val startMediaDownload: (Context, DownloadMediaInfo) -> Unit,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DownloaderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DownloaderViewModel(
                instagramFetcher,
                youTubeFetcher,
                pinterestFetcher,
                facebookFetcher,
                redditFetcher,
                twitterFetcher,
                getCookiesForUrl,
                startMediaDownload
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
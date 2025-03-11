package com.ratila.universitybase1;

import android.content.Context;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

public class PicassoCache {
    private static Picasso picassoInstance;

    private PicassoCache(Context context) {
        File cacheDir = new File(context.getCacheDir(), "picasso-cache");
        Cache cache = new Cache(cacheDir, 15 * 1024 * 1024); // 15 MB кэша

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .build();

        picassoInstance = new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(okHttpClient))
                .build();
    }

    public static Picasso getInstance(Context context) {
        if (picassoInstance == null) {
            synchronized (PicassoCache.class) {
                if (picassoInstance == null) {
                    new PicassoCache(context);
                }
            }
        }
        return picassoInstance;
    }
}
package com.tizisolutions.smartcacherxjava2calladapter;

import android.content.Context;
import android.util.Log;
import android.util.LruCache;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import okhttp3.Request;

import com.google.common.hash.Hashing;
import com.jakewharton.disklrucache.DiskLruCache;
import retrofit2.Response;

public class BasicCaching implements CachingSystem {

    private DiskLruCache diskCache;
    private LruCache<String, Object> memoryCache;
    private static final long REASONABLE_DISK_SIZE = 1048576L;
    private static final int REASONABLE_MEM_ENTRIES = 50;

    public BasicCaching(File diskDirectory, long maxDiskSize, int memoryEntries) {
        try {
            this.diskCache = DiskLruCache.open(diskDirectory, 1, 1, maxDiskSize);
        } catch (IOException var6) {
            Log.e("SmartCall", "", var6);
            this.diskCache = null;
        }
        this.memoryCache = new LruCache(memoryEntries);
    }

    public static BasicCaching fromCtx(Context context) {
        return new BasicCaching(new File(context.getCacheDir(), "retrofit_smartcache_rx"), 1048576L, 50);
    }


    @Override
    public <T> void addInCache(Response<T> response, byte[] rawResponse) {

        String cacheKey = this.urlToKey(response.raw().request().url().url());
        this.memoryCache.put(cacheKey, rawResponse);

        try {
            DiskLruCache.Editor editor = this.diskCache.edit(this.urlToKey(response.raw().request().url().url()));
            editor.set(0, new String(rawResponse, Charset.defaultCharset()));
            editor.commit();
        } catch (IOException var5) {
            Log.e("SmartCall", "", var5);
        }

    }

    @Override
    public <T> byte[] getFromCache(Request request) {

        String cacheKey = this.urlToKey(request.url().url());

        byte[] memoryResponse = (byte[])((byte[])this.memoryCache.get(cacheKey) );
        if (memoryResponse != null) {
            Log.d("SmartCall", "Memory hit!");
            return memoryResponse;
        } else {
            try {
                DiskLruCache.Snapshot cacheSnapshot = this.diskCache.get(cacheKey);
                if (cacheSnapshot != null) {
                    Log.d("SmartCall", "Disk hit!");
                    return cacheSnapshot.getString(0).getBytes();
                } else {
                    return null;
                }
            } catch (IOException var5) {
                return null;
            }
        }
    }

    private String urlToKey(URL url) {
        return Hashing.sha1().hashString(url.toString(), Charset.defaultCharset()).toString();
    }
}

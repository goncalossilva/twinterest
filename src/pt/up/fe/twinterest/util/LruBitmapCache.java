package pt.up.fe.twinterest.util;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class LruBitmapCache extends LruCache<String, Bitmap> implements ImageCache {
	private static LruBitmapCache sInstance;
	
	public static synchronized LruBitmapCache getInstance(Context context) {
		if(sInstance == null)
			sInstance = new LruBitmapCache(context);
		
		return sInstance;
	}
	
	private LruBitmapCache(Context context) {
		// Use 1/8th of the available memory for this memory cache.
		super(1024 * 1024 * ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass() / 8);
	}
	
	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}
	
	@Override
	public Bitmap getBitmap(String url) {
		return get(url);
	}
	
	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		put(url, bitmap);
	}
	
}

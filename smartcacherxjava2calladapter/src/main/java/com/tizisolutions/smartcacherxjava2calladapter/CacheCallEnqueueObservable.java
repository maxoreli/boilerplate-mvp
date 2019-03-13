package com.tizisolutions.smartcacherxjava2calladapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.Executor;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

final class CacheCallEnqueueObservable<T> extends Observable<CachedResult<T>> {
	private final Call<T> originalCall;
	private final CachingSystem cachingSystem;
	private final Retrofit retrofit;
	private final Type returnType;
	private final Annotation[] annotations;
	private Executor callbackExecutor;
	
	
	CacheCallEnqueueObservable(Call<T> originalCall, CachingSystem cachingSystem,
	                           Retrofit retrofit, Type returnType, Annotation[] annotations) {
		this.originalCall = originalCall;
		this.cachingSystem = cachingSystem;
		this.retrofit = retrofit;
		this.returnType = returnType;
		this.annotations = annotations;
	}
	
	@Override
	protected void subscribeActual(Observer<? super CachedResult<T>> observer) {
		// Since Call is a one-shot type, clone it for each new observer.
		Call<T> call = originalCall.clone();
		//
		CallCallback<T> callback = new CallCallback<>(call, observer);
		observer.onSubscribe(callback);
		if (!callback.isDisposed()) {
			enqueue(call, callback);
		}
	}
	
	public void enqueue(Call<T> call, final CallCallback<T> callback) {
		if (this.buildRequest(call).method().equals("GET")) {
			this.enqueueWithCache(call, callback);
		} else {
			call.enqueue(new Callback<T>() {
				public void onResponse(final Call<T> call, final Response<T> response) {
					callback.onResponse(call, response);
				}
				
				public void onFailure(final Call<T> call, final Throwable t) {
					callback.onFailure(call, t);
				}
			});
		}
		
	}
	
	public void enqueueWithCache(final Call<T> call, final CallCallback<T> callback) {
		
		Runnable enqueueRunnable = new Runnable() {
			public void run() {
				final byte[] data = cachingSystem.getFromCache(buildRequest(call));
				if (data != null) {
					final T convertedData = SmartUtils.bytesToResponse(retrofit, returnType, annotations, data);
					callback.onFinalResponse(call, Response.success(convertedData), Boolean.TRUE, true);
				}
				
				call.enqueue(new Callback<T>() {
					public void onResponse(final Call<T> call, final Response<T> response) {
						if (response.isSuccessful()) {
							byte[] rawData = SmartUtils.responseToBytes(retrofit, response.body(), returnType, annotations);
							cachingSystem.addInCache(response, rawData);
						}
						callback.onFinalResponse(call, response, Boolean.FALSE, data != null);
					}
					
					public void onFailure(final Call<T> call, final Throwable t) {
						callback.onFailure(call, t);
					}
				});
			}
		};
		Thread enqueueThread = new Thread(enqueueRunnable);
		enqueueThread.start();
	}
	
	
	public Request buildRequest(Call<T> call) {
		return call.request().newBuilder().build();
	}
	
	
	private static final class CallCallback<T> implements Disposable, Callback<T> {
		private final Call<T> call;
		private final Observer<? super CachedResult<T>> observer;
		private volatile boolean disposed;
		boolean terminated = false;
		
		
		CallCallback(Call<T> call, Observer<? super CachedResult<T>> observer) {
			this.call = call;
			this.observer = observer;
		}
		
		public void onFinalResponse(Call<T> call, Response<T> response, Boolean cached, Boolean cacheExists) {
			if (response.isSuccessful()) {
				actOnResponse(call, response, cached, Boolean.TRUE, cacheExists);
			} else {
				terminated = true;
				Throwable t = new retrofit2.HttpException(response);
				try {
					observer.onError(t);
				} catch (Throwable inner) {
					Exceptions.throwIfFatal(inner);
					RxJavaPlugins.onError(new CompositeException(t, inner));
				}
			}
		}
		
		@Override
		public void onResponse(Call<T> call, Response<T> response) {
			actOnResponse(call, response, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE);
		}
		
		void actOnResponse(Call<T> call, Response<T> response, Boolean cached, Boolean withCaching, Boolean cacheExists) {
			if (disposed) return;
			
			try {
				observer.onNext(CachedResult.response(response, cached, cacheExists));
				
				if (!disposed) {
					if (!withCaching) {
						terminated = true;
						observer.onComplete();
					} else if (!cached) {
						terminated = true;
						observer.onComplete();
					}
				}
			} catch (Throwable t) {
				if (terminated) {
					RxJavaPlugins.onError(t);
				} else if (!disposed) {
					try {
						observer.onError(t);
					} catch (Throwable inner) {
						Exceptions.throwIfFatal(inner);
						RxJavaPlugins.onError(new CompositeException(t, inner));
					}
				}
			}
		}
		
		@Override
		public void onFailure(Call<T> call, Throwable t) {
			if (call.isCanceled())
				return;
			
			try {
				observer.onError(t);
			} catch (Throwable inner) {
				Exceptions.throwIfFatal(inner);
				RxJavaPlugins.onError(new CompositeException(t, inner));
			}
		}
		
		@Override
		public void dispose() {
			disposed = true;
			call.cancel();
		}
		
		@Override
		public boolean isDisposed() {
			return disposed;
		}
	}
}
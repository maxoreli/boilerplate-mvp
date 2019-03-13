package com.tizisolutions.smartcacherxjava2calladapter;

import java.io.IOException;
import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.annotations.Nullable;
import retrofit2.Response;

/**
 * The result of executing an HTTP request.
 */
public final class CachedResult<T> implements ObservableSource<CachedResult<T>> {
	@SuppressWarnings("ConstantConditions") // Guarding public API nullability.
	public static <T> CachedResult<T> error(Throwable error) {
		if (error == null) throw new NullPointerException("error == null");
		return new CachedResult<>(null, error);
	}
	
	@SuppressWarnings("ConstantConditions") // Guarding public API nullability.
	public static <T> CachedResult<T> response(Response<T> response) {
		if (response == null) throw new NullPointerException("response == null");
		return new CachedResult<>(response, null);
	}
	
	public static <T> CachedResult<T> response(Response<T> response, boolean cached, boolean cacheExists) {
		if (response == null) throw new NullPointerException("response == null");
		return new CachedResult<>(response, null, cached, cacheExists);
	}
	
	private final @Nullable
	Response<T> response;
	private final @Nullable
	Throwable error;
	
	private @Nullable
	Boolean cachedResponse;
	
	private Boolean cacheExists;
	
	
	private CachedResult(@Nullable Response<T> response, @Nullable Throwable error) {
		this.response = response;
		this.error = error;
	}
	
	private CachedResult(@Nullable Response<T> response, @Nullable Throwable error, Boolean cachedResponse,
	                     Boolean cacheExists) {
		this.response = response;
		this.error = error;
		this.cachedResponse = cachedResponse;
		this.cacheExists = cacheExists;
	}
	
	/**
	 * The flag to tells if is cached response or not
	 */
	public Boolean isCached() {
		return cachedResponse;
	}
	
	
	/**
	 * The flag to tells if is cached response or not
	 */
	public Boolean isCacheExists() {
		return cacheExists;
	}
	
	
	/**
	 * The response received from executing an HTTP request. Only present when {@link #isError()} is
	 * false, null otherwise.
	 */
	public @Nullable
	Response<T> response() {
		return response;
	}
	
	/**
	 * The error experienced while attempting to execute an HTTP request. Only present when {@link
	 * #isError()} is true, null otherwise.
	 * <p>
	 * If the error is an {@link IOException} then there was a problem with the transport to the
	 * remote server. Any other exception type indicates an unexpected failure and should be
	 * considered fatal (configuration error, programming error, etc.).
	 */
	public @Nullable
	Throwable error() {
		return error;
	}
	
	/**
	 * {@code true} if the request resulted in an error. See {@link #error()} for the cause.
	 */
	public boolean isError() {
		return error != null;
	}
	
	
	@Override
	public void subscribe(Observer<? super CachedResult<T>> observer) {
		observer.onNext(this);
		observer.onComplete();
	}
}
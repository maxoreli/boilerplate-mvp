package com.tizisolutions.smartcacherxjava2calladapter;


import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.annotations.Nullable;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;


public final class CacheRxJava2CallAdapterFactory extends CallAdapter.Factory {

    /**
     * Returns an instance which creates synchronous observables that do not operate on any scheduler
     * by default.
     */
    public static CacheRxJava2CallAdapterFactory create(CachingSystem cachingSystem) {
        return new CacheRxJava2CallAdapterFactory(null, false, cachingSystem);
    }

    /**
     * Returns an instance which creates asynchronous observables. Applying
     * {@link Observable#subscribeOn} has no effect on stream types created by this factory.
     */
    public static CacheRxJava2CallAdapterFactory createAsync(CachingSystem cachingSystem) {
        return new CacheRxJava2CallAdapterFactory(null, true, cachingSystem);
    }

    /**
     * Returns an instance which creates synchronous observables that
     * {@linkplain Observable#subscribeOn(Scheduler) subscribe on} {@code scheduler} by default.
     */
    @SuppressWarnings("ConstantConditions") // Guarding public API nullability.
    public static CacheRxJava2CallAdapterFactory createWithScheduler(Scheduler scheduler, CachingSystem cachingSystem) {
        if (scheduler == null) throw new NullPointerException("scheduler == null");
        return new CacheRxJava2CallAdapterFactory(scheduler, false, cachingSystem);
    }

    private final @Nullable
    Scheduler scheduler;
    private final boolean isAsync;
    private final CachingSystem cachingSystem;

    private CacheRxJava2CallAdapterFactory(@Nullable Scheduler scheduler, boolean isAsync, CachingSystem cachingSystem) {
        this.scheduler = scheduler;
        this.isAsync = isAsync;
        this.cachingSystem = cachingSystem;
    }

    @Override
    public @Nullable
    CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        Class<?> rawType = getRawType(returnType);

        if (rawType == Completable.class) {
            // Completable is not parameterized (which is what the rest of this method deals with) so it
            // can only be created with a single configuration.
            return new CacheRxJava2CallAdapter(Void.class,
                    scheduler, cachingSystem, retrofit, annotations,
                    isAsync, false,false, true,
                    false, false, false, true);
        }

        boolean isFlowable = rawType == Flowable.class;
        boolean isSingle = rawType == Single.class;
        boolean isMaybe = rawType == Maybe.class;

        if (rawType != Observable.class && !isFlowable && !isSingle && !isMaybe) {
            return null;
        }

        boolean isResult = false;
        boolean isCacheResult = false;
        boolean isBody = false;
        Type responseType;
        if (!(returnType instanceof ParameterizedType)) {
            String name = isFlowable ? "Flowable"
                    : isSingle ? "Single"
                    : isMaybe ? "Maybe" : "Observable";
            throw new IllegalStateException(name + " return type must be parameterized"
                    + " as " + name + "<Foo> or " + name + "<? extends Foo>");
        }

        Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType);
        Class<?> rawObservableType = getRawType(observableType);
        if (rawObservableType == Response.class) {
            if (!(observableType instanceof ParameterizedType)) {
                throw new IllegalStateException("Response must be parameterized"
                        + " as Response<Foo> or Response<? extends Foo>");
            }
            responseType = getParameterUpperBound(0, (ParameterizedType) observableType);
        } else if (rawObservableType == Result.class) {
            if (!(observableType instanceof ParameterizedType)) {
                throw new IllegalStateException("Result must be parameterized"
                        + " as Result<Foo> or Result<? extends Foo>");
            }
            responseType = getParameterUpperBound(0, (ParameterizedType) observableType);
            isResult = true;
        }
        else if (rawObservableType == CachedResult.class) {
            if (!(observableType instanceof ParameterizedType)) {
                throw new IllegalStateException("CachedResult must be parameterized"
                        + " as CachedResult<Foo> or CachedResult<? extends Foo>");
            }
            responseType = getParameterUpperBound(0, (ParameterizedType) observableType);
            isCacheResult = true;
        }

        else {
            responseType = observableType;
            isBody = true;
        }

        return new CacheRxJava2CallAdapter(responseType,
                scheduler, cachingSystem, retrofit, annotations,
                isAsync, isResult,isCacheResult, isBody, isFlowable, isSingle, isMaybe, false);
    }
}

package com.tizisolutions.smartcacherxjava2calladapter;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.annotations.Nullable;
import io.reactivex.plugins.RxJavaPlugins;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;

final class CacheRxJava2CallAdapter<R> implements CallAdapter<R, Object> {
    private final Type responseType;
    private final @Nullable Scheduler scheduler;
    private final boolean isAsync;
    private final boolean isResult;
    private final boolean isBody;
    private final boolean isFlowable;
    private final boolean isSingle;
    private final boolean isMaybe;
    private final boolean isCompletable;
    private final CachingSystem cachingSystem;
    private final Retrofit retrofit;
    private final Annotation[] annotations;
    private final boolean isCacheResult;

    CacheRxJava2CallAdapter(Type responseType, @Nullable Scheduler scheduler, CachingSystem cachingSystem,
                            Retrofit retrofit, Annotation[] annotations, boolean isAsync,
                            boolean isResult,
                            boolean isCachedResult,
                            boolean isBody,
                            boolean isFlowable, boolean isSingle, boolean isMaybe, boolean isCompletable) {
        this.responseType = responseType;
        this.scheduler = scheduler;
        this.cachingSystem = cachingSystem;
        this.retrofit = retrofit;
        this.annotations = annotations;

        this.isAsync = isAsync;
        this.isResult = isResult;
        this.isCacheResult = isCachedResult;
        this.isBody = isBody;
        this.isFlowable = isFlowable;
        this.isSingle = isSingle;
        this.isMaybe = isMaybe;
        this.isCompletable = isCompletable;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public Object adapt(Call<R> call) {

        Observable<Response<R>> responseObservable = isAsync
                ? new CallEnqueueObservable<>(call)
                : new CallExecuteObservable<>(call);

        Observable<?> observable;
        if (isResult) {
            observable = new ResultObservable<>(responseObservable);
        }
        else if( isCacheResult) {
            observable = new CacheCallEnqueueObservable<>(call,cachingSystem,retrofit,responseType,annotations);
        }
        else if (isBody) {
            observable = new BodyObservable<>(responseObservable);
        } else {
            observable = responseObservable;
        }

        if (scheduler != null) {
            observable = observable.subscribeOn(scheduler);
        }

        if (isFlowable) {
            return observable.toFlowable(BackpressureStrategy.LATEST);
        }
        if (isSingle) {
            return observable.singleOrError();
        }
        if (isMaybe) {
            return observable.singleElement();
        }
        if (isCompletable) {
            return observable.ignoreElements();
        }
        return RxJavaPlugins.onAssembly(observable);
    }
}

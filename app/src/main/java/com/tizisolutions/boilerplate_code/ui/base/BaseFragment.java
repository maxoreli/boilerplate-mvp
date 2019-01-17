/*
 * Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://mindorks.com/license/apache-v2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.tizisolutions.boilerplate_code.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;


import com.kennyc.view.MultiStateView;
import com.tizisolutions.boilerplate_code.R;
import com.tizisolutions.boilerplate_code.data.network.commons.NoInternetException;
import com.tizisolutions.boilerplate_code.data.network.model.ApiError;
import com.tizisolutions.boilerplate_code.di.component.ActivityComponent;

import butterknife.Unbinder;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;

/**
 * Created by janisharali on 27/01/17.
 */

public abstract class BaseFragment extends Fragment implements MvpView {

    private BaseActivity mActivity;
    private Unbinder mUnBinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) context;
            this.mActivity = activity;
            activity.onFragmentAttached();
        }
    }

    @Override
    public void showLoading() {
        if (mActivity != null) {
            mActivity.showLoading();
        }
    }

    @Override
    public void hideLoading() {
        if (mActivity != null) {
            mActivity.hideLoading();
        }
    }

    @Override
    public void onError(String message) {
        if (mActivity != null) {
            mActivity.onError(message);
        }
    }

    @Override
    public void onError(@StringRes int resId) {
        if (mActivity != null) {
            mActivity.onError(resId);
        }
    }

    @Override
    public boolean isNetworkConnected() {
        if (mActivity != null) {
            return mActivity.isNetworkConnected();
        }
        return false;
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    @Override
    public void hideKeyboard() {
        if (mActivity != null) {
            mActivity.hideKeyboard();
        }
    }

    @Override
    public void openActivityOnTokenExpire() {
        if (mActivity != null) {
            mActivity.openActivityOnTokenExpire();
        }
    }

    public ActivityComponent getActivityComponent() {
        return mActivity.getActivityComponent();
    }

    public BaseActivity getBaseActivity() {
        return mActivity;
    }

    public void setUnBinder(Unbinder unBinder) {
        mUnBinder = unBinder;
    }

    protected abstract void setUp(View view);

    @Override
    public void showLoading(String text) {
        if (mActivity != null) {
            mActivity.showLoading(text);
        }
    }

    @Override
    public void showLoading(@StringRes int res) {
        if (mActivity != null) {
            mActivity.showLoading(res);
        }
    }

    public boolean onNoConnectivityError(ApiError error, MultiStateView multiStateView) {

        if (error.getThrowable() instanceof NoInternetException) {
            // No internet connection
            Log.e("Error in view", error.getMessage());
            multiStateView.setViewState(MultiStateView.ViewState.ERROR_NETWORK);
            return true;
        }
        return false;
    }

    public boolean onNoConnectivityError(ApiError error) {
        if (error.getThrowable() instanceof NoInternetException) {
            // No internet connection
            Log.e("Error in view", error.getMessage());
            if (isResumed() && isVisible()) {
                showToast(getString(R.string.toast_error_title_network), Gravity.CENTER);
            }
            return true;
        }
        return false;
    }


    public boolean onNoConnectivityError(ApiError error, View snackView, @IdRes int anchorID) {

        if (error.getThrowable() instanceof NoInternetException) {
            // No internet connection
            Log.e("Error in view", error.getMessage());
            if (isResumed() && isVisible()) {

                Snackbar snack = Snackbar.make(snackView, R.string.toast_error_title_network,
                        Snackbar.LENGTH_LONG);
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) snack.getView().getLayoutParams();

                params.setAnchorId(anchorID);//Id for your bottomNavBar or TabLayout
                params.anchorGravity = Gravity.TOP;
                params.gravity = Gravity.TOP;
                snack.getView().setLayoutParams(params);
                snack.show();
            }
            return true;
        }
        return false;
    }


    @Override
    public void onDestroy() {
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
        super.onDestroy();
    }

    public interface Callback {

        void onFragmentAttached();
        void onFragmentDetached(String tag);
    }

    protected LinearLayoutManager createNewLinearLayoutManager() {
        SmoothScrollLinearLayoutManager smoothScrollLinearLayoutManager = new SmoothScrollLinearLayoutManager(getActivity());
        smoothScrollLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        return smoothScrollLinearLayoutManager;
    }

    protected LinearLayoutManager createNewLinearLayoutManager(int orientation) {
        SmoothScrollLinearLayoutManager smoothScrollLinearLayoutManager = new SmoothScrollLinearLayoutManager(getActivity());
        smoothScrollLinearLayoutManager.setOrientation(orientation);
        return smoothScrollLinearLayoutManager;
    }


    protected LinearLayoutManager createNewGridLayoutManager() {
        SmoothScrollLinearLayoutManager smoothScrollLinearLayoutManager = new SmoothScrollLinearLayoutManager(getActivity());
        smoothScrollLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        return smoothScrollLinearLayoutManager;
    }
}

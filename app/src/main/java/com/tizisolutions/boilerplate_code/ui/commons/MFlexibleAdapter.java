package com.tizisolutions.boilerplate_code.ui.commons;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;

/**
 * Created by billionaire on 20/08/2017.
 */

public class MFlexibleAdapter extends FlexibleAdapter<AbstractFlexibleItem> {


    IMRecyclerClickListener clickListener;

    public MFlexibleAdapter(@Nullable List<AbstractFlexibleItem> items) {
        super(items);
    }

    public MFlexibleAdapter(@Nullable List<AbstractFlexibleItem> items, @Nullable Object listeners) {
        // stableIds ? true = Items implement hashCode() so they can have stableIds!
        super(items, listeners, true);

        // In case you need a Handler, do this:
        // - Overrides the internal Handler with a custom callback that extends the internal one
        mHandler = new Handler(Looper.getMainLooper(), new MyHandlerCallback());

    }

    public MFlexibleAdapter() {
         super(null);
    }


    public IMRecyclerClickListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(IMRecyclerClickListener clickListener) {
        this.clickListener = clickListener;
    }



    @Override
    public void updateDataSet(List<AbstractFlexibleItem> items, boolean animate) {
        // NOTE: To have views/items not changed, set them into "items" before passing the final
        // list to the Adapter.

        // Overwrite the list and fully notify the change, pass false to not animate changes.
        // Watch out! The original list must a copy.
        super.updateDataSet(items, animate);

        // onPostUpdate() will automatically be called at the end of the Asynchronous update
        // process. Manipulate the list inside that method only or you won't see the changes.
    }


    /**
     * Showcase to reuse the internal Handler.
     *
     * <b>IMPORTANT:</b> In order to preserve the internal calls, this custom Callback
     * <u>must</u> extends {@link HandlerCallback}
     * which implements {@link Handler.Callback},
     * therefore you <u>must</u> call {@code super().handleMessage(message)}.
     * <p>This handler can launch asynchronous tasks.</p>
     * If you catch the reserved "what", keep in mind that this code should be executed
     * <u>before</u> that task has been completed.
     * <p><b>Note:</b> numbers 0-9 are reserved for the Adapter, use others for new values.</p>
     */
    private class MyHandlerCallback extends HandlerCallback {
        @Override
        public boolean handleMessage(Message message) {
            boolean done = super.handleMessage(message);
            switch (message.what) {
                // Currently reserved (you don't need to check these numbers!)
                case 1: //async updateDataSet
                case 2: //async filterItems
                case 3: //confirm delete
                case 8: //onLoadMore remove progress item
                    return done;

                // Free to use, example:
                case 10:
                case 11:
                    return true;
            }
            return false;
        }
    }
}

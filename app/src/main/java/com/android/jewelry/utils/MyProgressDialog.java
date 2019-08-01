package com.android.jewelry.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class MyProgressDialog extends ProgressDialog {
    private boolean isDismissed;

    public MyProgressDialog(Context context) {
        super(context);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        dismiss();
    }

    @Override
    public void dismiss() {
        if (isDismissed) {
            return;
        }
        try {
            super.dismiss();
        } catch (IllegalArgumentException e) {
            // ignore
        }
        isDismissed = true;
    }
}

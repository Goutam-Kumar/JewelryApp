package com.android.jewelry.activity.gallery;

import android.support.v7.app.AlertDialog;

import com.android.jewelry.dbmodel.DesignModel;

public interface IGalleryView {
    public void openCamera(DesignModel product,AlertDialog chooserDialog);
    public void openBrowser(DesignModel product, AlertDialog chooserDialog);
}

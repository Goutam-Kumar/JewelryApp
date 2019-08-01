package com.android.jewelry.apphelper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by goutam on 9/5/16.
 */
public class SharedValue {

    private static final String KEY_PRICE_CATEGORY = "price_category";
    private static final String KEY_SCROLL_POSITION = "scroll_position";
    private static final String PREFERENCE_NAME = "sp_category";
    private SharedPreferences spref;
    private SharedPreferences.Editor edit;
    private Context context;

    public SharedValue(Context context) {
        this.context = context;
        spref = context.getSharedPreferences(PREFERENCE_NAME,0);
    }

    public void setCategoryPref(int category){
        edit = spref.edit();
        edit.putInt(KEY_PRICE_CATEGORY,category);
        edit.commit();
    }

    public int getCategoryPref(){
        return spref.getInt(KEY_PRICE_CATEGORY,0);
    }

    public void setScrollPosition(int position){
        edit = spref.edit();
        edit.putInt(KEY_SCROLL_POSITION,position);
        edit.commit();
    }

    public int getScrollPosition(){
        return spref.getInt(KEY_SCROLL_POSITION,0);
    }
}

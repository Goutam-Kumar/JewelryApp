package com.android.jewelry.dbmodel;

/**
 * Created by Bubun Goutam on 5/12/2016.
 */
public class CurrentModel {
    private String designName;
    private int subName;

    public CurrentModel() {
    }

    public CurrentModel(String designName, int subName) {
        this.designName = designName;
        this.subName = subName;
    }


    public String getDesignName() {
        return designName;
    }

    public void setDesignName(String designName) {
        this.designName = designName;
    }

    public int getSubName() {
        return subName;
    }

    public void setSubName(int subName) {
        this.subName = subName;
    }
}

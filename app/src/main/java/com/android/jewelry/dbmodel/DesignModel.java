package com.android.jewelry.dbmodel;

/**
 * Created by goutam on 3/5/16.
 */
public class DesignModel {

    private String designName;
    private String designId;
    private int designCost;
    private String designUrl;
    private String designUri;
    private int ispresent;
    private int isSelected;
    private int isFirst;

    public DesignModel() {
    }

    public DesignModel(String designName, String designId, int designCost,
                       String designUrl, String designUri, int ispresent, int isSelected, int isFirst) {
        this.designName = designName;
        this.designId = designId;
        this.designCost = designCost;
        this.designUrl = designUrl;
        this.designUri = designUri;
        this.ispresent = ispresent;
        this.isSelected = isSelected;
        this.isFirst = isFirst;
    }

    public String getDesignName() {
        return designName;
    }

    public void setDesignName(String designName) {
        this.designName = designName;
    }

    public String getDesignId() {
        return designId;
    }

    public void setDesignId(String designId) {
        this.designId = designId;
    }

    public int getDesignCost() {
        return designCost;
    }

    public void setDesignCost(int designCost) {
        this.designCost = designCost;
    }

    public String getDesignUrl() {
        return designUrl;
    }

    public void setDesignUrl(String designUrl) {
        this.designUrl = designUrl;
    }

    public String getDesignUri() {
        return designUri;
    }

    public void setDesignUri(String designUri) {
        this.designUri = designUri;
    }

    public int getIspresent() {
        return ispresent;
    }

    public void setIspresent(int ispresent) {
        this.ispresent = ispresent;
    }

    public int getSelected() {
        return isSelected;
    }

    public void setSelected(int selected) {
        isSelected = selected;
    }

    public int getIsFirst() {
        return isFirst;
    }

    public void setIsFirst(int isFirst) {
        this.isFirst = isFirst;
    }
}

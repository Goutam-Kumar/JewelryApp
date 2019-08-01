package com.android.jewelry.dbmodel;

/**
 * Created by Bubun Goutam on 5/12/2016.
 */
public class PartyModel {
    private String partyName;

    public PartyModel() {
    }

    public PartyModel(String partyName) {
        this.partyName = partyName;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }
}

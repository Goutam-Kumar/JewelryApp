package com.android.jewelry.dbmodel;

/**
 * Created by Bubun Goutam on 5/12/2016.
 */
public class WorkerModel {
    private int id;
    private String name;
    private String address;
    private String phone;

    public WorkerModel() {
    }

    public WorkerModel(String phone, int id, String name, String address) {
        this.phone = phone;
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

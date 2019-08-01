package com.android.jewelry.apipresenter;

import com.android.jewelry.responsemodel.JewelryResponse;
import com.android.jewelry.responsemodel.PartyResponse;
import com.android.jewelry.responsemodel.RodiumResponse;
import com.android.jewelry.responsemodel.WorkerResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RestApi {
    @GET("viewtotaldesignlist/")
    Call<List<JewelryResponse>> getAllJewelry();
    @GET("getpartylist/")
    Call<List<PartyResponse>> getPartyList();
    @GET("getworkerlist/")
    Call<List<WorkerResponse>> getWorkerList();
    @GET("getrodiumworkerall/")
    Call<List<RodiumResponse>> getRodiumList();

}

package com.android.jewelry.volleyhandler;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bubun Goutam on 2/12/2016.
 * Developer: Goutam Kumar
 * Designation: Senior Android Developer
 * Company: Xentric Technologies Pvt. Ltd.
 * Contact: 8296569759 / 9062462941
 */
public class VolleyRequestHandler {
    public ProgressDialog pDialog;
    /**
     * Method for collect the String Request using required POST parameters and request URL
     *
     * @param requestUrl the request server url from which response must be collected
     * @param postParams POST parameters in Hashmap with its Key and Value pairs
     * @param volleyCallback Callback the success result
     */
    public StringRequest GeneralVolleyRequestWithPostParam(Context context, String requestUrl, final HashMap<String,String> postParams, final VolleyCallback volleyCallback){
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.setIndeterminate(true);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.show();

        StringRequest str = new StringRequest(Request.Method.POST,
                requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        volleyCallback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        Log.e("Volley Error>>", error.toString());
                        JSONObject errorObject = new JSONObject();
                        try {
                            errorObject.put("volley_error","1");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        volleyCallback.onFailure(errorObject.toString());
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = postParams;
                return params;
            }
        };
        return str;
    }

    /**
     * Method for collect the String Request using required POST parameters and request URL
     *
     * @param i
     * @param requestUrl the request server url from which response must be collected
     * @param volleyCallback Callback the success result
     */
    public StringRequest GeneralVolleyRequest(Context context, final int i, String requestUrl, final VolleyCallback volleyCallback){

        if (i == 1){
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(true);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();
        }

        StringRequest str = new StringRequest(Request.Method.POST,
                requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (i==1)
                        pDialog.dismiss();
                        volleyCallback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (i==1)
                            pDialog.dismiss();
                        Log.e("",error.toString());
                        JSONObject errorObject = new JSONObject();
                        try {
                            errorObject.put("volley_error","1");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        volleyCallback.onFailure(errorObject.toString());
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        return str;
    }

    public StringRequest GeneralVolleyRequestwithoutDialog(Context context, String requestUrl, final VolleyCallback volleyCallback){

        StringRequest str = new StringRequest(Request.Method.POST,
                requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        volleyCallback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e("",error.toString());
                        JSONObject errorObject = new JSONObject();
                        try {
                            errorObject.put("volley_error","1");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        volleyCallback.onFailure(errorObject.toString());
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        return str;
    }


    /**
     * Method for collect the String Request using required POST parameters and request URL
     *
     * @param requestUrl the request server url from which response must be collected
     * @param volleyCallback Callback the success result
     */
    public StringRequest GeneralVolleyRequestusinGet(final Context context, final int progress, String requestUrl, final VolleyCallback volleyCallback){

        if (progress == 1){
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(true);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();
        }

        StringRequest str = new StringRequest(Request.Method.GET,
                requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (progress == 1){
                            pDialog.dismiss();
                        }

                        volleyCallback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (progress == 1){
                            pDialog.dismiss();
                        }
                        Log.e("", error.toString());
                        Toast.makeText(context,error.toString(), Toast.LENGTH_LONG).show();
                        JSONObject errorObject = new JSONObject();
                        try {
                            errorObject.put("volley_error","1");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        volleyCallback.onFailure(errorObject.toString());
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                return params;
            }
        };
        return str;
    }

    public interface VolleyCallback{
        void onSuccess(String result);
        void onFailure(String fail);
    }

}

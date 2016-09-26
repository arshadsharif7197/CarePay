package com.carecloud.carepaylibray.googleapis;

import com.carecloud.carepaylibray.googleapis.Models.GoogleAddressModel;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Jahirul Bhuiyan on 9/15/2016.
 */
public interface GoogleAddressesService {
    @GET(value = "maps/api/geocode/json?key=AIzaSyDFrx3OGugrIUQzsoEJVobtQoz6f9MMimE&components=postal_code:33126")
    Call<GoogleAddressModel> getAddressByZipcode();
}

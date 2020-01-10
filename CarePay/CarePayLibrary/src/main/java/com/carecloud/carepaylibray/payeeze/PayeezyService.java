package com.carecloud.carepaylibray.payeeze;

import com.carecloud.carepaylibray.payeeze.model.TokenizeBody;
import com.carecloud.carepaylibray.payeeze.model.TokenizeResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * @author pjohnson on 2019-06-21.
 */
public interface PayeezyService {

    @POST()
    Call<TokenizeResponse> tokenizeCard(@Url String url, @Body TokenizeBody tokenizeBody, @HeaderMap Map<String, String> headers);
}

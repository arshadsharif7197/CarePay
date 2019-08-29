package com.carecloud.carepaylibray.payeeze;

import com.carecloud.carepaylibray.payeeze.model.TokenizeBody;
import com.carecloud.carepaylibray.payeeze.model.TokenizeResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

/**
 * @author pjohnson on 2019-06-21.
 */
public interface PayeezyService {

    @POST("transactions/tokens")
    Call<TokenizeResponse> tokenizeCard(@Body TokenizeBody tokenizeBody, @HeaderMap Map<String, String> headers);
}

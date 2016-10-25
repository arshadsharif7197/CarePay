package com.carecloud.carepay.practice.library.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.google.gson.Gson;

/**
 * Created by Jahirul Bhuiyan on 10/24/2016.
 * Common Activity class for any practice.
 * Use for holding the common DTO which will be converted to the desire DTO using getConvertedDTO
 */

public abstract class BasePracticeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Common WorkflowDTO which will converted to the desire DTO with dtoClass params
     * @param dtoClass class to convert
     * @param <S> Dynamic class to convert
     * @return Dynamic converted class object
     */
    public <S> S getConvertedDTO(Class<S> dtoClass){
        Bundle bundle = this.getIntent().getExtras();

        if (bundle != null) {
            Gson gson=new Gson();
            return gson.fromJson( bundle.getString(getApplicationContext().getClass().getSimpleName()),dtoClass);
        }
        return null;
    }
}

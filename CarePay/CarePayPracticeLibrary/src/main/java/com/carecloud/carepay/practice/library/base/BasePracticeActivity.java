package com.carecloud.carepay.practice.library.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.google.gson.Gson;

/**
 * Created by Jahirul Bhuiyan on 10/24/2016.
 */

public abstract class BasePracticeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public <S> S getConvertedDTO(Class<S> dtoClass){
        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            Gson gson=new Gson();
            return gson.fromJson( b.getString(getApplicationContext().getClass().getSimpleName()),dtoClass);
        }
        return null;
    }
}

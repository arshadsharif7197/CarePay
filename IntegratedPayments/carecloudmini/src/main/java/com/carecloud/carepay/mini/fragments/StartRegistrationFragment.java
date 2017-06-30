package com.carecloud.carepay.mini.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.mini.HttpConstants;
import com.carecloud.carepay.mini.R;
import com.carecloud.carepay.mini.services.ServiceCallback;
import com.carecloud.carepay.mini.services.ServiceRequestDTO;
import com.carecloud.carepay.mini.services.ServiceResponseDTO;

/**
 * Created by lmenendez on 6/23/17
 */

public class StartRegistrationFragment extends RegistrationFragment {
    private View startButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_registration_start, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        startButton = view.findViewById(R.id.button_start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegistration();
            }
        });
    }

    private void startRegistration(){
        ServiceRequestDTO serviceRequestDTO = new ServiceRequestDTO();
        serviceRequestDTO.setMethod(ServiceRequestDTO.METHOD_GET);
        serviceRequestDTO.setUrl(HttpConstants.getApiStartUrl());

        getApplicationHelper().getServiceHelper().execute(serviceRequestDTO, startRegistrationCallback);
    }

    private ServiceCallback startRegistrationCallback = new ServiceCallback() {
        @Override
        public void onPreExecute() {
            startButton.setEnabled(false);
        }

        @Override
        public void onPostExecute(ServiceResponseDTO serviceResponseDTO) {
            startButton.setEnabled(true);
            Log.d(StartRegistrationFragment.class.getName(),  serviceResponseDTO.toString());
            callback.setRegistrationDataModel(serviceResponseDTO);
            callback.replaceFragment(new LoginFragment(), true);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            startButton.setEnabled(true);
            Log.d(StartRegistrationFragment.class.getName(),  exceptionMessage);

        }
    };

}

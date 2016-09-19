package com.carecloud.carepaylibray.consentforms.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.consentforms.interfaces.ConsentActivity;
import com.carecloud.carepaylibray.consentforms.interfaces.FormData;
import com.carecloud.carepaylibray.consentforms.interfaces.IFragmentCallback;
import com.carecloud.carepaylibray.constants.CarePayConstants;


public class ConsentForm1Fragment extends Fragment {

    private TextView titleTv, descriptionTv, contentTv, dateTv;
    private Button signButton;
    private IFragmentCallback fragmentCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.consent_form_layout, container, false);




        titleTv = (TextView) view.findViewById(R.id.titleTv);
        descriptionTv = (TextView) view.findViewById(R.id.descriptionTv);
        contentTv = (TextView) view.findViewById(R.id.contentTv);
        dateTv = (TextView) view.findViewById(R.id.dateTv);
        signButton = (Button) view.findViewById(R.id.signButton);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity a = null;

        if (context instanceof ConsentActivity) {
            a = (Activity) context;

            try {
                fragmentCallback = (IFragmentCallback) a;
            } catch (Exception e) {
            }
        }


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (signButton != null) {
            signButton.setOnClickListener(clickListener);
        }

        FormData formData = (FormData)getArguments().getSerializable(CarePayConstants.FORM_DATA);

        titleTv.setText(formData.getTitle());
        descriptionTv.setText(formData.getDescription());
        contentTv.setText(formData.getContent());
        dateTv.setText(formData.getDate());
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.signButton) {
                if (fragmentCallback != null)
                    fragmentCallback.signButtonClicked();
            }
        }
    };
}

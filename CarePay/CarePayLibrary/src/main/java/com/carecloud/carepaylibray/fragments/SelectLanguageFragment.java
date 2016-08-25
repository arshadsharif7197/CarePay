package com.carecloud.carepaylibray.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.ApplicationWorkflow;
import com.carecloud.carepaylibray.models.ScreenComponentModel;

public class SelectLanguageFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        getActivity().setTitle("Sign In");
        LinearLayout ll=new LinearLayout(getActivity());

        ll.setOrientation(LinearLayout.HORIZONTAL);
        int index=0;
        LinearLayout parent= (LinearLayout) inflater.inflate(R.layout.fragment_select_language, container, false);
        for (ScreenComponentModel componentModel:ApplicationWorkflow.Instance().getLoginScreenModel().getComponentModels()){
            if(index==1){
                ll.addView(componentModel.getGeneratedView(getContext()));
            }else if(index==2){
                ll.addView(componentModel.getGeneratedView(getContext()));
                parent.addView(ll);
            }else {
                parent.addView(componentModel.getGeneratedView(getContext()));
            }
        }

        return parent;
    }



}

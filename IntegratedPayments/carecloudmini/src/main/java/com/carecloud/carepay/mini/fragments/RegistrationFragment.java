package com.carecloud.carepay.mini.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepay.mini.R;
import com.carecloud.carepay.mini.interfaces.ApplicationHelper;
import com.carecloud.carepay.mini.interfaces.RegistrationNavigationCallback;
import com.carecloud.carepay.mini.services.ServiceHelper;
import com.carecloud.carepay.mini.services.carepay.RestCallServiceHelper;

/**
 * Created by lmenendez on 6/23/17
 */

public abstract class RegistrationFragment extends Fragment {

    protected RegistrationNavigationCallback callback;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            callback = (RegistrationNavigationCallback) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached context must implement RegistrationNavigationCallback");
        }
    }

    protected void initProgressToolbar(View view, String titleString, int progress){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.registration_toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow_blue);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onBackPressed();
            }
        });

        TextView title = (TextView) view.findViewById(R.id.toolbar_title);
        title.setText(titleString);

        ProgressBar registrationProgress = (ProgressBar) view.findViewById(R.id.progress_registration);
        registrationProgress.setMax(6);
        registrationProgress.setProgress(progress);

    }


    protected ApplicationHelper getApplicationHelper(){
        return (ApplicationHelper) getActivity().getApplication();
    }

    protected ServiceHelper getServiceHelper(){
        return getApplicationHelper().getServiceHelper();
    }

    protected RestCallServiceHelper getRestHelper(){
        return getApplicationHelper().getRestHelper();
    }
}

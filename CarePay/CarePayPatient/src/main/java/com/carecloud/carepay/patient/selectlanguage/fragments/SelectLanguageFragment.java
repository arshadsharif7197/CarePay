package com.carecloud.carepay.patient.selectlanguage.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.patientsplash.dtos.LanguageListDTO;
import com.carecloud.carepay.patient.patientsplash.dtos.PayloadDTO;
import com.carecloud.carepay.patient.patientsplash.dtos.SelectLanguageDTO;
import com.carecloud.carepay.patient.selectlanguage.SelectLanguageActivity;
import com.carecloud.carepay.patient.selectlanguage.adapters.LanguageListAdapter;
import com.carecloud.carepay.patient.selectlanguage.models.LanguageOptionModel;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.utils.ApplicationPreferences;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by lsoco_user on 9/2/2016.
 * Select Language
 */
public class SelectLanguageFragment extends Fragment implements LanguageListAdapter.OnItemClickListener {

    private static final String LOG_TAG = SelectLanguageFragment.class.getSimpleName();
    RecyclerView languageListView;
    String languageName = null;
    String languageId=null;
    List<LanguageOptionModel> languageOptionModelList;
    ImageButton languageConfirmButton;
    WorkflowServiceCallback signinscreencallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PatientNavigationHelper.getInstance(getActivity()).navigateToWorkflow(workflowDTO);

        }

        @Override
        public void onFailure(String exceptionMessage) {
            //   SystemUtil.showDialogMessage(SplashActivity.this, getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };
    private SelectLanguageDTO languageSelectionDTO;
    private PayloadDTO payloadDTO;
    private LanguageListDTO languageListDTO;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        languageSelectionDTO = ((SelectLanguageActivity) getActivity()).getLanguageDTO();
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_select_language, container, false);
        languageListView = (RecyclerView) view.findViewById(R.id.languageRecyclerView);
        languageListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        languageConfirmButton = (ImageButton) view.findViewById(R.id.languageConfirmButton);
        languageConfirmButton.setEnabled(false);
        languageConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View onClickListener) {
                ApplicationPreferences.Instance.setUserLanguage(languageId);

                Map<String, String> header = WorkflowServiceHelper.getApplicationStartHeaders();
                header.put("Accept-Language", languageId);

                Map<String, String> query = new HashMap<>();
                //   WorkflowServiceHelper.getInstance().executeApplicationStartRequest(signinscreencallback);
                WorkflowServiceHelper.getInstance().execute(languageSelectionDTO.getMetadata().getTransitions().getSignin(), signinscreencallback, query, header);
            }
        });

        loadData();
        return view;

    }

    /**
     * Created data for language selection list
     */
    private void loadData() {
        LanguageOptionModel languageOptionModel;
        languageOptionModelList = new ArrayList<>();

        if (languageSelectionDTO != null) {
            payloadDTO = languageSelectionDTO.getPayload();
            languageListDTO = payloadDTO.getLanguageSelection().getLanguage();

            int size = languageListDTO.getOptions().size();
            for (int i = 0; i < size; i++) {
                languageOptionModel = new LanguageOptionModel();
                languageOptionModel.setValue(languageListDTO.getOptions().get(i).getLabel());
                languageOptionModel.setLanguageId(languageListDTO.getOptions().get(i).getCode());
                languageOptionModel.setChecked(false);
                languageOptionModelList.add(languageOptionModel);
            }

        }

        LanguageListAdapter languageListAdapter = new LanguageListAdapter(languageOptionModelList, this, getActivity());
        languageListView.setAdapter(languageListAdapter);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!StringUtil.isNullOrEmpty(languageName)) {
            outState.putString("language", languageName);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLanguageChange(String selectedLanguage,String languageCode) {
        languageName = selectedLanguage;
        languageId=languageCode;
        languageConfirmButton.setEnabled(true);
    }
}


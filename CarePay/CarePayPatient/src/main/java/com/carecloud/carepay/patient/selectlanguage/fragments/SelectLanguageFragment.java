package com.carecloud.carepay.patient.selectlanguage.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.selectlanguage.adapters.LanguageListAdapter;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.signinsignup.dto.OptionDTO;
import com.carecloud.carepaylibray.signinsignup.dto.SignInDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by lsoco_user on 9/2/2016.
 * Select Language
 */
public class SelectLanguageFragment extends BaseFragment implements LanguageListAdapter.OnItemClickListener {

    private static final String LOG_TAG = SelectLanguageFragment.class.getSimpleName();
    private SignInDTO dto;
    private FragmentActivityInterface callback;
    private ImageButton languageConfirmButton;
    private OptionDTO selectedLanguage;

    public static SelectLanguageFragment newInstance() {
        return new SelectLanguageFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (FragmentActivityInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Attached Context must implement FragmentActivityInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dto = (SignInDTO) callback.getDto();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_language, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView languageListView = (RecyclerView) view.findViewById(R.id.languageRecyclerView);
        languageListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        languageConfirmButton = (ImageButton) view.findViewById(R.id.languageConfirmButton);
        languageConfirmButton.setEnabled(false);
        languageConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View onClickListener) {
                changeLanguage();
            }
        });
        List<OptionDTO> languages = dto.getPayload().getLanguages();
        languageListView.setAdapter(new LanguageListAdapter(languages, this));
    }

    private void changeLanguage() {
        if (!getApplicationPreferences().getUserLanguage().equals(selectedLanguage.getCode())) {
            Map<String, String> query = new HashMap<>();
            getWorkflowServiceHelper().execute(dto.getMetadata().getLinks().getLanguage(),
                    changeLanguageCallBack, query, getWorkflowServiceHelper().getApplicationStartHeaders());
        } else {
            getActivity().onBackPressed();
        }
    }

    private WorkflowServiceCallback changeLanguageCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();

        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    public void onLanguageSelected(OptionDTO language) {
        selectedLanguage = language;
        languageConfirmButton.setEnabled(true);
    }
}


package com.carecloud.carepay.patient.selectlanguage.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.selectlanguage.adapters.LanguageListAdapter;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.ServerErrorDTO;
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView languageListView = view.findViewById(R.id.languageRecyclerView);
        languageListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        languageConfirmButton = view.findViewById(R.id.languageConfirmButton);
        languageConfirmButton.setEnabled(false);
        languageConfirmButton.setOnClickListener(onClickListener -> changeLanguage());
        List<OptionDTO> languages = dto.getPayload().getLanguages();
        languageListView.setAdapter(new LanguageListAdapter(languages, this));
    }

    private void changeLanguage() {
        if (!getApplicationPreferences().getUserLanguage().equals(selectedLanguage.getCode())) {
            Map<String, String> query = new HashMap<>();
            query.put("language", selectedLanguage.getCode());
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
            if (workflowDTO.getPayload().has("language_metadata")) {
                getApplicationPreferences().setUserLanguage(selectedLanguage.getCode());
                getWorkflowServiceHelper().saveLabels(workflowDTO.getPayload()
                        .getAsJsonObject("language_metadata").getAsJsonObject("metadata")
                        .getAsJsonObject("labels"));
            }
            getActivity().onBackPressed();

        }

        @Override
        public void onFailure(ServerErrorDTO serverErrorDto) {
            hideProgressDialog();
            showErrorNotification(serverErrorDto.getMessage().getBody().getError().getMessage());
            Log.e(getString(R.string.alert_title_server_error), serverErrorDto.getMessage().getBody().getError().getMessage());
        }
    };

    @Override
    public void onLanguageSelected(OptionDTO language) {
        selectedLanguage = language;
        languageConfirmButton.setEnabled(true);
    }
}


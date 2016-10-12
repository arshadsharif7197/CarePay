package com.carecloud.carepaylibray.selectlanguage.fragments;

import android.content.Intent;
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

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.selectlanguage.adapters.LanguageListAdapter;
import com.carecloud.carepaylibray.selectlanguage.models.LanguageOptionModel;
import com.carecloud.carepaylibray.signinsignup.SigninSignupActivity;
import com.carecloud.carepaylibray.utils.ApplicationPreferences;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lsoco_user on 9/2/2016.
 * Select Language
 */
public class SelectLanguageFragment extends Fragment implements LanguageListAdapter.OnItemClickListener {

    private static final String LOG_TAG = SelectLanguageFragment.class.getSimpleName();
    RecyclerView languageListView;
    String languageName = null;
    List<LanguageOptionModel> languageOptionModelList;
    ImageButton languageConfirmButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    /**
     * Creating view for language fragment
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
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
            public void onClick(View v) {
                ApplicationPreferences.Instance.setUserLanguage(languageName);
                Intent intent = new Intent(getActivity(), SigninSignupActivity.class);
                getActivity().startActivity(intent);
            }
        });
        loadData();
        return view;

    }

    /**
     * Created data for language selection list
     */
    private void loadData() {

        languageOptionModelList = new ArrayList<>();
        LanguageOptionModel languageOptionModel;
        languageOptionModel = new LanguageOptionModel();
        languageOptionModel.setValue("English");
        languageOptionModel.setChecked(false);
        languageOptionModelList.add(languageOptionModel);

        languageOptionModel = new LanguageOptionModel();
        languageOptionModel.setValue("Español");
        languageOptionModel.setChecked(false);
        languageOptionModelList.add(languageOptionModel);

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
    public void onLanguageChange(String selectedLanguage) {
        languageName=selectedLanguage;
        languageConfirmButton.setEnabled(true);
    }}


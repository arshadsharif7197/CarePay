package com.carecloud.carepaylibray.selectlanguage.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.carecloud.carepaylibray.utils.StringFunctions;

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

        languageOptionModel = new LanguageOptionModel();
        languageOptionModel.setValue("Français");
        languageOptionModel.setChecked(false);
        languageOptionModelList.add(languageOptionModel);

        languageOptionModel = new LanguageOptionModel();
        languageOptionModel.setValue("Português");
        languageOptionModel.setChecked(false);
        languageOptionModelList.add(languageOptionModel);

        languageOptionModel = new LanguageOptionModel();
        languageOptionModel.setValue("廣州話");
        languageOptionModel.setChecked(false);
        languageOptionModelList.add(languageOptionModel);

        if (!StringFunctions.isNullOrEmpty(languageName)) {
            for (int j = 0; j < languageOptionModelList.size(); j++) {
                LanguageOptionModel languageOptionModelData = languageOptionModelList.get(j);
                if (languageOptionModelData.getValue().equalsIgnoreCase(languageName)) {
                    languageOptionModelList.get(j).setChecked(true);
                } else {
                    languageOptionModelList.get(j).setChecked(false);
                }
            }
        }
        LanguageListAdapter languageListAdapter = new LanguageListAdapter(languageOptionModelList, this, getActivity());
        languageListView.setAdapter(languageListAdapter);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!StringFunctions.isNullOrEmpty(languageName)) {
            outState.putString("language", languageName);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * @param view
     * @param position  getting selected languauge position
     * @param language
     */
    @Override
    public void onItemClick(View view, int position, LanguageOptionModel language) {
        List<LanguageOptionModel> newLanguageList = new ArrayList<>();
        for (int i = 0; i < languageOptionModelList.size(); i++) {
            LanguageOptionModel languageOptionModel = languageOptionModelList.get(i);
            if (languageOptionModel.getValue().equalsIgnoreCase(language.getValue())) {
                languageOptionModel.setChecked(true);
            } else {
                languageOptionModel.setChecked(false);
            }
            newLanguageList.add(languageOptionModel);
        }
        LanguageListAdapter languageListAdapter = new LanguageListAdapter(newLanguageList, this, getActivity());
        languageListView.setAdapter(languageListAdapter);
        languageListAdapter.notifyDataSetChanged();
        languageName = language.getValue();
        languageConfirmButton.setEnabled(true);
        languageConfirmButton.setBackgroundResource(R.drawable.button_blue_fill_background);
    }
}


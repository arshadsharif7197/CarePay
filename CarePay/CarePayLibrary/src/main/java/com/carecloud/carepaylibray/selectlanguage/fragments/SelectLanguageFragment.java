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
    RecyclerView mListView;
    String language = null;
    List<LanguageOptionModel> mLanguageOptionModelList;
    ImageButton mLanguageConfirmButton;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * Creating view for language fragment
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
        mListView = (RecyclerView) view.findViewById(R.id.languageRecyclerView);
        mListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLanguageConfirmButton = (ImageButton) view.findViewById(R.id.languageConfirmButton);
        mLanguageConfirmButton.setEnabled(false);
        mLanguageConfirmButton.setOnClickListener(new View.OnClickListener() {
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

        mLanguageOptionModelList = new ArrayList<>();
        LanguageOptionModel mLanguageOptionModel;
        mLanguageOptionModel = new LanguageOptionModel();
        mLanguageOptionModel.setValue("English");
        mLanguageOptionModel.setChecked(false);
        mLanguageOptionModelList.add(mLanguageOptionModel);

        mLanguageOptionModel = new LanguageOptionModel();
        mLanguageOptionModel.setValue("Español");
        mLanguageOptionModel.setChecked(false);
        mLanguageOptionModelList.add(mLanguageOptionModel);

        mLanguageOptionModel = new LanguageOptionModel();
        mLanguageOptionModel.setValue("Français");
        mLanguageOptionModel.setChecked(false);
        mLanguageOptionModelList.add(mLanguageOptionModel);

        mLanguageOptionModel = new LanguageOptionModel();
        mLanguageOptionModel.setValue("Português");
        mLanguageOptionModel.setChecked(false);
        mLanguageOptionModelList.add(mLanguageOptionModel);

        mLanguageOptionModel = new LanguageOptionModel();
        mLanguageOptionModel.setValue("廣州話");
        mLanguageOptionModel.setChecked(false);
        mLanguageOptionModelList.add(mLanguageOptionModel);

        if (!StringFunctions.isNullOrEmpty(language)) {
            for (int j = 0; j < mLanguageOptionModelList.size(); j++) {
                LanguageOptionModel mLanguageOptionModelData = mLanguageOptionModelList.get(j);
                if (mLanguageOptionModelData.getValue().equalsIgnoreCase(language)) {
                    mLanguageOptionModelList.get(j).setChecked(true);
                } else {
                    mLanguageOptionModelList.get(j).setChecked(false);
                }
            }
        }
        LanguageListAdapter mLanguageListAdapter = new LanguageListAdapter(mLanguageOptionModelList, this, getActivity());
        mListView.setAdapter(mLanguageListAdapter);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!StringFunctions.isNullOrEmpty(language)) {
            outState.putString("language", language);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     *
     * @param view
     * @param position getting selected languauge position
     * @param mLanguage
     */
    @Override
    public void onItemClick(View view, int position, LanguageOptionModel mLanguage) {
        List<LanguageOptionModel> newLanguageList = new ArrayList<>();
        for (int i = 0; i < mLanguageOptionModelList.size(); i++) {
            LanguageOptionModel mLanguageOptionModel = mLanguageOptionModelList.get(i);
            if (mLanguageOptionModel.getValue().equalsIgnoreCase(mLanguage.getValue())) {
                mLanguageOptionModel.setChecked(true);
            } else {
                mLanguageOptionModel.setChecked(false);
            }
            newLanguageList.add(mLanguageOptionModel);
        }
        LanguageListAdapter mLanguageListAdapter = new LanguageListAdapter(newLanguageList, this, getActivity());
        mListView.setAdapter(mLanguageListAdapter);


        mLanguageListAdapter.notifyDataSetChanged();
        language = mLanguage.getValue();
       mLanguageConfirmButton.setEnabled(true);
       mLanguageConfirmButton.setBackgroundResource(R.drawable.button_blue_fill_background);
    }
}


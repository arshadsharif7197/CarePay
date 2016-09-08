package com.carecloud.carepaylibray.select_language;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.carecloud.carepaylibrary.R;

import com.carecloud.carepaylibray.fragments.HomeFragment;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;
import com.carecloud.carepaylibray.select_language.language_adapter.LanguageListAdapter;
import com.carecloud.carepaylibray.select_language.language_model.OptionModel;
import com.carecloud.carepaylibray.utils.StringFunctions;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lsoco_user on 9/2/2016.
 */
public class SelectLanguageFragment extends Fragment implements LanguageListAdapter.OnItemClickListener {

    private static final String LOG_TAG = SelectLanguageFragment.class.getSimpleName();
    private KeyboardHolderActivity mActivity;
    RecyclerView mListView;
    String language = null;
    List<OptionModel> mOptionModelList;
    ImageButton confirmButton;
    int viewId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
//        if (savedInstanceState != null) {
//            language = savedInstanceState.getString("language");
//            System.out.println("lang---" + language);
//        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_select_language, container, false);
        mListView = (RecyclerView) view.findViewById(R.id.language_recycler_view);
        mListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        confirmButton = (ImageButton)view.findViewById(R.id.confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getActivity(),HomeFragment.class);
                getActivity().startActivity(intent);
            }
        });
        loadData();
        return view;

    }

    private void loadData() {

        mOptionModelList = new ArrayList<>();
        OptionModel mOptionModel;
        mOptionModel = new OptionModel();
        mOptionModel.setValue("English");
        mOptionModel.setChecked(false);
        mOptionModelList.add(mOptionModel);

        mOptionModel = new OptionModel();
        mOptionModel.setValue("Español");
        mOptionModel.setChecked(false);
        mOptionModelList.add(mOptionModel);

        mOptionModel = new OptionModel();
        mOptionModel.setValue("français");
        mOptionModel.setChecked(false);
        mOptionModelList.add(mOptionModel);

        mOptionModel = new OptionModel();
        mOptionModel.setValue("Português");
        mOptionModel.setChecked(false);
        mOptionModelList.add(mOptionModel);

        mOptionModel = new OptionModel();
        mOptionModel.setValue("廣州話");
        mOptionModel.setChecked(false);
        mOptionModelList.add(mOptionModel);

        if (!StringFunctions.isNullOrEmpty(language)) {
            for (int j = 0; j < mOptionModelList.size(); j++) {
                OptionModel mOptionModelData = mOptionModelList.get(j);
                if (mOptionModelData.getValue().equalsIgnoreCase(language)) {
                    mOptionModelList.get(j).setChecked(true);
                } else {
                    mOptionModelList.get(j).setChecked(false);
                }
            }
        }
        LanguageListAdapter mLanguageListAdapter = new LanguageListAdapter(mOptionModelList, this, getActivity());
        mListView.setAdapter(mLanguageListAdapter);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!StringFunctions.isNullOrEmpty(language)) {
            outState.putString("language", language);
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onItemClick(View view, int position, OptionModel mLanguage) {
        List<OptionModel> newLanguageList = new ArrayList<>();
        for (int i = 0; i < mOptionModelList.size(); i++) {
            OptionModel mOptionModel = mOptionModelList.get(i);
            if (mOptionModel.getValue().equalsIgnoreCase(mLanguage.getValue())) {
                mOptionModel.setChecked(true);
            } else {
                mOptionModel.setChecked(false);

            }
            newLanguageList.add(mOptionModel);
        }
        LanguageListAdapter mLanguageListAdapter = new LanguageListAdapter(newLanguageList, this, getActivity());
        mListView.setAdapter(mLanguageListAdapter);
        mLanguageListAdapter.notifyDataSetChanged();
        language = mLanguage.getValue();
        confirmButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }
}


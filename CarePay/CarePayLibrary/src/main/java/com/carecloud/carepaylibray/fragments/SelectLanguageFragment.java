package com.carecloud.carepaylibray.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.StringFunctions;
import com.carecloud.carepaylibray.adapter.LanguageListAdapter;
import com.carecloud.carepaylibray.models.OptionModel;
import com.carecloud.carepaylibray.models.ScreenComponentModel;
import com.carecloud.carepaylibray.models.ScreenModel;
import com.carecloud.carepaylibray.models.WorkflowModel;

import java.util.ArrayList;
import java.util.List;

public class SelectLanguageFragment extends Fragment implements LanguageListAdapter.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View preparedView;
    ImageButton confirmButton = null;
    static List<OptionModel> mOptionModelList;
    RecyclerView mListView;
    String language = null;
    int viewId ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            language = savedInstanceState.getString("language");
            System.out.println("lang---" + language);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_language, container, false);
        FrameLayout mFrameLayout = (FrameLayout) view.findViewById(R.id.frame_container);
        viewId=this.getArguments().getInt("viewid");
        mFrameLayout.addView(prepareLayout());
        return view;
    }

    private RelativeLayout prepareLayout() {
        WorkflowModel model = new WorkflowModel();
        ScreenModel languageModel = model.getSelectLanguageScreenModel();
        RelativeLayout mRelativeLayout = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mRelativeLayout.setLayoutParams(params);

        RelativeLayout mRelativeLayout1 = new RelativeLayout(getActivity());

        ArrayList<ScreenComponentModel> mComponentModels = languageModel.getComponentModels();
        for (int i = 0; i < mComponentModels.size(); i++) {

            if (mComponentModels.get(i).getType().equalsIgnoreCase("choice")) {
                mListView = new RecyclerView(getActivity());
                RelativeLayout.LayoutParams mLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mLayoutParams.addRule(RelativeLayout.BELOW, mRelativeLayout1.getId());
                mListView.setId(View.generateViewId());
                mListView.setLayoutManager(new LinearLayoutManager(getActivity()));

                mLayoutParams.setMargins(15, 50, 15, 15);
                mOptionModelList = new ArrayList<>();
                OptionModel mOptionModel;
                mOptionModel = new OptionModel();
                mOptionModel.setValue("English");
                mOptionModel.setLabel("us");
                mOptionModel.setChecked(false);
                mOptionModelList.add(mOptionModel);

                mOptionModel = new OptionModel();
                mOptionModel.setValue("Espanol");
                mOptionModel.setLabel("flag2");
                mOptionModel.setChecked(false);
                mOptionModelList.add(mOptionModel);

                mOptionModel = new OptionModel();
                mOptionModel.setValue("French");
                mOptionModel.setLabel("flag3");
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


                mRelativeLayout.addView(mListView, mLayoutParams);
            } else if (mComponentModels.get(i).getType().equalsIgnoreCase("button")) {

                if (mComponentModels.get(i).getLabel().equalsIgnoreCase("Continue")) {
                    confirmButton = new ImageButton(getActivity());
                    RelativeLayout.LayoutParams layout_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            100);

                    layout_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    layout_params.setMargins(40,15,40,15);


                    confirmButton.setLayoutParams(layout_params);
                    confirmButton.setBackgroundColor(getResources().getColor(R.color.uncheck_confirm_button));
                    confirmButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                 //   confirmButton.
                    if (!StringFunctions.isNullOrEmpty(language)) {
                        confirmButton.setBackgroundColor(getResources().getColor(R.color.confirm_button));
                    } else {
                        confirmButton.setImageResource(R.drawable.icn_check_disabled);

                    }
                    confirmButton.setImageResource(R.drawable.icn_check_disabled);
                    confirmButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            HomeScreenFragment mSelectLanguageFragment = new HomeScreenFragment();
                            getFragmentManager().beginTransaction().replace(viewId, mSelectLanguageFragment).commit();
                        }
                    });
                    mRelativeLayout.addView(confirmButton, layout_params);
                }

            } else if (mComponentModels.get(i).getType().equalsIgnoreCase("imageview")) {
                RelativeLayout.LayoutParams layout_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                mRelativeLayout1 = new RelativeLayout(getActivity());
                mRelativeLayout1.setId(View.generateViewId());
                mRelativeLayout1.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_signup_1_phone));
                layout_params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                mRelativeLayout1.setLayoutParams(layout_params);

                ImageView mImageView = new ImageView(getActivity());
                RelativeLayout.LayoutParams layout_params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layout_params1.addRule(RelativeLayout.CENTER_VERTICAL);
                mImageView.setId(View.generateViewId());
                mImageView.setImageResource(R.drawable.logo_light);
                mImageView.setLayoutParams(layout_params1);
                mRelativeLayout1.addView(mImageView, layout_params1);
                mRelativeLayout.addView(mRelativeLayout1, layout_params);
            }
        }

        return mRelativeLayout;

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
        confirmButton.setBackgroundColor(getResources().getColor(R.color.confirm_button));
    }

}
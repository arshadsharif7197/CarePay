package com.carecloud.carepaylibray.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.activities.DemographicsActivity;
import com.carecloud.carepaylibray.adapter.HomeViewAdapter;
import com.carecloud.carepaylibray.constants.ComponentTypeConstants;
import com.carecloud.carepaylibray.models.OptionModel;
import com.carecloud.carepaylibray.models.ScreenComponentModel;
import com.carecloud.carepaylibray.models.WorkflowModel;

import java.util.ArrayList;
import java.util.List;


public class HomeScreenFragment extends Fragment implements HomeViewAdapter.OnItemClickListener {

    List<OptionModel> mOptionModelList;
    RecyclerView mGridView;
    TextView carepay_text_view;
    TextView profile_text_view;
    ProgressBar mProgressBar;
    ScreenComponentModel componentModel;
    int viewId;

   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
      //  viewId=this.getArguments().getInt("viewid");
   }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);
        FrameLayout mFrameLayout = (FrameLayout) view.findViewById(R.id.home_container);
        mFrameLayout.addView(prepareLayout());
        return view;
    }

    private View prepareLayout() {
        WorkflowModel workFlowModel = new WorkflowModel();
        ArrayList<ScreenComponentModel> mComponentModels = workFlowModel.getHomeScreenModel().getComponentModels();
        RelativeLayout mRelativeLayout = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mRelativeLayout.setLayoutParams(params);

        RelativeLayout.LayoutParams toolbarparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);


//        Toolbar toolbar = new Toolbar(getActivity());
//        toolbar.setLayoutParams(toolbarparams);
//        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color.white));
//        toolbar.setTitle("Menu"); // todo get title from component
//        toolbar.setBackgroundColor(getResources().getColor(R.color.charcoal));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            toolbar.setElevation(10);
//        }
//        toolbar.setNavigationIcon(R.drawable.icn_patient_mode_nav_close;
//        (AppCompatActivity.getActivity().setSupportActionBar(toolbar));
//Calling Components from the workflowmodel using ScreenComponent Model
        for (int i = 0; i < mComponentModels.size(); i++) {
            //Adding TextView
            if (mComponentModels.get(i).getType().equalsIgnoreCase("textview")) {
                RelativeLayout.LayoutParams layout_params0 = null;
                if (mComponentModels.get(i).getLabel().equalsIgnoreCase("How can we help?")) {
                    layout_params0 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    carepay_text_view = new TextView(getActivity());
                    carepay_text_view.setText(mComponentModels.get(i).getLabel());
                    carepay_text_view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    carepay_text_view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
                    layout_params0.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    carepay_text_view.setLayoutParams(layout_params0);
                    carepay_text_view.setId(View.generateViewId());
                    layout_params0.setMargins(10, 10, 10, 20);
                    mRelativeLayout.addView(carepay_text_view, layout_params0);

                } else if (mComponentModels.get(i).getLabel().equalsIgnoreCase("Profile Meter")) {
                    RelativeLayout.LayoutParams layout_params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    profile_text_view = new TextView(getActivity());
                    profile_text_view.setText(mComponentModels.get(i).getLabel());
                    // carepay_text_view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    profile_text_view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                    layout_params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    profile_text_view.setLayoutParams(layout_params2);
                    profile_text_view.setId(View.generateViewId());
                    layout_params2.setMargins(10, 10, 10, 5);
                    mRelativeLayout.addView(profile_text_view, layout_params2);

                } else if (mComponentModels.get(i).getLabel().equalsIgnoreCase("Add Details")) {
                    RelativeLayout.LayoutParams  layout_params3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    TextView add_detail_text_view = new TextView(getActivity());
                    add_detail_text_view.setText(mComponentModels.get(i).getLabel());
                    // carepay_text_view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    add_detail_text_view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                    layout_params3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    //layout_params3.addRule(RelativeLayout.ALIGN_BASELINE, profile_text_view.getId());
                    add_detail_text_view.setLayoutParams(layout_params3);
                    add_detail_text_view.setId(View.generateViewId());
                    layout_params3.setMargins(10, 10, 10, 5);
                    add_detail_text_view.setTextColor(getResources().getColor(R.color.colorPrimary));
                    mRelativeLayout.addView(add_detail_text_view, layout_params3);
                    add_detail_text_view.setOnClickListener(addDetailsListener);

                }
            } else if ((mComponentModels.get(i).getType()).equalsIgnoreCase("gridview")) {
                mGridView = new RecyclerView(getActivity());
                params.addRule(RelativeLayout.BELOW, mProgressBar.getId());
                GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);

                mGridView.setHasFixedSize(true);
                mGridView.setLayoutManager(manager);
                mGridView.setLayoutParams(params);
                mOptionModelList = new ArrayList<>();
                OptionModel mOptionModel = null;

                mOptionModel = new OptionModel();
                mOptionModel.setValue("Check In");
                mOptionModel.setChecked(false);
                mOptionModel.setLabel("us");
                mOptionModelList.add(mOptionModel);

                mOptionModel = new OptionModel();
                mOptionModel.setValue("Purchase");
                mOptionModel.setChecked(false);
                mOptionModel.setLabel("flag2");
                mOptionModelList.add(mOptionModel);

                mOptionModel = new OptionModel();
                mOptionModel.setValue("Check Out");
                mOptionModel.setChecked(false);
                mOptionModel.setLabel("us");
                mOptionModelList.add(mOptionModel);

                mOptionModel = new OptionModel();
                mOptionModel.setValue("Payment Plan");
                mOptionModel.setChecked(false);
                mOptionModel.setLabel("us");
                mOptionModelList.add(mOptionModel);

                mOptionModel = new OptionModel();
                mOptionModel.setValue("Patient Queue");
                mOptionModel.setChecked(false);
                mOptionModel.setLabel("us");
                mOptionModelList.add(mOptionModel);

                mOptionModel = new OptionModel();
                mOptionModel.setValue("Appointments");
                mOptionModel.setChecked(false);
                mOptionModel.setLabel("us");
                mOptionModelList.add(mOptionModel);

                HomeViewAdapter mHomeViewAdapter = new HomeViewAdapter(mOptionModelList, this, getActivity());
                mGridView.setAdapter(mHomeViewAdapter);
                mRelativeLayout.addView(mGridView, params);
            }else if((mComponentModels.get(i).getType()).equalsIgnoreCase("progressbar")){
                RelativeLayout.LayoutParams params12 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                mRelativeLayout.setLayoutParams(params);
                mProgressBar = new ProgressBar(getActivity(),null,android.R.attr.progressBarStyleHorizontal);
                RelativeLayout.LayoutParams progressparams=new RelativeLayout.LayoutParams(300,10);
                mProgressBar.setMax(100);
                mProgressBar.setProgress(65);
                params12.setMargins(10, 5, 10, 20);
                mProgressBar.setMinimumHeight(100);

                mProgressBar.setMinimumWidth(250);
              //mProgressBar.setLayoutParams(progressparams);
                params12.addRule(RelativeLayout.BELOW, profile_text_view.getId());
                mProgressBar.setId(View.generateViewId());
                 mProgressBar.setLayoutParams(params12);
                mRelativeLayout.addView(mProgressBar,params12);
            }
        }
        return mRelativeLayout;
    }
    private View.OnClickListener addDetailsListener= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getContext(), DemographicsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    };

    @Override
    public void onItemClick(View view, int position, OptionModel mLanguage) {

    }
}


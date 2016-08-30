package com.carecloud.carepaylibray.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.adapter.HomeViewAdapter;
import com.carecloud.carepaylibray.models.OptionModel;
import com.carecloud.carepaylibray.models.ScreenComponentModel;
import com.carecloud.carepaylibray.models.WorkflowModel;

import java.util.ArrayList;
import java.util.List;


public class HomeScreenFragment extends Fragment implements HomeViewAdapter.OnItemClickListener{

    List<OptionModel> mOptionModelList;
    RecyclerView mGridView;
    TextView carepay_text_view;
    ScreenComponentModel componentModel;
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
        ArrayList<ScreenComponentModel> mComponentModels =  workFlowModel.getHomeScreenModel().getComponentModels();
        RelativeLayout mRelativeLayout = new RelativeLayout(getActivity());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mRelativeLayout.setLayoutParams(params);
//Calling Components from the workflowmodel using ScreenComponent Model
        for(int i=0;i<mComponentModels.size();i++){
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
                    layout_params0.setMargins(10,10,10,20);
                    mRelativeLayout.addView(carepay_text_view,layout_params0);

                }

            }
            //Adding Grid View
           else if((mComponentModels.get(i).getType()).equalsIgnoreCase("gridview")){
                mGridView = new RecyclerView(getActivity());
                params.addRule(RelativeLayout.BELOW, carepay_text_view.getId());
                GridLayoutManager manager = new GridLayoutManager(getActivity(),2);

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

                HomeViewAdapter mHomeViewAdapter = new HomeViewAdapter(mOptionModelList,this,getActivity());
                mGridView.setAdapter(mHomeViewAdapter);
                mRelativeLayout.addView(mGridView,params);
            }

        }

        return mRelativeLayout;
    }

    @Override
    public void onItemClick(View view, int position, OptionModel mLanguage) {

    }
}

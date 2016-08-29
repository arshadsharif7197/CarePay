package com.carecloud.carepaylibray.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.adapter.HomeViewAdapter;
import com.carecloud.carepaylibray.models.Option;
import com.carecloud.carepaylibray.models.ScreenComponentModel;
import com.carecloud.carepaylibray.models.WorkflowModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeScreenFragment extends Fragment implements HomeViewAdapter.OnItemClickListener{

    List<Option> mOptionList;
    RecyclerView mGridView;
    TextView mHelp;
    ScreenComponentModel model;
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
            if((mComponentModels.get(i).getType()).equalsIgnoreCase("text")){
                mHelp= new TextView(getActivity());
                mHelp.setGravity(Gravity.TOP);
                mHelp.setLayoutParams(params);
        //        mHelp.setText(model.getLabel());
                mRelativeLayout.addView(mHelp,params);
            }

            //Adding Grid View
           else if((mComponentModels.get(i).getType()).equalsIgnoreCase("gridview")){

                mGridView = new RecyclerView(getActivity());
                GridLayoutManager manager = new GridLayoutManager(getActivity(),2);
                mGridView.setHasFixedSize(true);
                mGridView.setLayoutManager(manager);
                mGridView.setLayoutParams(params);
                mOptionList = new ArrayList<>();
                Option mOption = null;
                mOption = new Option();
                mOption.setValue("Check In");
                mOption.setChecked(false);
                mOption.setLabel("us");
                mOptionList.add(mOption);

                mOption = new Option();
                mOption.setValue("Purchase");
                mOption.setChecked(false);
                mOption.setLabel("us");
                mOptionList.add(mOption);

                mOption = new Option();
                mOption.setValue("Check Out");
                mOption.setChecked(false);
                mOption.setLabel("us");
                mOptionList.add(mOption);

                mOption = new Option();
                mOption.setValue("Payment Plan");
                mOption.setChecked(false);
                mOption.setLabel("us");
                mOptionList.add(mOption);

                mOption = new Option();
                mOption.setValue("Patient Queue");
                mOption.setChecked(false);
                mOption.setLabel("us");
                mOptionList.add(mOption);

                HomeViewAdapter mHomeViewAdapter = new HomeViewAdapter(mOptionList,this,getActivity());
                mGridView.setAdapter(mHomeViewAdapter);
                mRelativeLayout.addView(mGridView,params);
            }

        }

        return mRelativeLayout;
    }

    @Override
    public void onItemClick(View view, int position, Option mLanguage) {

    }
}

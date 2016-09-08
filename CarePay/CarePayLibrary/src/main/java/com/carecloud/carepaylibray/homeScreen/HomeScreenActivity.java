package com.carecloud.carepaylibray.homescreen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.homescreen.adapters.HomeScreenAdapter;
import com.carecloud.carepaylibray.selectlanguage.models.LanguageOptionModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lsoco_user on 9/2/2016.
 */
public class HomeScreenActivity extends AppCompatActivity implements HomeScreenAdapter.OnItemClickListener {
    RecyclerView mGridView;
    List<LanguageOptionModel> mOptionModelList;
    TextView mProfileMeter;
    TextView mAddDetails;
    ProgressBar mProgressBar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_activity);
        mGridView = (RecyclerView)findViewById(R.id.home_recycler_view);
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        mGridView.setHasFixedSize(true);
        mGridView.setLayoutManager(manager);
        loadData();


        mProfileMeter=(TextView)findViewById(R.id.profile_meter);
        mAddDetails=(TextView)findViewById(R.id.add_profile);
        mProgressBar=(ProgressBar)findViewById(R.id.progressBar);

    }



    private void loadData() {
        mOptionModelList = new ArrayList<>();
       LanguageOptionModel mOptionModel = null;

        mOptionModel = new LanguageOptionModel();
        mOptionModel.setValue("Check In");
        mOptionModel.setChecked(false);
        mOptionModelList.add(mOptionModel);

        mOptionModel = new LanguageOptionModel();
        mOptionModel.setValue("Purchase");
        mOptionModel.setChecked(false);
        mOptionModelList.add(mOptionModel);

        mOptionModel = new LanguageOptionModel();
        mOptionModel.setValue("Check Out");
        mOptionModel.setChecked(false);
        mOptionModel.setLabel("us");
        mOptionModelList.add(mOptionModel);

        mOptionModel = new LanguageOptionModel();
        mOptionModel.setValue("Payment Plan");
        mOptionModel.setChecked(false);
        mOptionModelList.add(mOptionModel);

        mOptionModel = new LanguageOptionModel();
        mOptionModel.setValue("Patient Queue");
        mOptionModelList.add(mOptionModel);

        mOptionModel = new LanguageOptionModel();
        mOptionModel.setValue("Appointments");
        mOptionModel.setChecked(false);
        mOptionModelList.add(mOptionModel);

        HomeScreenAdapter mHomeViewAdapter = new HomeScreenAdapter(mOptionModelList, this, this);
        mGridView.setAdapter(mHomeViewAdapter);
    }

    @Override
    public void onItemClick(View view, int position, LanguageOptionModel mLanguage) {
        //Click listener for the Grid view in home screen
       /* if (mLanguage.getValue().equalsIgnoreCase("Check In")) {

        }*/
    }
}

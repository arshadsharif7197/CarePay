package com.carecloud.carepay.patient.tutorial.tutorial;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.tutorial.TutorialFragment;
import com.carecloud.carepay.patient.tutorial.TutorialItem;
import com.carecloud.carepay.patient.tutorial.adapter.TutorialAdapter;
import com.carecloud.carepay.patient.tutorial.view.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class TutorialActivity extends BasePatientActivity implements TutorialContract.View {

    private ViewPager viewPager;
    private TextView skepTextView;
    private Button doneButton;
    private TutorialPresenter presenter;

    private View.OnClickListener finishTutorialClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            presenter.doneOrSkipClick();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        presenter = new TutorialPresenter(this);
        ActionBar actionBar = getActionBar();

        if (actionBar != null) {
            getActionBar().hide();
        }
        viewPager = (ViewPager) findViewById(R.id.tutorial_view_pager);
        skepTextView = (TextView) findViewById(R.id.tutorial_skip_textview);
        doneButton = (Button) findViewById(R.id.tutorial_done_button);

        skepTextView.setOnClickListener(finishTutorialClickListener);
        doneButton.setOnClickListener(finishTutorialClickListener);

        presenter.loadViewPagerFragments(getTutorialItems());
    }

    @Override
    public void showEndTutorial() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void showDoneButton() {
        skepTextView.setVisibility(View.GONE);
        doneButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void showSkipButton() {
        skepTextView.setVisibility(View.VISIBLE);
        doneButton.setVisibility(View.GONE);
    }

    @Override
    public void setViewPagerFragments(List<TutorialFragment> tutorialFragments) {
        viewPager.setAdapter(new TutorialAdapter(getSupportFragmentManager(), tutorialFragments));

        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.tutorial_view_page_indicator);
        indicator.setViewPager(viewPager);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                presenter.onPageSelected(viewPager.getCurrentItem());

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private ArrayList<TutorialItem> getTutorialItems() {
        ArrayList<TutorialItem> items = new ArrayList<>();
        items.add(new TutorialItem("tutorial_step_1_header", "tutorial_step_1_subheader", R.drawable.tutorial_step_1));
        items.add(new TutorialItem("tutorial_step_2_header", "tutorial_step_2_subheader", R.drawable.tutorial_step_2));
        items.add(new TutorialItem("tutorial_step_3_header", "tutorial_step_3_subheader", R.drawable.tutorial_step_3));
        items.add(new TutorialItem("tutorial_step_4_header", "tutorial_step_4_subheader", R.drawable.tutorial_step_4));
        items.add(new TutorialItem("tutorial_step_5_header", "tutorial_step_5_subheader", R.drawable.tutorial_step_5));

        return items;
    }
}

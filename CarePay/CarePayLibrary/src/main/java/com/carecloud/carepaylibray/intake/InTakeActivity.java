package com.carecloud.carepaylibray.intake;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.fragments.viewpager.DemographicsAddressFragment;
import com.carecloud.carepaylibray.demographics.fragments.viewpager.DemographicsDetailsFragment;
import com.carecloud.carepaylibray.demographics.fragments.viewpager.DemographicsDocumentsFragment;
import com.carecloud.carepaylibray.demographics.fragments.viewpager.DemographicsMoreDetailsFragment;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;
import com.carecloud.carepaylibray.payment.PaymentActivity;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.List;
import java.util.Locale;

public class InTakeActivity extends KeyboardHolderActivity {

    private JsonFormParseSimulator formsParseSimulator;
    private List<IntakeFormModel>  forms;
    private ViewPager              formsViewPager;
    private Toolbar                formsToolbar;
    private TextView               formsToolbarTitleTv;
    private ImageView[]            intakeDotsImageView;
    private Button                 intakeNextButton;
    private int                    intakeCurrentPageIndex;

    @Override
    public void replaceFragment(Class fragClass, boolean addToBackStack) {

    }

    @Override
    public void placeInitContentFragment() {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_in_take;
    }

    @Override
    public int getContentsHolderId() {
        return 0;
    }

    @Override
    public int getKeyboardHolderId() {
        return R.id.intakeKeyboardHolder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the form models
        formsParseSimulator = new JsonFormParseSimulator();
        forms = formsParseSimulator.getIntakeFormModels();

        // set the toolbar
        formsToolbar = (Toolbar) findViewById(R.id.intakeToolbar);
        formsToolbarTitleTv = (TextView) formsToolbar.findViewById(R.id.intakeToolbarTitle);
        SystemUtil.setGothamRoundedMediumTypeface(this, formsToolbarTitleTv);
        formsToolbar.setTitle("");
        formsToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.icn_patient_mode_nav_back));
        setSupportActionBar(formsToolbar);

        // set the dotted tab
        LinearLayout dottedTabLl = (LinearLayout) findViewById(R.id.intakeDottedTab);
        intakeDotsImageView = createDotsImageViews(forms.size());
        for (ImageView dotView : intakeDotsImageView) {
            dottedTabLl.addView(dotView);
        }

        // set the next button
        intakeNextButton = (Button) findViewById(R.id.intakeBtnNext);
        intakeNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String intakeButtonText = intakeNextButton.getText().toString();
                /* Based on the below logic user will be navigated to payment screen if he has finished all the consent forms */
                if(intakeButtonText.equalsIgnoreCase(getString(R.string.intakeNextButtonText))) {
                    moveToNextQuestion();
                } else {
                    Intent intent = new Intent(InTakeActivity.this, PaymentActivity.class);
                    startActivity(intent);
                }

            }
        });

        // set the viewpager
        formsViewPager = (ViewPager) findViewById(R.id.intakeViewPager);
        FragmentStatePagerAdapter pagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                InTakeFragment fragment = getSelectedItem(position);
                if(fragment !=null) {
                    fragment.setFormModel(forms.get(position));
                }
                return fragment;
            }

            @Override
            public int getCount() {
                return forms.size();
            }
        };
        formsViewPager.setAdapter(pagerAdapter);
        formsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // change next button text if lst fragment
                if (position == formsViewPager.getAdapter().getCount() - 1) {
                    intakeNextButton.setText(getString(R.string.intakeNextButtonLastText));
                } else {
                    intakeNextButton.setText(getString(R.string.intakeNextButtonText));
                }
                // if scrolled next, just enable the next tab dot
                if(position > intakeCurrentPageIndex) {
                    ImageView tabDot = intakeDotsImageView[position];
                    tabDot.setImageDrawable(ContextCompat.getDrawable(InTakeActivity.this, R.drawable.circle_indicator_blue));
                } else if(position < intakeCurrentPageIndex){ // scrolled to prev
                    ImageView tabDot = intakeDotsImageView[intakeCurrentPageIndex];
                    tabDot.setImageDrawable(ContextCompat.getDrawable(InTakeActivity.this, R.drawable.circle_indicator_gray));
                }
                intakeCurrentPageIndex = position;
                // update the title
                updateTitle();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        intakeCurrentPageIndex = 0;
        // enable first tab dot
        ImageView tabDot = intakeDotsImageView[intakeCurrentPageIndex];
        tabDot.setImageDrawable(ContextCompat.getDrawable(InTakeActivity.this, R.drawable.circle_indicator_blue));
        setCurrentItem(intakeCurrentPageIndex);
        updateTitle();
    }

    /**
     * Shows the fragment at index in the viewpager
     *
     * @param index The index
     */
    public void setCurrentItem(int index) {
        formsViewPager.setCurrentItem(index);
    }

    /**
     * Update the title according to the index of the current form
     */
    private void updateTitle() {
        // set the title to show the first screen
        formsToolbarTitleTv.setText(String.format(Locale.getDefault(),
                                                  "Intake Form %d of %d", intakeCurrentPageIndex + 1,
                                                  formsViewPager.getAdapter().getCount()));
    }

    /**
     * Creates the image views representing the dots in the dotted tab
     *
     * @param count The number of tabs
     * @return An arrays if image views
     */
    private ImageView[] createDotsImageViews(int count) {
        ImageView[] dots = new ImageView[count];

        for (int i = 0; i < count; i++) {
            ImageView dot = (ImageView) getLayoutInflater().inflate(R.layout.intake_dot, null);
            dots[i] = dot;
        }
        return dots;
    }

    /**
     * Shows the next fragment in the viewpager
     */
    public void moveToNextQuestion() {
        // the index of the current fragment
        if (intakeCurrentPageIndex < formsViewPager.getAdapter().getCount() - 1) {
            ++intakeCurrentPageIndex;
        }
        setCurrentItem(intakeCurrentPageIndex);
        ImageView tabDot = intakeDotsImageView[intakeCurrentPageIndex];
        tabDot.setImageDrawable(ContextCompat.getDrawable(InTakeActivity.this, R.drawable.circle_indicator_blue));

    }

    /**
     * Shows previous next fragment in the viewpager
     */
    private void moveToPreviousQuestionOrBack() {
        // the index of the current fragment
        if (intakeCurrentPageIndex == 0) {
            onBackPressed();
        } else {
            // disable the previous dot
            ImageView tabDot = intakeDotsImageView[intakeCurrentPageIndex];
            tabDot.setImageDrawable(ContextCompat.getDrawable(InTakeActivity.this, R.drawable.circle_indicator_gray));

            // just get back to HomeActivity for now
            --intakeCurrentPageIndex;
            setCurrentItem(intakeCurrentPageIndex);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(LOG_TAG, "onBack");
        if (item.getItemId() == android.R.id.home) {
            moveToPreviousQuestionOrBack();
        }
        return true;
    }

    /**
     * This method will be invoked when a page is requested to create
     */
    public InTakeFragment getSelectedItem(int position) {

        switch (position) {
            case 0:
                return new IntakeReviewvisitFragment();
            case 1:
                return new InTakecardiacSymptomsfragment();
            case 2:
                return new InTakeMedicalHistoryFragment();
            default:
                return null;
        }
    }
}
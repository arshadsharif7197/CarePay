package com.carecloud.carepaylibray.demographics.fragments.viewpager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.activities.DemographicsActivity;
import com.carecloud.carepaylibray.demographics.adapters.CustomAlertAdapter;
import com.carecloud.carepaylibray.demographics.fragments.scanner.DocumentScannerFragment;
import com.carecloud.carepaylibray.demographics.fragments.scanner.ProfilePictureFragment;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadPersonalDetailsModel;

import java.util.Arrays;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;
import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

/**
 * Created by lsoco_user on 9/2/2016.
 * Implements demographics details screen
 */
public class DemographicsDetailsFragment extends Fragment
        implements View.OnClickListener,
                   DocumentScannerFragment.NextAddRemoveStatusModifier {

    private View     view;
    private String[] raceArray;
    private String[] ethnicityArray;
    private String[] preferredLanguageArray;
    private int      selectedArray;
    private TextView raceTextView, ethnicityTextView, preferredLanguageTextView;
    private Button nextButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_demographics_details, container, false);

        initialiseUIFields();

        raceArray = getResources().getStringArray(R.array.Race);
        ethnicityArray = getResources().getStringArray(R.array.Ethnicity);
        preferredLanguageArray = getResources().getStringArray(R.array.Language);


        return view;
    }

    private void initialiseUIFields() {
        // add capture picture fragment
        FragmentManager fm = getChildFragmentManager();
        String tag = ProfilePictureFragment.class.getSimpleName();
        ProfilePictureFragment fragment
                = (ProfilePictureFragment) fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new ProfilePictureFragment();
            fragment.setButtonsStatusCallback(this);
        }
        fm.beginTransaction()
                .replace(R.id.demographicsAddressPicCapturer, fragment, tag)
                .commit();

        // get handlers to the other views
        raceTextView = (TextView) view.findViewById(R.id.raceListTextView);
        raceTextView.setOnClickListener(this);
        ethnicityTextView = (TextView) view.findViewById(R.id.ethnicityListTextView);
        ethnicityTextView.setOnClickListener(this);
        preferredLanguageTextView = (TextView) view.findViewById(R.id.preferredLanguageListTextView);
        preferredLanguageTextView.setOnClickListener(this);
        nextButton = (Button) view.findViewById(R.id.demographicsDetailsNextButton);
        nextButton.setOnClickListener(this);
//        enableNextButton(false); // 'next' is initially disabled // TODO: 9/27/2016 uncomment

        setTypefaces(view);

        populateViewsFromModel();
    }

    private void populateViewsFromModel() {
        DemographicPayloadModel payload = ((DemographicsActivity)getActivity()).getDemographicPayloadModel();
        DemographicPayloadPersonalDetailsModel model = null;
        if(payload != null) {
            model = payload.getPersonalDetails();
        }
        if(model != null) {
            raceTextView.setText(model.getPrimaryRace());
            ethnicityTextView.setText(model.getEthnicity());
            preferredLanguageTextView.setText(model.getPreferredLanguage());
            String pictureByteStream = model.getProfilePhoto();
            setPictureFromByteStream(pictureByteStream);
        } else {
            Log.v(LOG_TAG, "demographics details: views populated with defaults");
        }
    }

    private void setPictureFromByteStream(String pictureByteStream) {
        // TODO: 9/28/2016 implement
    }

    @Override
    public void onClick(View view) {
        if (view == raceTextView) {
            selectedArray = 1;
            showAlertDialogWithListview(raceArray, "Select Race");
        } else if (view == ethnicityTextView) {
            selectedArray = 2;
            showAlertDialogWithListview(ethnicityArray, "Select Ethnicity");
        } else if (view == preferredLanguageTextView) {
            selectedArray = 3;
            showAlertDialogWithListview(preferredLanguageArray, "Select Preferred Language");
        } else if (view == nextButton) {
            nextbuttonClick();
        }
    }

    private void nextbuttonClick() {
        ((DemographicsActivity) getActivity()).setCurrentItem(2, true);
        // other task may be performed...
    }

    private void showAlertDialogWithListview(final String[] raceArray, String title) {
        Log.e("raceArray==", raceArray.toString());
        Log.e("raceArray 23==", Arrays.asList(raceArray).toString());

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(title);
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        View customView = LayoutInflater.from(getActivity()).inflate(
                R.layout.alert_list_layout, null, false);
        ListView listView = (ListView) customView.findViewById(R.id.dialoglist);
        CustomAlertAdapter mAdapter = new CustomAlertAdapter(getActivity(), Arrays.asList(raceArray));
        listView.setAdapter(mAdapter);
        dialog.setView(customView);
        final AlertDialog alert = dialog.create();
        alert.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (selectedArray) {
                    case 1:
                        raceTextView.setText(raceArray[position]);
                        break;
                    case 2:
                        ethnicityTextView.setText(ethnicityArray[position]);
                        break;
                    case 3:
                        preferredLanguageTextView.setText(preferredLanguageArray[position]);
                        break;
                }
                alert.dismiss();
            }
        });
    }

    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.detailsHeading));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.detailsSubHeading));

        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.raceTextView));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.raceListTextView));

        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.ethnicityTextView));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.ethnicityListTextView));

        setGothamRoundedMediumTypeface(getActivity(), nextButton);

        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.preferredLanguageTextView));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.preferredLanguageListTextView));
    }

    @Override
    public void showAddCardButton(boolean isVisible) {

    }

    @Override
    public void enableNextButton(boolean isEnabled) {
        nextButton.setEnabled(isEnabled);
    }

    @Override
    public void scrollToBottom() {

    }
}
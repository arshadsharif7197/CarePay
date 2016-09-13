package com.carecloud.carepaylibray.demographics.fragments.viewpager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.adapters.CustomAlertAdapter;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.Utility;

import java.util.Arrays;

import static com.carecloud.carepaylibray.utils.Utility.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.Utility.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.Utility.setProximaNovaSemiboldTypeface;

/**
 * Created by lsoco_user on 9/2/2016.
 * Implements demographics details screen
 */
public class DemographicsDetailsFragment extends Fragment implements View.OnClickListener {


    ImageView imageViewDetailsImage;
    private ImageCaptureHelper imageCaptureHelper;

    View view;
    String[] raceArray;
    String[] ethnicityArray;
    String[] preferredLanguageArray;
    int selectedArray;
    TextView raceTextView, ethnicityTextView, preferredLanguageTextView;
    Button buttonChangeCurrentPhoto;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_demographics_details, container, false);

        initialiseUIFields();
        raceArray = getResources().getStringArray(R.array.Race);
        ethnicityArray = getResources().getStringArray(R.array.Ethnicity);
        preferredLanguageArray = getResources().getStringArray(R.array.Language);

        imageCaptureHelper = new ImageCaptureHelper(getActivity());
        imageCaptureHelper.setImageViewTarget(imageViewDetailsImage);
        imageCaptureHelper.setImgWidth(129); // TODO: 9/9/2016 create dimen
        setTypefaces(view);
        return view;
    }

    private void initialiseUIFields() {
        raceTextView = (TextView) view.findViewById(R.id.raceListTextView);
        raceTextView.setOnClickListener(this);
        ethnicityTextView = (TextView) view.findViewById(R.id.ethnicityListTextView);
        ethnicityTextView.setOnClickListener(this);
        preferredLanguageTextView = (TextView) view.findViewById(R.id.preferredLanguageListTextView);
        preferredLanguageTextView.setOnClickListener(this);
        buttonChangeCurrentPhoto = (Button) view.findViewById(R.id.changeCurrentPhotoButton);
        buttonChangeCurrentPhoto.setOnClickListener(this);
        imageViewDetailsImage = (ImageView) view.findViewById(R.id.DetailsProfileImage);
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
        } else if (view == buttonChangeCurrentPhoto) {
            selectImage();
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        String userChoosenTask = imageCaptureHelper.getUserChoosenTask();

        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals(ImageCaptureHelper.chooseActionDlOptions[1].toString()))
                        startActivityForResult(Intent.createChooser(imageCaptureHelper.galleryIntent(),
                                ImageCaptureHelper.CHOOSER_NAME),
                                ImageCaptureHelper.SELECT_FILE);
                } else {
                    //code for deny
                }
                break;

            case Utility.MY_PERMISSIONS_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals(ImageCaptureHelper.chooseActionDlOptions[0].toString()))
                        startActivityForResult(imageCaptureHelper.cameraIntent(), ImageCaptureHelper.REQUEST_CAMERA);
                } else {
                    //code for deny
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == ImageCaptureHelper.SELECT_FILE) {
                imageCaptureHelper.onSelectFromGalleryResult(data, ImageCaptureHelper.ROUND_IMAGE);
                changeButtonLabel();
            } else if (requestCode == ImageCaptureHelper.REQUEST_CAMERA)
                imageCaptureHelper.onCaptureImageResult(data, ImageCaptureHelper.ROUND_IMAGE);
            changeButtonLabel();
        }
    }

    public void selectImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(ImageCaptureHelper.chooseActionDlgTitle);
        builder.setItems(ImageCaptureHelper.chooseActionDlOptions,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (ImageCaptureHelper.chooseActionDlOptions[item].equals(ImageCaptureHelper.chooseActionDlOptions[0])) {
                            imageCaptureHelper.setUserChoosenTask(ImageCaptureHelper.chooseActionDlOptions[0].toString());
                            boolean result = Utility.checkPermissionCamera(getActivity());
                            if (result) {
                                startActivityForResult(imageCaptureHelper.cameraIntent(), ImageCaptureHelper.REQUEST_CAMERA);
                            }
                        } else if (ImageCaptureHelper.chooseActionDlOptions[item].equals(ImageCaptureHelper.chooseActionDlOptions[1])) {
                            imageCaptureHelper.setUserChoosenTask(ImageCaptureHelper.chooseActionDlOptions[1].toString());
                            boolean result = Utility.checkPermission(getActivity());
                            if (result) {
                                startActivityForResult(Intent.createChooser(imageCaptureHelper.galleryIntent(),
                                        ImageCaptureHelper.CHOOSER_NAME),
                                        ImageCaptureHelper.SELECT_FILE);
                            }
                        } else if (ImageCaptureHelper.chooseActionDlOptions[item].equals(ImageCaptureHelper.chooseActionDlOptions[2])) {
                            dialog.dismiss();
                        }
                    }
                });
        builder.show();

    }

    private void changeButtonLabel() {
        buttonChangeCurrentPhoto.setText(getString(R.string.changeCurrentPhotoButton));
    }

    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.detailsHeading));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.detailsSubHeading));

        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.changeCurrentPhotoButton));

        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.raceTextView));

        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.raceListTextView));

        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.ethnicityListTextView));

        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.ethnicityListTextView));


        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.preferredLanguageTextView));

        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.preferredLanguageListTextView));
    }
}




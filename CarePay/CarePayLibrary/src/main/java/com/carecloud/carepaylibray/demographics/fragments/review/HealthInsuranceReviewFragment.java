package com.carecloud.carepaylibray.demographics.fragments.review;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.carecloud.carepaylibray.utils.PermissionsUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.Arrays;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

public class HealthInsuranceReviewFragment extends Fragment implements View.OnClickListener {



    Button buttonAddHealthInsuranceInfo;
    View view;



    String[] providerDataArray;
    String[] planDataArray;
    int selectedDataArray;
    TextView providerDataTextView, planDataTextView;



    private ImageCaptureHelper reviewInsuranceScanHelper;
    private ImageCaptureHelper reviewImageCaptureHelper;

    private Button reviewBtnScanInsurance;

    public static HealthInsuranceReviewFragment newInstance() {
        return new HealthInsuranceReviewFragment();
    }

    public HealthInsuranceReviewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_review_health_insurance, container, false);


        Toolbar toolbar = (Toolbar) view.findViewById(R.id.health_insurance_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.health_insurance_toolbar_title);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Intent intent = new Intent(getContext(), SigninSignupActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();*/
            }
        });

        initialiseUIFields();

        providerDataArray = getResources().getStringArray(R.array.providers);
        planDataArray = getResources().getStringArray(R.array.plans);

        setTypefaces(view);
        return view;
    }

    private void initialiseUIFields() {
        buttonAddHealthInsuranceInfo = (Button) view.findViewById(R.id.buttonAddHealthInsuranceInfo);
        buttonAddHealthInsuranceInfo.setOnClickListener(this);
        reviewBtnScanInsurance =(Button)view.findViewById(R.id.demogr_review_insurance_scan_insurance_btn);
        reviewBtnScanInsurance.setOnClickListener(this);

        ImageView imInsurance = (ImageView) view.findViewById(R.id.demogr_review_insurance_image);
        reviewInsuranceScanHelper = new ImageCaptureHelper(getActivity(), imInsurance);


        providerDataTextView = (TextView) view.findViewById(R.id.demogr_review_docs_provider);
        providerDataTextView.setOnClickListener(this);
        planDataTextView = (TextView) view.findViewById(R.id.demogr_review_docs_plan);
        planDataTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == buttonAddHealthInsuranceInfo) {
            openNewFragment();
        }else if(view == reviewBtnScanInsurance){

            selectImage(reviewInsuranceScanHelper);

        }else if (view == providerDataTextView) {
            selectedDataArray = 1;
            showAlertDialogWithListview(providerDataArray, "Select Race");
        } else if (view == planDataTextView) {
            selectedDataArray = 2;
            showAlertDialogWithListview(planDataArray, "Select Ethnicity");

        }
    }

    private void openNewFragment() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment fragment = ReviewFragment.newInstance();
        transaction.replace(R.id.root_layout, fragment, HealthInsuranceReviewFragment.class.getName());
        transaction.addToBackStack("HealthInsuranceReviewFragment -> ReviewFragment");
        transaction.commit();
    }


    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.ReviewInsuranceTitle));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewInsuranceSubTitle));

        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_review_insurance_scan_insurance_btn));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_review_insurance_number_label));

        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_review_docs_provider));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_review_docs_plan));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_review_docs_plan));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_review_docs_provider));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        String userChoosenTask = reviewImageCaptureHelper.getUserChoosenTask();
        switch (requestCode) {
            case PermissionsUtil.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals(ImageCaptureHelper.chooseActionDlOptions[1].toString()))
                        startActivityForResult(Intent.createChooser(reviewImageCaptureHelper.galleryIntent(),
                                ImageCaptureHelper.CHOOSER_NAME),
                                ImageCaptureHelper.SELECT_FILE);
                } else {
                    //code for deny

                }
                break;

            case PermissionsUtil.MY_PERMISSIONS_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals(ImageCaptureHelper.chooseActionDlOptions[0].toString()))
                        startActivityForResult(reviewImageCaptureHelper.cameraIntent(), ImageCaptureHelper.REQUEST_CAMERA);
                }
                }
        }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ImageCaptureHelper.SELECT_FILE) {
                reviewImageCaptureHelper.onSelectFromGalleryResult(data, ImageCaptureHelper.RECTANGULAR_IMAGE);
            } else if (requestCode == ImageCaptureHelper.REQUEST_CAMERA) {
                reviewImageCaptureHelper.onCaptureImageResult(data, ImageCaptureHelper.RECTANGULAR_IMAGE);
            }
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
                switch (selectedDataArray) {
                    case 1:
                        providerDataTextView.setText(providerDataArray[position]);
                        break;
                    case 2:
                        planDataTextView.setText(planDataArray[position]);
                        break;

                }
                alert.dismiss();
            }
        });
    }




    public void selectImage(final ImageCaptureHelper imageCaptureHelper) {
        reviewImageCaptureHelper = imageCaptureHelper;
        // create the chooser dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(ImageCaptureHelper.chooseActionDlgTitle);
        builder.setItems(ImageCaptureHelper.chooseActionDlOptions,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) { // "Take picture" chosen
                            imageCaptureHelper.setUserChoosenTask(ImageCaptureHelper.chooseActionDlOptions[0].toString());
                            boolean result = PermissionsUtil.checkPermissionCamera(getActivity());
                            if (result) {
                                startActivityForResult(imageCaptureHelper.cameraIntent(), ImageCaptureHelper.REQUEST_CAMERA);
                            }
                        } else if (item == 1) {  // "Select from Gallery" chosen
                            imageCaptureHelper.setUserChoosenTask(ImageCaptureHelper.chooseActionDlOptions[1].toString());
                            boolean result = PermissionsUtil.checkPermission(getActivity());
                            if (result) {
                                startActivityForResult(Intent.createChooser(imageCaptureHelper.galleryIntent(),
                                        ImageCaptureHelper.CHOOSER_NAME),
                                        ImageCaptureHelper.SELECT_FILE);
                            }
                        } else if (item == 3) { // "Cancel"
                            dialog.dismiss();
                        }
                    }
                });
        builder.show();
    }

    /**
     * Creates a generic dialog that contains a list of choices
     *
     * @param options              The choices
     * @param title                The dlg title
     * @param selectionDestination The textview where the selected option will be displayed
     */
    private void showChooseDialog(final String[] options, String title, final TextView selectionDestination) {
        final String cancelLabel = "Cancel";
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(title);
        // add cancel button
        dialog.setNegativeButton(cancelLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        // create dialog layout
        View customView = LayoutInflater.from(getActivity()).inflate(R.layout.alert_list_layout, null, false);
        ListView listView = (ListView) customView.findViewById(R.id.dialoglist);
        // create the adapter
        CustomAlertAdapter mAdapter = new CustomAlertAdapter(getActivity(), Arrays.asList(options));
        listView.setAdapter(mAdapter);
        // show the dialog
        dialog.setView(customView);
        final AlertDialog alert = dialog.create();
        alert.show();
        // set item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedOption = options[position];
                selectionDestination.setText(selectedOption);
                alert.dismiss();
            }
        });
    }


}

package com.carecloud.carepaylibray.fragments.demographics;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.ApplicationWorkflow;
import com.carecloud.carepaylibray.models.ScreenComponentModel;
import com.carecloud.carepaylibray.models.ScreenModel;
import com.carecloud.carepaylibray.util.PhotoDataUtil;
import com.carecloud.carepaylibray.util.Utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DocumentsFragment extends Fragment {


    private ImageView scanDriverLicenceImage;
    private ImageView scanInsuranceImage;
    private int driversLicence = R.id.cameraImage;
    private int insuranceImage = R.id.insuranceImage;
    private int driveLicenceButton = R.id.driverLicenceButton;
    private int insuranceButton = R.id.insuranceButton;
    private String userChosenTask;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ScrollView scrollView = new ScrollView(getActivity());
        ViewGroup.LayoutParams scrollViewLayoutparams = new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        scrollView.setLayoutParams(scrollViewLayoutparams);

        LinearLayout.LayoutParams matchWidthParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout parent = new LinearLayout(getActivity());
        parent.setLayoutParams(matchWidthParams);
        parent.setOrientation(LinearLayout.VERTICAL);
        //parent.setPadding(20, 10, 20, 10);
        scrollView.addView(parent);


        LinearLayout insuranceCardInfoTextLayout = new LinearLayout(getActivity());
        LinearLayout.LayoutParams insuranceCardInfoTextLayoutLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        insuranceCardInfoTextLayout.setOrientation(LinearLayout.VERTICAL);
        insuranceCardInfoTextLayoutLayoutParams.setMargins(0, 0, 0, 60);
        insuranceCardInfoTextLayout.setLayoutParams(insuranceCardInfoTextLayoutLayoutParams);


        LinearLayout scanInsuranceCardLayout = new LinearLayout(getActivity());


        ScreenModel screenModel = ApplicationWorkflow.Instance().getDemographicsDocumentsScreenModel();

        getActivity().setTitle(screenModel.getName());
        int index = 0;
        //Top Logo Image
        for (ScreenComponentModel componentModel : screenModel.getComponentModels()) {
            if (componentModel.getType().equals("image")) {
                ImageView mIVCard = new ImageView(getActivity());
                LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                childLayoutParams.setMargins(0, 0, 0, 20);
                mIVCard.setLayoutParams(childLayoutParams);
                mIVCard.setImageResource(R.drawable.icn_signup);
                mIVCard.setScaleType(ImageView.ScaleType.FIT_CENTER);
                mIVCard.setLayoutParams(matchWidthParams);
                parent.addView(mIVCard);
                // Documents Heading
            } else if (componentModel.getType().equals("heading")) {
                TextView titleText = new TextView(getActivity());
                titleText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                titleText.setText(componentModel.getLabel());
                titleText.setGravity(Gravity.CENTER);
                titleText.setTextSize(21.0f);
                titleText.setTextColor(ContextCompat.getColor(getActivity(), R.color.charcoal));
                parent.addView(titleText);

            }
            // Documents Subheading

            else if (componentModel.getType().equals("subHeading")) {
                TextView titleText = new TextView(getActivity());
                LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                childLayoutParams.setMargins(0, 0, 0, 60);
                titleText.setLayoutParams(childLayoutParams);
                titleText.setText(componentModel.getLabel());
                titleText.setGravity(Gravity.CENTER);
                titleText.setTextSize(13.0f);
                titleText.setTextColor(ContextCompat.getColor(getActivity(), R.color.cadet_gray));
                parent.addView(titleText);

            }
            // Scan Driver's License button and Imageview
            else if (componentModel.getType().equals("buttonWithImage")) {
                LinearLayout childLayout = new LinearLayout(getActivity());
                getChildLayout(childLayout);
                ImageView iv =  getCamera();
                iv.setId(driversLicence);
                childLayout.addView(iv);
                childLayout.addView(getButton(componentModel,driveLicenceButton));
                parent.addView(childLayout);
            }
            // Scan Insurance Card button and Imageview hide

            else if (componentModel.getType().equals("scanButton")) {
                getChildLayout(scanInsuranceCardLayout);
                ImageView iv = getCamera();
                iv.setId(insuranceImage);
                scanInsuranceCardLayout.addView(iv);
                scanInsuranceCardLayout.addView(getButton(componentModel, insuranceButton));
                scanInsuranceCardLayout.setVisibility(View.GONE);
                parent.addView(scanInsuranceCardLayout);
            }
            // Driver's License Number

            else if (componentModel.getType().equals("titleText")) {
                TextView titleText = new TextView(getActivity());
                LinearLayout.LayoutParams titleTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                titleTextParams.gravity = Gravity.LEFT;
                titleTextParams.setMargins(20, 0, 0, 0);
                titleText.setLayoutParams(titleTextParams);
                titleText.setText(componentModel.getLabel());
                titleText.setTextSize(17.0f);
                titleText.setTextColor(ContextCompat.getColor(getActivity(), R.color.light_gray));
                parent.addView(titleText);

            }
            // Driver's License State

            else if (componentModel.getType().equals("selector")) {
                LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout childLayout = new LinearLayout(getActivity());
                childLayout.setWeightSum(2.0f);
                childLayoutParams.setMargins(0, 0, 0, 60);
                childLayout.setLayoutParams(childLayoutParams);
                childLayout.setOrientation(LinearLayout.HORIZONTAL);
                childLayout.setPadding(0, 20, 0, 0);
                TextView inputText = new TextView(getActivity());
                LinearLayout.LayoutParams inputTextLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                inputText.setLayoutParams(inputTextLayoutParams);
                //inputTextLayoutParams.setMargins(20, 0, 0, 0);
                inputTextLayoutParams.gravity = Gravity.LEFT;
                inputTextLayoutParams.weight = 1.3f;
                inputText.setText(componentModel.getLabel());
                inputText.setTextSize(17.0f);
                inputText.setTextColor(ContextCompat.getColor(getActivity(), R.color.charcoal));


                TextView selectText = new TextView(getActivity());
                LinearLayout.LayoutParams selectTextLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                selectText.setLayoutParams(inputTextLayoutParams);
                //selectTextLayoutParams.setMargins(20, 0, 0, 0);
                selectText.setText(R.string.select);
                selectText.setGravity(Gravity.CENTER_HORIZONTAL);
                selectText.setTextSize(14.0f);
                selectText.setTextColor(ContextCompat.getColor(getActivity(), R.color.lightSlateGray));
                selectText.setClickable(true);
                selectText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), "Select State", Toast.LENGTH_SHORT).show();
                    }
                });


                childLayout.addView(inputText);
                childLayout.addView(selectText);

                parent.addView(childLayout);
            }

            // Patient has Insurance yes or No
            else if (componentModel.getType().equals("togglebutton")) {
                LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout childLayout = new LinearLayout(getActivity());
                //childLayout.setWeightSum(2.0f);
                childLayoutParams.setMargins(0, 0, 0, 60);
                childLayout.setLayoutParams(childLayoutParams);
                childLayout.setOrientation(LinearLayout.HORIZONTAL);
                childLayout.setPadding(0, 20, 0, 0);
                TextView inputText = new TextView(getActivity());
                LinearLayout.LayoutParams inputTextLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                inputText.setLayoutParams(inputTextLayoutParams);
                //inputTextLayoutParams.setMargins(20, 0, 0, 0);
                inputTextLayoutParams.gravity = Gravity.LEFT;
                inputTextLayoutParams.weight = 1.1f;
                inputText.setText(componentModel.getLabel());
                inputText.setTextSize(17.0f);
                inputText.setTextColor(ContextCompat.getColor(getActivity(), R.color.charcoal));


                Switch switchView = new Switch(getActivity());
                LinearLayout.LayoutParams switchViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                //switchView.setLayoutParams(inputTextLayoutParams);
                switchViewLayoutParams.setMargins(20, 0, 0, 0);

                childLayout.addView(inputText);
                childLayout.addView(switchView);

                final LinearLayout InsuranceCardInfoTextLayout = insuranceCardInfoTextLayout;
                final LinearLayout scanInsuranceCardButtonLayout = scanInsuranceCardLayout;
                switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                        if (checked) {
                            InsuranceCardInfoTextLayout.setVisibility(View.VISIBLE);
                            scanInsuranceCardButtonLayout.setVisibility(View.VISIBLE);
                        } else {
                            InsuranceCardInfoTextLayout.setVisibility(View.GONE);
                            scanInsuranceCardButtonLayout.setVisibility(View.GONE);
                        }
                    }
                });

                parent.addView(childLayout);
            }

            //Insurance Card Info show and hide

            else if (componentModel.getType().equals("inputtext")) {
                TextView insuranceCardInfoText = new TextView(getActivity());
                LinearLayout.LayoutParams inputTextLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
                inputTextLayoutParams.setMargins(20,0,0,0);
                insuranceCardInfoText.setLayoutParams(inputTextLayoutParams);
                inputTextLayoutParams.gravity = Gravity.LEFT;
                insuranceCardInfoText.setText(componentModel.getLabel());
                insuranceCardInfoText.setTextSize(17.0f);
                insuranceCardInfoText.setTextColor(ContextCompat.getColor(getActivity(), R.color.light_gray));
                insuranceCardInfoTextLayout.setVisibility(View.GONE);

                if (insuranceCardInfoTextLayout.getChildCount() != 0) {
                    insuranceCardInfoTextLayout.removeView(insuranceCardInfoText);
                    insuranceCardInfoTextLayout.addView(insuranceCardInfoText);
                } else {
                    insuranceCardInfoTextLayout.addView(insuranceCardInfoText);
                }

                if (parent.getChildCount() != 0) {
                    parent.removeView(insuranceCardInfoTextLayout);
                    parent.addView(insuranceCardInfoTextLayout);
                } else {
                    parent.addView(insuranceCardInfoTextLayout);
                }

            }

            //Go to Next

            else if (componentModel.getType().equals("button")) {
                Button button = new Button(getActivity());
                LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                button.setLayoutParams(buttonParams);
                buttonParams.setMargins(0, 0, 0, 60);
                buttonParams.gravity = Gravity.CENTER;
                button.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_next));
                button.setText(componentModel.getLabel());
                button.setTextSize(13.0f);
                button.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
                button.setGravity(Gravity.CENTER);
                button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), "GoTo Next Screen", Toast.LENGTH_SHORT).show();
                    }
                });
                parent.addView(button);

            }
        }

        return scrollView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scanDriverLicenceImage = (ImageView) view.findViewById(R.id.cameraImage);
        scanInsuranceImage = (ImageView) view.findViewById(R.id.insuranceImage);

    }

    private void getChildLayout(LinearLayout childLayout) {
        LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        childLayoutParams.setMargins(0, 0, 0, 60);
        childLayout.setLayoutParams(childLayoutParams);
        childLayout.setOrientation(LinearLayout.HORIZONTAL);
        childLayout.setPadding(0, 20, 0, 0);
    }

    @NonNull
    private Button getButton(ScreenComponentModel componentModel, int id) {
        Button button = new Button(getActivity());
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
        button.setLayoutParams(buttonParams);
        buttonParams.setMargins(20, 0, 0, 0);
        buttonParams.gravity = Gravity.CENTER;
        button.setId(id);
        button.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_background));
        button.setTextSize(13.0f);
        button.setTextColor(ContextCompat.getColor(getActivity(), R.color.rich_electric_blue));
        button.setGravity(Gravity.CENTER);
        button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        String photoImagePath = getDriversLicenceImagePath();
        if (photoImagePath != null) {
            button.setText("RE-SCAN");
        } else {
            button.setText(componentModel.getLabel());

        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == driveLicenceButton){
                    PhotoDataUtil.getInstance().setDriveLicense(true);
                }else{
                    PhotoDataUtil.getInstance().setDriveLicense(false);
                }
                selectImage();

            }
        });
        return button;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(getActivity());

                if (items[item].equals("Take Photo")) {
                    userChosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void galleryIntent() {
        PhotoDataUtil.getInstance().setFromGallery(true);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        PhotoDataUtil.getInstance().setFromGallery(false);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);

    }



    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        String path;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            if(PhotoDataUtil.getInstance().isDriveLicense()) {
                PhotoDataUtil.getInstance().setDriversLicenceImagePath(destination.getAbsolutePath());
            }else{
                PhotoDataUtil.getInstance().setInsuranceImagePath(destination.getAbsolutePath());
            }
            Toast.makeText(getActivity(),"Photo Downloaded",Toast.LENGTH_LONG).show();
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        displayImage();
    }




    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                if(PhotoDataUtil.getInstance().isDriveLicense()) {
                    PhotoDataUtil.getInstance().setDriversLicenceBitmap(bm);
                }else{
                    PhotoDataUtil.getInstance().setInsuranceBitmap(bm);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        displayImage();
    }

    private void displayImage() {
        loadImage(scanDriverLicenceImage, getDriversLicenceBitmap(), getDriversLicenceImagePath());
        loadImage(scanInsuranceImage, getInsuranceBitmap(), getInsuranceImagePath());

    }

    private String getInsuranceImagePath() {
        return PhotoDataUtil.getInstance().getInsuranceImagePath();
    }

    private Bitmap getInsuranceBitmap() {
        return PhotoDataUtil.getInstance().getInsuranceBitmap();
    }

    private String getDriversLicenceImagePath() {
        return PhotoDataUtil.getInstance().getDriversLicenceImagePath();
    }

    private Bitmap getDriversLicenceBitmap() {
        return PhotoDataUtil.getInstance().getDriversLicenceBitmap();
    }

    @Override
    public void onResume() {
        super.onResume();
        displayImage();
    }


    @NonNull
    private ImageView getCamera() {
        ImageView cameraImage = new ImageView(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 20, 0);
        cameraImage.setLayoutParams(params);
        cameraImage.setImageResource(R.drawable.icn_camera);
        return cameraImage;
    }

    private void loadImage(final ImageView camera,Bitmap bm, String photoImagePath) {
        if (PhotoDataUtil.getInstance().isFromGallery()) {
            if(bm != null){
                camera.setImageBitmap(Bitmap.createScaledBitmap(bm, 120, 200, false));
            }

        } else{
            if (photoImagePath != null) {
                getBitmap(camera, photoImagePath);
            } else {
                camera.setImageResource(R.drawable.icn_camera);
            }
        }
    }

    private void getBitmap(ImageView camera, String photoImagePath) {
        Bitmap photoBitmap = BitmapFactory.decodeFile(photoImagePath);
        camera.setImageBitmap(Bitmap.createScaledBitmap(photoBitmap, 120, 200, false));
    }

}
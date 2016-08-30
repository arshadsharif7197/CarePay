package com.carecloud.carepaylibray.fragments.demographics;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.carecloud.carepaylibray.util.Utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DetailsFragment extends Fragment {
    ScreenModel getDemographicsDetailsScreenModel =new ScreenModel();
    ArrayList<ScreenComponentModel> componentModels= new ArrayList<ScreenComponentModel>();
    List<String> raceData = new ArrayList<String>();
    List<String> ethnicityData = new ArrayList<String>();
    List<String> languageData = new ArrayList<String>();
    LinearLayout parent;
    ScrollView scrollView;
    String selectedRace =null;

    private String userChoosenTask;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private ImageView iv;
    public static final DetailsFragment newInstance(Bundle bundle) {
        DetailsFragment fragment = new DetailsFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("DetailsFragment");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Scan Documents");


        scrollView = new ScrollView(getActivity());
        ViewGroup.LayoutParams scrollViewLayoutparams = new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        scrollView.setLayoutParams(scrollViewLayoutparams);

        LinearLayout.LayoutParams matchWidthParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        parent = new LinearLayout(getActivity());
        parent.setLayoutParams(matchWidthParams);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setPadding(20, 10, 20, 10);
        scrollView.addView(parent);


        ScreenModel screenModel = ApplicationWorkflow.Instance().getDemographicsDetailsScreenModel();

        ethnicityData =ApplicationWorkflow.Instance().getethincityDataModel();
        languageData=ApplicationWorkflow.Instance().getlanguageDataModel();

        getActivity().setTitle(screenModel.getName());
        int index = 0;
        for (ScreenComponentModel componentModel : screenModel.getComponentModels()) {
            if (componentModel.getType().equals("image") || componentModel.getType().equals("ImageView")) {
                ImageView mIVCard = new ImageView(getActivity());
                LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                childLayoutParams.setMargins(33, 90, 57, 14);
                mIVCard.setLayoutParams(childLayoutParams);
                mIVCard.setImageResource(R.drawable.icn_signup_illustration_step_2_details);
                mIVCard.setScaleType(ImageView.ScaleType.FIT_CENTER);
                mIVCard.setLayoutParams(childLayoutParams);
                parent.addView(mIVCard);
                index++;

            } else if (componentModel.getType().equals("heading")) {
                TextView titleText = new TextView(getActivity());
                titleText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                titleText.setText(componentModel.getLabel());
                titleText.setGravity(Gravity.CENTER);
                titleText.setTextSize(21.0f);
                titleText.setTextColor(ContextCompat.getColor(getActivity(), R.color.charcoal));
                parent.addView(titleText);
                index++;

            } else if (componentModel.getType().equals("subHeading")) {
                TextView titleText = new TextView(getActivity());
                LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                childLayoutParams.setMargins(0, 0, 0, 60);
                titleText.setLayoutParams(childLayoutParams);
                titleText.setText(componentModel.getLabel());
                titleText.setGravity(Gravity.CENTER);
                titleText.setTextSize(13.0f);
                titleText.setTextColor(ContextCompat.getColor(getActivity(), R.color.cadet_gray));
                parent.addView(titleText);
                index++;

            } else if (componentModel.getType().equals("buttonWithImage")) {
                LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout childLayout = new LinearLayout(getActivity());
                childLayoutParams.setMargins(0, 0, 0, 60);
                childLayout.setLayoutParams(childLayoutParams);
                childLayout.setOrientation(LinearLayout.HORIZONTAL);
                childLayout.setTag(componentModel.getType());
                childLayout.setPadding(0, 20, 0, 0);
                iv = new ImageView(getActivity());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 20, 0);
                iv.setLayoutParams(params);
                iv.setImageResource(R.drawable.icn_placeholder_user_profile);

                Button button = new Button(getActivity());
                LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
                button.setLayoutParams(buttonParams);
                buttonParams.setMargins(20, 0, 0, 0);
                buttonParams.gravity = Gravity.CENTER;
                button.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_background));
                button.setText(componentModel.getLabel());
                button.setTextSize(13.0f);
                button.setTextColor(ContextCompat.getColor(getActivity(), R.color.rich_electric_blue));
                button.setGravity(Gravity.CENTER);
                button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                button.setClickable(true);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), "Scan Successful", Toast.LENGTH_LONG).show();
                    }

                });
                button.setClickable(false);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectImage();
                        Toast.makeText(getActivity(), "ReScan", Toast.LENGTH_LONG).show();
                    }

                });

                //  button.setPadding(10,10,10,10);
                childLayout.addView(iv);
                childLayout.addView(button);
                parent.addView(childLayout);
                index++;
            }else if (componentModel.getType().equals("titleText")) {
                TextView titleText = new TextView(getActivity());
                LinearLayout.LayoutParams titleTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                titleTextParams.gravity = Gravity.LEFT;
                titleTextParams.setMargins(0, 0, 0, 60);
                titleText.setLayoutParams(titleTextParams);
                titleText.setText(componentModel.getLabel());
                titleText.setTextSize(17.0f);
                titleText.setTextColor(ContextCompat.getColor(getActivity(), R.color.light_gray));
                parent.addView(titleText);
                index++;

            } else if (componentModel.getType().equals("selector")) {

                LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout childLayout = new LinearLayout(getActivity());
                childLayout.setWeightSum(2.0f);
                childLayoutParams.setMargins(17, 20, 0, 17);
                childLayout.setLayoutParams(childLayoutParams);
                childLayout.setOrientation(LinearLayout.HORIZONTAL);
                childLayout.setTag(componentModel.getType());
                childLayout.setPadding(0, 20, 0, 0);
                TextView inputText = new TextView(getActivity());
                LinearLayout.LayoutParams inputTextLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                inputText.setLayoutParams(inputTextLayoutParams);
                inputTextLayoutParams.setMargins(20, 0, 0, 0);
                inputTextLayoutParams.gravity = Gravity.LEFT;
                inputTextLayoutParams.weight = 1.3f;
                inputText.setText(componentModel.getLabel());
                inputText.setTextSize(17.0f);
                inputText.setTag("Select"+componentModel.getLabel());
                inputText.setTextColor(ContextCompat.getColor(getActivity(), R.color.charcoal));

                TextView selectText = new TextView(getActivity());
                LinearLayout.LayoutParams selectTextLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                selectText.setLayoutParams(inputTextLayoutParams);
                selectTextLayoutParams.setMargins(20, 0, 0, 0);
                selectText.setText("Select");
                selectText.setTag(componentModel.getLabel());
                selectText.setGravity(Gravity.CENTER_HORIZONTAL);
                selectText.setTextSize(14.0f);
                selectText.setTextColor(ContextCompat.getColor(getActivity(), R.color.bright_cerulean));
                selectText.setClickable(true);
                selectText.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showAlertDialogWithListview( String.valueOf(((TextView) view).getTag()));
//                                Toast.makeText(getActivity(), "Select State", Toast.LENGTH_LONG).show();
                            }
                        }

                );


                childLayout.addView(inputText);
                childLayout.addView(selectText);

                parent.addView(childLayout);
                index++;
            } else if (componentModel.getType().equals("togglebutton")) {
                LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout childLayout = new LinearLayout(getActivity());
                childLayout.setWeightSum(2.0f);
                childLayoutParams.setMargins(0, 0, 0, 60);
                childLayout.setLayoutParams(childLayoutParams);
                childLayout.setOrientation(LinearLayout.HORIZONTAL);
                childLayout.setPadding(0, 20, 0, 0);
                final TextView inputText = new TextView(getActivity());
                final LinearLayout.LayoutParams inputTextLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                inputText.setLayoutParams(inputTextLayoutParams);
                inputTextLayoutParams.setMargins(20, 0, 0, 0);
                inputTextLayoutParams.gravity = Gravity.LEFT;
                inputTextLayoutParams.weight = 1.2f;
                inputText.setText(componentModel.getLabel());
                inputText.setTextSize(17.0f);
                inputText.setTextColor(ContextCompat.getColor(getActivity(), R.color.charcoal));


                Switch switchView = new Switch(getActivity());
                LinearLayout.LayoutParams switchViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                switchView.setLayoutParams(inputTextLayoutParams);
                switchViewLayoutParams.setMargins(20, 0, 0, 0);
                childLayout.addView(inputText);
                childLayout.addView(switchView);
                parent.addView(childLayout);
                index++;
            } else if (componentModel.getType().equals("inputtext")) {

                EditText inputText = new EditText(getActivity());
                LinearLayout.LayoutParams inputTextLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
                inputText.setLayoutParams(inputTextLayoutParams);
                inputTextLayoutParams.setMargins(0, 0, 0, 60);
                inputText.setHint(componentModel.getLabel());
                inputTextLayoutParams.gravity = Gravity.LEFT;
                // inputText.setText(componentModel.getLabel());
                inputText.setTextSize(17.0f);
                inputText.setTextColor(ContextCompat.getColor(getActivity(), R.color.light_gray));

                parent.addView(inputText);
                index++;
            } else if (componentModel.getType().equals("button")) {

                Button button = new Button(getActivity());
                LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                button.setLayoutParams(buttonParams);
                buttonParams.setMargins(17, 56, 17, 14);
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
                        Toast.makeText(getActivity(), "Go TO Next Screen", Toast.LENGTH_LONG).show();
                    }
                });
                //  button.setPadding(10,10,10,10);

                parent.addView(button);
                index++;
            }

        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                int x = iv.getWidth() - iv.getPaddingLeft() - iv.getPaddingRight();
                imgWidth = x == 0 ? imgWidth : x;
                Log.e("ssssHandler imgWidth", imgWidth + "");
            }
        });

        return scrollView;
    }

    private int imgWidth = 0;


    public void showAlertDialogWithListview(final String type )  {
        if(type.equals("Race")){
            raceData=ApplicationWorkflow.Instance().getRaceDataModel();}
        else if(type.equals("Ethnicity")){
            raceData=ApplicationWorkflow.Instance().getethincityDataModel();}
        else if(type.equals("Preferred Language")){
            raceData=ApplicationWorkflow.Instance().getlanguageDataModel();}


        //Create sequence of items
        final CharSequence[] raceDataArray = raceData.toArray(new String[raceData.size()]);
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(type);
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogBuilder.setItems(raceDataArray, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                selectedRace = raceDataArray[item].toString();  //Selected item in listview
//                Toast.makeText(getActivity(), selectedRace,Toast.LENGTH_SHORT).show();

                int count = parent.getChildCount();
                View v = null;
                for(int i=0; i<count; i++) {
                    v = parent.getChildAt(i);
                    if(v instanceof LinearLayout && v.getTag().equals("selector")){
                        View innerView=null;
                        int innerCount=((LinearLayout) v).getChildCount();
                        for(int innerI=0;innerI<innerCount;innerI++){
                            innerView = ((LinearLayout) v).getChildAt(innerI);
                            if(innerView.getTag().equals(type) && innerView instanceof TextView){
                                ((TextView) innerView).setText(selectedRace);
                            }
                        }
                    }
                }
            }
        });
        //Create alert dialog object via builder
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;

            case Utility.MY_PERMISSIONS_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();

                } else {
                    //code for deny
                }
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {


                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    boolean result = Utility.checkPermissionCamera(getActivity());
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    boolean result = Utility.checkPermission(getActivity());
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
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

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("imgWidth",""+imgWidth);
        Bitmap roundBitmap = Utility.getRoundedCroppedBitmap(Bitmap.createScaledBitmap(thumbnail, imgWidth, imgWidth, true), imgWidth);
        iv.setImageBitmap(roundBitmap);
//        iv.setImageBitmap(thumbnail);
    }


    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

//        Bitmap bm = null;
//        if (data != null) {
//            try {
//                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        iv.setImageBitmap(bm);
        try {
            Bitmap thumbnail = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

            File destination = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + ".jpg");

            FileOutputStream fo;

            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();

            Log.e("imgWidth",""+imgWidth);
            Bitmap roundBitmap = Utility.getRoundedCroppedBitmap(Bitmap.createScaledBitmap(thumbnail, imgWidth, imgWidth, true), imgWidth);
            iv.setImageBitmap(roundBitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}






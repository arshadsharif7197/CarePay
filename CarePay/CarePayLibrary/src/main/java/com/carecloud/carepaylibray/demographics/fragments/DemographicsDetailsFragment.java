package com.carecloud.carepaylibray.demographics.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
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
import com.carecloud.carepaylibray.utils.Utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;


/**
 * Created by lsoco_user on 9/2/2016.
 */
public class DemographicsDetailsFragment extends Fragment implements View.OnClickListener {

    View     view;
    String[] raceArray;
    String[] ethnicityArray;
    String[] preferredLanguageArray;
    int      selectedArray;
    TextView raceTextView, ethnicityTextView, preferredLanguageTextView;
    Button    buttonChangeCurrentPhoto;
    private ImageView imageViewDetailsImage;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private CameraScannerHelper mCameraScannerHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_details, container, false);

        initialiseUIFields();
        raceArray = getResources().getStringArray(R.array.Race);
        ethnicityArray = getResources().getStringArray(R.array.Ethnicity);
        preferredLanguageArray = getResources().getStringArray(R.array.Language);

//        int x = imageViewDetailsImage.getWidth()
//                - imageViewDetailsImage.getPaddingLeft()
//                - imageViewDetailsImage.getPaddingRight();
//        if(x == 0) {
//            Log.e("onCreateView() x == ", "" + x);
//        }
//        int imgWidth = (x == 0 ? 129 : x);

        mCameraScannerHelper = new CameraScannerHelper(getActivity());
        mCameraScannerHelper.setImageViewDetailsProfileImage(imageViewDetailsImage);
        mCameraScannerHelper.setImgWidth(129); // TODO: 9/9/2016 create dimen


//        new Handler().post(new Runnable() {
//            @Override
//            public void run() {
//                int x = imageViewDetailsProfileImage.getWidth() - imageViewDetailsProfileImage.getPaddingLeft() - imageViewDetailsProfileImage.getPaddingRight();
//                imgWidth = x == 0 ? imgWidth : x;
//                Log.e("ssssHandler imgWidth", imgWidth + "");
//            }
//        });

        return view;
    }

    private void initialiseUIFields() {
        raceTextView = (TextView) view.findViewById(R.id.raceListTextView);
        raceTextView.setOnClickListener(this);
        ethnicityTextView = (TextView) view.findViewById(R.id.ethnicityListTextView);
        ethnicityTextView.setOnClickListener(this);
        preferredLanguageTextView = (TextView) view.findViewById(R.id.preferredLanguageListTextView);
        preferredLanguageTextView.setOnClickListener(this);
        buttonChangeCurrentPhoto = (Button) view.findViewById(R.id.changeCurrentPhoto);
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
            mCameraScannerHelper.selectImage();
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
        String userChoosenTask = mCameraScannerHelper.getUserChoosenTask();

        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Choose from Library"))
                        mCameraScannerHelper.galleryIntent();
                } else {
                    //code for deny
                }
                break;

            case Utility.MY_PERMISSIONS_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        mCameraScannerHelper.cameraIntent();

                } else {
                    //code for deny
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                mCameraScannerHelper.onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                mCameraScannerHelper.onCaptureImageResult(data);
        }
    }

    /**
     * Helper for scan with camera functionality
     */
    public class CameraScannerHelper {
        private String userChoosenTask;
        private ImageView imageViewDetailsProfileImage;
        private int imgWidth;
        private Activity mContext;

        public CameraScannerHelper(Activity activity) {
            mContext = activity;
        }

        public int getImgWidth() {
            return imgWidth;
        }

        public void setImgWidth(int imgWidth) {
            this.imgWidth = imgWidth;
        }

        public ImageView getImageViewDetailsProfileImage() {
            return imageViewDetailsProfileImage;
        }

        public void setImageViewDetailsProfileImage(ImageView imageViewDetailsProfileImage) {
            this.imageViewDetailsProfileImage = imageViewDetailsProfileImage;
        }

        public String getUserChoosenTask() {
            return userChoosenTask;
        }

        public void setUserChoosenTask(String userChoosenTask) {
            this.userChoosenTask = userChoosenTask;
        }

        public void selectImage() {
            final CharSequence[] items = {"Take Photo", "Choose from Library",
                    "Cancel"};

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
            builder.setTitle("Add Photo!");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (items[item].equals("Take Photo")) {
                        setUserChoosenTask("Take Photo");
                        boolean result = Utility.checkPermissionCamera(mContext);
                        if (result)
                            cameraIntent();

                    } else if (items[item].equals("Choose from Library")) {
                        setUserChoosenTask("Choose from Library");
                        boolean result = Utility.checkPermission(mContext);
                        if (result)
                            galleryIntent();

                    } else if (items[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        }

        public void galleryIntent() {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
        }

        public void cameraIntent() {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA);
        }


        public void onCaptureImageResult(Intent data) {
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
            Log.e("imgWidth", "" + imgWidth);
            Bitmap roundBitmap = Utility.getRoundedCroppedBitmap(Bitmap.createScaledBitmap(thumbnail, imgWidth, imgWidth, true), imgWidth);
            imageViewDetailsProfileImage.setImageBitmap(roundBitmap);
//        iv.setImageBitmap(thumbnail);
        }


        @SuppressWarnings("deprecation")
        public void onSelectFromGalleryResult(Intent data) {
            try {
                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), data.getData());
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                File destination = new File(Environment.getExternalStorageDirectory(),
                                            System.currentTimeMillis() + ".jpg");

                FileOutputStream fo;

                destination.createNewFile();
                fo = new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
                fo.close();

                Log.e("imgWidth", "" + imgWidth);
                Bitmap roundBitmap = Utility.getRoundedCroppedBitmap(Bitmap.createScaledBitmap(thumbnail, imgWidth, imgWidth, true), imgWidth);
                imageViewDetailsProfileImage.setImageBitmap(roundBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
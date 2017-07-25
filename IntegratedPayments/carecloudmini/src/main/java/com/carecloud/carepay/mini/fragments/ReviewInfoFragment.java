package com.carecloud.carepay.mini.fragments;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.mini.R;
import com.carecloud.carepay.mini.models.response.LocationsDTO;
import com.carecloud.carepay.mini.models.response.UserPracticeDTO;
import com.carecloud.carepay.mini.utils.ApplicationPreferences;
import com.carecloud.carepay.mini.utils.Defs;
import com.carecloud.carepay.mini.utils.StringUtil;
import com.carecloud.carepay.mini.views.CustomErrorToast;
import com.carecloud.shamrocksdk.registrations.DeviceRegistration;
import com.carecloud.shamrocksdk.registrations.interfaces.RegistrationCallback;
import com.carecloud.shamrocksdk.registrations.models.Device;
import com.carecloud.shamrocksdk.registrations.models.DeviceGroup;
import com.carecloud.shamrocksdk.registrations.models.Registration;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by lmenendez on 7/11/17
 */

public class ReviewInfoFragment extends RegistrationFragment {

    private TextView deviceName;
    private TextView locationName;
    private TextView practiceName;
    private TextView practiceInitials;
    private ImageView practiceLogo;
    private View buttonRegisterDevice;

    private View practiceInitialsLayout;
    private View carecloudLogoLayout;
    private View practiceLogoLayout;

    private UserPracticeDTO selectedPractice;
    private @Defs.ImageStyles int selectedImageStyle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_review_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        initProgressToolbar(view, getString(R.string.registration_review_title), 6);

        deviceName = (TextView) view.findViewById(R.id.device_name);
        locationName = (TextView) view.findViewById(R.id.location_name);
        practiceName = (TextView) view.findViewById(R.id.practice_name);
        practiceInitials = (TextView) view.findViewById(R.id.practice_initials_name);
        practiceLogo = (ImageView) view.findViewById(R.id.practice_logo);

        practiceInitialsLayout = view.findViewById(R.id.practice_initials_layout);
        carecloudLogoLayout = view.findViewById(R.id.carecloud_logo_layout);
        practiceLogoLayout = view.findViewById(R.id.practice_logo_layout);

        View buttonEditName = view.findViewById(R.id.button_edit_name);
        buttonEditName.setOnClickListener(clickListener);

        View buttonEditLocation = view.findViewById(R.id.button_edit_location);
        buttonEditLocation.setOnClickListener(clickListener);

        View buttonEditImage = view.findViewById(R.id.button_edit_image);
        buttonEditImage.setOnClickListener(clickListener);

        View buttonEditPractice = view.findViewById(R.id.button_edit_practice);
        buttonEditPractice.setOnClickListener(clickListener);

        if(!canPopToFragment(PracticesFragment.class.getName())){
            buttonEditPractice.setVisibility(View.GONE);
        }

        buttonRegisterDevice = view.findViewById(R.id.button_register);
        buttonRegisterDevice.setOnClickListener(clickListener);

        View backButton = view.findViewById(R.id.button_back);
        backButton.setOnClickListener(clickListener);

        setupPracticeInfo();

    }

    private void setupPracticeInfo(){
        if(callback.getRegistrationDataModel() != null) {
            selectedPractice = callback.getRegistrationDataModel().getPayloadDTO().getUserPractices().get(0);
        }else {
            String selectedPracticeId = getApplicationHelper().getApplicationPreferences().getPracticeId();
            selectedPractice = callback.getPreRegisterDataModel().getPracticeById(selectedPracticeId);
        }

        practiceName.setText(selectedPractice.getPracticeName());

        String locationId = getApplicationHelper().getApplicationPreferences().getLocationId();
        locationName.setText(getLocationNameById(locationId));

        deviceName.setText(getApplicationHelper().getApplicationPreferences().getDeviceName());

        practiceInitialsLayout.setVisibility(View.GONE);
        practiceLogoLayout.setVisibility(View.GONE);
        carecloudLogoLayout.setVisibility(View.GONE);

        selectedImageStyle = getApplicationHelper().getApplicationPreferences().getImageStyle();
        switch (selectedImageStyle) {
            case Defs.IMAGE_STYLE_PRACTICE_INITIALS: {
                practiceInitialsLayout.setVisibility(View.VISIBLE);

                if (selectedPractice.getPracticeInitials() != null) {
                    practiceInitials.setText(selectedPractice.getPracticeInitials());
                } else {
                    practiceInitials.setText(StringUtil.getShortName(selectedPractice.getPracticeName()));
                }
                break;
            }
            case Defs.IMAGE_STYLE_PRACTICE_LOGO: {
                practiceLogoLayout.setVisibility(View.VISIBLE);
                String imageUrl = selectedPractice.getPracticePhoto();
                Picasso.with(getContext())
                        .load(imageUrl)
                        .resize(400, 400)
                        .centerCrop()
                        .transform(new CropCircleTransformation())
                        .placeholder(R.drawable.practice_no_image_bkg)
                        .into(practiceLogo);
                break;
            }
            case Defs.IMAGE_STYLE_CARECLOUD_LOGO:
            default:{
                carecloudLogoLayout.setVisibility(View.VISIBLE);
                break;
            }
        }

    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.button_edit_image:
                    popToFragment(ImageSelectFragment.class.getName());
                    break;
                case R.id.button_edit_location:
                    popToFragment(LocationsFragment.class.getName());
                    break;
                case R.id.button_edit_name:
                    popToFragment(DeviceFragment.class.getName());
                    break;
                case R.id.button_edit_practice:
                    popToFragment(PracticesFragment.class.getName());
                    break;
                case R.id.button_register:
                    registerDevice();
                    break;
                case R.id.button_back:
                default:
                    callback.onBackPressed();
                    break;
            }
        }
    };

    private boolean canPopToFragment(String name){
        return getFragmentManager().findFragmentByTag(name) != null;
    }

    private void popToFragment(String name){
        getFragmentManager().popBackStack(name, 0);
    }

    private String getLocationNameById(String locationId){
        if(callback.getRegistrationDataModel() != null) {
            for (LocationsDTO location : callback.getRegistrationDataModel().getPayloadDTO().getLocations()) {
                if (location.getGuid().equals(locationId)) {
                    return location.getName();
                }
            }
        }else{
            LocationsDTO locationsDTO = callback.getPreRegisterDataModel().getPracticeById(selectedPractice.getPracticeId()).findLocationById(locationId);
            if(locationsDTO != null){
                return locationsDTO.getName();
            }
        }
        return null;
    }

    private void registerDevice(){
        if(selectedImageStyle == Defs.IMAGE_STYLE_PRACTICE_LOGO){
            cacheLogoToFile();
        }
        ApplicationPreferences applicationPreferences = getApplicationHelper().getApplicationPreferences();
        applicationPreferences.setUserPracticeDTO(selectedPractice);

        Device device = new Device();
        device.setDeviceName(applicationPreferences.getDeviceName());
        device.setOrganizationId(selectedPractice.getOrganizationId());
        device.setSerialNumber(Build.SERIAL);

        DeviceGroup deviceGroup = new DeviceGroup();
        deviceGroup.setName(applicationPreferences.getLocationId());
        device.setDeviceGroup(deviceGroup);

        DeviceRegistration.register(device, registrationCallback, getContext());
    }

    private void cacheLogoToFile(){
        File cacheDirectory = getContext().getCacheDir();
        File imageFile = new File(cacheDirectory, selectedPractice.getPracticeId()+".png");

        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(imageFile);
            Bitmap bitmap = practiceLogo.getDrawingCache();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        selectedPractice.setPracticePhoto(imageFile.getAbsolutePath());
    }

    private RegistrationCallback registrationCallback = new RegistrationCallback() {
        @Override
        public void onPreExecute() {
            buttonRegisterDevice.setEnabled(false);
        }

        @Override
        public void onPostExecute(Registration registration) {
            buttonRegisterDevice.setEnabled(true);

            ApplicationPreferences applicationPreferences = getApplicationHelper().getApplicationPreferences();
            applicationPreferences.setDeviceId(registration.getId());

            getFragmentManager().popBackStackImmediate(LoginFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            callback.replaceFragment(new CompleteRegistrationFragment(), false);

        }


        @Override
        public void onFailure(String errorMessage) {
            buttonRegisterDevice.setEnabled(true);
            CustomErrorToast.showWithMessage(getContext(), errorMessage);
        }
    };

}

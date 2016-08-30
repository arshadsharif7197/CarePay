package com.carecloud.carepaylibray.util;

import android.graphics.Bitmap;

/**
 * Created by RB-19 on 8/29/16.
 */
public class PhotoDataUtil {
        private static PhotoDataUtil ourInstance = new PhotoDataUtil();
        private  String photoImagePath;
    private String driversLicenceImagePath;
    private Bitmap insuranceBitmap;
    private String insuranceImagePath;
    private boolean isFromGallery;
    private Bitmap driversLicenceBitmap;
    private boolean isDriveLicense;

        public static PhotoDataUtil getInstance() {
            return ourInstance;
        }
    public boolean isDriverLicense(){
        return isDriverLicense();
    }

        private PhotoDataUtil() {

        }

        public String getPhotoImagePath() {
            return photoImagePath;
        }

        public void setPhotoImagePath(String photoImagePath) {
            this.photoImagePath = photoImagePath;
        }

        public String getInsuranceImagePath() {
            return insuranceImagePath;
        }

        public void setInsuranceImagePath(String insuranceImagePath) {
            this.insuranceImagePath = insuranceImagePath;
        }

    public boolean isDriveLicense() {
        return isDriveLicense;
    }

    public void setDriveLicense(boolean driveLicense) {
        isDriveLicense = driveLicense;
    }

    public Bitmap getInsuranceBitmap() {
        return insuranceBitmap;
    }

    public void setInsuranceBitmap(Bitmap insuranceBitmap) {
        this.insuranceBitmap = insuranceBitmap;
    }


    public boolean isFromGallery() {
        return isFromGallery;
    }

    public void setFromGallery(boolean fromGallery) {
        isFromGallery = fromGallery;
    }

    public Bitmap getDriversLicenceBitmap() {
        return driversLicenceBitmap;
    }

    public void setDriversLicenceBitmap(Bitmap driversLicenceBitmap) {
        this.driversLicenceBitmap = driversLicenceBitmap;
    }

    public String getDriversLicenceImagePath() {
        return driversLicenceImagePath;
    }

    public void setDriversLicenceImagePath(String driversLicenceImagePath) {
        this.driversLicenceImagePath = driversLicenceImagePath;
    }
}





package com.carecloud.carepaylibray.base.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Created by Jahirul Bhuiyan on 9/26/2016.
 */
public class BasePersonModel implements Parcelable{

    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("middle_name")
    @Expose
    private String middleName;
    @SerializedName("last_name")
    @Expose
    private String lastName;

    @SerializedName("date_of_birth")
    @Expose
    private String dateOfBirth;
    @SerializedName("gender")
    @Expose
    private String gender;

    @SerializedName("profile_photo")
    @Expose
    private String profilePhoto;

    public BasePersonModel() {

    }

    protected BasePersonModel(Parcel in) {
        firstName = in.readString();
        middleName = in.readString();
        lastName = in.readString();
        dateOfBirth = in.readString();
        gender = in.readString();
        profilePhoto = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(middleName);
        dest.writeString(lastName);
        dest.writeString(dateOfBirth);
        dest.writeString(gender);
        dest.writeString(profilePhoto);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BasePersonModel> CREATOR = new Creator<BasePersonModel>() {
        @Override
        public BasePersonModel createFromParcel(Parcel in) {
            return new BasePersonModel(in);
        }

        @Override
        public BasePersonModel[] newArray(int size) {
            return new BasePersonModel[size];
        }
    };

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
}

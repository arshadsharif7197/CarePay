package com.carecloud.carepay.patient.tutorial;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.carecloud.carepay.service.library.label.Label;

public class TutorialItem implements Parcelable {
    private String titleText;
    private String subTitleText;
    private int foregroundImageRes = -1;
    private int titleTextRes = -1;
    private int subTitleTextRes = -1;

    public TutorialItem(@NonNull String titleText, @Nullable String subTitleText, @DrawableRes int foregroundImageRes) {
        this.titleText = Label.getLabel(titleText);
        this.subTitleText = Label.getLabel(subTitleText);
        this.foregroundImageRes = foregroundImageRes;
    }

    public String getTitleText() {
        return titleText;
    }

    public String getSubTitleText() {
        return subTitleText;
    }

    public int getForegroundImageRes() {
        return foregroundImageRes;
    }

    public int getTitleTextRes() {
        return titleTextRes;
    }

    public int getSubTitleTextRes() {
        return subTitleTextRes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.titleText);
        dest.writeString(this.subTitleText);
        dest.writeInt(this.foregroundImageRes);
        dest.writeInt(this.titleTextRes);
        dest.writeInt(this.subTitleTextRes);
    }

    protected TutorialItem(Parcel in) {
        this.titleText = in.readString();
        this.subTitleText = in.readString();
        this.foregroundImageRes = in.readInt();
        this.titleTextRes = in.readInt();
        this.subTitleTextRes = in.readInt();
    }

    public static final Parcelable.Creator<TutorialItem> CREATOR = new Parcelable.Creator<TutorialItem>() {
        public TutorialItem createFromParcel(Parcel source) {
            return new TutorialItem(source);
        }

        public TutorialItem[] newArray(int size) {
            return new TutorialItem[size];
        }
    };
}

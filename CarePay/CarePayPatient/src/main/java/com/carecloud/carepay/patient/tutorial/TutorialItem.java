package com.carecloud.carepay.patient.tutorial;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.carecloud.carepay.service.library.label.Label;

public class TutorialItem implements Parcelable {
    private String title;
    private String subTitle;
    private int imageRes = -1;

    /**
     * @param title title text
     * @param subTitle sub title text
     * @param imageRes image resource
     */
    public TutorialItem(@NonNull String title, @Nullable String subTitle, @DrawableRes int imageRes) {
        this.title = Label.getLabel(title);
        this.subTitle = Label.getLabel(subTitle);
        this.imageRes = imageRes;
    }

    String getTitle() {
        return title;
    }

    String getSubTitle() {
        return subTitle;
    }

    int getImageRes() {
        return imageRes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.subTitle);
        dest.writeInt(this.imageRes);
    }

    private TutorialItem(Parcel in) {
        this.title = in.readString();
        this.subTitle = in.readString();
        this.imageRes = in.readInt();
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

package com.carecloud.carepaylibray.base.models;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Created by Jahirul Bhuiyan on 9/20/2016.
 */
public class BaseTransitionsDataModel implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("action")
    @Expose
    private BaseTransitionsActionModel action;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("properties")
    @Expose
    private JsonObject properties;

    private Gson gson = new Gson();

    public BaseTransitionsDataModel() {

    }

    protected BaseTransitionsDataModel(Parcel in) {
        name = in.readString();
        label = in.readString();
        action = in.readParcelable(BaseTransitionsActionModel.class.getClassLoader());
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(label);
        dest.writeParcelable(action, flags);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BaseTransitionsDataModel> CREATOR = new Creator<BaseTransitionsDataModel>() {
        @Override
        public BaseTransitionsDataModel createFromParcel(Parcel in) {
            return new BaseTransitionsDataModel(in);
        }

        @Override
        public BaseTransitionsDataModel[] newArray(int size) {
            return new BaseTransitionsDataModel[size];
        }
    };

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The label
     */
    public String getLabel() {
        return label;
    }

    /**
     *
     * @param label
     * The label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     *
     * @return
     * The action
     */
    public BaseTransitionsActionModel getAction() {
        return action;
    }

    /**
     *
     * @param action
     * The action
     */
    public void setAction(BaseTransitionsActionModel action) {
        this.action = action;
    }

    /**
     *
     * @return
     * The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     * The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     * The properties
     */

    public <S>S getProperties(Class<S> convertTo){

        return gson.fromJson(properties, convertTo);
    }


    /**
     *
     * @param properties
     * The properties
     */
    public void setProperties(JsonObject properties) {
        this.properties = properties;
    }
}

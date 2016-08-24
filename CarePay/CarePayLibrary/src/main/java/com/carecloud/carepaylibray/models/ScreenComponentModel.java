package com.carecloud.carepaylibray.models;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.carecloud.carepaylibray.constants.ComponentTypeConstants;
import com.carecloud.carepaylibray.customcomponents.InputText;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 8/23/2016.
 */
public class ScreenComponentModel {

    @SerializedName("label")
    private String Label;

    @SerializedName("type")
    private String Type;

    @SerializedName("required")
    private boolean isRequired;

    @SerializedName("value")
    private Object Value;

    public ScreenComponentModel(String label, String type, boolean isRequired) {
        Label = label;
        Type = type;
        this.isRequired = isRequired;
    }

    public String getLabel() {
        return Label;
    }

    public void setLabel(String label) {
        Label = label;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    public Object getValue() {
        return Value;
    }

    public void setValue(Object value) {
        Value = value;
    }

    public View getGeneratedView(Context context) {
        switch (Type) {
            case ComponentTypeConstants.INPUT_TEXT: {
                return generateInputView(context,InputType.TYPE_CLASS_TEXT);
            }case ComponentTypeConstants.PASSWORD: {
                return generateInputView(context,InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }case ComponentTypeConstants.EMAIL: {
                return generateInputView(context,InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
            }case ComponentTypeConstants.PHONE_NUMBER: {
                return generateInputView(context,InputType.TYPE_CLASS_PHONE);
            }case ComponentTypeConstants.TEXT: {
                return generateInputView(context,InputType.TYPE_CLASS_TEXT);
            }case ComponentTypeConstants.BUTTON: {
                Button button=new Button(context);
                button.setText(Label);
                return button;
            }case ComponentTypeConstants.IMAGE: {
                ImageView imageView=new ImageView(context);
                return imageView;
            }
        }
        return null;
    }

    private InputText generateInputView(Context context, int inputType){
        InputText inputText=new InputText(context);
        inputText.setHint(Label);
        inputText.changeInputType(inputType);//);
        if(isRequired){
            inputText.setError("Please enter your value");
        }
        return inputText;
    }
}



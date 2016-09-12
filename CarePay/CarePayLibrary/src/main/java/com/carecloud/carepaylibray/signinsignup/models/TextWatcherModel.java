package com.carecloud.carepaylibray.signinsignup.models;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by harish_revuri on 9/12/2016.
 */
public class TextWatcherModel implements TextWatcher {

    public static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


    private InputType inputType;
    private EditText editText;
    private TextView errorView;
    private String errorMessage;
    private boolean isOptional;
    private OnInputChangedListner onInputChangedListner;

    public TextWatcherModel(final InputType inputType, EditText editText, final TextView errorView, final String errorMessage, boolean isOptional, OnInputChangedListner onInputChangedListner) {
        this.inputType =inputType;
        this.editText =editText;
        this.errorView =errorView;
        this.errorMessage = errorMessage;
        this.isOptional =isOptional;
        this.onInputChangedListner = onInputChangedListner;

        if(!isOptional){
            this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                        doValidation();
                    }
                    return false;
                }
            });

        }else{
            updateStatus(true);
        }

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


        if(!isOptional) {
            doValidation();
        }else{
            updateStatus(true);
        }

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private void doValidation(){
        if(inputType.equals(InputType.TYPE_EMAIL)){
            if(!isValidmail()){
                errorView.setText(errorMessage);
                updateStatus(false);
            }else{
                errorView.setText(null);
                updateStatus(true);
            }
        }else if(inputType.equals(InputType.TYPE_PASSWORD)|| inputType.equals(InputType.TYPE_TEXT)){
            if(!isValid()){
                errorView.setText(errorMessage);
                updateStatus(false);
            }else{
                errorView.setText(null);
                updateStatus(true);
            }
        }
    }

    private void updateStatus(boolean isValid) {

        if(onInputChangedListner!=null){
            onInputChangedListner.OnInputChangedListner(isValid);
        }

    }

    private boolean isValidmail() {
        if(editText!=null) {
            String email = editText.getText().toString();
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);

            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        }else{
            return false;
        }
    }

    private boolean isValid(){
        if(editText!=null) {
            String text = editText.getText().toString();
            if(text==null|| text.length()<=0){
                return  false;
            }else{
                return true;
            }
        }else{
            return true;
        }
    }


    public enum InputType{

        TYPE_PASSWORD,
        TYPE_TEXT,
        TYPE_EMAIL

    }

    public interface  OnInputChangedListner{

        public void OnInputChangedListner(boolean isValid);
    }
}

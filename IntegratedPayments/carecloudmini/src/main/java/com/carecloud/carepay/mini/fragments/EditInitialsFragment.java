package com.carecloud.carepay.mini.fragments;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepay.mini.R;
import com.carecloud.carepay.mini.models.response.UserPracticeDTO;

/**
 * Created by lmenendez on 7/11/17
 */

public class EditInitialsFragment extends RegistrationFragment {

    private UserPracticeDTO selectedPractice;

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        if(callback.getRegistrationDataModel()!=null){
            selectedPractice = callback.getRegistrationDataModel().getPayloadDTO().getUserPractices().get(0);
        }else{
            String selectedPracticeId = getApplicationHelper().getApplicationPreferences().getPracticeId();
            selectedPractice = callback.getPreRegisterDataModel().getPracticeById(selectedPracticeId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_registration_initials, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        initToolbar(view);

        EditText practiceInitials = (EditText) view.findViewById(R.id.practice_initials_name);
        practiceInitials.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    selectedPractice.setPracticeInitials(textView.getText().toString());
                    callback.replaceFragment(new ImageSelectFragment(), false);
                    return true;
                }
                return false;
            }
        });

        practiceInitials.setText(selectedPractice.getPracticeInitials());
        practiceInitials.addTextChangedListener(capsInputWatcher);
        practiceInitials.requestFocus();
    }

    private void initToolbar(View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.registration_toolbar);
        toolbar.setNavigationIcon(R.drawable.close_blue);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.replaceFragment(new ImageSelectFragment(), false);
            }
        });

        TextView title = (TextView) view.findViewById(R.id.toolbar_title);
        title.setText(getString(R.string.registration_image_edit_initials));
    }

    private TextWatcher capsInputWatcher = new TextWatcher() {
        boolean shiftCase = false;

        @Override
        public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence sequence, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(!shiftCase && editable.length() > 0){
                shiftCase = true;
                editable.replace(0, editable.length(), editable.toString().toUpperCase());
            }else{
                shiftCase = false;
            }
        }
    };

}
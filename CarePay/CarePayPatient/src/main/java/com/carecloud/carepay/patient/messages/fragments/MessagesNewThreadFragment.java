package com.carecloud.carepay.patient.messages.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.messages.MessageNavigationCallback;
import com.carecloud.carepay.patient.messages.models.ProviderContact;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by lmenendez on 7/7/17
 */

public class MessagesNewThreadFragment extends BaseFragment {

    private EditText subjectInput;
    private EditText messageInput;
    private View buttonCreate;

    private ProviderContact provider;
    private MessageNavigationCallback callback;

    /**
     * Get a new instance of MessagesNewThreadFragment
     * @param provider provider to use for sending message
     * @return MessagesNewThreadFragment
     */
    public static MessagesNewThreadFragment newInstance(ProviderContact provider){
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, provider);

        MessagesNewThreadFragment fragment = new MessagesNewThreadFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            callback = (MessageNavigationCallback) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached context must implement MessageNavigationCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);

        Bundle args = getArguments();
        if(args != null){
            provider = DtoHelper.getConvertedDTO(ProviderContact.class, args);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_new_thread, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        initToolbar(view);

        TextInputLayout subjectLayout = (TextInputLayout) view.findViewById(R.id.subjectInputLayout);
        subjectInput = (EditText) view.findViewById(R.id.subjectEditText);
        subjectInput.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(subjectLayout, null));
        subjectInput.addTextChangedListener(getEmptyTextWatcher(subjectLayout));

        TextInputLayout messageLayout = (TextInputLayout) view.findViewById(R.id.messageInputLayout);
        messageInput = (EditText) view.findViewById(R.id.messageEditText);
        messageInput.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(messageLayout, null));
        messageInput.addTextChangedListener(getEmptyTextWatcher(messageLayout));

        buttonCreate = view.findViewById(R.id.new_message_button);
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.postNewMessage(provider, subjectInput.getText().toString(), messageInput.getText().toString());
            }
        });

        validateForm();
    }

    private void initToolbar(View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText(Label.getLabel("messaging_subject_title"));

        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    private void validateForm(){
        boolean valid = !StringUtil.isNullOrEmpty(subjectInput.getText().toString()) &&
                !StringUtil.isNullOrEmpty(messageInput.getText().toString());

        buttonCreate.setEnabled(valid);
        buttonCreate.setClickable(valid);

    }

    private TextWatcher getEmptyTextWatcher(final TextInputLayout layout){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!StringUtil.isNullOrEmpty(editable.toString())){
                    layout.setError(null);
                    layout.setErrorEnabled(false);
                    validateForm();
                }else{
                    layout.setErrorEnabled(true);
                    layout.setError(Label.getLabel("demographics_required_field_msg"));
                }
            }
        };
    }


}

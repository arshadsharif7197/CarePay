package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.adapters.CustomOptionsAdapter;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowCallback;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.marcok.stepprogressbar.StepProgressBar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by jorge on 27/02/17
 */

public abstract class CheckInDemographicsBaseFragment extends BaseCheckinFragment {

    public static final String PREVENT_NAV_BACK = "prevent_nav_back";

    protected DemographicDTO demographicDTO;
    StepProgressBar stepProgressBar;
    boolean preventNavBack = false;
    private boolean userAction = false;
    private ScrollView scrollView;
    protected CheckinFlowCallback checkinFlowCallback;

    private WorkflowServiceCallback consentformcallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            if (checkinFlowCallback.getCurrentStep() >= checkinFlowCallback.getTotalSteps()) {
                if(NavigationStateConstants.PATIENT_HOME.equals(workflowDTO.getState())){
                    onUpdate(checkinFlowCallback, workflowDTO);
                }else {
                    checkinFlowCallback.setCurrentStep(checkinFlowCallback.getCurrentStep() + 1);
                    checkinFlowCallback.navigateToWorkflow(workflowDTO);
                }
            } else {
                DemographicDTO demographicDTO = new Gson().fromJson(workflowDTO.toString(), DemographicDTO.class);
                checkinFlowCallback.applyChangesAndNavTo(demographicDTO, checkinFlowCallback.getCurrentStep() + 1);
            }

        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            if(getActivity() != null) {
                Log.e(getActivity().getString(R.string.alert_title_server_error), exceptionMessage);
            }
        }
    };

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_demographic_base, container, false);
        stepProgressBar = (StepProgressBar) view.findViewById(R.id.stepProgressBarCheckin);
        inflateContent(inflater, view);
        inflateToolbarViews(view);

        View mainContainer = view.findViewById(R.id.container_main);
        hideKeyboardOnViewTouch(mainContainer);
        hideKeyboardOnViewTouch(view);
        scrollView = (ScrollView) view.findViewById(R.id.demographicsScrollView);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkinFlowCallback == null) {
            attachCallback(getContext());
        }
        stepProgressBar.setNumDots(checkinFlowCallback.getTotalSteps());
    }

    private void inflateToolbarViews(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        if (toolbar == null) {
            return;
        }
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        if (!preventNavBack) {
            toolbar.setNavigationIcon(R.drawable.icn_nav_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SystemUtil.hideSoftKeyboard(getActivity());
                    getActivity().onBackPressed();
                }
            });
        }
    }

    protected boolean checkTextEmptyValue(int textEditableId, View view) {
        EditText editText = (EditText) view.findViewById(textEditableId);
        return StringUtil.isNullOrEmpty(editText.getText().toString());
    }

    private void inflateContent(LayoutInflater inflater, View view) {
        View childview = inflater.inflate(getContentId(), null);
        ((FrameLayout) view.findViewById(R.id.checkinDemographicsContentLayout)).addView(childview);
    }

    protected void setHeaderTitle(String title, String heading, String subHeading, View view) {
        TextView titleTextView = (TextView) view.findViewById(R.id.toolbar_title);
        if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PATIENT) {
            TextView mainHeadingTextView = (TextView) view.findViewById(R.id.demographicsMainHeading);
            TextView subHeadingTextView = (TextView) view.findViewById(R.id.demographicsSubHeading);
            (view.findViewById(R.id.toolbar_layout)).setVisibility(View.VISIBLE);

            if (StringUtil.isNullOrEmpty(heading) || heading.equalsIgnoreCase(CarePayConstants.NOT_DEFINED)) {
                mainHeadingTextView.setVisibility(View.GONE);
            } else {
                mainHeadingTextView.setVisibility(View.VISIBLE);
                mainHeadingTextView.setText(heading);
            }

            if (StringUtil.isNullOrEmpty(subHeading) || subHeading.equalsIgnoreCase(CarePayConstants.NOT_DEFINED)) {
                subHeadingTextView.setVisibility(View.GONE);
            } else {
                subHeadingTextView.setVisibility(View.VISIBLE);
                subHeadingTextView.setText(subHeading);
            }
        } else {
            (view.findViewById(R.id.toolbar_layout)).setVisibility(View.VISIBLE);
            titleTextView.setText(title);

            TextView subHeadingTextView = (TextView) view.findViewById(R.id.demographicsSubHeading);
            if (StringUtil.isNullOrEmpty(subHeading) || subHeading.equalsIgnoreCase(CarePayConstants.NOT_DEFINED)) {
                subHeadingTextView.setVisibility(View.GONE);
            } else {
                subHeadingTextView.setVisibility(View.VISIBLE);
                subHeadingTextView.setText(subHeading);
            }
        }
    }

    protected void initNextButton(final View view) {
        Button nextButton = (Button) view.findViewById(R.id.checkinDemographicsNextButton);
        nextButton.setVisibility(View.VISIBLE);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                setUserAction(true);
                if (buttonView.isSelected() && passConstraints(view)) {
                    DemographicDTO demographicDTO = updateDemographicDTO(view);
                    openNextFragment(demographicDTO, (checkinFlowCallback.getCurrentStep() + 1) > checkinFlowCallback.getTotalSteps());
                }
            }
        });
        nextButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View buttonView, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN && !buttonView.isSelected()) {
                    setUserAction(true);
                    checkIfEnableButton(view);
                    return true;
                }
                return false;
            }
        });
    }

    protected void checkIfEnableButton(View view) {
        if (view != null) {
            Button nextButton = (Button) view.findViewById(R.id.checkinDemographicsNextButton);
            boolean isEnabled = passConstraints(view);
            if (nextButton != null) {
                nextButton.setSelected(isEnabled);
                nextButton.setClickable(isEnabled);
            }
        }
    }

    protected void initSelectableInput(TextView textView, DemographicsOption storeOption, String value, View optional) {
        storeOption.setName(value);
        storeOption.setLabel(value);

        if (StringUtil.isNullOrEmpty(value)) {
            value = Label.getLabel("demographics_choose");
            if (optional != null) {
                optional.setVisibility(View.VISIBLE);
            }
        }
        textView.setText(value);

    }

    protected abstract boolean passConstraints(View view);

    protected abstract int getContentId();

    protected abstract DemographicDTO updateDemographicDTO(View view);

    protected void openNextFragment(DemographicDTO demographicDTO, boolean transition) {
        Map<String, String> queries = new HashMap<>();
        if (!demographicDTO.getPayload().getAppointmentpayloaddto().isEmpty()) {
            queries.put("practice_mgmt", demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getPracticeMgmt());
            queries.put("practice_id", demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getPracticeId());
            queries.put("appointment_id", demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getAppointmentId());
        }

        Map<String, String> header = getWorkflowServiceHelper().getPreferredLanguageHeader();
        header.put("transition", Boolean.valueOf(transition).toString());

        Gson gson = new Gson();
        String demogrPayloadString = gson.toJson(demographicDTO.getPayload().getDemographics().getPayload());
        TransitionDTO transitionDTO = demographicDTO.getMetadata().getTransitions().getUpdateDemographics();
        getApplicationPreferences().writeObjectToSharedPreference(CarePayConstants.DEMOGRAPHICS_ADDRESS_BUNDLE,
                demographicDTO.getPayload().getDemographics().getPayload().getAddress());
        getWorkflowServiceHelper().execute(transitionDTO, consentformcallback, demogrPayloadString, queries, header);
    }


    protected void setVisibility(View view, boolean isDisplayed) {
        if (isDisplayed) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean navigateBack() {
        return preventNavBack;
    }

    @Override
    public void attachCallback(Context context) {
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            if (context instanceof DemographicsView) {
                checkinFlowCallback = ((DemographicsView) context).getPresenter();
            } else {
                checkinFlowCallback = (CheckinFlowCallback) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException(context.toString() + " must implement CheckinFlowCallback");
        }
    }

    protected TextWatcher getValidateEmptyTextWatcher(final TextInputLayout inputLayout) {
        return new TextWatcher() {
            public int count;

            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
                this.count = sequence.length();
            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtil.isNullOrEmpty(editable.toString())) {
                    inputLayout.setErrorEnabled(true);
                    inputLayout.setError(Label.getLabel("demographics_required_validation_msg"));
                } else if (count == 0) {
                    inputLayout.setError(null);
                    inputLayout.setErrorEnabled(false);
                }
                checkIfEnableButton(getView());
            }
        };
    }

    protected TextWatcher getOptionalViewTextWatcher(final View optionalView) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtil.isNullOrEmpty(editable.toString())) {
                    optionalView.setVisibility(View.VISIBLE);
                } else {
                    optionalView.setVisibility(View.GONE);
                }
                checkIfEnableButton(getView());
            }
        };
    }

    protected TextWatcher clearValidationErrorsOnTextChange(final TextInputLayout inputLayout) {
        return new TextWatcher() {
            public int count;

            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
                this.count = sequence.length();
            }

            @Override
            public void onTextChanged(CharSequence sequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!StringUtil.isNullOrEmpty(editable.toString()) && count == 0) {
                    inputLayout.setError(null);
                    inputLayout.setErrorEnabled(false);
                }
                checkIfEnableButton(getView());
            }
        };
    }

    protected TextWatcher ssnInputFormatter = new TextWatcher() {
        int lastLength;

        @Override
        public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
            lastLength = sequence.length();
        }

        @Override
        public void onTextChanged(CharSequence sequence, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            StringUtil.autoFormatSocialSecurityNumber(editable, lastLength);
        }
    };

    protected TextWatcher phoneInputFormatter = new TextWatcher() {
        int lastLength;

        @Override
        public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
            lastLength = sequence.length();
        }

        @Override
        public void onTextChanged(CharSequence sequence, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            StringUtil.autoFormatPhone(editable, lastLength);
            checkIfEnableButton(getView());
        }
    };

    protected OnOptionSelectedListener getDefaultOnOptionsSelectedListener(final TextView textView, final DemographicsOption storeOption, final View optional) {
        return new OnOptionSelectedListener() {
            @Override
            public void onOptionSelected(DemographicsOption option) {
                if (textView != null) {
                    textView.setText(option.getLabel());
                }
                if (optional != null) {
                    optional.setVisibility(View.GONE);
                }
                storeOption.setLabel(option.getLabel());
                storeOption.setName(option.getName());
                if (getView() != null) {
                    checkIfEnableButton(getView());
                }
            }
        };
    }

    protected View.OnClickListener getSelectOptionsListener(final List<DemographicsOption> options,
                                                            final OnOptionSelectedListener listener,
                                                            final String title) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog(getContext(), options, title, listener);
            }
        };
    }


    private void showChooseDialog(Context context,
                                  List<DemographicsOption> options,
                                  String title,
                                  final OnOptionSelectedListener listener) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        // add cancel button
        dialog.setNegativeButton(Label.getLabel("demographics_cancel_label"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int pos) {
                dialogInterface.dismiss();
            }
        });

        // create dialog layout
        View customView = LayoutInflater.from(context).inflate(R.layout.alert_list_layout, null, false);
        dialog.setView(customView);
        TextView titleTextView = (TextView) customView.findViewById(R.id.title_view);
        titleTextView.setText(title);
        titleTextView.setVisibility(View.VISIBLE);


        // create the adapter
        ListView listView = (ListView) customView.findViewById(R.id.dialoglist);
        CustomOptionsAdapter customOptionsAdapter = new CustomOptionsAdapter(context, options);
        listView.setAdapter(customOptionsAdapter);


        final AlertDialog alert = dialog.create();
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.show();

        // set item click listener
        AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long row) {
                DemographicsOption selectedOption = (DemographicsOption) adapterView.getAdapter().getItem(position);
                if (listener != null) {
                    listener.onOptionSelected(selectedOption);
                }
                alert.dismiss();
            }
        };
        listView.setOnItemClickListener(clickListener);
    }

    protected boolean isUserAction() {
        return userAction;
    }

    protected void setUserAction(boolean userAction) {
        this.userAction = userAction;
    }

    protected void setDefaultError(TextInputLayout inputLayout, boolean shouldRequestFocus) {
        setFieldError(inputLayout, Label.getLabel("demographics_required_validation_msg"), shouldRequestFocus);
    }

    protected void unsetFieldError(View baseView, int id) {
        TextInputLayout inputLayout = (TextInputLayout) baseView.findViewById(id);
        unsetFieldError(inputLayout);
    }

    protected void unsetFieldError(TextInputLayout inputLayout) {
        if (inputLayout != null) {
            inputLayout.setError(null);
            inputLayout.setErrorEnabled(false);
        }

    }

    public interface OnOptionSelectedListener {
        void onOptionSelected(DemographicsOption option);
    }

    protected void showErrorViews(boolean isError, ViewGroup container) {
        final String TAG_ERROR_HIDE_INV = getString(R.string.tag_demographics_error_hide_inv);
        final String TAG_ERROR_HIDE_GONE = getString(R.string.tag_demographics_error_hide_gone);
        final String TAG_ERROR_SHOW_INV = getString(R.string.tag_demographics_error_show_inv);
        final String TAG_ERROR_SHOW_GONE = getString(R.string.tag_demographics_error_show_gone);
        final String TAG_ERROR_COLOR = getString(R.string.tag_demographics_error_color);

        for (int i = 0; i < container.getChildCount(); i++) {
            final View view = container.getChildAt(i);
            if (view instanceof ViewGroup) {
                showErrorViews(isError, (ViewGroup) view);
            }
            if (view.getTag() instanceof String) {
                String tag = (String) view.getTag();
                if (tag != null) {
                    if (isError) {
                        if (tag.equals(TAG_ERROR_HIDE_GONE)) {
                            view.setVisibility(View.GONE);
                        } else if (tag.equals(TAG_ERROR_HIDE_INV)) {
                            view.setVisibility(View.INVISIBLE);
                        } else if (tag.equals(TAG_ERROR_SHOW_GONE) || tag.equals(TAG_ERROR_SHOW_INV)) {
                            view.setVisibility(View.VISIBLE);
                        } else if (tag.equals(TAG_ERROR_COLOR)) {
                            view.setSelected(true);
                            if (view instanceof TextInputLayout) {
                                EditText editText = ((TextInputLayout) view).getEditText();
                                editText.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.remove_red), PorterDuff.Mode.SRC_IN);
                            }
                        }
                        Rect rect = new Rect();
                        boolean dontNeedScroll = view.getGlobalVisibleRect(rect);
                        if (!dontNeedScroll) {
                            scrollView.scrollBy(0, rect.top);
                        }
                    } else {
                        if (tag.equals(TAG_ERROR_SHOW_GONE)) {
                            view.setVisibility(View.GONE);
                        } else if (tag.equals(TAG_ERROR_SHOW_INV)) {
                            view.setVisibility(View.INVISIBLE);
                        } else if (tag.equals(TAG_ERROR_HIDE_GONE) || tag.equals(TAG_ERROR_HIDE_INV)) {
                            view.setVisibility(View.VISIBLE);
                        } else if (tag.equals(TAG_ERROR_COLOR)) {
                            view.setSelected(false);
                        }
                    }
                }
            }

        }

    }

    protected TextWatcher zipInputFormatter = new TextWatcher() {
        int lastLength;

        @Override
        public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
            lastLength = sequence.length();
        }

        @Override
        public void onTextChanged(CharSequence sequence, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            StringUtil.autoFormatZipcode(editable, lastLength);
            checkIfEnableButton(getView());
        }
    };

    protected TextWatcher dateInputFormatter = new TextWatcher() {
        int lastLength;

        @Override
        public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
            lastLength = sequence.length();
        }

        @Override
        public void onTextChanged(CharSequence sequence, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            StringUtil.autoFormatDateOfBirth(editable, lastLength);
        }
    };

    protected View.OnClickListener selectEndOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EditText editText = (EditText) view;
            editText.setSelection(editText.length());
        }
    };

    protected void setUpField(TextInputLayout textInputLayout, EditText editText, boolean isVisible,
                              String value, boolean isRequired, View optionalView) {
        editText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(textInputLayout, null));
        setVisibility(textInputLayout, isVisible);
        editText.setText(StringUtil.captialize(value).trim());
        editText.getOnFocusChangeListener().onFocusChange(editText,
                !StringUtil.isNullOrEmpty(editText.getText().toString().trim()));
        if (isRequired) {
            editText.addTextChangedListener(getValidateEmptyTextWatcher(textInputLayout));
        } else if (optionalView != null) {
            editText.addTextChangedListener(getOptionalViewTextWatcher(optionalView));
        }
        if (optionalView != null && !StringUtil.isNullOrEmpty(value)) {
            optionalView.setVisibility(View.GONE);
        }
    }

    protected boolean validateField(View view, boolean isRequired, String value, int containerId,
                                    int inputLayoutId, boolean shouldRequestFocus) {
        if (isRequired && StringUtil.isNullOrEmpty(value)) {
            if (shouldRequestFocus) {
                showErrorViews(true, (ViewGroup) view.findViewById(containerId));
                setDefaultError(view, inputLayoutId, shouldRequestFocus);
            }
            return true;
        } else {
            showErrorViews(false, (ViewGroup) view.findViewById(containerId));
        }
        return false;
    }

    protected void setDefaultError(View baseView, int id, boolean shouldRequestFocus) {
        setFieldError(baseView, id, Label.getLabel("demographics_required_validation_msg"), shouldRequestFocus);
    }

    protected void setFieldError(View baseView, int id, String error, boolean shouldRequestFocus) {
        TextInputLayout inputLayout = (TextInputLayout) baseView.findViewById(id);
        setFieldError(inputLayout, error, shouldRequestFocus);
    }

    protected void setFieldError(TextInputLayout inputLayout, String error, boolean shouldRequestFocus) {
        if (inputLayout != null) {
            if (!inputLayout.isErrorEnabled()) {
                inputLayout.setError(error);
                inputLayout.setErrorEnabled(true);
            }
            if (shouldRequestFocus) {
                inputLayout.clearFocus();
                inputLayout.requestFocus();
            }
        }
    }

    @Override
    public DTO getDto() {
        return demographicDTO;
    }
}

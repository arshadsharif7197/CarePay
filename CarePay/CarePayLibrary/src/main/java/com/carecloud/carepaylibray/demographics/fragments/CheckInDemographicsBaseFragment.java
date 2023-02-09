package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.common.options.OnOptionSelectedListener;
import com.carecloud.carepaylibray.common.options.SelectOptionFragment;
import com.carecloud.carepaylibray.customdialogs.LargeAlertDialogFragment;
import com.carecloud.carepaylibray.customdialogs.LargeConfirmationAlertDialog;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicDataModel;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicsInfoDto;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowCallback;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.viewModel.PatientResponsibilityViewModel;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.android.material.textfield.TextInputLayout;
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
    protected DemographicDataModel dataModel;
    StepProgressBar stepProgressBar;
    boolean preventNavBack = false;
    private boolean userAction = false;
    private ScrollView scrollView;
    protected CheckinFlowCallback checkinFlowCallback;
    protected Button nextButton;
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        demographicDTO = checkinFlowCallback.getDemographicDTO();
        dataModel = demographicDTO.getMetadata().getNewDataModel();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_demographic_base, container, false);
        stepProgressBar = view.findViewById(R.id.stepProgressBarCheckin);
        inflateContent(inflater, view);
        inflateToolbarViews(view);

        View mainContainer = view.findViewById(R.id.container_main);
        hideKeyboardOnViewTouch(mainContainer);
        hideKeyboardOnViewTouch(view);
        scrollView = view.findViewById(R.id.demographicsScrollView);
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
        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
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
                    if (nextButton.isClickable()&&nextButton.isEnabled()){
                        SystemUtil.hideSoftKeyboard(getActivity());
                        getActivity().onBackPressed();
                    }

                }
            });
        }
    }

    private void inflateContent(LayoutInflater inflater, View view) {
        View childview = inflater.inflate(getContentId(), null);
        ((FrameLayout) view.findViewById(R.id.checkinDemographicsContentLayout)).addView(childview);
    }

    protected void setHeaderTitle(String title, String heading, String subHeading, View view) {
        TextView titleTextView = view.findViewById(R.id.toolbar_title);
        if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PATIENT) {
            TextView mainHeadingTextView = view.findViewById(R.id.demographicsMainHeading);
            TextView subHeadingTextView = view.findViewById(R.id.demographicsSubHeading);
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

            TextView subHeadingTextView = view.findViewById(R.id.demographicsSubHeading);
            if (StringUtil.isNullOrEmpty(subHeading) || subHeading.equalsIgnoreCase(CarePayConstants.NOT_DEFINED)) {
                subHeadingTextView.setVisibility(View.GONE);
            } else {
                subHeadingTextView.setVisibility(View.VISIBLE);
                subHeadingTextView.setText(subHeading);
            }
        }
    }

    protected void initNextButton(final View view) {
        nextButton = (Button) view.findViewById(R.id.checkinDemographicsNextButton);
        nextButton.setVisibility(View.VISIBLE);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                setUserAction(true);
                nextButton.setEnabled(false);
                nextButton.setClickable(false);
                if (buttonView.isSelected() && passConstraints(view)) {
                    DemographicDTO demographicDTO = updateDemographicDTO(view);

                    // checking for CDR Maguire Practice
                    if ((checkinFlowCallback.getCurrentStep() == 3)) {
                        if (!CDRPracticeFlowExist()) {
                            gotoNextFragment(demographicDTO);
                        }
                    } else {
                        gotoNextFragment(demographicDTO);
                    }
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

    private boolean CDRPracticeFlowExist() {
        boolean isRequiredFieldValid = getCDRFieldsStatus();
        String currentPracticeId = demographicDTO.getPayload().getAppointmentpayloaddto().get(0).getMetadata().getPracticeId();
        List<DemographicsInfoDto> demographicInfoList = demographicDTO.getPayload().getCheckinSettings().getDemographicsInfoDtoList();

        if (currentPracticeId.equalsIgnoreCase("f1fe3157-5eae-4796-912f-16f297aac0da")) {
            // check optional either check
            boolean isOptionalEitherCheckExist = false;
            for (DemographicsInfoDto demographicsInfoDto : demographicInfoList) {
                if (demographicsInfoDto.getOptionalEitherFields() != null && demographicsInfoDto.getOptionalEitherFields().size() > 0) {
                    isOptionalEitherCheckExist = true;
                    break;
                }
            }

            if (isOptionalEitherCheckExist
                    && isRequiredFieldValid) {

                LargeConfirmationAlertDialog largeAlertDialogFragment =
                        LargeConfirmationAlertDialog.newInstance(Label.getLabel("payment_cdr_popup"),
                                Label.getLabel("proceed_label"), Label.getLabel("go_back_label"));
                largeAlertDialogFragment.setLargeAlertInterface(new LargeAlertDialogFragment.LargeAlertInterface() {
                    @Override
                    public void onActionButton() {
                        gotoNextFragment(demographicDTO);
                    }
                });
                largeAlertDialogFragment.show(requireActivity().getSupportFragmentManager(), largeAlertDialogFragment.getClass().getName());
            } else {
                gotoNextFragment(demographicDTO);
            }
            return true;
        }
        return false;
    }

    protected abstract boolean getCDRFieldsStatus();

    protected void gotoNextFragment(DemographicDTO demographicDTO) {
        openNextFragment(demographicDTO, (checkinFlowCallback.getCurrentStep() + 1) > checkinFlowCallback.getTotalSteps());
    }

    protected void checkIfEnableButton(View view) {
        if (view != null) {
            Button nextButton = view.findViewById(R.id.checkinDemographicsNextButton);
            boolean isEnabled = passConstraints(view);
            if (nextButton != null) {
                nextButton.setSelected(isEnabled);
                nextButton.setClickable(isEnabled);
            }
        }
    }

    protected void initSelectableInput(TextView textView, DemographicsOption storeOption,
                                       String storedName, View requiredView, List<DemographicsOption> options) {
        String key = storeOption.getName();
        if (StringUtil.isNullOrEmpty(key)) {
            key = storedName;
        }
        storeOption = getOptionByKey(options, key, storeOption);
        if (!StringUtil.isNullOrEmpty(storedName)) {
            textView.setText(storeOption.getLabel());
        } else if (requiredView != null) {
            requiredView.setVisibility(View.VISIBLE);
        }

    }

    private DemographicsOption getOptionByKey(List<DemographicsOption> options,
                                              String name,
                                              DemographicsOption storeOption) {
        for (DemographicsOption option : options) {
            if (option.getName().equals(name)) {
                storeOption.setName(option.getName());
                storeOption.setLabel(option.getLabel());
                return storeOption;
            }
        }
        storeOption.setName(name);
        storeOption.setLabel(name);
        return storeOption;
    }

    protected abstract boolean passConstraints(View view);

    protected abstract int getContentId();

    protected abstract DemographicDTO updateDemographicDTO(View view);

    protected void openNextFragment(DemographicDTO demographicDTO, boolean transition) {
        patientResponsibilityViewModel.setDemographicDTOModel(demographicDTO);
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
        getWorkflowServiceHelper().execute(transitionDTO, consentFormCallback, demogrPayloadString, queries, header);
    }

    private WorkflowServiceCallback consentFormCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            if (checkinFlowCallback.getCurrentStep() == CheckinFlowCallback.IDENTITY) {
                if (getActivityProxy()!=null)
                    MixPanelUtil.endTimer(getActivityProxy().getString(R.string.timer_identification_docs));
            } else if (checkinFlowCallback.getCurrentStep() == CheckinFlowCallback.INSURANCE) {
                if (getActivityProxy()!=null)
                    MixPanelUtil.endTimer(getActivityProxy().getString(R.string.timer_health_insurance));
            }

            if (checkinFlowCallback.getCurrentStep() >= checkinFlowCallback.getTotalSteps()) {
                if (getActivityProxy()!=null)
                    MixPanelUtil.endTimer(getActivityProxy().getString(R.string.timer_demographics));
                if (NavigationStateConstants.PATIENT_HOME.equals(workflowDTO.getState())
                        || NavigationStateConstants.APPOINTMENTS.equals(workflowDTO.getState())) {
                    onUpdate(checkinFlowCallback, workflowDTO);
                } else {
                    checkinFlowCallback.setCurrentStep(checkinFlowCallback.getCurrentStep() + 1);
                    checkinFlowCallback.navigateToWorkflow(workflowDTO);
                }
            } else {
                DemographicDTO demographicDTO = new Gson().fromJson(workflowDTO.toString(), DemographicDTO.class);
                checkinFlowCallback.applyChangesAndNavTo(demographicDTO, checkinFlowCallback.getCurrentStep() + 1);


            }
            nextButton.setEnabled(true);
            nextButton.setClickable(true);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            nextButton.setEnabled(true);
            nextButton.setClickable(true);
            showErrorNotification(exceptionMessage);
            if (getActivity() != null) {
                Log.e(getActivity().getString(R.string.alert_title_server_error), exceptionMessage);
            }
        }
    };


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
            int count;

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

    protected TextWatcher getRequiredViewTextWatcher(final View requiredView) {
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
                    requiredView.setVisibility(View.VISIBLE);
                } else {
                    requiredView.setVisibility(View.GONE);
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

    protected OnOptionSelectedListener getDefaultOnOptionsSelectedListener(final TextView textView, final DemographicsOption storeOption, final View requiredView) {
        return new OnOptionSelectedListener() {
            @Override
            public void onOptionSelected(DemographicsOption option, int position) {
                if (textView != null) {
                    textView.setText(option.getLabel());
                }
                if (requiredView != null) {
                    requiredView.setVisibility(View.GONE);
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
                SelectOptionFragment fragment = SelectOptionFragment.newInstance(title);
                fragment.setOptions(options);
                fragment.setCallback(listener);
                fragment.show(getActivity().getSupportFragmentManager(), fragment.getClass().getName());
            }
        };
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
        TextInputLayout inputLayout = baseView.findViewById(id);
        unsetFieldError(inputLayout);
    }

    protected void unsetFieldError(TextInputLayout inputLayout) {
        if (inputLayout != null) {
            inputLayout.setError(null);
            inputLayout.setErrorEnabled(false);
        }

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
                              String value, boolean isRequired, View requiredView) {


        editText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(textInputLayout, null));
        setVisibility(textInputLayout, isVisible);
        editText.setText(StringUtil.captialize(value).trim());
        editText.getOnFocusChangeListener().onFocusChange(editText,
                !StringUtil.isNullOrEmpty(editText.getText().toString().trim()));
        if (isRequired) {
            editText.addTextChangedListener(getValidateEmptyTextWatcher(textInputLayout));
        }

        if (requiredView != null) {
            editText.addTextChangedListener(getRequiredViewTextWatcher(requiredView));
        }

        if (requiredView != null) {
            if (!StringUtil.isNullOrEmpty(value)) {
                requiredView.setVisibility(View.GONE);
            } else if (isRequired) {
                requiredView.setVisibility(View.VISIBLE);
            }
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
        TextInputLayout inputLayout = baseView.findViewById(id);
        setFieldError(inputLayout, error, shouldRequestFocus);
    }

    protected void setFieldError(TextInputLayout inputLayout, String error, boolean shouldRequestFocus) {
        if (inputLayout != null) {
            if (!inputLayout.isErrorEnabled()) {
                inputLayout.setErrorEnabled(true);
                inputLayout.setError(error);
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

    public void afterLanguageChanged(DemographicDTO demographicDTO) {
        dataModel = demographicDTO.getMetadata().getNewDataModel();
        replaceTranslatedOptionsValues();
    }

    protected abstract void replaceTranslatedOptionsValues();
}

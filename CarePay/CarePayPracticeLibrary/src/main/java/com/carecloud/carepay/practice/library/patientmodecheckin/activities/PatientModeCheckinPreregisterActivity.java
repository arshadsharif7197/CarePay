package com.carecloud.carepay.practice.library.patientmodecheckin.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinConsentForm1Fragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinIntakeForm1Fragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinMedicationsAllergyFragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.IFragmentCallback;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.ResponsibilityFragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.interfaces.CheckinFlowCallback;
import com.carecloud.carepay.practice.library.patientmodecheckin.interfaces.CheckinFlowState;
import com.carecloud.carepay.practice.library.payments.dialogs.PaymentAmountReceiptDialog;
import com.carecloud.carepay.practice.library.payments.dialogs.PracticePartialPaymentDialog;
import com.carecloud.carepay.practice.library.payments.fragments.PatientPaymentPlanFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentMethodFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;
import com.carecloud.carepaylibray.consentforms.models.labels.ConsentFormLabelsDTO;
import com.carecloud.carepaylibray.constants.CustomAssetStyleable;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityItemIdDocDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPersDetailsPayloadDTO;
import com.carecloud.carepaylibray.demographics.fragments.AddressFragment;
import com.carecloud.carepaylibray.demographics.fragments.CheckInDemographicsBaseFragment;
import com.carecloud.carepaylibray.demographics.fragments.DemographicsFragment;
import com.carecloud.carepaylibray.demographics.fragments.HealthInsuranceFragment;
import com.carecloud.carepaylibray.demographics.fragments.IdentificationFragment;
import com.carecloud.carepaylibray.demographics.fragments.PersonalInfoFragment;
import com.carecloud.carepaylibray.demographics.fragments.PracticeIdDocScannerFragment;
import com.carecloud.carepaylibray.demographics.misc.CheckinDemographicsInterface;
import com.carecloud.carepaylibray.demographics.misc.DemographicsLabelsHolder;
import com.carecloud.carepaylibray.demographics.misc.DemographicsReviewLabelsHolder;
import com.carecloud.carepaylibray.demographics.scanner.ProfilePictureFragment;
import com.carecloud.carepaylibray.intake.models.IntakeResponseModel;
import com.carecloud.carepaylibray.medications.fragments.MedicationAllergySearchFragment;
import com.carecloud.carepaylibray.medications.fragments.MedicationsAllergyFragment;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesObject;
import com.carecloud.carepaylibray.medications.models.MedicationsAllergiesResultsModel;
import com.carecloud.carepaylibray.payments.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.fragments.AddNewCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.ChooseCreditCardFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentExecution;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepaylibray.practice.FlowStateInfo;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lsoco_user on 11/16/2016.
 * Main activity for patient check in flow
 */
public class PatientModeCheckinPreregisterActivity extends BasePracticeActivity implements IFragmentCallback, DemographicsReviewLabelsHolder, DemographicsLabelsHolder,
        /*CheckinDemographicsFragment.CheckinDemographicsFragmentListener, DemographicsCheckInDocumentsFragment.DemographicsCheckInDocumentsFragmentListener,
        HealthInsuranceFragment.InsuranceDocumentScannerListener,*/ MedicationsAllergyFragment.MedicationAllergyCallback,
        CheckinDemographicsInterface, MedicationAllergySearchFragment.MedicationAllergySearchCallback,
        PaymentNavigationCallback, CheckinFlowCallback,
        CheckInDemographicsBaseFragment.CheckInNavListener, AddressFragment.AddressFragmentListener, DemographicsFragment.DemographicsListener,
        PersonalInfoFragment.UpdateProfilePictureListener, IdentificationFragment.UpdateIdentificationDocListener {


    public final static  int SUBFLOW_DEMOGRAPHICS_INS = 0;
    public final static  int SUBFLOW_CONSENT          = 1;
    public final static  int SUBFLOW_INTAKE           = 2;
    public final static  int SUBFLOW_PAYMENTS         = 3;
    private static final int NUM_OF_SUBFLOWS          = 4;
    private              int numIntakeForms           = 3;
    private static final int numConsentForms          = 3;

    //demographics nav
    private Map<Integer, CheckInDemographicsBaseFragment> demographicFragMap= new HashMap<>();
    private int currentDemographicStep = 1;
    //

    private DemographicDTO  demographicDTO;
    private CarePayTextView backButton;
    private ImageView       logoImageView;
    private ImageView       homeClickable;

    private View[] sectionTitleTextViews;
    private TextView consentCounterTextView;
    private TextView intakeCounterTextView;

    // Using for consent form
    // TODO : Will be create separate fragment in next sprint
    private ConsentFormLabelsDTO consentFormLabelsDTO;

    private AppointmentsPayloadDTO  appointmentsPayloadDTO;

    private ConsentFormDTO consentFormDTO;
    private FormId showingForm = FormId.FORM1;
    private String readCarefullySign;
    private String medicareDescription;
    private String authorizationDescription1;
    private String authorizationDescription2;

    private int intakeFormIndex = 1;

    private MedicationsAllergiesResultsModel medicationsAllergiesDTO;
    private PaymentsModel paymentDTO;

    // Intake Form
    private IntakeResponseModel intakeResponseModel;
    /**
     * IntakeForm Navigation
     */

    private FlowStateInfo currentFlowStateInfo;


    private View[] checkinFlowViews;
    private String[] checkinFlowLabels;
    private View checkinDemographics;
    private View checkinConsent;
    private View checkinMedications;
    private View checkinIntake;
    private View checkinPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_mode_checkin);
        demographicDTO = getConvertedDTO(DemographicDTO.class);

        setNavigationBarVisibility();
        instantiateViewsRefs();
        initializeViews();

        initializeCheckinViews();

        // place the initial fragment
        demographicFragMap.put(1, new PersonalInfoFragment());
        demographicFragMap.put(2, AddressFragment.newInstance(null, null));
        demographicFragMap.put(3, DemographicsFragment.newInstance(null, null));
        demographicFragMap.put(4, new IdentificationFragment());
        demographicFragMap.put(5, new HealthInsuranceFragment());

        navigateToDemographicFragment(1);

    }


    @Override
    protected void onPause() {
        super.onPause();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void instantiateViewsRefs() {
        backButton = (CarePayTextView) findViewById(R.id.checkinBack);
        logoImageView = (ImageView) findViewById(R.id.checkinLogo);
        homeClickable = (ImageView) findViewById(R.id.checkinHomeClickable);

        sectionTitleTextViews = new View[NUM_OF_SUBFLOWS];
        sectionTitleTextViews[SUBFLOW_DEMOGRAPHICS_INS] = findViewById(R.id.checkinSectionDemogrInsTitle);
        sectionTitleTextViews[SUBFLOW_CONSENT] = findViewById(R.id.checkinSectionConsentFormsTitle);
        sectionTitleTextViews[SUBFLOW_INTAKE] = findViewById(R.id.checkinSectionIntakeFormsTitle);
        sectionTitleTextViews[SUBFLOW_PAYMENTS] = findViewById(R.id.checkinSectionPaymentsTitle);

        consentCounterTextView = (TextView) findViewById(R.id.checkinConsentFormCounterTextView);
        intakeCounterTextView = (TextView) findViewById(R.id.checkinIntakeFormCounterTextView);
    }

    private void initializeViews() {
        toggleVisibleBackButton(false);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        homeClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PatientModeCheckinPreregisterActivity.this.finish();
            }
        });
    }

    private void initializeCheckinViews(){
        checkinDemographics = findViewById(R.id.checkin_flow_demographics);
        checkinConsent = findViewById(R.id.checkin_flow_consent);
        checkinMedications = findViewById(R.id.checkin_flow_medications);
        checkinIntake = findViewById(R.id.checkin_flow_intake);
        checkinPayment = findViewById(R.id.checkin_flow_payment);

        checkinFlowViews = new View[]{checkinDemographics, checkinConsent, checkinMedications, checkinIntake, checkinPayment};
        checkinFlowLabels = new String[]{"Patient Information", "Consent Forms", "Medications & Allergies", "Patient Intake", "Payment"};//todo get from DTO
        for(int i=0; i<checkinFlowViews.length; i++){
            View view = checkinFlowViews[i];
            TextView textView = (TextView) view.findViewById(R.id.checkin_flow_title);
            textView.setText(checkinFlowLabels[i]);
        }
    }

    /**
     * Helper method to replace fragments
     *
     * @param fragment       The fragment
     * @param addToBackStack Whether to add the transaction to back-stack
     */
    public void navigateToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.checkInContentHolderId, fragment, fragment.getClass().getSimpleName());
        if (addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getName());
        }
        transaction.commitAllowingStateLoss();
    }

    /**
     * Getter
     *
     * @return The main DTO
     */
    public DemographicDTO getDemographicDTO() {
        return demographicDTO;
    }

    /**
     * Re-sets the global DTO from a string JSON
     *
     * @param jsonString The main DTO as string
     */
    public void resetDemographicDTO(String jsonString) {
        this.demographicDTO = getConvertedDTO(DemographicDTO.class, jsonString);
    }

    @Override
    public DemographicLabelsDTO getLabelsDTO() {
        return demographicDTO.getMetadata().getLabels();
    }

    /**
     * Consent form
     */
    @Override
    public void signButtonClicked() {
        Intent intent = new Intent(this, PracticeAppSignatureActivity.class);

        intent.putExtra("consentFormLabelsDTO", consentFormLabelsDTO);
        intent.putExtra("consentform", showingForm);
        if (PracticeAppSignatureActivity.numOfLaunches == 2) {
            // pass the whole DTO
            Gson gson = new Gson();
            String consentformDTO = gson.toJson(consentFormDTO);
            intent.putExtra("consentform_model", consentformDTO);
        }
        if (showingForm == FormId.FORM1) {
            intent.putExtra("Header_Title", consentFormLabelsDTO.getSignConsentForMedicareTitle());
            intent.putExtra("Subtitle", consentFormLabelsDTO.getConsentReadCarefullyWarning());
        } else if (showingForm == FormId.FORM2) {
            intent.putExtra("Header_Title", consentFormLabelsDTO.getSignAuthorizationFormTitle());
            intent.putExtra("Subtitle", consentFormLabelsDTO.getBeforeSignatureWarningText());
        } else {
            intent.putExtra("Header_Title", consentFormLabelsDTO.getSignHipaaAgreementTitle());
            intent.putExtra("Subtitle", consentFormLabelsDTO.getBeforeSignatureWarningText());
        }
        startActivityForResult(intent, CarePayConstants.SIGNATURE_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CarePayConstants.SIGNATURE_REQ_CODE) {
            if (PracticeAppSignatureActivity.isBackButtonClicked) {
                PracticeAppSignatureActivity.isBackButtonClicked = false;
            } else {
                Fragment nextConsentForm = getNextConsentForm();
                if (nextConsentForm != null) {
                    navigateToFragment(nextConsentForm, true);
                }
            }

        }
    }



    ////////////////////////////
    // Consent form framework //
    ////////////////////////////

    /**
     * Consent form navigation
     *
     * @param workflowJson consent DTO
     */
    public void getConsentFormInformation(String workflowJson) {
        consentFormDTO = getConvertedDTO(ConsentFormDTO.class, workflowJson);
        if (consentFormDTO != null) {
            Fragment consentForm = getConsentForm(workflowJson);
            navigateToFragment(consentForm, true);

            Log.d(this.getClass().getSimpleName(), "consent form information");
        }
    }

    private Fragment getNextConsentForm() {
        showingForm = showingForm.next();
        if (showingForm != FormId.NONE) {
            return getConsentForm("");
        }
        return null;
    }

    private Fragment getConsentForm(String jsonResponse) {

        if (showingForm == FormId.FORM1) {
            Bundle bundle = new Bundle();
            bundle.putString(CarePayConstants.INTAKE_BUNDLE, jsonResponse);
            CheckinConsentForm1Fragment consentForm1Fragment = new CheckinConsentForm1Fragment();
            consentForm1Fragment.setArguments(bundle);
            return consentForm1Fragment;
        } else {
            Bundle bundle = new Bundle();
            CheckinConsentForm1Fragment consentForm1Fragment = new CheckinConsentForm1Fragment();
            consentForm1Fragment.setArguments(bundle);
            return consentForm1Fragment;
        }
    }


    /**
     * Consent form navigation
     *
     * @param workflowJson intake DTO
     */
    public void getPaymentInformation(final String workflowJson) {
        ResponsibilityFragment responsibilityFragment = new ResponsibilityFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CarePayConstants.INTAKE_BUNDLE, workflowJson);
        bundle.putString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO, workflowJson);
        responsibilityFragment.setArguments(bundle);
        navigateToFragment(responsibilityFragment, true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                paymentDTO = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowJson);

            }
        }).start();
    }


    @Override
    public void showMedicationSearch() {
        MedicationAllergySearchFragment medicationAllergySearchFragment = new MedicationAllergySearchFragment();
        if(medicationsAllergiesDTO!=null){
            Gson gson = new Gson();
            String jsonExtra = gson.toJson(medicationsAllergiesDTO);
            Bundle bundle = new Bundle();
            bundle.putString(CarePayConstants.MEDICATION_ALLERGIES_DTO_EXTRA, jsonExtra);

            bundle.putString(CarePayConstants.MEDICATION_ALLERGIES_SEARCH_MODE_EXTRA, MedicationAllergySearchFragment.SearchMode.MEDICATION.name());
            medicationAllergySearchFragment.setArguments(bundle);

        }

        medicationAllergySearchFragment.show(getSupportFragmentManager(), medicationAllergySearchFragment.getClass().getSimpleName());
    }

    @Override
    public void showAllergiesSearch() {

    }

    @Override
    public void medicationSubmitSuccess(WorkflowDTO workflowDTO) {
        PracticeNavigationHelper.navigateToWorkflow(this, workflowDTO);
    }

    @Override
    public void medicationSubmitFail(String message) {
        SystemUtil.showDefaultFailureDialog(getContext());
        Log.e(getContext().getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), message);
    }

    @Override
    public void addMedicationAllergyItem(MedicationsAllergiesObject item) {
        MedicationsAllergyFragment medicationsAllergyFragment = (MedicationsAllergyFragment) getSupportFragmentManager().findFragmentById(R.id.checkInContentHolderId);
        medicationsAllergyFragment.addItem(item);
    }


    @Override
    public void startPartialPayment() {
        new PracticePartialPaymentDialog(this, paymentDTO).show();
    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        String paymentsDTOString = gson.toJson(paymentDTO);
        bundle.putString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO, paymentsDTOString);
        bundle.putString(CarePayConstants.INTAKE_BUNDLE, paymentsDTOString);
        bundle.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);

        PracticePaymentMethodFragment fragment = new PracticePaymentMethodFragment();
        fragment.setArguments(bundle);
        navigateToFragment(fragment, true);
    }

    @Override
    public void onPaymentMethodAction(String selectedPaymentMethod, double amount, PaymentsModel paymentsModel) {
        if(paymentDTO.getPaymentPayload().getPatientCreditCards()!=null && !paymentDTO.getPaymentPayload().getPatientCreditCards().isEmpty()){
            Gson gson = new Gson();
            Bundle args = new Bundle();
            String paymentsDTOString = gson.toJson(paymentDTO);
            args.putString(CarePayConstants.PAYMENT_METHOD_BUNDLE, selectedPaymentMethod);
            args.putString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO, paymentsDTOString);
            args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
            DialogFragment fragment = new ChooseCreditCardFragment();
            fragment.setArguments(args);
//            navigateToFragment(fragment, true);
            fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
        } else {
            showAddCard(amount);
        }
    }

    @Override
    public void showAddCard(double amount) {
        Gson gson = new Gson();
        Bundle args = new Bundle();
        String paymentsDTOString = gson.toJson(paymentDTO);
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, paymentsDTOString);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE,  amount);
        DialogFragment fragment = new AddNewCreditCardFragment();
        fragment.setArguments(args);
//        navigateToFragment(fragment, true);
        fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
    }

    @Override
    public void onPaymentPlanAction() {
        PatientPaymentPlanFragment fragment = new PatientPaymentPlanFragment();

        Bundle args = new Bundle();
        Gson gson = new Gson();
        String paymentsDTOString = gson.toJson(paymentDTO);
        args.putString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO, paymentsDTOString);
        fragment.setArguments(args);

        navigateToFragment(fragment, true);
    }

    @Override
    public void showReceipt(PaymentsModel paymentsModel) {
        PaymentAmountReceiptDialog receiptDialog = new PaymentAmountReceiptDialog(this, paymentsModel, paymentsModel);
        receiptDialog.show();
    }



    /**
     * Enum to identify the forms
     */
    public enum FormId {
        FORM1, FORM2, FORM3, NONE;

        /**
         * Go to next
         */
        public FormId next() {
            if (this.equals(FORM1)) {
                return FORM2;
            } else if (this.equals(FORM2)) {
                return FORM3;
            }
            return NONE;
        }

        /**
         * Go to prev
         */
        public FormId prev() {
            if (this.equals(FORM3)) {
                return FORM2;
            } else if (this.equals(FORM2)) {
                return FORM1;
            }
            return NONE;
        }
    }


    /**
     * Toogle visible the back button (and hides/shows accordingly the logo)
     *
     * @param isVisible Whether to toggle visible
     */
    public void toggleVisibleBackButton(boolean isVisible) {
        if (isVisible) {
            backButton.setVisibility(View.VISIBLE);
            logoImageView.setVisibility(View.GONE);
        } else {
            backButton.setVisibility(View.GONE);
            logoImageView.setVisibility(View.VISIBLE);
        }
    }



    @Override
    public void setCheckinFlow(CheckinFlowState flowState, int totalPages, int currentPage){
        View view = null;
        switch (flowState){
            case DEMOGRAPHICS:
                view = checkinDemographics;
                break;
            case CONSENT:
                view = checkinConsent;
                break;
            case MEDICATIONS_AND_ALLERGIES:
                view = checkinMedications;
                break;
            case INTAKE:
                view = checkinIntake;
                break;
            case PAYMENT:
                view = checkinPayment;
                break;
            default:
                return;
        }

        updateCheckinFlow(view, totalPages, currentPage);
    }

    private void updateCheckinFlow(View highlightView, int totalPages, int currentPage){
        if(highlightView==null){
            return;
        }

        for(View flowView : checkinFlowViews) {
            CarePayTextView section = (CarePayTextView) flowView.findViewById(R.id.checkin_flow_title);
            TextView progress = (TextView) flowView.findViewById(R.id.checkin_flow_progress);

            if(flowView == highlightView) {
                section.setFontAttribute(CustomAssetStyleable.GOTHAM_ROUNDED_BOLD);
                if(totalPages>1){
                    progress.setVisibility(View.VISIBLE);
                    progress.setText(String.format("%d %s %d", currentPage, "of", totalPages));//todo label for "of"
                }else{
                    progress.setVisibility(View.GONE);
                }
            }else{
                section.setFontAttribute(CustomAssetStyleable.GOTHAM_ROUNDED_LIGHT);
                progress.setVisibility(View.GONE);
            }
        }
    }



    @Override
    public void onBackPressed() {
        setCurrentStep(currentDemographicStep-1);
        try{
            BaseCheckinFragment fragment = (BaseCheckinFragment) getSupportFragmentManager().findFragmentById(R.id.checkInContentHolderId);
            if(!fragment.navigateBack())  {
                super.onBackPressed();
            }
        }catch (ClassCastException cce){
            cce.printStackTrace();
            super.onBackPressed();
        }
    }



    /**
     * Launch intake forms
     * @param workflowJson workflowJson
     *
     */
    public void startIntakeForms(String workflowJson) {
        intakeResponseModel = getConvertedDTO(IntakeResponseModel.class, workflowJson);

        CheckinIntakeForm1Fragment checkinIntakeForm1Fragment = new CheckinIntakeForm1Fragment();
        Gson gson = new Gson();
        String intakeFormDTO = gson.toJson(intakeResponseModel);
        Bundle bundle = new Bundle();
        bundle.putString(CarePayConstants.INTAKE_BUNDLE, intakeFormDTO);
        checkinIntakeForm1Fragment.setArguments(bundle);
        navigateToFragment(checkinIntakeForm1Fragment, true);
    }

    /*
     * Changes the global DTO
     *
     * @param demographicDTO The new DTO
     *
    @Override
    public void onDemographicDtoChanged(DemographicDTO demographicDTO) {
        this.demographicDTO = demographicDTO;


        IdDocScannerFragment idDocScannerFragment = (IdDocScannerFragment)
                getSupportFragmentManager().findFragmentById(R.id.demographicsDocsLicense);

        if (idDocScannerFragment != null) {
            demographicDTO.getPayload().getDemographics().getPayload().getIdDocuments().clear();
            demographicDTO.getPayload().getDemographics().getPayload().getIdDocuments()
                    .add(idDocScannerFragment.getModel());
        }
    }

    @Override
    public void initializeDocumentFragment(){

        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, demographicDTO.getMetadata().getDataModels().demographic.identityDocuments);
        DtoHelper.bundleDto(args, demographicDTO.getMetadata().getLabels());
        DtoHelper.bundleDto(args, getDemographicIdDocPayloadDTO());

        DemographicsCheckInDocumentsFragment fragment = new DemographicsCheckInDocumentsFragment();
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.documentCapturer, fragment, DemographicsCheckInDocumentsFragment.class.getSimpleName());
        transaction.commit();

    }

    @Override
    public void initializeInsurancesFragment(){
        String tag = HealthInsuranceFragment.class.getSimpleName();

        HealthInsuranceFragment fragment = new HealthInsuranceFragment();
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, demographicDTO);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.insuranceCapturer, fragment, tag);
        transaction.commit();
    }

    @Override
    public void navigateToInsuranceDocumentFragment(final int index, DemographicInsurancePayloadDTO model) {
        CheckinDemographicsFragment checkinFragment = (CheckinDemographicsFragment)
                getSupportFragmentManager().findFragmentById(R.id.checkInContentHolderId);
        onDemographicDtoChanged(checkinFragment.updateModels());

        CheckinInsuranceEditDialog.CheckinInsuranceEditDialogListener listener = new CheckinInsuranceEditDialog.CheckinInsuranceEditDialogListener() {
            @Override
            public void onInsuranceSaved(DemographicInsurancePayloadDTO insuranceDTO) {
                updateInsuranceDTO(index, insuranceDTO);
            }

            @Override
            public void onInsuranceRemoved() {
                disableMainButton(false);
                initializeInsurancesFragment();
            }
        };

        CheckinInsuranceEditDialog checkinInsuranceEditDialog = new CheckinInsuranceEditDialog(this,false,demographicDTO, index, listener);
        checkinInsuranceEditDialog.show();
    }

    @Override
    public void navigateToParentFragment() {
        CheckinDemographicsFlowFragment fragment = new CheckinDemographicsFlowFragment();
        Bundle args = new Bundle();
        DtoHelper.bundleBaseDTO(args, getIntent(), demographicDTO.getClass());

        fragment.setArguments(args);

        navigateToFragment(fragment, false);
        Log.v(PatientModeCheckinPreregisterActivity.class.getSimpleName(), "PatientModeCheckinActivity");
    }

    @Override
    public void updateInsuranceDTO(int index, DemographicInsurancePayloadDTO model) {
        List<DemographicInsurancePayloadDTO> insurances = demographicDTO.getPayload().getDemographics().getPayload()
                .getInsurances();
        if (index>=0){
            insurances.set(index, model);
        } else if (index == CarePayConstants.NO_INDEX) {
            insurances.add(model);
        }
        disableMainButton(false);
        initializeInsurancesFragment();
    }

    @Override
    public void disableMainButton(boolean isDisabled) {
        CheckinDemographicsFragment checkinFragment = (CheckinDemographicsFragment)
                getSupportFragmentManager().findFragmentById(R.id.checkInContentHolderId);
        checkinFragment.checkIfDisableButton(isDisabled);
    }

    @Override
    public void initializeIdDocScannerFragment() {

        // add license fragment
        PracticeIdDocScannerFragment fragment = new PracticeIdDocScannerFragment();

        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, getDemographicIdDocPayloadDTO());

        DemographicMetadataEntityIdDocsDTO idDocsMetaDTO =
                demographicDTO.getMetadata().getDataModels().demographic.identityDocuments;

        if (null != idDocsMetaDTO) {
            DtoHelper.bundleDto(args, idDocsMetaDTO.properties.items.identityDocument);
        }
        String tag = "license";
        FragmentManager fm = getSupportFragmentManager();
        fragment.setArguments(args);
        fm.beginTransaction().replace(R.id.demographicsDocsLicense, fragment, tag).commit();
    }*/

    @Override
    public void navigateToConsentFlow(WorkflowDTO workflowDTO) {

        PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
    }

    private DemographicIdDocPayloadDTO getDemographicIdDocPayloadDTO() {
        DemographicIdDocPayloadDTO demographicIdDocPayloadDTO = new DemographicIdDocPayloadDTO();

        if (demographicDTO.getPayload().getDemographics() != null) {
            int size = demographicDTO.getPayload().getDemographics().getPayload().getIdDocuments().size();
            for (int i = 0; i < size; i++) {
                demographicIdDocPayloadDTO = demographicDTO.getPayload().getDemographics().getPayload().getIdDocuments().get(i);
            }
        }

        return demographicIdDocPayloadDTO;
    }


    @Override
    protected void processExternalPayment(PaymentExecution execution, Intent data){
        if(data.hasExtra(CarePayConstants.CLOVER_PAYMENT_SUCCESS_INTENT_DATA)) {
            Intent intent = getIntent();
            intent.putExtra(CarePayConstants.CLOVER_PAYMENT_SUCCESS_INTENT_DATA, data.getStringExtra(CarePayConstants.CLOVER_PAYMENT_SUCCESS_INTENT_DATA));
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    /**
     * Entry point for navigating to medication fragment
     * @param workflowDTO navigation dto
     */
    public void loadMedicationsAllergy(String workflowDTO){
        CheckinMedicationsAllergyFragment medicationsAllergyFragment = new CheckinMedicationsAllergyFragment();
        medicationsAllergiesDTO = DtoHelper.getConvertedDTO(MedicationsAllergiesResultsModel.class, workflowDTO);
        Bundle args = new Bundle();
        args.putString(CarePayConstants.MEDICATION_ALLERGIES_DTO_EXTRA, workflowDTO);

        medicationsAllergyFragment.setArguments(args);
        navigateToFragment(medicationsAllergyFragment, true);
    }

    public void initializeProfilePictureFragment(DemographicLabelsDTO globalLabelDTO,
                                                 DemographicPersDetailsPayloadDTO persDetailsDTO) {

        FragmentManager fm = getSupportFragmentManager();
        String tag = ProfilePictureFragment.class.getSimpleName();
        ProfilePictureFragment fragment = (ProfilePictureFragment) fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new ProfilePictureFragment();
            fragment.setGlobalLabelsDTO(globalLabelDTO);

            Bundle args = new Bundle();
            DtoHelper.bundleDto(args, persDetailsDTO);
            fragment.setArguments(args);
        }
        fm.beginTransaction()
                .replace(com.carecloud.carepaylibrary.R.id.revdemographicsAddressPicCapturer, fragment, tag)
                .commit();
    }

    @Override
    public String getProfilePicture() {
        ProfilePictureFragment fragment = (ProfilePictureFragment)
                getSupportFragmentManager().findFragmentById(R.id.revdemographicsAddressPicCapturer);

        if (fragment != null) {
            DemographicPersDetailsPayloadDTO demographicPersDetailsPayloadDTO = fragment.getDemographicPersDetailsPayloadDTO();
            if(demographicPersDetailsPayloadDTO != null){
                return demographicPersDetailsPayloadDTO.getProfilePhoto();
            }
        }
        return null;
    }

    public void initializeIdentificationFragment(DemographicLabelsDTO globalLabelDTO,
                                                 DemographicIdDocPayloadDTO demPayloadIdDocDTO,
                                                 DemographicMetadataEntityItemIdDocDTO demographicMetadataEntityItemIdDocDTO) {

        FragmentManager fm = getSupportFragmentManager();
        String tag = PracticeIdDocScannerFragment.class.getSimpleName();
        PracticeIdDocScannerFragment fragment = (PracticeIdDocScannerFragment) fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new PracticeIdDocScannerFragment();
            fragment.setGlobalLabelsDTO(globalLabelDTO);
            Bundle args = new Bundle();
            DtoHelper.bundleDto(args, demPayloadIdDocDTO);

            if (null != demographicMetadataEntityItemIdDocDTO) {
                DtoHelper.bundleDto(args, demographicMetadataEntityItemIdDocDTO);
            }

            fragment.setArguments(args);
        }
        fm.beginTransaction()
                .replace(com.carecloud.carepaylibrary.R.id.revDemographicsIdentificationPicCapturer, fragment, tag)
                .commit();
    }

    @Override
    public DemographicIdDocPayloadDTO getUpdatedDocuments() {
        PracticeIdDocScannerFragment fragment = (PracticeIdDocScannerFragment)
                getSupportFragmentManager().findFragmentById(R.id.revDemographicsIdentificationPicCapturer);

        if (fragment != null) {
            return fragment.getModel();
        }
        return null;
    }

    @Override
    public void applyChangesAndNavTo(DemographicDTO demographicDTO, Integer step) {
        currentDemographicStep = step;
        this.demographicDTO = demographicDTO;
        navigateToDemographicFragment(step);
    }

    @Override
    public Integer getCurrentStep() {
        return currentDemographicStep;
    }

    @Override
    public void setCurrentStep(Integer step){
        if(step>0){
            currentDemographicStep=step;
        }
    }

    public void navigateToDemographicFragment(Integer step) {
        CheckInDemographicsBaseFragment fragment = demographicFragMap.get(step);
        Bundle args = new Bundle();
        DtoHelper.bundleBaseDTO(args, getIntent(), demographicDTO.getClass());

        fragment.setArguments(args);

        navigateToFragment(fragment, true);
        if(step==1){
            initializeProfilePictureFragment(demographicDTO.getMetadata().getLabels(),
                    demographicDTO.getPayload().getDemographics().getPayload().getPersonalDetails());
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void navigateToAddressFragment() {

    }

    @Override
    public void navigateToIdentificationFragment() {

    }

    @Override
    public void updateDTO(DemographicDTO model) {

    }

    @Override
    public void navigateToPersonalInformationFragment() {

    }

    @Override
    public void navigateToDemographicsFragment() {

    }


}

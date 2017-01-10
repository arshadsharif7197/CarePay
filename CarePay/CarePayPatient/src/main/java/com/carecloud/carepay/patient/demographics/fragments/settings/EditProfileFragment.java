package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.demographics.scanner.DocumentScannerFragment;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDataModelsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDetailsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsFirstNameDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsLabelsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsLastNameDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsMetadataDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsMiddleNameDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsPropertiesDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsTransitionsDTO;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import static com.carecloud.carepaylibray.utils.SystemUtil.hideSoftKeyboard;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends DocumentScannerFragment {
    private static final String LOG_TAG = DemographicsSettingsFragment.class.getSimpleName();
    private AppCompatActivity appCompatActivity;
    private DemographicsSettingsDTO demographicsSettingsDTO = null;
    private String profileString = null;
    private String firstNameString = null;
    private String middleNameString = null;
    private String lastNameString = null;
    private String emailString = null;
    private String firstNameValString = null;
    private String lastNameValString = null;
    private String middleNameValString = null;
    private Button changeProfilePictureButton = null;
    private EditText firstNameEditText = null;
    private EditText middleNameEditText = null;
    private EditText lastNameEditText = null;
    private EditText emailEditText = null;
    private EditText createPasswordEditText = null;
    private EditText repeatPasswordEditText = null;

    private Button updateProfileButton = null;
    private DemographicsSettingsLabelsDTO demographicsSettingsLabelsDTO = null;
    private TextInputLayout firstNameLabel = null;
    private TextInputLayout middleNameLabel = null;
    private TextInputLayout lastNameLabel = null;
    private TextInputLayout emailLabel = null;
    private TextInputLayout createPasswordLabel = null;
    private TextInputLayout repeatPasswordLabel = null;
    private LinearLayout rootview;
    private boolean isFirstNameEmpty;
    private boolean isLastNameEmpty;
    private DemographicsSettingsPersonalDetailsDTO demographicsSettingsDetailsDTO = null;
    private DemographicsSettingsFirstNameDTO demographicsSettingsFirstNameDTO = null;
    private DemographicsSettingsLastNameDTO demographicsSettingsLastNameDTO = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCompatActivity = (AppCompatActivity) getActivity();
    }

    public EditProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_update, container, false);
        rootview = (LinearLayout) view.findViewById(R.id.demographicsReviewRootLayout);

        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.settings_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.settings_toolbar_title);

        setGothamRoundedMediumTypeface(appCompatActivity, title);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_close));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Gson gson = new Gson();
            bundle = getArguments();
            String demographicsSettingsDTOString = bundle.getString(CarePayConstants.DEMOGRAPHICS_SETTINGS_BUNDLE);
            demographicsSettingsDTO = gson.fromJson(demographicsSettingsDTOString, DemographicsSettingsDTO.class);
        }
        firstNameEditText = (EditText) view.findViewById(R.id.reviewdemogrFirstNameEdit);
        middleNameEditText = (EditText) view.findViewById(R.id.reviewdemogrMiddleNameEdit);
        lastNameEditText = (EditText) view.findViewById(R.id.reviewdemogrLastNameEdit);
        emailEditText = (EditText) view.findViewById(R.id.signinEmailEditText);
        createPasswordEditText = (EditText) view.findViewById(R.id.passwordEditText);
        repeatPasswordEditText = (EditText) view.findViewById(R.id.repeatPasswordEditText);

        changeProfilePictureButton = (Button) view.findViewById(R.id.changeCurrentPhotoButton);
        updateProfileButton = (Button) view.findViewById(R.id.buttonAddDemographicInfo);
        ImageView imageViewDetailsImage = (ImageView) view.findViewById(R.id.providerPicImageView);
        if (demographicsSettingsDTO != null && demographicsSettingsLabelsDTO!=null ) {
                DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
                demographicsSettingsLabelsDTO = demographicsSettingsMetadataDTO.getLabels();
                imageCaptureHelper = new ImageCaptureHelper(getActivity(), imageViewDetailsImage, demographicsSettingsLabelsDTO);
        }
        getEditProfileLabels();

        initialiseUIFields(view);
        getProfileProperties();
        setEditTexts(view);

        getPersonalDetails();
        title.setText(profileString);

        setClickables(view);
        formatEditText();

        return view;

    }

    private void initialiseUIFields(View view) {

        firstNameLabel = (TextInputLayout) view.findViewById(R.id.reviewdemogrFirstNameTextInput);
        middleNameLabel = (TextInputLayout) view.findViewById(R.id.reviewdemogrMiddleNameTextInputLayout);
        lastNameLabel = (TextInputLayout) view.findViewById(R.id.reviewdemogrLastNameTextInput);
        emailLabel = (TextInputLayout) view.findViewById(R.id.signInEmailTextInputLayout);
        createPasswordLabel = (TextInputLayout) view.findViewById(R.id.passwordTextInputLayout);
        repeatPasswordLabel = (TextInputLayout) view.findViewById(R.id.repeatPasswordTextInputLayout);

    }

    private void setEditTexts(View view) {

        firstNameLabel.setTag(firstNameString);
        firstNameEditText.setTag(firstNameLabel);

        middleNameLabel.setTag(middleNameString);
        middleNameEditText.setTag(middleNameLabel);

        lastNameLabel.setTag(lastNameString);
        lastNameEditText.setTag(lastNameLabel);

        emailLabel.setTag(emailString);
        emailEditText.setTag(emailLabel);

        createPasswordLabel.setTag("Create Password");
        createPasswordEditText.setTag(createPasswordLabel);

        repeatPasswordLabel.setTag("Repeat Password");
        repeatPasswordEditText.setTag(repeatPasswordLabel);

        setChangeFocusListeners();
    }

    private void setChangeFocusListeners() {
        firstNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });
        middleNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

        lastNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

        createPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

        repeatPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });

    }

    /**
     * demographics Edit Profile labels
     */
    public void getEditProfileLabels() {
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
            if (demographicsSettingsMetadataDTO != null) {
                demographicsSettingsLabelsDTO = demographicsSettingsMetadataDTO.getLabels();
                if (demographicsSettingsLabelsDTO != null) {
                    profileString = demographicsSettingsLabelsDTO.getProfileHeadingLabel();
                    emailString = demographicsSettingsLabelsDTO.getEmailLabel();

                }
            }
        }
    }

    public void getProfileProperties() {
        if (demographicsSettingsDTO != null) {
            DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
            if (demographicsSettingsMetadataDTO != null) {
                DemographicsSettingsDataModelsDTO demographicsSettingsDataModelsDTO = demographicsSettingsMetadataDTO.getDataModels();
                if (demographicsSettingsDataModelsDTO != null) {
                    DemographicsSettingsDetailsDTO demographicsSettingsDetailsDTO = demographicsSettingsDataModelsDTO.getDemographic();
                    if (demographicsSettingsDetailsDTO != null) {
                        DemographicsSettingsPersonalDetailsPropertiesDTO demographicsSettingsPersonalDetailsPreopertiesDTO = demographicsSettingsDetailsDTO.getPersonalDetails();
                        DemographicsSettingsPersonalDetailsDTO demographicsSettingsPersonalDetailsDTO = demographicsSettingsPersonalDetailsPreopertiesDTO.getProperties();
                        DemographicsSettingsFirstNameDTO demographicsSettingsFirstNameDTO = demographicsSettingsPersonalDetailsDTO.getFirstName();
                        DemographicsSettingsLastNameDTO demographicsSettingsLastNameDTO = demographicsSettingsPersonalDetailsDTO.getLastName();
                        DemographicsSettingsMiddleNameDTO demographicsSettingsMiddleNameDTO = demographicsSettingsPersonalDetailsDTO.getMiddleName();

                        firstNameString = demographicsSettingsFirstNameDTO.getLabel();
                        lastNameString = demographicsSettingsLastNameDTO.getLabel();
                        middleNameString = demographicsSettingsMiddleNameDTO.getLabel();

                        firstNameEditText.setHint(firstNameString);
                        lastNameEditText.setHint(lastNameString);
                        middleNameEditText.setHint(middleNameString);
                        createPasswordEditText.setHint("Create Password");
                        repeatPasswordEditText.setHint("Repeat Password");

                    }
                }
            }
        }

    }

    private void getPersonalDetails() {
        String userId = CognitoAppHelper.getCurrUser();

        if (demographicsSettingsDTO != null) {
            DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
            if (demographicsSettingsPayloadDTO != null) {
                DemographicsSettingsDemographicsDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
                DemographicsSettingsDemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();
                DemographicsSettingsPersonalDetailsPayloadDTO demographicsPersonalDetails = demographicPayload.getPersonalDetails();
                firstNameValString = demographicsPersonalDetails.getFirstName();
                lastNameValString = demographicsPersonalDetails.getLastName();
                middleNameValString = demographicsPersonalDetails.getMiddleName();
                if (SystemUtil.isNotEmptyString(firstNameValString)) {
                    firstNameEditText.setText(firstNameValString);
                    firstNameEditText.requestFocus();
                }

                if (SystemUtil.isNotEmptyString(lastNameValString)) {
                    lastNameEditText.setText(lastNameValString);
                    lastNameEditText.requestFocus();
                }

                if (SystemUtil.isNotEmptyString(middleNameValString)) {
                    middleNameEditText.setText(middleNameValString);
                    middleNameEditText.requestFocus();

                }
                if (SystemUtil.isNotEmptyString(userId)) {
                    emailEditText.setText(userId);
                    emailEditText.requestFocus();
                } else {
                    Log.v(LOG_TAG, "middle name field is empty");
                }
                rootview.requestFocus();
                hideSoftKeyboard(getActivity());

            }
        }

    }

    private boolean isAllFieldsValid() {

        return !isFirstNameEmpty && !isLastNameEmpty;

    }


    private void formatEditText() {

        if (demographicsSettingsDTO != null) {
            DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
            if (demographicsSettingsMetadataDTO != null) {
                DemographicsSettingsDataModelsDTO demographicsSettingsDataModelsDTO = demographicsSettingsMetadataDTO.getDataModels();
                DemographicsSettingsDetailsDTO demographicsSettingsDemographicsDTO = demographicsSettingsDataModelsDTO.getDemographic();
                DemographicsSettingsPersonalDetailsPropertiesDTO demographicsSettingsPersonalDetailsDTO = demographicsSettingsDemographicsDTO.getPersonalDetails();
                demographicsSettingsDetailsDTO = demographicsSettingsPersonalDetailsDTO.getProperties();

            }
        }
        firstNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isFirstNameEmpty = StringUtil.isNullOrEmpty(firstNameEditText.getText().toString());
                if (!isFirstNameEmpty) {
                    firstNameLabel.setError(null);
                    firstNameLabel.setErrorEnabled(false);
                } else {
                    demographicsSettingsFirstNameDTO = demographicsSettingsDetailsDTO.getFirstName();
                    final String firstNameError = demographicsSettingsFirstNameDTO.getValidations().get(0).getErrorMessage();
                    firstNameLabel.setError(firstNameError);
                    firstNameLabel.setErrorEnabled(true);
                }
            }
        });

        lastNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int end) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isLastNameEmpty = StringUtil.isNullOrEmpty(lastNameEditText.getText().toString());
                if (!isLastNameEmpty) {
                    lastNameLabel.setError(null);
                    lastNameLabel.setErrorEnabled(false);
                } else {
                    demographicsSettingsLastNameDTO = demographicsSettingsDetailsDTO.getLastName();
                    final String lastNameError = demographicsSettingsLastNameDTO.getValidations().get(0).getErrorMessage();
                    lastNameLabel.setError(lastNameError);
                    lastNameLabel.setErrorEnabled(true);
                }

            }
        });

    }

    private void setClickables(View view) {
        changeProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(imageCaptureHelper, ImageCaptureHelper.CameraType.DEFAULT_CAMERA);
            }

        });

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAllFieldsValid()) {
                    if (demographicsSettingsDTO != null) {
                        DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = demographicsSettingsDTO.getDemographicsSettingsMetadataDTO();
                        if (demographicsSettingsMetadataDTO != null) {
                            DemographicsSettingsTransitionsDTO demographicsSettingsTransitionsDTO = demographicsSettingsMetadataDTO.getTransitions();
                            TransitionDTO demographicsSettingsUpdateDemographicsDTO = demographicsSettingsTransitionsDTO.getUpdateDemographics();
                            JSONObject payload = new JSONObject();
                            Map<String, String> queries = null;
                            Map<String, String> header = null;
                            try {
                                if (demographicsSettingsDTO != null) {
                                    DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
                                    if (demographicsSettingsPayloadDTO != null) {
                                        DemographicsSettingsDemographicsDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
                                        DemographicsSettingsDemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();
                                        DemographicsSettingsPersonalDetailsPayloadDTO demographicsPersonalDetails = demographicPayload.getPersonalDetails();
                                        demographicsPersonalDetails.setFirstName(firstNameEditText.getText().toString());
                                        demographicsPersonalDetails.setLastName(lastNameEditText.getText().toString());
                                        demographicsPersonalDetails.setMiddleName(middleNameEditText.getText().toString());

                                        Gson gson = new Gson();
                                        String jsonInString = gson.toJson(demographicPayload);
                                        WorkflowServiceHelper.getInstance().execute(demographicsSettingsUpdateDemographicsDTO, updateProfileCallback, jsonInString, header);
                                    }
                                }
                                header = new HashMap<>();
                                header.put("transition", "true");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
    }

    WorkflowServiceCallback updateProfileCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PatientNavigationHelper.getInstance(getActivity()).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showFaultDialog(getActivity());
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    public int getImageShape() {
        return ImageCaptureHelper.ROUND_IMAGE;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ImageCaptureHelper.SELECT_FILE) {
                bitmap = imageCaptureHelper.onSelectFromGalleryResult(data, getImageShape());
            } else if (requestCode == ImageCaptureHelper.REQUEST_CAMERA) {
                if (cameraType == ImageCaptureHelper.CameraType.CUSTOM_CAMERA) {
                    bitmap = imageCaptureHelper.onCaptureImageResult(getImageShape());
                    Log.v(LOG_TAG, "Orientation camera to: " + imageCaptureHelper.getOrientation());
                } else {
                    bitmap = imageCaptureHelper.onCaptureImageResult(data, getImageShape());
                }
            }
            updateModelAndViewsAfterScan(imageCaptureHelper);
        }
    }


    @Override
    public void populateViewsFromModel() {
        DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
        if (demographicsSettingsPayloadDTO != null) {
            DemographicsSettingsDemographicsDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
            DemographicsSettingsDemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();
            DemographicsSettingsPersonalDetailsPayloadDTO demographicsPersonalDetails = demographicPayload.getPersonalDetails();
            String profilePicURL = demographicsPersonalDetails.getProfilePhoto();
            if (!StringUtil.isNullOrEmpty(profilePicURL)) {
                try {
                    URL url = new URL(profilePicURL);
                    Log.v(LOG_TAG, "valid url: " + url.toString());
                    Picasso.with(getContext())
                            .load(profilePicURL)
                            .transform(new CircleImageTransform())
                            .resize(imageCaptureHelper.getImgWidth(), imageCaptureHelper.getImgWidth())
                            .into(imageCaptureHelper.getImageViewTarget());
                    // successfully load a profile image
                    return;
                } catch (MalformedURLException e) {
                    // just log
                    Log.d(LOG_TAG, "invalid url: " + profilePicURL);
                }
            }
        }
        // if no image to load, simply load the placeholder
        imageCaptureHelper.getImageViewTarget()
                .setImageDrawable(ContextCompat.getDrawable(getActivity(),
                        R.drawable.icn_placeholder_user_profile_png));
    }

    @Override
    protected void updateModel(TextView selectionDestination) {

    }

    @Override
    protected void updateModelAndViewsAfterScan(ImageCaptureHelper scanner) {
        // save the image as base64 in the model
        if (bitmap != null) {
            String imageAsBase64 = SystemUtil.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 90);
            DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = demographicsSettingsDTO.getPayload();
            if (demographicsSettingsPayloadDTO != null) {
                DemographicsSettingsDemographicsDTO demographicsDTO = demographicsSettingsPayloadDTO.getDemographics();
                DemographicsSettingsDemographicPayloadDTO demographicPayload = demographicsDTO.getPayload();
                DemographicsSettingsPersonalDetailsPayloadDTO demographicsPersonalDetails = demographicPayload.getPersonalDetails();
                demographicsPersonalDetails.setProfilePhoto(imageAsBase64);
            }
        }
    }

    @Override
    protected void setTypefaces(View view) {

    }
}

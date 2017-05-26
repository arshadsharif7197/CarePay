package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.demographics.interfaces.DemographicsSettingsFragmentListener;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraCallback;
import com.carecloud.carepaylibray.carepaycamera.CarePayCameraReady;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDTO;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.ImageCaptureHelper;
import com.carecloud.carepaylibray.utils.PermissionsUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends BaseFragment implements CarePayCameraCallback {
    private static final String LOG_TAG = DemographicsSettingsFragment.class.getSimpleName();
    private DemographicsSettingsDTO demographicsSettingsDTO = null;

    private ImageView profileImageView;
    private TextView patientNameValue;
    private TextView patientEmailValue;

    private DemographicsSettingsFragmentListener callback;
    protected CarePayCameraReady carePayCameraReady;

    /**
     * @return an instance of EditProfileFragment
     */
    public static EditProfileFragment newInstance() {
        return new EditProfileFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (DemographicsSettingsFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DemographicsSettingsFragmentListener");
        }
        try {
            carePayCameraReady = (CarePayCameraReady) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CarePayCameraReady");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        demographicsSettingsDTO = (DemographicsSettingsDTO) callback.getDto();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_update, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.settings_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.settings_toolbar_title);
        title.setText(Label.getLabel("profile_heading"));
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        callback.setToolbar(toolbar);

        profileImageView = (ImageView) view.findViewById(R.id.providerPicImageView);
        patientNameValue = (TextView) view.findViewById(R.id.patientNameTextView);
        patientEmailValue = (CarePayTextView) view.findViewById(R.id.patientEmailTextView);

        getPersonalDetails();

        setClickListeners(view);
    }

    private void getPersonalDetails() {
        PatientModel demographicsPersonalDetails = demographicsSettingsDTO.getPayload().getDemographics().getPayload().getPersonalDetails();
        String imageUrl = demographicsPersonalDetails.getProfilePhoto();
        if (!StringUtil.isNullOrEmpty(imageUrl)) {
            Picasso.with(getContext())
                    .load(imageUrl)
                    .transform(new CircleImageTransform())
                    .resize(160, 160)
                    .into(profileImageView);
        }

        patientNameValue.setText(StringUtil.capitalize(demographicsPersonalDetails.getFullName()));
        patientEmailValue.setText(demographicsSettingsDTO.getPayload().getCurrentEmail());
    }

    private void setClickListeners(View view) {
        Button changeProfilePictureButton = (Button) view.findViewById(R.id.changeCurrentPhotoButton);
        changeProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }

        });

        Button saveChangesButton = (Button) view.findViewById(R.id.buttonAddDemographicInfo);
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionDTO demographicsSettingsUpdateDemographicsDTO = demographicsSettingsDTO.getMetadata().getTransitions().getUpdateDemographics();
                DemographicPayloadDTO demographicPayload = demographicsSettingsDTO.getPayload().getDemographics()
                        .getPayload();
                Gson gson = new Gson();
                String jsonInString = gson.toJson(demographicPayload);
                getWorkflowServiceHelper().execute(demographicsSettingsUpdateDemographicsDTO, updateProfileCallback, jsonInString, null);
            }
        });

        TextView patientNameLabel = (TextView) view.findViewById(R.id.patientChangeNameTextView);
        patientNameLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.displayUpdateNameFragment();
            }

        });

        TextView patientEmailLabel = (TextView) view.findViewById(R.id.patientChangeEmailTextView);
        patientEmailLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.displayUpdateEmailFragment();
            }

        });

        TextView patientPasswordLabel = (TextView) view.findViewById(R.id.patientChangePasswordTextView);
        patientPasswordLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.displayUpdatePasswordFragment();
            }

        });
    }

    WorkflowServiceCallback updateProfileCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            DemographicsSettingsDTO updatedModel = DtoHelper.getConvertedDTO(DemographicsSettingsDTO.class, workflowDTO);
            demographicsSettingsDTO.getPayload().getDemographics().getPayload().setPersonalDetails(updatedModel.getPayload().getDemographics().getPayload().getPersonalDetails());
            SystemUtil.showSuccessToast(getContext(), Label.getLabel("settings_saved_success_message"));
            getActivity().onBackPressed();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);

        }
    };


    private void selectImage() {
        // create the chooser dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(ImageCaptureHelper.chooseActionDlgTitle);
        builder.setItems(ImageCaptureHelper.getActionDlOptions(), dialogOnClickListener);


        builder.show();
    }

    private DialogInterface.OnClickListener dialogOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int item) {
            switch (item) {
                case 0:  // "Take picture" chosen
                    ImageCaptureHelper.setUserChoosenTask(ImageCaptureHelper.getActionDlOptions(0));

                    if (PermissionsUtil.checkPermissionCamera(getActivity())) {
                        // uncomment when camera activity
                        carePayCameraReady.captureImage(EditProfileFragment.this);
                    }
                    break;

                case 1:   // "Select from Gallery" chosen
                    ImageCaptureHelper.setUserChoosenTask(ImageCaptureHelper.getActionDlOptions(1));

                    if (PermissionsUtil.checkPermission(getActivity())) {
                        startActivityForResult(ImageCaptureHelper.galleryIntent(), ImageCaptureHelper.SELECT_FILE);
                    }
                    break;

                default:  // "Cancel"
                    dialog.dismiss();
            }
        }
    };


    @Override
    public void onCapturedSuccess(Bitmap bitmap) {
        // save the image as base64 in the model
        if (bitmap != null) {
            final Bitmap rotateBitmap = ImageCaptureHelper.rotateBitmap(bitmap, ImageCaptureHelper.getOrientation());
            String tempFile = "profile_pic";
            final ImageView imageView = profileImageView;

            final int width = imageView.getWidth();
            final int height = imageView.getHeight();
            imageView.getLayoutParams().width = width;
            imageView.getLayoutParams().height = height;


            File file = ImageCaptureHelper.getBitmapFileUrl(getContext(), rotateBitmap, tempFile);
            Picasso.with(getContext()).load(file)
                    .placeholder(R.drawable.icn_camera)
                    .resize(width, height)
                    .centerCrop()
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .transform(new CircleImageTransform())
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                            ViewGroup.LayoutParams lp = imageView.getLayoutParams();
                            lp.width = width;
                            lp.height = height;
                            imageView.setLayoutParams(lp);
                        }

                        @Override
                        public void onError() {
                            imageView.setImageDrawable(ContextCompat.getDrawable(getActivity(),
                                    R.drawable.icn_camera));
                        }
                    });


            String imageAsBase64 = SystemUtil.convertBitmapToString(bitmap, Bitmap.CompressFormat.JPEG, 90);
            PatientModel demographicsPersonalDetails = demographicsSettingsDTO.getPayload().getDemographics().getPayload().getPersonalDetails();
            demographicsPersonalDetails.setProfilePhoto(imageAsBase64);

        }
    }

    @Override
    public void onCaptureFail() {

    }

}

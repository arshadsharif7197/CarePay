package com.carecloud.carepay.practice.library.homescreen;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.checkin.adapters.LanguageAdapter;
import com.carecloud.carepay.practice.library.customdialog.ChangeModeDialogFragment;
import com.carecloud.carepay.practice.library.customdialog.ConfirmationPinDialog;
import com.carecloud.carepay.practice.library.homescreen.adapters.OfficeNewsListAdapter;
import com.carecloud.carepay.practice.library.homescreen.dialogs.OfficeNewsDetailsDialog;
import com.carecloud.carepay.practice.library.homescreen.dtos.AppointmentCountsModel;
import com.carecloud.carepay.practice.library.homescreen.dtos.HomeScreenAlertsDTO;
import com.carecloud.carepay.practice.library.homescreen.dtos.HomeScreenAppointmentCountsDTO;
import com.carecloud.carepay.practice.library.homescreen.dtos.HomeScreenDTO;
import com.carecloud.carepay.practice.library.homescreen.dtos.HomeScreenOfficeNewsDTO;
import com.carecloud.carepay.practice.library.homescreen.dtos.PatientHomeScreenTransitionsDTO;
import com.carecloud.carepay.practice.library.homescreen.dtos.PracticeHomeScreenPayloadDTO;
import com.carecloud.carepay.practice.library.homescreen.dtos.PracticeHomeScreenTransitionsDTO;
import com.carecloud.carepay.practice.library.patientmode.dtos.PatientModeLinksDTO;
import com.carecloud.carepay.practice.library.payments.dialogs.PopupPickerLanguage;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.Defs;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.signinsignup.dto.OptionDTO;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CloverMainActivity extends BasePracticeActivity implements View.OnClickListener {

    private ImageView modeSwitchImageView;
    private ImageView homeLockImageView;
    private HomeScreenDTO homeScreenDTO;
    private LinearLayout homeCheckInLl;
    private LinearLayout homeAlertLinearLl;
    private HomeScreenMode homeScreenMode;
    private TextView languageSpinner;

    private Handler handler = new Handler();

    public enum HomeScreenMode {
        PATIENT_HOME, PRACTICE_HOME
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        homeScreenDTO = getConvertedDTO(HomeScreenDTO.class);
        homeScreenMode = HomeScreenMode.valueOf(homeScreenDTO.getState().toUpperCase());
        if (homeScreenMode.equals(HomeScreenMode.PRACTICE_HOME)) {
            setContentView(R.layout.activity_main_practice_mode);
        } else {
            setContentView(R.layout.activity_main_patient_mode);
        }

        // init UI fields
        initUIFields();
        populateLanguageSpinner();

        if (modeSwitchImageView != null) {
            modeSwitchImageView.setOnClickListener(this);
        }
        View lock = findViewById(R.id.homeLockIcon);
        if (lock != null) {
            lock.setOnClickListener(this);
        }
        View checkinLayout = findViewById(R.id.homeCheckinClickable);
        if (checkinLayout != null) {
            checkinLayout.setOnClickListener(this);
        }
        View appointments = findViewById(R.id.homeAppointmentsClickable);
        if (appointments != null) {
            appointments.setOnClickListener(this);
        }
        View payments = findViewById(R.id.homePaymentsClickable);
        if (payments != null) {
            payments.setOnClickListener(this);
        }
        View checkout = findViewById(R.id.homeCheckoutClickable);
        if (checkout != null) {
            checkout.setOnClickListener(this);
        }

        View updateAlertContainer = findViewById(R.id.updateAlertContainer);
        boolean isLatest = ApplicationPreferences.getInstance().isLatestVersion();
        if (updateAlertContainer != null && !isLatest) {
            updateAlertContainer.setVisibility(View.VISIBLE);
            updateAlertContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String appPackageName = getPackageName();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
            });
        }

        changeScreenMode(homeScreenMode);
        getNews();
    }

    private void populateLanguageSpinner() {
        JsonObject payloadAsJsonObject = homeScreenDTO.getPayload();
        Gson gson = new Gson();
        PracticeHomeScreenPayloadDTO practiceHomeScreenPayloadDTO
                = gson.fromJson(payloadAsJsonObject, PracticeHomeScreenPayloadDTO.class);
        JsonObject transitionsAsJsonObject = homeScreenDTO.getMetadata().getLinks();
        final PracticeHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject,
                PracticeHomeScreenTransitionsDTO.class);
        final Map<String, String> headers = getWorkflowServiceHelper().getApplicationStartHeaders();
        headers.put("username", getApplicationPreferences().getUserName());
        headers.put("username_patient", getApplicationPreferences().getPatientId());

        final PopupPickerLanguage popupPickerLanguage = new PopupPickerLanguage(getContext(), false,
                practiceHomeScreenPayloadDTO.getLanguages(), new LanguageAdapter.LanguageInterface() {
            @Override
            public void onLanguageSelected(OptionDTO language) {
                changeLanguage(transitionsDTO.getLanguage(),
                        language.getCode().toLowerCase(), headers);
            }
        });
        languageSpinner = findViewById(R.id.languageSpinner);
        if (languageSpinner != null) {
            languageSpinner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupPickerLanguage.showAsDropDown(view);
                }
            });
            languageSpinner.setText(getApplicationPreferences().getUserLanguage().toUpperCase());
        }
    }

    private void initUIFields() {
        homeCheckInLl = findViewById(R.id.homeQueueLayout);
        homeAlertLinearLl = findViewById(R.id.homeAlertLayout);
        homeLockImageView = findViewById(R.id.homeLockIcon);
        modeSwitchImageView = findViewById(R.id.homeModeSwitchClickable);
        languageSpinner = findViewById(R.id.languageSpinner);
    }

    private void populateWithLabels() {
    }

    private void changeScreenMode(HomeScreenMode homeScreenMode) {
        TextView checkinLabelTextView = findViewById(R.id.homeCheckinLabel);
        if (homeScreenMode == HomeScreenMode.PATIENT_HOME) {
            if (homeCheckInLl != null) {
                homeCheckInLl.setVisibility(View.GONE);
            }
            if (homeAlertLinearLl != null) {
                homeAlertLinearLl.setVisibility(View.GONE);
            }
            if (modeSwitchImageView != null) {
                modeSwitchImageView.setVisibility(View.GONE);
            }
            if (homeLockImageView != null) {
                homeLockImageView.setVisibility(View.VISIBLE);
            }
            if (languageSpinner != null) {
                languageSpinner.setVisibility(View.VISIBLE);
            }
            setNavigationBarVisibility();
            View checkout = findViewById(R.id.homeCheckoutClickable);
            if (checkout != null) {
                checkout.setVisibility(View.VISIBLE);
            }
            if (checkinLabelTextView != null) {
                checkinLabelTextView.setText(Label.getLabel("checkin_button_patient_mode"));
            }
            JsonObject payloadAsJsonObject = homeScreenDTO.getPayload();
            Gson gson = new Gson();
            PracticeHomeScreenPayloadDTO practiceHomeScreenPayloadDTO
                    = gson.fromJson(payloadAsJsonObject, PracticeHomeScreenPayloadDTO.class);
            boolean showShop = !practiceHomeScreenPayloadDTO.getUserPractices().isEmpty()
                    && practiceHomeScreenPayloadDTO.getUserPractices().get(0).isRetailEnabled();
            if (showShop) {
                View shopContainer = findViewById(R.id.homeShopClickable);
                if (shopContainer != null) {
                    shopContainer.setVisibility(View.VISIBLE);
                    shopContainer.setOnClickListener(this);
                    findViewById(R.id.separator).setVisibility(View.VISIBLE);

                    ImageView checkinImageView = findViewById(R.id.homeCheckinImageView);
                    redesignTopLayouts(checkinLabelTextView, checkinImageView, R.id.checkInFakeCenter);

                    TextView checkOutLabelTextView = findViewById(R.id.homeCheckoutLabel);
                    ImageView checkoutImageView = findViewById(R.id.checkoutImageView);
                    redesignTopLayouts(checkOutLabelTextView, checkoutImageView, R.id.checkoutFakeCenter);
                }
            }
        } else {
            if (homeCheckInLl != null) {
                homeCheckInLl.setVisibility(View.VISIBLE);
            }
            if (homeAlertLinearLl != null) {
                homeAlertLinearLl.setVisibility(View.GONE);//// TODO: 10/3/17 this is temporary until alerts is ready to use
            }
            if (modeSwitchImageView != null) {
                modeSwitchImageView.setVisibility(View.VISIBLE);
            }
            if (homeLockImageView != null) {
                homeLockImageView.setVisibility(View.GONE);
            }
            if (languageSpinner != null) {
                languageSpinner.setVisibility(View.GONE);
            }
            if (checkinLabelTextView != null) {
                checkinLabelTextView.setText(Label.getLabel("checkin_button"));
            }

            if (homeScreenDTO != null && homeScreenDTO.getPayload() != null) {
                JsonObject payloadAsJsonObject = homeScreenDTO.getPayload();
                Gson gson = new Gson();
                PracticeHomeScreenPayloadDTO practiceHomeScreenPayloadDTO
                        = gson.fromJson(payloadAsJsonObject, PracticeHomeScreenPayloadDTO.class);
                setPracticeUser(practiceHomeScreenPayloadDTO);
                setAppointmentCounts(true);
                setAlertCount(practiceHomeScreenPayloadDTO);
            }
        }
    }

    private void redesignTopLayouts(TextView textView, ImageView imageView, int fakeView) {
        RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewParams.addRule(RelativeLayout.CENTER_VERTICAL);
        textViewParams.addRule(RelativeLayout.END_OF, fakeView);
        textViewParams.setMarginEnd(20);
        textView.setLayoutParams(textViewParams);

        RelativeLayout.LayoutParams imageViewParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageViewParams.addRule(RelativeLayout.CENTER_VERTICAL);
        imageViewParams.addRule(RelativeLayout.START_OF, fakeView);
        imageViewParams.setMarginEnd(20);
        imageView.setLayoutParams(imageViewParams);
    }

    private void setAppointmentCounts(boolean shouldUpdateNow) {
        HomeScreenAppointmentCountsDTO appointmentCounts = (HomeScreenAppointmentCountsDTO) getApplicationPreferences().getAppointmentCounts(HomeScreenAppointmentCountsDTO.class);
        if (appointmentCounts != null) {
            int checkinCounter = appointmentCounts.getCheckingInCount();
            TextView counter = findViewById(R.id.checkedInCounterTextview);
            if (counter != null) {
                counter.setText(String.valueOf(checkinCounter));
            }
        }

        if (shouldUpdateNow) {
            updateCheckinCounts();
        } else {
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setAppointmentCounts(false);
                }
            }, 1000 * 60);//30s
        }
    }

    private void updateCheckinCounts() {
        JsonObject transitionsAsJsonObject = homeScreenDTO.getMetadata().getLinks();
        Gson gson = new Gson();

        UserPracticeDTO practiceDTO = getApplicationMode().getUserPracticeDTO();
        Set<String> locationsSavedFilteredIds = getApplicationPreferences()
                .getSelectedLocationsIds(practiceDTO.getPracticeId(), practiceDTO.getUserId());

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_id", practiceDTO.getPracticeId());
        queryMap.put("practice_mgmt", practiceDTO.getPracticeMgmt());

        if (locationsSavedFilteredIds != null && !locationsSavedFilteredIds.isEmpty()) {
            queryMap.put("location_ids", StringUtil.getListAsCommaDelimitedString(locationsSavedFilteredIds));
        }

        PracticeHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PracticeHomeScreenTransitionsDTO.class);
        TransitionDTO transitionDTO = transitionsDTO.getAppointmentCounts();

        getWorkflowServiceHelper().execute(transitionDTO, getAppointmentCounts(transitionDTO), queryMap);
    }

    private void setAlertCount(PracticeHomeScreenPayloadDTO practiceHomeScreenPayloadDTO) {
        HomeScreenAlertsDTO alertsDTO = practiceHomeScreenPayloadDTO.getAlerts();
        if (alertsDTO != null) {
            TextView alertText = findViewById(R.id.alertTextView);
            if (alertText != null) {
                int alertCounter = alertsDTO.getCount();
                alertText.setText(String.valueOf(alertCounter));
            }
        }
    }

    private void setPracticeUser(PracticeHomeScreenPayloadDTO practiceHomeScreenPayloadDTO) {
        if (practiceHomeScreenPayloadDTO.getUserPractices() != null
                && practiceHomeScreenPayloadDTO.getUserPractices().size() > 0) {
            getApplicationMode().setUserPracticeDTO(getAppAuthorizationHelper(),
                    practiceHomeScreenPayloadDTO.getUserPractices().get(0));
        } else {
            showUnAuthorizedDialog();
        }
    }

    /**
     * When no practice user show anauthorized user and logout
     */
    public void showUnAuthorizedDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getString(R.string.unauthorized));
        alertDialogBuilder
                .setMessage(getString(R.string.unauthorized_practice_user))
                .setCancelable(false)
                .setPositiveButton(getString(com.carecloud.carepaylibrary.R.string.alert_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        getWorkflowServiceHelper().executeApplicationStartRequest(logOutCall);
                        //CloverMainActivity.this.onBackPressed();
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        alertDialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.icn_notification_error);
        alertDialog.show();
    }

    BroadcastReceiver newCheckedInReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String count = intent.getExtras().getString("appointments_checking_in");
            TextView counter = findViewById(R.id.checkedInCounterTextview);
            if (count != null) {
                counter.setText(count);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.homeModeSwitchClickable) {
            navigateToPatientHome();
        } else if (viewId == R.id.homeCheckinClickable) {
            navigateToCheckIn();
        } else if (viewId == R.id.homePaymentsClickable) {
            navigateToPayments();
        } else if (viewId == R.id.homeShopClickable) {
            navigateToShop();
        } else if (viewId == R.id.homeAppointmentsClickable) {
            navigateToAppointments();
        } else if (viewId == R.id.homeCheckoutClickable) {
            checkOut();
        } else if (viewId == R.id.homeLockIcon) {
            unlockPracticeMode();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        disableUnavailableItems();
        updateCheckinCounts();
    }

    private void disableUnavailableItems() {
        if (homeScreenMode == HomeScreenMode.PRACTICE_HOME) {
            setViewsDisabled((ViewGroup) findViewById(R.id.homeCheckoutClickable));
        }
    }

    private void setViewsDisabled(ViewGroup viewGroup) {
        if (viewGroup != null) {
            viewGroup.setEnabled(false);
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View view = viewGroup.getChildAt(i);
                if (view instanceof ViewGroup) {
                    setViewsDisabled((ViewGroup) view);
                } else {
                    view.setEnabled(false);
                }
            }
        }
    }

    private void unlockPracticeMode() {
        Gson gson = new Gson();
        PatientModeLinksDTO pinPadObject = gson.fromJson(homeScreenDTO.getMetadata().getLinks(), PatientModeLinksDTO.class);
        ConfirmationPinDialog confirmationPinDialog = ConfirmationPinDialog.newInstance(
                pinPadObject.getPinpad(),
                false,
                pinPadObject.getLanguage());
        displayDialogFragment(confirmationPinDialog, false);
    }

    private void getNews() {
        JsonObject transitionsAsJsonObject = homeScreenDTO.getMetadata().getLinks();
        Gson gson = new Gson();
        TransitionDTO transitionDTO;

        PracticeHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PracticeHomeScreenTransitionsDTO.class);
        transitionDTO = transitionsDTO.getOfficeNews();

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
        queryMap.put("practice_id", getApplicationMode().getUserPracticeDTO().getPracticeId());
        queryMap.put("publish_date", DateUtil.getInstance().setToCurrent().toStringWithFormatYyyyDashMmDashDd());
        getWorkflowServiceHelper().execute(transitionDTO, getNewsCallback, queryMap);
    }

    private void navigateToShop() {
        JsonObject transitionsAsJsonObject = homeScreenDTO.getMetadata().getTransitions();
        Gson gson = new Gson();
        TransitionDTO transitionDTO;
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
        queryMap.put("practice_id", getApplicationMode().getUserPracticeDTO().getPracticeId());

        if (homeScreenMode == HomeScreenMode.PRACTICE_HOME) {
            PracticeHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PracticeHomeScreenTransitionsDTO.class);
            transitionDTO = transitionsDTO.getShop();
        } else {
            PatientHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PatientHomeScreenTransitionsDTO.class);
            transitionDTO = transitionsDTO.getShop();
        }

        getWorkflowServiceHelper().interrupt();
        getWorkflowServiceHelper().execute(transitionDTO, commonTransitionCallback, queryMap);
    }

    private void checkOut() {
        JsonObject transitionsAsJsonObject = homeScreenDTO.getMetadata().getTransitions();
        Gson gson = new Gson();
        TransitionDTO transitionDTO;
        if (homeScreenMode == HomeScreenMode.PRACTICE_HOME) {
            PracticeHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PracticeHomeScreenTransitionsDTO.class);
            transitionDTO = transitionsDTO.getPracticeCheckout();
        } else {
            getApplicationPreferences().setAppointmentNavigationOption(Defs.NAVIGATE_CHECKOUT);
            PatientHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PatientHomeScreenTransitionsDTO.class);
            transitionDTO = transitionsDTO.getPatientCheckout();
        }
        getWorkflowServiceHelper().interrupt();
        getWorkflowServiceHelper().execute(transitionDTO, commonTransitionCallback);
    }


    private void navigateToAppointments() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("start_date", DateUtil.getInstance().setToCurrent().toStringWithFormatYyyyDashMmDashDd());
        queryMap.put("end_date", DateUtil.getInstance().setToCurrent().toStringWithFormatYyyyDashMmDashDd());

        JsonObject transitionsAsJsonObject = homeScreenDTO.getMetadata().getTransitions();
        Gson gson = new Gson();
        TransitionDTO transitionDTO = null;
        if (homeScreenMode == HomeScreenMode.PRACTICE_HOME) {
            PracticeHomeScreenTransitionsDTO transitionsDTO = DtoHelper.getConvertedDTO(PracticeHomeScreenTransitionsDTO.class, transitionsAsJsonObject);
            transitionDTO = transitionsDTO.getPracticeAppointments();

            String practiceId = getApplicationMode().getUserPracticeDTO().getPracticeId();
            String userId = getApplicationMode().getUserPracticeDTO().getUserId();
            Set<String> locationsSavedFilteredIds = getApplicationPreferences().getSelectedLocationsIds(practiceId, userId);

            if (locationsSavedFilteredIds != null && !locationsSavedFilteredIds.isEmpty()) {
                queryMap.put("location_ids", StringUtil.getListAsCommaDelimitedString(locationsSavedFilteredIds));
            }

        } else {
            getApplicationPreferences().setAppointmentNavigationOption(Defs.NAVIGATE_APPOINTMENT);

            PatientHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PatientHomeScreenTransitionsDTO.class);
            transitionDTO = transitionsDTO.getPatientAppointments();
        }
        getWorkflowServiceHelper().interrupt();
        getWorkflowServiceHelper().execute(transitionDTO, checkInCallback, queryMap);
    }

    private void navigateToPayments() {
        JsonObject transitionsAsJsonObject = homeScreenDTO.getMetadata().getTransitions();
        Gson gson = new Gson();
        TransitionDTO transitionDTO;

        if (homeScreenMode == HomeScreenMode.PRACTICE_HOME) {
            PracticeHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PracticeHomeScreenTransitionsDTO.class);
            transitionDTO = transitionsDTO.getPracticePayments();

            String practiceId = getApplicationMode().getUserPracticeDTO().getPracticeId();
            String userId = getApplicationMode().getUserPracticeDTO().getUserId();
            Set<String> locationsSavedFilteredIds = getApplicationPreferences().getSelectedLocationsIds(practiceId, userId);

            Map<String, String> queryMap = new HashMap<>();
            if (locationsSavedFilteredIds != null && !locationsSavedFilteredIds.isEmpty()) {
                queryMap.put("location_ids", StringUtil.getListAsCommaDelimitedString(locationsSavedFilteredIds));
            }
            getWorkflowServiceHelper().interrupt();
            getWorkflowServiceHelper().execute(transitionDTO, commonTransitionCallback, queryMap);

        } else {
            PatientHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PatientHomeScreenTransitionsDTO.class);
            transitionDTO = transitionsDTO.getPatientPayments();
            getWorkflowServiceHelper().interrupt();
            getWorkflowServiceHelper().execute(transitionDTO, commonTransitionCallback);
        }
    }

    private void navigateToCheckIn() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("start_date", DateUtil.getInstance().setToCurrent().toStringWithFormatYyyyDashMmDashDd());
        queryMap.put("end_date", DateUtil.getInstance().setToCurrent().toStringWithFormatYyyyDashMmDashDd());

        JsonObject transitionsAsJsonObject = homeScreenDTO.getMetadata().getTransitions();
        Gson gson = new Gson();
        TransitionDTO transitionDTO;
        if (homeScreenMode == HomeScreenMode.PRACTICE_HOME) {
            PracticeHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PracticeHomeScreenTransitionsDTO.class);
            transitionDTO = transitionsDTO.getPracticeCheckin();

            String practiceId = getApplicationMode().getUserPracticeDTO().getPracticeId();
            String userId = getApplicationMode().getUserPracticeDTO().getUserId();
            Set<String> locationsSavedFilteredIds = getApplicationPreferences().getSelectedLocationsIds(practiceId, userId);

            if (locationsSavedFilteredIds != null && !locationsSavedFilteredIds.isEmpty()) {
                queryMap.put("location_ids", StringUtil.getListAsCommaDelimitedString(locationsSavedFilteredIds));
            }

        } else {
            getApplicationPreferences().setAppointmentNavigationOption(Defs.NAVIGATE_CHECKIN);
            PatientHomeScreenTransitionsDTO transitionsDTO = gson.fromJson(transitionsAsJsonObject, PatientHomeScreenTransitionsDTO.class);
            transitionDTO = transitionsDTO.getPatientCheckin();
        }

        getWorkflowServiceHelper().interrupt();
        getWorkflowServiceHelper().execute(transitionDTO, checkInCallback, queryMap);

    }

    private void navigateToPatientHome() {
        if (homeScreenMode == HomeScreenMode.PRACTICE_HOME) {
            createChangeModeDialog();
        } else if (homeScreenMode == HomeScreenMode.PATIENT_HOME) {
            // add transitions
        }
    }

    private void createChangeModeDialog() {
        String transitionsAsString = homeScreenDTO.getMetadata().getTransitions().toString();
        ChangeModeDialogFragment.newInstance(transitionsAsString).show(getSupportFragmentManager(), ChangeModeDialogFragment.class.getName());
    }

    /**
     * Log out transition
     *
     * @param transitionsDTO the transitionsDTO
     */
    private void logOut(TransitionDTO transitionsDTO) {
        Map<String, String> query = new HashMap<>();
        Map<String, String> headers = getWorkflowServiceHelper().getApplicationStartHeaders();
        query.put("transition", "true");
        getWorkflowServiceHelper().execute(transitionsDTO, logOutCall, query, headers);
    }

    WorkflowServiceCallback checkInCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            if (homeScreenMode == HomeScreenMode.PATIENT_HOME) {
                Bundle extra = new Bundle();
                extra.putBoolean(CarePayConstants.LOGIN_OPTION_QR, getApplicationPreferences().getAppointmentNavigationOption() != Defs.NAVIGATE_APPOINTMENT);
                extra.putBoolean(CarePayConstants.LOGIN_OPTION_SEARCH, true);
                PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO, extra);
            } else {
                PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
                AppointmentCountUpdateService.cancelScheduledServiceRun(CloverMainActivity.this);
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    WorkflowServiceCallback logOutCall = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            getApplicationMode().clearUserPracticeDTO();
            getAppAuthorizationHelper().setUser(null);
            AppointmentCountUpdateService.cancelScheduledServiceRun(CloverMainActivity.this);
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
            CloverMainActivity.this.finish();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    WorkflowServiceCallback getNewsCallback = new WorkflowServiceCallback() {

        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            Gson gson = new Gson();
            HomeScreenDTO homeScreenDTO = gson.fromJson(workflowDTO.toString(), HomeScreenDTO.class);
            JsonObject payloadAsJsonObject = homeScreenDTO.getPayload();
            PracticeHomeScreenPayloadDTO practiceHomePayloadDTO = gson.fromJson(payloadAsJsonObject,
                    PracticeHomeScreenPayloadDTO.class);
            List<HomeScreenOfficeNewsDTO> officeNews = practiceHomePayloadDTO.getOfficeNews();
            if (!officeNews.isEmpty()) {
                RecyclerView newsListRecyclerView = findViewById(R.id.office_news_list);
                if (newsListRecyclerView != null) {
                    newsListRecyclerView.setVisibility(View.VISIBLE);
                    findViewById(R.id.office_news_header).setVisibility(View.VISIBLE);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(CloverMainActivity.this,
                            LinearLayoutManager.HORIZONTAL, false);
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(newsListRecyclerView.getContext(),
                            layoutManager.getOrientation());
                    newsListRecyclerView.setLayoutManager(layoutManager);
                    newsListRecyclerView.addItemDecoration(dividerItemDecoration);

                    OfficeNewsListAdapter adapter = new OfficeNewsListAdapter(CloverMainActivity.this,
                            officeNews, officeNewsClickedListener);
                    newsListRecyclerView.setAdapter(adapter);
                }
            } else {
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int mid = findViewById(R.id.parent).getHeight() - metrics.heightPixels;

                ObjectAnimator positionAnimation = ObjectAnimator.ofFloat(findViewById(R.id.layoutContainer), "translationY", mid);
                ObjectAnimator positionAnimation2 = ObjectAnimator.ofFloat(findViewById(R.id.shadow), "translationY", mid);
                positionAnimation.start();
                positionAnimation2.start();
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    OfficeNewsListAdapter.OnOfficeNewsClickedListener officeNewsClickedListener
            = new OfficeNewsListAdapter.OnOfficeNewsClickedListener() {
        @Override
        public void onOfficeNewsSelected(List<HomeScreenOfficeNewsDTO> officeNewsList, int position) {
            OfficeNewsDetailsDialog detailsDialog = OfficeNewsDetailsDialog.newInstance(officeNewsList, position);
            displayDialogFragment(detailsDialog, false);
        }
    };

    WorkflowServiceCallback commonTransitionCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            if (homeScreenMode == HomeScreenMode.PATIENT_HOME) {
                Bundle extra = new Bundle();
                extra.putBoolean(CarePayConstants.LOGIN_OPTION_QR, false);
                extra.putBoolean(CarePayConstants.LOGIN_OPTION_SEARCH, true);
                PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO, extra);
            } else {
                PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            disableUnavailableItems();
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    public void onPinConfirmationCheck(boolean isCorrectPin, String pin) {
        // call for transition
        Gson gson = new Gson();
        PatientHomeScreenTransitionsDTO transitions = gson.fromJson(homeScreenDTO.getMetadata().getTransitions(),
                PatientHomeScreenTransitionsDTO.class);
        TransitionDTO transitionDTO = transitions.getPracticeMode();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("transition", "true");
        // pass the pin when supported
        getWorkflowServiceHelper().execute(transitionDTO, practiceModeCallback, queryMap);
    }

    WorkflowServiceCallback practiceModeCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private WorkflowServiceCallback getAppointmentCounts(final TransitionDTO transitionDTO) {
        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                AppointmentCountsModel countsModel = DtoHelper.getConvertedDTO(AppointmentCountsModel.class, workflowDTO);
                HomeScreenAppointmentCountsDTO appointmentCounts = countsModel.getAppointmentCountsPayload().getAppointmentCounts();
                getApplicationPreferences().setAppointmentCounts(appointmentCounts);
                setAppointmentCounts(false);

                Intent intent = new Intent(CloverMainActivity.this, AppointmentCountUpdateService.class);
                intent.putExtra(AppointmentCountUpdateService.KEY_TRANSITION, DtoHelper.getStringDTO(transitionDTO));
                intent.putExtra(AppointmentCountUpdateService.KEY_PRACTICE_ID, getApplicationMode().getUserPracticeDTO().getPracticeId());
                intent.putExtra(AppointmentCountUpdateService.KEY_PRACTICE_MGMT, getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
                startService(intent);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                //set anyway to trigger future updates
                setAppointmentCounts(false);
            }
        };
    }

    @Override
    public void onBackPressed() {
        if (homeScreenMode == HomeScreenMode.PRACTICE_HOME) {
            Gson gson = new Gson();
            JsonObject transitionsAsJsonObject = homeScreenDTO.getMetadata().getTransitions();
            final PracticeHomeScreenTransitionsDTO transitionsDTO = gson
                    .fromJson(transitionsAsJsonObject, PracticeHomeScreenTransitionsDTO.class);
            logOut(transitionsDTO.getLogout());
            getAppAuthorizationHelper().setUser(null);
            getApplicationMode().setUserPracticeDTO(getAppAuthorizationHelper(), null);
        }
    }

}

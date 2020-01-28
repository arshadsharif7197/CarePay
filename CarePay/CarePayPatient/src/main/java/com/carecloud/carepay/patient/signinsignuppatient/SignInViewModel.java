package com.carecloud.carepay.patient.signinsignuppatient;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.myhealth.BaseViewModel;
import com.carecloud.carepay.patient.myhealth.dtos.MyHealthDto;
import com.carecloud.carepay.patient.notifications.models.NotificationsDTO;
import com.carecloud.carepay.patient.session.PatientSessionService;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.ServerErrorDTO;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepay.service.library.platform.AndroidPlatform;
import com.carecloud.carepay.service.library.platform.Platform;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedAuthenticationTokens;
import com.carecloud.carepaylibray.CarePayApplication;
import com.carecloud.carepaylibray.profile.UserLinks;
import com.carecloud.carepaylibray.signinsignup.dto.SignInDTO;
import com.carecloud.carepaylibray.unifiedauth.UnifiedSignInDTO;
import com.carecloud.carepaylibray.unifiedauth.UnifiedSignInResponse;
import com.carecloud.carepaylibray.unifiedauth.UnifiedSignInUser;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.EncryptionUtil;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.google.gson.Gson;
import com.newrelic.agent.android.NewRelic;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pjohnson on 2019-10-28.
 */
public class SignInViewModel extends BaseViewModel {

    public static final Integer SHOW_CHOOSE_PROFILE_SCREEN = 100;
    public static final Integer SIGN_IN_ERROR = 101;

    private MutableLiveData<Integer> signInResultObservable = new MutableLiveData<>();
    private MutableLiveData<WorkflowDTO> signInResultNavigatorObservable = new MutableLiveData<>();
    private MutableLiveData<UserLinks> userLinksObservable = new MutableLiveData<>();
    private SignInDTO signInDto;

    public SignInViewModel(@NonNull Application application) {
        super(application);
    }

    public void setSignInDto(SignInDTO signInDto) {
        this.signInDto = signInDto;
    }

    public MutableLiveData<WorkflowDTO> getSignInResultNavigatorObservable() {
        if (signInResultNavigatorObservable == null) {
            signInResultNavigatorObservable = new MutableLiveData<>();
        }
        return signInResultNavigatorObservable;
    }

    public MutableLiveData<Integer> getSignInResultObservable() {
        if (signInResultObservable == null) {
            signInResultObservable = new MutableLiveData<>();
        }
        return signInResultObservable;
    }

    public MutableLiveData<UserLinks> getUserLinksObservable() {
        if (userLinksObservable == null) {
            userLinksObservable = new MutableLiveData<>();
        }
        return userLinksObservable;
    }

    public MutableLiveData<Integer> signInStep1(String userName,
                                                String password,
                                                String inviteId,
                                                boolean shouldShowNotificationScreen) {
        ApplicationPreferences.getInstance().setProfileId(null);
        UnifiedSignInUser user = new UnifiedSignInUser();
        user.setEmail(userName);
        user.setPassword(password);
        user.setDeviceToken(((AndroidPlatform) Platform.get()).openDefaultSharedPreferences()
                .getString(CarePayConstants.FCM_TOKEN, null));

        UnifiedSignInDTO bodyDto = new UnifiedSignInDTO();
        bodyDto.setUser(user);

        Map<String, String> queryParams = new HashMap<>();
        Map<String, String> headers = getWorkflowServiceHelper().getApplicationStartHeaders();
        Gson gson = new Gson();
        getWorkflowServiceHelper().execute(signInDto.getMetadata().getTransitions().getSignIn(),
                getUnifiedLoginCallback(userName, password, inviteId, shouldShowNotificationScreen),
                gson.toJson(bodyDto), queryParams, headers);
        ((CarePayApplication) getApplication()).getAppAuthorizationHelper().setUser(userName);
        ApplicationPreferences.getInstance().setUserId(userName);
        NewRelic.setUserId(userName);
        return signInResultObservable;
    }

    private WorkflowServiceCallback getUnifiedLoginCallback(final String user,
                                                            final String password,
                                                            final String inviteId,
                                                            boolean shouldShowNotificationScreen) {
        return new WorkflowServiceCallback() {

            @Override
            public void onPreExecute() {
                setLoading(true);
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                UnifiedSignInResponse signInResponse = DtoHelper.getConvertedDTO(UnifiedSignInResponse.class, workflowDTO);
                ApplicationPreferences.getInstance().setBadgeCounterTransition(signInResponse
                        .getMetadata().getTransitions().getBadgeCounter());
                ApplicationPreferences.getInstance()
                        .setMessagesBadgeCounter(signInResponse.getPayload().getBadgeCounter().getMessages());
                ApplicationPreferences.getInstance()
                        .setFormsBadgeCounter(signInResponse.getPayload().getBadgeCounter().getPendingForms());

                UnifiedAuthenticationTokens authTokens = signInResponse.getPayload().getAuthorizationModel()
                        .getCognito().getAuthenticationTokens();
                getAppAuthorizationHelper().setAuthorizationTokens(authTokens);
                getAppAuthorizationHelper().setRefreshTransition(signInDto.getMetadata().getTransitions().getRefresh());
                getWorkflowServiceHelper().setAppAuthorizationHelper(getAppAuthorizationHelper());
                if (signInResponse.getPayload().getAuthorizationModel().getUserLinks().getRepresentedUsers().size() > 0) {
                    setLoading(false);
                    userLinksObservable.setValue(signInResponse.getPayload().getAuthorizationModel().getUserLinks());
                    signInResultObservable.setValue(SHOW_CHOOSE_PROFILE_SCREEN);
                } else {
                    signInStep2(user, password, shouldShowNotificationScreen);
                }

                if (inviteId != null) {
                    callAcceptInviteEndpoint(signInResponse, inviteId);
                }
            }

            @Override
            public void onFailure(ServerErrorDTO serverErrorDto) {
                setLoading(false);
                getWorkflowServiceHelper().setAppAuthorizationHelper(null);
                setErrorMessage(CarePayConstants.INVALID_LOGIN_ERROR_MESSAGE);
                Log.e("Server Error", serverErrorDto.getMessage().getBody().getError().getMessage());
                signInResultObservable.setValue(SIGN_IN_ERROR);
            }
        };
    }

    public MutableLiveData<Integer> signInStep2(String user,
                                                String password,
                                                boolean shouldShowNotificationScreen) {
        Map<String, String> query = new HashMap<>();
        Map<String, String> header = new HashMap<>();
        String languageId = ApplicationPreferences.getInstance().getUserLanguage();
        header.put("Accept-Language", languageId);
        getWorkflowServiceHelper().execute(signInDto.getMetadata().getTransitions().getAuthenticate(),
                getSignInCallback(user, password, shouldShowNotificationScreen), query, header);
        return signInResultObservable;
    }

    private WorkflowServiceCallback getSignInCallback(final String user,
                                                      final String password,
                                                      boolean shouldShowNotificationScreen) {
        return new WorkflowServiceCallback() {

            @Override
            public void onPreExecute() {
                setLoading(true);
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                setLoading(false);
                ApplicationPreferences.getInstance().setUserPhotoUrl(null);
                ApplicationPreferences.getInstance().setUserFullName("N/A");
                ApplicationPreferences.getInstance().writeObjectToSharedPreference(CarePayConstants
                        .DEMOGRAPHICS_ADDRESS_BUNDLE, null);
                ApplicationPreferences.getInstance().setLandingScreen(true);

                Intent serviceIntent = new Intent(getApplication(), PatientSessionService.class);
                getApplication().startService(serviceIntent);

                if (shouldShowNotificationScreen) {
                    manageNotificationAsLandingScreen(workflowDTO.toString());
                } else {
                    signInResultNavigatorObservable.setValue(workflowDTO);
                }
                ApplicationPreferences.getInstance().setUserName(user);
                String encryptedPassword = EncryptionUtil.encrypt(getApplication(), password, user);
                ApplicationPreferences.getInstance().setUserPassword(encryptedPassword);

                MyHealthDto myHealthDto = DtoHelper.getConvertedDTO(MyHealthDto.class, workflowDTO);
                String userId;
                if (myHealthDto.getPayload().getUserLinks().getLoggedInUser() != null) {
                    userId = myHealthDto.getPayload().getUserLinks().getLoggedInUser().getUserId();
                } else {
                    userId = myHealthDto.getPayload().getDemographicDTO().getMetadata().getUserId();
                }
                MixPanelUtil.setUser(getApplication(), userId, myHealthDto.getPayload().getDemographicDTO().getPayload());
                MixPanelUtil.logEvent(getApplication().getString(R.string.event_signin_loginSuccess),
                        getApplication().getString(R.string.param_login_type),
                        getApplication().getString(R.string.login_password));
            }

            @Override
            public void onFailure(ServerErrorDTO serverErrorDto) {
                setLoading(false);
                setErrorMessage(serverErrorDto.getMessage().getBody().getError().getMessage());
                Log.e("Server Error", serverErrorDto.getMessage().getBody().getError().getMessage());
            }
        };
    }

    private void manageNotificationAsLandingScreen(String workflow) {
        final Gson gson = new Gson();
        final MyHealthDto landingDto = gson.fromJson(workflow, MyHealthDto.class);

        TransitionDTO transition = landingDto.getMetadata().getLinks().getNotifications();
        getWorkflowServiceHelper().execute(transition, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                setLoading(true);
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                NotificationsDTO notificationsDTO = gson
                        .fromJson(workflowDTO.toString(), NotificationsDTO.class);
                notificationsDTO.getPayload().setPracticePatientIds(landingDto
                        .getPayload().getPracticePatientIds());

                notificationsDTO.getMetadata().getLinks().setPatientBalances(landingDto.getMetadata()
                        .getLinks().getPatientBalances());
                notificationsDTO.getMetadata().getTransitions().setLogout(landingDto.getMetadata()
                        .getTransitions().getLogout());
                notificationsDTO.getMetadata().getLinks().setProfileUpdate(landingDto.getMetadata()
                        .getLinks().getProfileUpdate());
                notificationsDTO.getMetadata().getLinks().setAppointments(landingDto.getMetadata()
                        .getLinks().getAppointments());
                notificationsDTO.getMetadata().getLinks().setNotifications(landingDto.getMetadata()
                        .getLinks().getNotifications());
                notificationsDTO.getMetadata().getLinks().setMyHealth(landingDto.getMetadata()
                        .getLinks().getMyHealth());

                WorkflowDTO notificationWorkFlow = gson.fromJson(gson.toJson(notificationsDTO),
                        WorkflowDTO.class);
                setLoading(false);
                PatientNavigationHelper.setAccessPaymentsBalances(false);
                signInResultNavigatorObservable.setValue(notificationWorkFlow);
            }

            @Override
            public void onFailure(ServerErrorDTO serverErrorDto) {
                setLoading(false);
                setErrorMessage(serverErrorDto.getMessage().getBody().getError().getMessage());
                Log.e("Server Error", serverErrorDto.getMessage().getBody().getError().getMessage());
            }
        });
    }

    private void callAcceptInviteEndpoint(UnifiedSignInResponse signInResponse, String inviteId) {
        Map<String, String> query = new HashMap<>();
        query.put("invite_id", inviteId);
        Map<String, String> headers = new HashMap<>();
        headers.put("id_token", getAppAuthorizationHelper().getIdToken());
        headers.put("x-api-key", HttpConstants.getApiStartKey());
        getWorkflowServiceHelper().execute(signInResponse.getMetadata().getTransitions().getAcceptConnectInvite(),
                new WorkflowServiceCallback() {
                    @Override
                    public void onPreExecute() {

                    }

                    @Override
                    public void onPostExecute(WorkflowDTO workflowDTO) {
                        if (workflowDTO.getPayload().getAsJsonObject("invite_info") != null) {
                            setSuccessMessage(Label.getLabel("connectionInvite.banner.success"));
                        }
                    }

                    @Override
                    public void onFailure(ServerErrorDTO serverErrorDto) {
                        Log.e("Breeze", serverErrorDto.getMessage().getBody().getError().getMessage());
                    }
                }, query, headers);
    }
}

package com.carecloud.carepay.practice.library.signin;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.signin.dtos.PracticeSelectionDTO;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.AvailableLocationDTO;
import com.carecloud.carepay.service.library.dtos.ServerErrorDTO;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.platform.AndroidPlatform;
import com.carecloud.carepay.service.library.platform.Platform;
import com.carecloud.carepaylibray.common.BaseViewModel;
import com.carecloud.carepaylibray.unifiedauth.UnifiedSignInDTO;
import com.carecloud.carepaylibray.unifiedauth.UnifiedSignInResponse;
import com.carecloud.carepaylibray.unifiedauth.UnifiedSignInUser;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.google.gson.Gson;
import com.newrelic.agent.android.NewRelic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author pjohnson on 2020-01-22.
 */
public class SignInPracticeViewModel extends BaseViewModel {

    private MutableLiveData<UnifiedSignInResponse> signInDtoObservable = new MutableLiveData<>();
    private MutableLiveData<PracticeSelectionDTO> practicesInfoDtoObservable = new MutableLiveData<>();
    private MutableLiveData<WorkflowDTO> authenticateDtoObservable = new MutableLiveData<>();
    private WorkflowDTO uniquePracticeWorkflow;

    public SignInPracticeViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<UnifiedSignInResponse> getSignInDtoObservable() {
        return signInDtoObservable;
    }

    public MutableLiveData<PracticeSelectionDTO> getPracticesInfoDtoObservable() {
        return practicesInfoDtoObservable;
    }

    public MutableLiveData<WorkflowDTO> getAuthenticateDtoObservable() {
        return authenticateDtoObservable;
    }

    public void unifiedSignIn(String userName, String password, TransitionDTO signInTransition) {
        UnifiedSignInUser user = new UnifiedSignInUser();
        user.setEmail(userName);
        user.setPassword(password);
        user.setDeviceToken(((AndroidPlatform) Platform.get()).openDefaultSharedPreferences()
                .getString(CarePayConstants.FCM_TOKEN, null));

        UnifiedSignInDTO signInDTO = new UnifiedSignInDTO();
        signInDTO.setUser(user);

        Map<String, String> queryParams = new HashMap<>();
        Map<String, String> headers = getWorkflowServiceHelper().getApplicationStartHeaders();
        if (signInDTO.isValidUser()) {
            Gson gson = new Gson();
            getWorkflowServiceHelper().execute(signInTransition, new WorkflowServiceCallback() {
                        @Override
                        public void onPreExecute() {
                            setLoading(true);
                        }

                        @Override
                        public void onPostExecute(WorkflowDTO workflowDTO) {
                            setLoading(false);
                            UnifiedSignInResponse practiceSelectionModel = DtoHelper
                                    .getConvertedDTO(UnifiedSignInResponse.class, workflowDTO);
                            signInDtoObservable.postValue(practiceSelectionModel);
                        }

                        @Override
                        public void onFailure(ServerErrorDTO serverErrorDTO) {
                            setLoading(false);
                            setErrorMessage(serverErrorDTO.getMessage().getBody().getError().getMessage());
                        }
                    }, gson.toJson(signInDTO),
                    queryParams, headers);
            getAppAuthorizationHelper().setUser(userName);
            NewRelic.setUserId(userName);
        }
    }

    public void getPracticesInfo(TransitionDTO transitionDTO) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("language", ApplicationPreferences.getInstance().getUserLanguage());
        getWorkflowServiceHelper().execute(transitionDTO, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                setLoading(true);
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                setLoading(false);
                PracticeSelectionDTO practicesInfoDto = DtoHelper
                        .getConvertedDTO(PracticeSelectionDTO.class, workflowDTO);
                if (practicesInfoDto.getPayload().getUserPracticesList().size() == 1) {
                    //doing this because when user has only 1 practice, middleware returns the
                    //"practice_home" state without any other data, (dont blame me, blame MW)
                    uniquePracticeWorkflow = workflowDTO;
                }
                practicesInfoDtoObservable.postValue(practicesInfoDto);
            }

            @Override
            public void onFailure(ServerErrorDTO serverErrorDTO) {
                setLoading(false);
                setErrorMessage(serverErrorDTO.getMessage().getBody().getError().getMessage());
                getWorkflowServiceHelper().setAppAuthorizationHelper(null);
            }
        }, queryMap);
    }

    public void authenticate(UserPracticeDTO selectedPractice, AvailableLocationDTO selectedLocation) {
        ApplicationPreferences.getInstance().setPracticeId(selectedPractice.getPracticeId());
        ApplicationPreferences.getInstance().setPracticeLocationId(selectedLocation.getId());
        Set<String> locationIds = new HashSet<>();
        locationIds.add(String.valueOf(selectedLocation.getId()));
        ApplicationPreferences.getInstance()
                .setSelectedLocationsId(selectedPractice.getPracticeId(),
                        selectedPractice.getUserId(), locationIds);

        if (uniquePracticeWorkflow != null) {
            identifyPracticeUser(selectedPractice.getUserId());
            //doing this because when user has only 1 practice, middleware returns the
            //"practice_home" state without any other data, (dont blame me, blame MW)
            authenticateDtoObservable.postValue(uniquePracticeWorkflow);
            return;
        }

        TransitionDTO transitionDTO = practicesInfoDtoObservable.getValue().getMetadata().getTransitions().getAuthenticate();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("language", ApplicationPreferences.getInstance().getUserLanguage());
        queryMap.put("practice_mgmt", selectedPractice.getPracticeMgmt());
        queryMap.put("practice_id", selectedPractice.getPracticeId());
        getWorkflowServiceHelper().execute(transitionDTO, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                setLoading(true);
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                setLoading(false);
                identifyPracticeUser(selectedPractice.getUserId());
                authenticateDtoObservable.postValue(workflowDTO);
            }

            @Override
            public void onFailure(ServerErrorDTO serverErrorDTO) {
                setLoading(false);
                setErrorMessage(serverErrorDTO.getMessage().getBody().getError().getMessage());
            }
        }, queryMap);
    }


    private void identifyPracticeUser(String userId) {
        MixPanelUtil.setUser(getApplication(), userId, null);
        MixPanelUtil.addCustomPeopleProperty(getApplication().getString(R.string.people_is_practice_user), true);

        String[] params = {getApplication().getString(R.string.param_login_type), getApplication().getString(R.string.param_app_mode)};
        Object[] values = {getApplication().getString(R.string.login_password), getApplication().getString(R.string.app_mode_practice)};
        MixPanelUtil.logEvent(getApplication().getString(R.string.event_signin_loginSuccess), params, values);
    }

    public void authenticatePatient(TransitionDTO transitionDTO, Map<String, String> queryMap) {
        getWorkflowServiceHelper().execute(transitionDTO, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                setLoading(true);
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                setLoading(false);
                authenticateDtoObservable.postValue(workflowDTO);
            }

            @Override
            public void onFailure(ServerErrorDTO serverErrorDTO) {
                setLoading(false);
                setErrorMessage(serverErrorDTO.getMessage().getBody().getError().getMessage());
            }
        }, queryMap);
    }
}

package com.carecloud.carepay.practice.library.payments.dialogs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BreezeDataBase;
import com.carecloud.carepay.practice.library.payments.IntegratedPaymentsQueueUploadService;
import com.carecloud.carepay.practice.library.payments.adapter.IntegratedPaymentsChooseDeviceAdapter;
import com.carecloud.carepay.practice.library.payments.interfaces.ShamrockPaymentsCallback;
import com.carecloud.carepay.practice.library.payments.models.IntegratedPaymentDeviceGroup;
import com.carecloud.carepay.practice.library.payments.models.IntegratedPaymentDeviceGroupPayload;
import com.carecloud.carepay.practice.library.payments.models.IntegratedPaymentQueueRecord;
import com.carecloud.carepay.practice.library.payments.models.ShamrockPaymentMetadata;
import com.carecloud.carepay.practice.library.payments.models.ShamrockPaymentModel;
import com.carecloud.carepay.practice.library.payments.models.ShamrockPaymentsPostModel;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.RestCallServiceCallback;
import com.carecloud.carepay.service.library.RestCallServiceHelper;
import com.carecloud.carepay.service.library.RestDef;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.common.options.OnOptionSelectedListener;
import com.carecloud.carepaylibray.common.options.SelectOptionFragment;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PatientPaymentsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.shamrocksdk.connections.DeviceInfo;
import com.carecloud.shamrocksdk.connections.interfaces.ConnectionActionCallback;
import com.carecloud.shamrocksdk.connections.interfaces.ListDeviceCallback;
import com.carecloud.shamrocksdk.connections.models.Device;
import com.carecloud.shamrocksdk.connections.models.defs.DeviceDef;
import com.carecloud.shamrocksdk.payment.ClientPayment;
import com.carecloud.shamrocksdk.payment.interfaces.ClientPaymentRequestCallback;
import com.carecloud.shamrocksdk.payment.models.PaymentRequest;
import com.carecloud.shamrocksdk.payment.models.defs.StateDef;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

/**
 * Created by lmenendez on 11/9/17
 */

public class IntegratedPaymentsChooseDeviceFragment extends BaseDialogFragment implements IntegratedPaymentsChooseDeviceAdapter.ChooseDeviceCallback {
    public static final String KEY_LAST_SELECTED_LOCATION = "last_selected_payments_location";

    private double paymentAmount;
    private PaymentsModel paymentsModel;
    private UserPracticeDTO practiceInfo;
    private ShamrockPaymentsCallback callback;
    private String userId;
    private String authToken;

    private RecyclerView deviceRecycler;
    private TextView selectedLocationText;
    private View processPaymentButton;

    private Map<String, LocationDTO> locationsMap = new HashMap<>();
    private List<DemographicsOption> locationsSelectList = new ArrayList<>();
    private LocationDTO selectedLocation;

    private Map<String, Device> deviceMap = new HashMap<>();
    private List<Device> availableDevices = new ArrayList<>();
    private Device selectedDevice;

    private Map<String, IntegratedPaymentDeviceGroup> groupMap = new HashMap<>();

    public static IntegratedPaymentsChooseDeviceFragment newInstance(PaymentsModel paymentsModel, double paymentAmount) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, paymentAmount);

        IntegratedPaymentsChooseDeviceFragment fragment = new IntegratedPaymentsChooseDeviceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (ShamrockPaymentsCallback) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement ShamrockPaymentsCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        if (args != null) {
            paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, args);
            paymentAmount = args.getDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE);
            practiceInfo = paymentsModel.getPaymentPayload().getUserPractices().get(0);
            initLocationsMap();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_payment_choose_device, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        initToolbar(view);

        deviceRecycler = view.findViewById(R.id.processing_devices_recycler);
        deviceRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        selectedLocationText = view.findViewById(R.id.selected_location);
        selectedLocationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectOptionFragment fragment = SelectOptionFragment.newInstance("");
                fragment.setOptions(locationsSelectList);
                fragment.setCallback(new OnOptionSelectedListener() {
                    @Override
                    public void onOptionSelected(DemographicsOption option, int position) {
                        selectedLocation = locationsMap.get(option.getId());
                        getApplicationPreferences().writeStringToSharedPref(KEY_LAST_SELECTED_LOCATION, option.getId());
                        selectedDevice = null;
                        updateSelectedLocation();
                    }
                });
                fragment.show(getActivity().getSupportFragmentManager(), fragment.getClass().getName());
//                showChooseDialog(getContext(), locationsSelectList);
            }
        });

        processPaymentButton = view.findViewById(R.id.process_payment);
        processPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processIntegratedPayment();
            }
        });
        refreshProcessButton();
        updateSelectedLocation();
        getDeviceGroups();
    }

    private void initToolbar(View view) {
        View closeButton = view.findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

        TextView title = view.findViewById(R.id.respons_toolbar_title);
        ViewGroup.LayoutParams layoutParams = title.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        title.setLayoutParams(layoutParams);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setText(Label.getLabel("payment_integrated_choose_device_title"));

    }

    private void initLocationsMap() {
        Integer filterLocationId = null;
        String lastSelectedLocation = getApplicationPreferences().readStringFromSharedPref(KEY_LAST_SELECTED_LOCATION, null);
        if (lastSelectedLocation == null) {
            String practiceId = getApplicationMode().getUserPracticeDTO().getPracticeId();
            String userId = getApplicationMode().getUserPracticeDTO().getUserId();
            Set<String> locationIds = getApplicationPreferences().getSelectedLocationsIds(practiceId, userId);
            if (locationIds != null && !locationIds.isEmpty()) {
                try {
                    filterLocationId = Integer.parseInt(locationIds.iterator().next());
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
            }
        }

        for (LocationDTO locationDTO : paymentsModel.getPaymentPayload().getLocations()) {
            locationsMap.put(locationDTO.getGuid(), locationDTO);
            DemographicsOption option = new DemographicsOption();
            option.setId(locationDTO.getGuid());
            option.setName(locationDTO.getName());
            option.setLabel(locationDTO.getName());
            locationsSelectList.add(option);

            if (filterLocationId != null && locationDTO.getId().equals(filterLocationId)) {
                selectedLocation = locationDTO;
            }
        }

        if (lastSelectedLocation != null) {
            selectedLocation = locationsMap.get(lastSelectedLocation);
        }

        if (selectedLocation == null) {
            selectedLocation = locationsMap.get(locationsSelectList.get(0).getId());
        }

    }

    private void initDeviceMap() {
        deviceMap.clear();
        for (Device device : availableDevices) {
            deviceMap.put(device.getDeviceId(), device);
        }
    }

    private void setAdapter() {
        if (isAdded() && getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    IntegratedPaymentsChooseDeviceAdapter adapter = (IntegratedPaymentsChooseDeviceAdapter) deviceRecycler.getAdapter();
                    if (adapter == null) {
                        adapter = new IntegratedPaymentsChooseDeviceAdapter(getContext(), availableDevices, IntegratedPaymentsChooseDeviceFragment.this);
                        deviceRecycler.setAdapter(adapter);
                    } else {
                        adapter.setDeviceList(availableDevices);
                    }
                }
            });
        }
    }

    private void updateSelectedLocation() {
        if (selectedLocation != null) {
            selectedLocationText.setText(selectedLocation.getName());
        }

        getAvailableDevices();
    }

    private void refreshProcessButton() {
        if (isAdded() && getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    processPaymentButton.setEnabled(selectedDevice != null && selectedDevice.getState().equals(DeviceDef.STATE_READY));
                }
            });
        }
    }

    @Override
    public void showProgressDialog() {
        if (isAdded() && getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    IntegratedPaymentsChooseDeviceFragment.super.showProgressDialog();
                }
            });
        }
    }

    @Override
    public void hideProgressDialog() {
        if (isAdded() && getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    IntegratedPaymentsChooseDeviceFragment.super.hideProgressDialog();
                }
            });
        }
    }

    @Override
    public void dismiss() {
        if (isAdded() && getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    IntegratedPaymentsChooseDeviceFragment.super.dismiss();
                }
            });
        }
    }

    private void toggleSelectDevice(final boolean processing) {
        Log.d("Toggle Selectable", "Processing: " + processing);
        if (isAdded() && getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    IntegratedPaymentsChooseDeviceAdapter adapter = (IntegratedPaymentsChooseDeviceAdapter) deviceRecycler.getAdapter();
                    if (adapter != null) {
                        adapter.setProcessing(processing);
                    }
                    selectedLocationText.setClickable(!processing);
                    refreshProcessButton();
                }
            });
        }
    }

    private void processIntegratedPayment() {
        IntegratedPaymentPostModel postModel = paymentsModel.getPaymentPayload().getPaymentPostModel();

        ShamrockPaymentsPostModel shamrockPaymentsPostModel = new ShamrockPaymentsPostModel().setIntegratedPaymentPostModel(postModel);
        shamrockPaymentsPostModel.setOrganizationId(paymentsModel.getPaymentPayload().getOrganizationId());
        shamrockPaymentsPostModel.setPaymentProfileId(paymentsModel.getPaymentPayload().getPaymentProfileId());
        shamrockPaymentsPostModel.setExecution(IntegratedPaymentPostModel.EXECUTION_CLOVER);

        ShamrockPaymentMetadata metadata = shamrockPaymentsPostModel.getMetadata();
        metadata.setPracticeId(practiceInfo.getPracticeId());
        metadata.setPracticeMgmt(practiceInfo.getPracticeMgmt());
        metadata.setPatientId(practiceInfo.getPatientId());
        metadata.setUserId(userId);

        if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE) {
            metadata.setBreezeUserId(getAppAuthorizationHelper().getPatientUser());
        } else {
            metadata.setBreezeUserId(userId);
        }

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("patient_id", practiceInfo.getPatientId());

        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getInitializePaymentRequest();
        String payload = DtoHelper.getStringDTO(shamrockPaymentsPostModel);
        getWorkflowServiceHelper().execute(transitionDTO, initPaymentRequestCallback, payload, queryMap);
    }

    private WorkflowServiceCallback initPaymentRequestCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();

            ShamrockPaymentModel shamrockPaymentModel = DtoHelper.getConvertedDTO(ShamrockPaymentModel.class, workflowDTO);
            ShamrockPaymentsPostModel postModel = shamrockPaymentModel.getPayload().getPostModel();
            selectedDevice.setPaymentRequestId(postModel.getDeepstreamId());
            DeviceInfo.updateDevice(userId, authToken, selectedDevice.getDeviceId(), selectedDevice);
            ClientPayment.trackPaymentRequest(userId, authToken, selectedDevice.getPaymentRequestId(), paymentRequestCallback);
        }

        @Override
        public void onFailure(String errorMessage) {
            hideProgressDialog();
            toggleSelectDevice(false);
            new CustomMessageToast(getContext(), errorMessage, CustomMessageToast.NOTIFICATION_TYPE_ERROR).show();
        }
    };

    private void getDeviceGroups() {
        String endpoint = String.format(getString(R.string.carepay_device_groups), paymentsModel.getPaymentPayload().getOrganizationId());
        String url = HttpConstants.getPaymentsUrl();

        Map<String, String> headers = new HashMap<>();
        headers.put("x-api-key", HttpConstants.getPaymentsApiKey());

        RestCallServiceHelper restCallServiceHelper = new RestCallServiceHelper(getAppAuthorizationHelper(), getApplicationMode());
        restCallServiceHelper.executeRequest(RestDef.GET, url, deviceGroupCallback, true, false, null, null, headers, "", endpoint);

    }

    private void getAvailableDevices() {
        if (selectedLocation == null || groupMap.isEmpty()) {
            return;
        }

        userId = getApplicationMode().getUserPracticeDTO().getUserId();
        authToken = getAppAuthorizationHelper().getIdToken();
        IntegratedPaymentDeviceGroup deviceGroup = groupMap.get(selectedLocation.getGuid());
        if (deviceGroup != null) {
            DeviceInfo.listDevices(userId, authToken, deviceGroup.getGroupId(), listDeviceCallback, connectionActionCallback);
        }
    }

    private void onPaymentCompleted(String paymentRequestId, JsonElement recordObject) {
        releasePaymentRequest(paymentRequestId);
        selectedDevice = null;
        DeviceInfo.releaseAllDevices(userId, authToken);

        postCompletedPayment(paymentRequestId, recordObject);

    }

    private void postCompletedPayment(String paymentRequestId, JsonElement recordObject) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("patient_id", practiceInfo.getPatientId());
        queryMap.put("deepstream_record_id", paymentRequestId);

        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getRecordPayment();
        getWorkflowServiceHelper().execute(transitionDTO, getPostPaymentCallback(paymentRequestId, recordObject), queryMap);
    }

    //store in local DB
    private void queuePayment(String paymentRequestId) {
        final IntegratedPaymentQueueRecord paymentQueueRecord = new IntegratedPaymentQueueRecord();
        paymentQueueRecord.setPracticeID(practiceInfo.getPracticeId());
        paymentQueueRecord.setPracticeMgmt(practiceInfo.getPracticeMgmt());
        paymentQueueRecord.setPatientID(practiceInfo.getPatientId());
        paymentQueueRecord.setDeepstreamId(paymentRequestId);
        paymentQueueRecord.setUsername(userId);

        Gson gson = new Gson();
        paymentQueueRecord.setQueueTransition(gson.toJson(paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getQueuePayment()));

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                BreezeDataBase database = BreezeDataBase.getDatabase(getContext());
                database.getIntegratedAndroidPayDao().insert(paymentQueueRecord);
            }
        });

        Intent intent = new Intent(getContext(), IntegratedPaymentsQueueUploadService.class);
        getContext().startService(intent);

    }

    private void releasePaymentRequest(String paymentRequestId) {
        ClientPayment.releasePaymentRequest(userId, authToken, paymentRequestId);
    }

    private RestCallServiceCallback deviceGroupCallback = new RestCallServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(JsonElement jsonElement) {
            hideProgressDialog();
            Log.d("Integrated Callback", jsonElement.toString());
            Gson gson = new Gson();
            IntegratedPaymentDeviceGroupPayload payload = gson.fromJson(jsonElement, IntegratedPaymentDeviceGroupPayload.class);
            if (payload != null) {
                for (IntegratedPaymentDeviceGroup deviceGroup : payload.getDeviceGroupList()) {
                    groupMap.put(deviceGroup.getGroupName(), deviceGroup);
                }
            }
            if (isVisible()) {
                updateSelectedLocation();
            }
        }

        @Override
        public void onFailure(String errorMessage) {
            hideProgressDialog();
            new CustomMessageToast(getContext(), errorMessage, CustomMessageToast.NOTIFICATION_TYPE_ERROR).show();
        }
    };

    private WorkflowServiceCallback getPostPaymentCallback(final String paymentRequestId, final JsonElement recordObject) {
        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                callback.showPaymentConfirmation(workflowDTO);
                dismiss();
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();

                queuePayment(paymentRequestId);

                Gson gson = new Gson();
                IntegratedPatientPaymentPayload patientPaymentPayload = gson.fromJson(recordObject, IntegratedPatientPaymentPayload.class);
                PatientPaymentsDTO patientPayment = new PatientPaymentsDTO();
                patientPayment.setPayload(patientPaymentPayload);
                paymentsModel.getPaymentPayload().setPatientPayments(patientPayment);
                WorkflowDTO workflowDTO = DtoHelper.getConvertedDTO(WorkflowDTO.class, DtoHelper.getStringDTO(paymentsModel));
                callback.showPaymentConfirmation(workflowDTO);
                dismiss();
            }
        };
    }

    private ListDeviceCallback listDeviceCallback = new ListDeviceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(List<Device> devices) {
            availableDevices = devices;
            initDeviceMap();
            setAdapter();
        }

        @Override
        public void onFailure(String errorMessage) {
            new CustomMessageToast(getContext(), errorMessage, CustomMessageToast.NOTIFICATION_TYPE_ERROR).show();
        }

        @Override
        public void onListChanged() {
            getAvailableDevices();
        }
    };

    private ConnectionActionCallback connectionActionCallback = new ConnectionActionCallback() {
        @Override
        public void onConnectionError(String deviceName, String errorCode, String errorMessage) {
            new CustomMessageToast(getContext(), errorMessage, CustomMessageToast.NOTIFICATION_TYPE_ERROR).show();
        }

        @Override
        public void onConnectionDestroyed(String deviceName) {
            int index = deviceName.indexOf("/");
            if (index + 1 > deviceName.length()) {
                return;
            }
            Device device = deviceMap.get(deviceName.substring(index));
            device.setState(DeviceDef.STATE_OFFLINE);
            setAdapter();
            if (selectedDevice != null && selectedDevice.getDeviceId().equals(deviceName)) {
                selectedDevice = device;
                refreshProcessButton();
            }
        }

        @Override
        public void onConnectionUpdate(String deviceName, Device device) {
            Device findDevice = deviceMap.get(device.getDeviceId());
            findDevice.setState(device.getState());
            findDevice.setProcessing(device.isProcessing());
            findDevice.setPaymentRequestId(device.getPaymentRequestId());
            setAdapter();
            if (selectedDevice != null && selectedDevice.getDeviceId().equals(device.getDeviceId())) {
                selectedDevice = findDevice;
                if (selectedDevice.getState().equals(DeviceDef.STATE_IN_USE)) {
                    toggleSelectDevice(true);
                } else {
                    toggleSelectDevice(false);
                }
                refreshProcessButton();
            }
        }

        @Override
        public void onConnectionUpdateFail(String deviceName, JsonElement recordObject) {

        }
    };

    private ClientPaymentRequestCallback paymentRequestCallback = new ClientPaymentRequestCallback() {

        @Override
        public void onPaymentRequestUpdate(String paymentRequestId, PaymentRequest paymentRequest, JsonElement recordObject) {
            switch (paymentRequest.getState()) {
                default:
                case StateDef.STATE_ACKNOWLEDGED:
                case StateDef.STATE_CAPTURED:
                    showProgressDialog();
                    break;
                case StateDef.STATE_CANCELED:
                    releasePaymentRequest(paymentRequestId);
                    hideProgressDialog();
                    break;
                case StateDef.STATE_COMPLETED:
                case StateDef.STATE_PROCESSING:
                case StateDef.STATE_RECORDING:
                    hideProgressDialog();
                    onPaymentCompleted(paymentRequestId, recordObject);
                    break;
            }

        }

        @Override
        public void onPaymentRequestUpdateFail(String paymentRequestId, JsonElement recordObject) {

        }

        @Override
        public void onPaymentConnectionFailure(String message) {

        }

        @Override
        public void onPaymentRequestDestroyed(String paymentRequestId) {

        }
    };

    @Override
    public void onDeviceSelected(Device device) {
        selectedDevice = device;
        refreshProcessButton();
    }
}

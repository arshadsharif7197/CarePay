package com.carecloud.carepay.practice.library.payments.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.payments.adapter.IntegratedPaymentsChooseDeviceAdapter;
import com.carecloud.carepay.practice.library.payments.interfaces.ShamrockPaymentsCallback;
import com.carecloud.carepay.practice.library.payments.models.IntegratedPaymentDeviceGroup;
import com.carecloud.carepay.practice.library.payments.models.IntegratedPaymentDeviceGroupPayload;
import com.carecloud.carepay.practice.library.payments.models.ShamrockPaymentMetadata;
import com.carecloud.carepay.practice.library.payments.models.ShamrockPaymentsPostModel;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.RestCallServiceCallback;
import com.carecloud.carepay.service.library.RestCallServiceHelper;
import com.carecloud.carepay.service.library.RestDef;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.adapters.CustomOptionsAdapter;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.shamrocksdk.connections.DeviceInfo;
import com.carecloud.shamrocksdk.connections.interfaces.ConnectionActionCallback;
import com.carecloud.shamrocksdk.connections.interfaces.ListDeviceCallback;
import com.carecloud.shamrocksdk.connections.models.Device;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lmenendez on 11/9/17
 */

public class IntegratedPaymentsChooseDeviceFragment extends BaseDialogFragment implements IntegratedPaymentsChooseDeviceAdapter.ChooseDeviceCallback {

    private double paymentAmount;
    private PaymentsModel paymentsModel;
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

    public static IntegratedPaymentsChooseDeviceFragment newInstance(PaymentsModel paymentsModel, double paymentAmount){
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, paymentAmount);

        IntegratedPaymentsChooseDeviceFragment fragment = new IntegratedPaymentsChooseDeviceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            callback = (ShamrockPaymentsCallback) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached context must implement ShamrockPaymentsCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        Bundle args = getArguments();
        if(args != null){
            paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, args);
            paymentAmount = args.getDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE);
            initLocationsMap();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_payment_choose_device, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        initToolbar(view);

        deviceRecycler = (RecyclerView) view.findViewById(R.id.processing_devices_recycler);
        deviceRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        selectedLocationText = (TextView) view.findViewById(R.id.selected_location);
        selectedLocationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog(getContext(), locationsSelectList);
            }
        });

        processPaymentButton = view.findViewById(R.id.process_payment);
        processPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processShamrockPayment();
            }
        });
        refreshProcessButton();
        updateSelectedLocation();
        getDeviceGroups();
    }

    private void initToolbar(View view){
        View closeButton = view.findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                callback.dismissChooseDeviceList(paymentAmount, paymentsModel);
            }
        });

        TextView title = (TextView) view.findViewById(R.id.respons_toolbar_title);
        ViewGroup.LayoutParams layoutParams = title.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        title.setLayoutParams(layoutParams);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setText(Label.getLabel("payment_integrated_choose_device_title"));

    }

    private void initLocationsMap(){
        for(LocationDTO locationDTO : paymentsModel.getPaymentPayload().getLocations()){
            locationsMap.put(locationDTO.getGuid(), locationDTO);
            DemographicsOption option = new DemographicsOption();
            option.setId(locationDTO.getGuid());
            option.setName(locationDTO.getName());
            option.setLabel(locationDTO.getName());
            locationsSelectList.add(option);
        }

        String practiceId = getApplicationMode().getUserPracticeDTO().getPracticeId();
        String userId = getApplicationMode().getUserPracticeDTO().getUserId();
        Set<String> locationIds = getApplicationPreferences().getSelectedLocationsIds(practiceId, userId);
        if(locationIds != null && !locationIds.isEmpty()){
            selectedLocation = locationsMap.get(locationIds.iterator().next());
        }else{
            selectedLocation = locationsMap.get(locationsSelectList.get(0).getId());
        }

    }

    private void initDeviceMap(){
        deviceMap.clear();
        for(Device device : availableDevices){
            deviceMap.put(device.getDeviceId(), device);
        }
    }

    private void setAdapter(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                IntegratedPaymentsChooseDeviceAdapter adapter = (IntegratedPaymentsChooseDeviceAdapter) deviceRecycler.getAdapter();
                if(adapter == null){
                    adapter = new IntegratedPaymentsChooseDeviceAdapter(getContext(), availableDevices, IntegratedPaymentsChooseDeviceFragment.this);
                    deviceRecycler.setAdapter(adapter);
                }else{
                    adapter.setDeviceList(availableDevices);
                }
            }
        });
    }

    private void updateSelectedLocation(){
        if(selectedLocation != null){
            selectedLocationText.setText(selectedLocation.getName());
        }

        getAvailableDevices();
    }

    private void refreshProcessButton(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                processPaymentButton.setEnabled(selectedDevice != null && selectedDevice.getState().equals(Device.STATE_READY));
            }
        });
    }

    private void toggleRecycler(final boolean processing){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                IntegratedPaymentsChooseDeviceAdapter adapter = (IntegratedPaymentsChooseDeviceAdapter) deviceRecycler.getAdapter();
                if(adapter != null){
                    adapter.setProcessing(processing);
                }
            }
        });
    }

    private void processShamrockPayment(){
        IntegratedPaymentPostModel postModel = paymentsModel.getPaymentPayload().getPaymentPostModel();

        ShamrockPaymentsPostModel shamrockPaymentsPostModel = new ShamrockPaymentsPostModel().setIntegratedPaymentPostModel(postModel);
        shamrockPaymentsPostModel.setOrganizationId(paymentsModel.getPaymentPayload().getOrganizationId());
        shamrockPaymentsPostModel.setPaymentProfileId(paymentsModel.getPaymentPayload().getPaymentProfileId());
        shamrockPaymentsPostModel.setExecution(IntegratedPaymentPostModel.EXECUTION_CLOVER);

        ShamrockPaymentMetadata metadata = shamrockPaymentsPostModel.getMetadata();
        metadata.setPracticeId(getApplicationMode().getUserPracticeDTO().getPracticeId());
        metadata.setPracticeMgmt(getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
        metadata.setPatientId(paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getMetadata().getPatientId());//todo don't hardcode this
        metadata.setUserId(userId);
        metadata.setBreezeUserId(getAppAuthorizationHelper().getPatientUser());

        String endpoint = getString(R.string.carepay_init_payment_request);
        String url = HttpConstants.getPaymentsUrl();

        Map<String, String> headers = new HashMap<>();
        headers.put("x-api-key", HttpConstants.getPaymentsApiKey());

        Gson gson = new Gson();
        RestCallServiceHelper restCallServiceHelper = new RestCallServiceHelper(getAppAuthorizationHelper(), getApplicationMode());
        restCallServiceHelper.executeRequest(RestDef.POST, url, initPaymentCallback, true, false, null, null, headers, gson.toJson(shamrockPaymentsPostModel), endpoint);
        toggleRecycler(true);
    }

    private RestCallServiceCallback initPaymentCallback = new RestCallServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(JsonElement jsonElement) {
            hideProgressDialog();

            Gson gson = new Gson();
            ShamrockPaymentsPostModel postModel = gson.fromJson(jsonElement, ShamrockPaymentsPostModel.class);
            selectedDevice.setPaymentRequestId(postModel.getDeepstreamId());
            DeviceInfo.updateDevice(userId, authToken, selectedDevice.getDeviceId(), selectedDevice);

        }

        @Override
        public void onFailure(String errorMessage) {
            hideProgressDialog();
            toggleRecycler(false);
            new CustomMessageToast(getContext(), errorMessage, CustomMessageToast.NOTIFICATION_TYPE_ERROR).show();
        }
    };

    private void showChooseDialog(Context context, List<DemographicsOption> options) {

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
        titleTextView.setText(Label.getLabel("payment_choose_location"));
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
                selectedLocation = locationsMap.get(selectedOption.getId());
                selectedDevice = null;
                updateSelectedLocation();
                alert.dismiss();
            }
        };
        listView.setOnItemClickListener(clickListener);
    }

    private void getDeviceGroups(){
        String endpoint = String.format(getString(R.string.carepay_device_groups), paymentsModel.getPaymentPayload().getOrganizationId());
        String url = HttpConstants.getPaymentsUrl();

        Map<String, String> headers = new HashMap<>();
        headers.put("x-api-key", HttpConstants.getPaymentsApiKey());

        RestCallServiceHelper restCallServiceHelper = new RestCallServiceHelper(getAppAuthorizationHelper(), getApplicationMode());
        restCallServiceHelper.executeRequest(RestDef.GET, url, deviceGroupCallback, true, false, null, null, headers, "", endpoint);

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
            if(payload != null){
                for(IntegratedPaymentDeviceGroup deviceGroup : payload.getDeviceGroupList()){
                    groupMap.put(deviceGroup.getGroupName(), deviceGroup);
                }
            }
            updateSelectedLocation();
        }

        @Override
        public void onFailure(String errorMessage) {
            hideProgressDialog();
            new CustomMessageToast(getContext(), errorMessage, CustomMessageToast.NOTIFICATION_TYPE_ERROR).show();
        }
    };


    private void getAvailableDevices(){
        if(selectedLocation == null || groupMap.isEmpty()){
            return;
        }

        userId = getApplicationMode().getUserPracticeDTO().getUserId();
        authToken = getAppAuthorizationHelper().getIdToken();
        IntegratedPaymentDeviceGroup deviceGroup = groupMap.get(selectedLocation.getGuid());
        if(deviceGroup != null) {
            DeviceInfo.listDevices(userId, authToken, deviceGroup.getGroupId(), listDeviceCallback, connectionActionCallback);
        }
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
            if(index + 1 > deviceName.length()){
                return;
            }
            Device device = deviceMap.get(deviceName.substring(index));
            device.setState(Device.STATE_OFFLINE);
            setAdapter();
            if(selectedDevice != null && selectedDevice.getDeviceId().equals(deviceName)){
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
            if(selectedDevice != null && selectedDevice.getDeviceId().equals(deviceName)){
                selectedDevice = findDevice;
                refreshProcessButton();
                if(selectedDevice.getState().equals(Device.STATE_IN_USE)){
                    //// TODO: 11/9/17 start tracking payment request status
                }else{
                    toggleRecycler(false);
                }
            }

        }

        @Override
        public void onConnectionUpdateFail(String deviceName, JsonElement recordObject) {

        }
    };

    @Override
    public void onDeviceSelected(Device device) {
        selectedDevice = device;
        refreshProcessButton();
    }
}

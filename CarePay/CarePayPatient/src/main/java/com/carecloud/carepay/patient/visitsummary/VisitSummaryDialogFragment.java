package com.carecloud.carepay.patient.visitsummary;


import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.myhealth.dtos.MyHealthDto;
import com.carecloud.carepay.patient.myhealth.dtos.PatientDto;
import com.carecloud.carepay.patient.visitsummary.dto.VisitSummaryDTO;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.adapters.CustomOptionsAdapter;
import com.carecloud.carepaylibray.appointments.models.PortalSetting;
import com.carecloud.carepaylibray.appointments.models.PortalSettingDTO;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.common.DatePickerFragment;
import com.carecloud.carepaylibray.customcomponents.CarePayProgressButton;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.FileDownloadUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.ValidationHelper;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pjohnson on 1/3/19.
 */
public class VisitSummaryDialogFragment extends BaseDialogFragment {

    public static final long RETRY_TIME = 10000;
    private static final int MY_PERMISSIONS_VS_WRITE_EXTERNAL_STORAGE = 10;
    public static final int MAX_NUMBER_RETRIES = 6;
    private FragmentActivityInterface callback;
    private MyHealthDto myHealthDto;
    private UserPracticeDTO selectedPractice;
    private Date fromDate;
    private Date toDate;
    private TextView dateFromTextView;
    private TextView dateToTextView;
    private CheckBox encryptedCheckBox;
    private EditText emailEditText;
    private RadioButton pdfOption;
    private RadioButton xmlOption;
    private CarePayProgressButton exportButton;
    private long enqueueId;
    private Handler handler;
    private String format;
    private int retryIntent = 0;

    private boolean isExporting = false;

    public static VisitSummaryDialogFragment newInstance() {
        return new VisitSummaryDialogFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (FragmentActivityInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("the Activity must implement MyHealthInterface");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myHealthDto = (MyHealthDto) callback.getDto();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_visit_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpUI(view);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                DownloadManager.Query query = new DownloadManager.Query();
                DownloadManager dm = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);

                query.setFilterById(enqueueId);
                final Cursor cursor = dm.query(query);
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
//                    int reasonIndex = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
                    if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(columnIndex)) {
                        exportButton.setEnabled(true);
//                        exportButton.setProgressEnabled(false);
                        if (format.equals("pdf")) {
                            exportButton.setText(Label.getLabel("visitSummary.createVisitSummary.button.label.openFile"));
                            exportButton.setOnClickListener(v -> {
                                String downloadLocalUri = cursor
                                        .getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                                if (downloadLocalUri != null) {
                                    String downloadMimeType = cursor.getString(cursor
                                            .getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));
                                    FileDownloadUtil
                                            .openDownloadedAttachment(context, Uri.parse(downloadLocalUri), downloadMimeType);
                                }
                            });
                        } else {
                            exportButton.setText(Label
                                    .getLabel("visitSummary.createVisitSummary.button.label.openDirectory"));
                            exportButton.setOnClickListener(v -> FileDownloadUtil.openDownloadDirectory(getContext()));
                        }
                        enqueueId = -1;
                    } else if (DownloadManager.STATUS_PAUSED == cursor.getInt(columnIndex)) {
                        //TODO: test if this can occur
                    } else {
                        Log.e("DownloadManager", "Status: " + cursor.getInt(columnIndex));
                    }
                }
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(receiver);
        if (enqueueId > 0) {
            DownloadManager dm = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            dm.remove(enqueueId);
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        super.onStop();
    }

    private void setUpUI(View view) {
        View closeView = view.findViewById(R.id.dialog_close_header);
        if (closeView != null) {
            view.findViewById(R.id.dialog_close_header).setOnClickListener(view1 -> dismiss());
        }

        final TextView practiceTextView = view.findViewById(R.id.practiceTextView);
        TextInputLayout practiceTextInputLayout = view.findViewById(R.id.practiceTextInputLayout);
        final List<UserPracticeDTO> practices = filterPractices(myHealthDto.getPayload().getPracticeInformation());
        practiceTextView.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(practiceTextInputLayout, null));
        if (practices.size() == 1) {
            selectedPractice = practices.get(0);
            practiceTextView.setText(selectedPractice.getPracticeName());
            practiceTextView.getOnFocusChangeListener().onFocusChange(practiceTextView, true);
        } else {
            practiceTextView.setOnClickListener(v -> showChooseDialog(getContext(), practices,
                    Label.getLabel("visitSummary.createVisitSummary.choosePracticeDialog.title.choose"),
                    (option, index) -> {
                        selectedPractice = practices.get(index);
                        practiceTextView.setText(selectedPractice.getPracticeName());
                        practiceTextView.getOnFocusChangeListener().onFocusChange(practiceTextView, true);
                        enableExportButton();
                    }));
        }

        TextInputLayout dateFromTextInputLayout = view.findViewById(R.id.dateFromTextInputLayout);
        dateFromTextView = view.findViewById(R.id.dateFromTextView);
        dateFromTextView.setOnClickListener(v -> showCalendar(DatePickerFragment.FROM_DATE_FLAG,
                dateFromTextView.getText().toString()));
        dateFromTextView.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(dateFromTextInputLayout,
                null));

        TextInputLayout dateToTextInputLayout = view.findViewById(R.id.dateToTextInputLayout);
        dateToTextView = view.findViewById(R.id.dateToTextView);
        dateToTextView.setOnClickListener(v -> showCalendar(DatePickerFragment.TO_DATE_FLAG,
                dateToTextView.getText().toString()));
        dateToTextView.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(dateToTextInputLayout,
                null));

        TextInputLayout emailTextInputLayout = view.findViewById(R.id.emailTextInputLayout);
        emailEditText = view.findViewById(R.id.emailEditText);
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                enableExportButton();
            }
        });
        emailEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(emailTextInputLayout, null));

        CompoundButton.OnCheckedChangeListener optionListener = (compoundButton, b) -> enableExportButton();
        pdfOption = view.findViewById(R.id.pdfOption);
        xmlOption = view.findViewById(R.id.xmlOption);
        pdfOption.setOnCheckedChangeListener(optionListener);
        xmlOption.setOnCheckedChangeListener(optionListener);

        exportButton = view.findViewById(R.id.exportButton);
        exportButton.setOnClickListener(exportSummaryListener);

        encryptedCheckBox = view.findViewById(R.id.encryptedCheckBox);
    }

    private List<UserPracticeDTO> filterPractices(List<UserPracticeDTO> practiceInformation) {
        List<UserPracticeDTO> filteredPractices = new ArrayList<>();
        for (UserPracticeDTO userPracticeDTO : practiceInformation) {
            if (userPracticeDTO.isVisitSummaryEnabled()) {
                for (PortalSettingDTO portalSettingDTO : myHealthDto.getPayload().getPortalSettings()) {
                    if (userPracticeDTO.getPracticeId().equals(portalSettingDTO.getMetadata().getPracticeId())) {
                        for (PortalSetting portalSetting : portalSettingDTO.getPayload()) {
                            if (portalSetting.getName().toLowerCase().equals("visit summary")
                                    && portalSetting.getStatus().toLowerCase().equals("a")
                                    && myHealthDto.getPayload().canViewAndCreateVisitSummaries(userPracticeDTO.getPracticeId())) {
                                filteredPractices.add(userPracticeDTO);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return filteredPractices;
    }

    private void enableExportButton() {
        boolean wasEnabled = exportButton.isEnabled();
        exportButton.setEnabled(formIsValid());
        if (!wasEnabled && exportButton.isEnabled()) {
            resetExportButton();
        }
    }

    private boolean formIsValid() {
        if (selectedPractice == null) {
            return false;
        }
        if (fromDate == null) {
            return false;
        }
        if (toDate == null) {
            return false;
        }
        String email = emailEditText.getText().toString();
        if (!StringUtil.isNullOrEmpty(email) && !ValidationHelper.isValidEmail(email)) {
            return false;
        }
        return (pdfOption.isChecked() || xmlOption.isChecked()) && !isExporting;
    }

    private void resetExportButton() {
        exportButton.setText(Label.getLabel("visitSummary.createVisitSummary.button.label.export"));
        exportButton.setOnClickListener(exportSummaryListener);
    }

    private void showCalendar(int flag, String showDate) {
        Calendar fromCalendar = Calendar.getInstance();
        Calendar toCalendar = Calendar.getInstance();
        Calendar showCalendar = Calendar.getInstance();
        if (flag == DatePickerFragment.FROM_DATE_FLAG) {
            fromCalendar.set(2000, 0, 1);
            if (toDate != null) {
                toCalendar.setTimeInMillis(toDate.getTime());
                toCalendar.set(Calendar.HOUR_OF_DAY, 23);
            }
        } else {
            toCalendar.add(Calendar.DATE, 1);
            if (fromDate != null) {
                fromCalendar.setTimeInMillis(fromDate.getTime());
                fromCalendar.add(Calendar.DATE, 1);
                showCalendar = fromCalendar;
            }
        }
        Date selectedDate = null;
        if (!StringUtil.isNullOrEmpty(showDate)) {
            selectedDate = DateUtil.getInstance().setDateRaw(showDate).getDate();
        }
        DatePickerFragment fragment = DatePickerFragment
                .newInstance(Label.getLabel("pick_date_heading"),
                        fromCalendar.getTime(),
                        toCalendar.getTime(),
                        selectedDate,
                        showCalendar.getTime(),
                        (selectedDate1, flag1) -> {
                            if (flag1 == DatePickerFragment.FROM_DATE_FLAG) {
                                if (selectedDate1 != null) {
                                    fromDate = selectedDate1;
                                    dateFromTextView.setText(DateUtil.getInstance().setDate(fromDate)
                                            .toStringWithFormatMmSlashDdSlashYyyy());
                                    dateFromTextView.getOnFocusChangeListener().onFocusChange(dateFromTextView, true);
                                    dateToTextView.setEnabled(true);
                                }
                            } else {
                                if (selectedDate1 != null) {
                                    toDate = selectedDate1;
                                    dateToTextView.setText(DateUtil.getInstance().setDate(toDate)
                                            .toStringWithFormatMmSlashDdSlashYyyy());
                                    dateToTextView.getOnFocusChangeListener().onFocusChange(dateToTextView, true);
                                }
                            }
                            enableExportButton();
                        },
                        flag);
        callback.displayDialogFragment(fragment, true);
    }

    private void showChooseDialog(Context context,
                                  List<UserPracticeDTO> options,
                                  String title,
                                  final OnOptionSelectedListener listener) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setNegativeButton(Label.getLabel("demographics_cancel_label"),
                (dialogInterface, pos) -> dialogInterface.dismiss());

        View customView = LayoutInflater.from(context).inflate(R.layout.alert_list_layout,
                null, false);
        dialog.setView(customView);
        TextView titleTextView = customView.findViewById(R.id.title_view);
        titleTextView.setText(title);
        titleTextView.setVisibility(View.VISIBLE);


        ListView listView = customView.findViewById(R.id.dialoglist);
        CustomOptionsAdapter customOptionsAdapter = new CustomOptionsAdapter(context, options);
        listView.setAdapter(customOptionsAdapter);


        final AlertDialog alert = dialog.create();
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.show();

        AdapterView.OnItemClickListener clickListener = (adapterView, view, position, row) -> {
            UserPracticeDTO practice = (UserPracticeDTO) adapterView.getAdapter().getItem(position);
            if (listener != null) {
                listener.onOptionSelected(practice.getPracticeName(), position);
            }
            alert.dismiss();
        };
        listView.setOnItemClickListener(clickListener);
    }

    private void callVisitSummaryService(final UserPracticeDTO selectedPractice) {
        TransitionDTO transition = myHealthDto.getMetadata().getLinks().getVisitSummary();
        JsonObject query = new JsonObject();
        query.addProperty("practice_id", selectedPractice.getPracticeId());
        query.addProperty("patient_id", getPatientGuid(selectedPractice));
        query.addProperty("send_unsecured", encryptedCheckBox.isChecked());
        query.addProperty("practice_mgmt", selectedPractice.getPracticeMgmt());
        format = pdfOption.isChecked() ? "pdf" : "xml";
        query.addProperty("format", pdfOption.isChecked() ? "pdf" : "xml");
        if (!StringUtil.isNullOrEmpty(emailEditText.getText().toString())) {
            query.addProperty("email", emailEditText.getText().toString());
        }
        query.addProperty("start_date", DateUtil.getInstance().setDate(fromDate)
                .toStringWithFormatYyyyDashMmDashDd());
        query.addProperty("end_date", DateUtil.getInstance().setDate(toDate)
                .toStringWithFormatYyyyDashMmDashDd());
        ((BaseActivity) getActivity()).getWorkflowServiceHelper().execute(transition, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                VisitSummaryDTO visitSummaryDTO = DtoHelper.getConvertedDTO(VisitSummaryDTO.class, workflowDTO);
                ((BaseActivity) getActivity()).hideProgressDialog();
                isExporting = true;
                exportButton.setEnabled(false);
                exportButton.setText(Label.getLabel("visitSummary.createVisitSummary.button.label.processing"));
//                exportButton.setProgressEnabled(true);
                callForStatus(visitSummaryDTO.getPayload().getVisitSummaryRequest().getJobId(), selectedPractice, format);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                Log.e("okHttp", exceptionMessage);
                hideProgressDialog();
                showErrorNotification(Label.getLabel("visitSummary.createVisitSummary.error.label.downloadError"));
            }
        }, query.toString());
    }

    private void callForStatus(final String jobId, final UserPracticeDTO selectedPractice, final String format) {
        TransitionDTO transition = myHealthDto.getMetadata().getLinks().getVisitSummaryStatus();
        HashMap<String, String> query = new HashMap<>();
        query.put("job_id", jobId);
        query.put("practice_mgmt", selectedPractice.getPracticeMgmt());
        ((BaseActivity) getActivity()).getWorkflowServiceHelper().execute(transition, new WorkflowServiceCallback() {

            @Override
            public void onPreExecute() {
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                VisitSummaryDTO visitSummaryDTO = null;
                try {
                    visitSummaryDTO = DtoHelper.getConvertedDTO(VisitSummaryDTO.class, workflowDTO);
                } catch (Exception ex) {
                    onFailure("");
                }
                String status = visitSummaryDTO.getPayload().getVisitSummary().getStatus();
                if (retryIntent > MAX_NUMBER_RETRIES) {
                    resetProcess();
                } else if (status.equals("queued") || status.equals("working")) {
                    retryIntent++;
                    handler = new Handler();
                    handler.postDelayed(() -> callForStatus(jobId, selectedPractice, format), RETRY_TIME);
                }
            }

            @Override
            public void onFailure(String exceptionMessage) {
                Log.e("OkHttp", exceptionMessage);
                if (exceptionMessage.contains("JSON")) {
                    downloadFile(jobId, selectedPractice, format);
                } else {
                    resetProcess();
                }
                isExporting = false;
//                exportButton.setProgressEnabled(false);
                exportButton.setEnabled(formIsValid());
            }
        }, query);
    }

    private void resetProcess() {
        retryIntent = 0;
        isExporting = false;
        exportButton.setEnabled(true);
//        exportButton.setProgressEnabled(false);
        exportButton.setText(Label.getLabel("visitSummary.createVisitSummary.button.label.export"));
        showErrorNotification(Label.getLabel("visitSummary.createVisitSummary.error.label.downloadError"));
    }

    private void downloadFile(String jobId, UserPracticeDTO selectedPractice, String format) {
        TransitionDTO transitionDTO = myHealthDto.getMetadata().getLinks().getVisitSummaryStatus();
        String url = String.format("%s?%s=%s&%s=%s", transitionDTO.getUrl(), "job_id",
                jobId, "practice_mgmt", selectedPractice.getPracticeMgmt());
        if (format.equals("pdf")) {
            enqueueId = FileDownloadUtil.downloadPdf(getContext(), url, selectedPractice.getPracticeName(),
                    "." + format, "Visit Summary");
        } else {
            Map<String, String> headers = new HashMap<>();
            headers.put("x-api-key", HttpConstants.getApiStartKey());
            headers.put("username", getAppAuthorizationHelper().getCurrUser());
            headers.put("Authorization", getAppAuthorizationHelper().getIdToken());
            enqueueId = FileDownloadUtil.downloadFile(getContext(), url, selectedPractice.getPracticeName(),
                    format, "Visit Summary", headers);
        }
    }

    private String getPatientGuid(UserPracticeDTO selectedPractice) {
        String patientGuid = "";
        for (PatientDto patientDto : myHealthDto.getPayload().getMyHealthData().getPatient().getPatients()) {
            if (patientDto.getBusinessEntity().getGuid().equals(selectedPractice.getPracticeId())) {
                patientGuid = patientDto.getGuid();
            }
        }
        return patientGuid;
    }

    private View.OnClickListener exportSummaryListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (formIsValid()) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PermissionChecker.PERMISSION_GRANTED && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_VS_WRITE_EXTERNAL_STORAGE);
                } else {
                    callVisitSummaryService(selectedPractice);
                }
            }
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_VS_WRITE_EXTERNAL_STORAGE
                && (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            callVisitSummaryService(selectedPractice);
        }
    }

    public interface OnOptionSelectedListener {
        void onOptionSelected(String option, int index);
    }

    @Override
    public void onResume() {
        super.onResume();
        resetExportButton();
    }
}

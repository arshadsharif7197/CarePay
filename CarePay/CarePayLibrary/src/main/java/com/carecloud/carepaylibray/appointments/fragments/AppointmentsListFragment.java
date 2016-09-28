package com.carecloud.carepaylibray.appointments.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.activities.AddAppointmentActivity;
import com.carecloud.carepaylibray.appointments.activities.AppointmentsActivity;
import com.carecloud.carepaylibray.appointments.adapters.AppointmentsAdapter;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentSectionHeader;
import com.carecloud.carepaylibray.appointments.utils.CustomPopupNotification;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.utils.ApplicationPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AppointmentsListFragment extends Fragment {
    private static final String LOG_TAG = AppointmentsListFragment.class.getSimpleName();
    private AppointmentModel aptItem;
    private AppointmentsAdapter appointmentsAdapter;
    private ArrayList<AppointmentModel> appointmentsItems = new ArrayList<>();
    private ArrayList<Object> appointmentListWithHeader;
    private RecyclerView appointmentRecyclerView;
    private AppointmentsListFragment appointmentsListFragment;

    public static boolean showNewAddedAppointment;
    private CustomPopupNotification popup;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (showNewAddedAppointment) {
            showNewAddedAppointment();
        }
    }

    /**
     * This function will check today's appointment
     * and notify if its within 2 hours
     */
    private void checkUpcomingAppointmentForReminder() {
        if (appointmentsItems != null && !appointmentsItems.isEmpty() &&
                !appointmentsItems.get(0).getAppointmentId().equalsIgnoreCase(
                ApplicationPreferences.Instance.readStringFromSharedPref(
                        CarePayConstants.PREF_LAST_REMINDER_POPUP_APPT_ID))) {

            try {
                String appointmentTimeStr = appointmentsItems.get(0).getAppointmentTime();
                String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());

                SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                Date appointmentDate = format.parse(appointmentTimeStr);
                Date currentDate = format.parse(currentTime);

                long differenceInMilli = appointmentDate.getTime() - currentDate.getTime();
                long differenceInMinutes = TimeUnit.MILLISECONDS.toMinutes(differenceInMilli);

                if (differenceInMinutes <= CarePayConstants.APPOINTMENT_REMINDER_TIME_IN_MINUTES &&
                        differenceInMinutes > 0) {

                    popup = new CustomPopupNotification(getActivity(), getView(), getString(R.string.checkin_early),
                            getString(R.string.dismiss),
                            getString(R.string.apt_popup_message_text, appointmentsItems.get(0).getDoctorName()),
                            positiveActionListener, negativeActionListener);
                    popup.showPopWindow();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private View.OnClickListener negativeActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            popup.dismiss();
            popup = null;

            ApplicationPreferences.Instance.writeStringToSharedPref(CarePayConstants.PREF_LAST_REMINDER_POPUP_APPT_ID,
                    appointmentsItems.get(0).getAppointmentId());
        }
    };

    private View.OnClickListener positiveActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            popup.dismiss();
            popup = null;

            ApplicationPreferences.Instance.writeStringToSharedPref(CarePayConstants.PREF_LAST_REMINDER_POPUP_APPT_ID,
                    appointmentsItems.get(0).getAppointmentId());

            //TODO: Go for next flow
        }
    };

    private void showNewAddedAppointment() {
        final AppointmentModel model = ((AppointmentsActivity) getActivity()).getModel();

        if (appointmentsItems != null && appointmentsAdapter != null) {
            AppointmentModel newAppointmentEntry = new AppointmentModel();
            newAppointmentEntry.setAptId(model.getAppointmentId());
            newAppointmentEntry.setDoctorName(model.getDoctorName());

            newAppointmentEntry.setAppointmentType(model.getAppointmentType());

            String mAptTime = model.getAppointmentDate();
            Calendar c = Calendar.getInstance();
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(CarePayConstants.DATE_FORMAT, Locale.ENGLISH);
            String mCurrentDate = mSimpleDateFormat.format(c.getTime());

            String mCurrentDateWithoutTime = "";
            if (mCurrentDate != null) {
                String[] mCurrentDateArr = mCurrentDate.split(" ");
                mCurrentDateWithoutTime = mCurrentDateArr[0];
            }

            String mAptDateWithoutTime = "";
            if (mAptTime != null) {
                String mAptDate = mAptTime.replaceAll(CarePayConstants.ATTR_UTC, "");
                String[] mAptDateArr = mAptDate.split(" ");
                mAptDateWithoutTime = mAptDateArr[0];
            }

            try {
                String mAptDateFormat = mSimpleDateFormat.format(mSimpleDateFormat.parse(model.getAppointmentDate()));
                Date mCurrentConvertedDate = mSimpleDateFormat.parse(mCurrentDate);
                Date mConvertedAptDate = mSimpleDateFormat.parse(mAptDateFormat);

                if (mConvertedAptDate.after(mCurrentConvertedDate) &&
                        !mAptDateWithoutTime.equalsIgnoreCase(mCurrentDateWithoutTime)) {

                    newAppointmentEntry.setAppointmentDay(CarePayConstants.DAY_UPCOMING);
                    Date mSourceAptDate = new SimpleDateFormat(CarePayConstants.DATE_FORMAT,
                            Locale.ENGLISH).parse(mAptTime.replaceAll(CarePayConstants.ATTR_UTC, ""));
                    SimpleDateFormat mSimpleDateFormat_Time = new SimpleDateFormat(
                            CarePayConstants.DATE_TIME_FORMAT, Locale.ENGLISH);
                    String mUpcomingDate = mSimpleDateFormat_Time.format(mSourceAptDate);
                    newAppointmentEntry.setAppointmentTime(mUpcomingDate);

                } else if (mConvertedAptDate.before(mCurrentConvertedDate)) {
                    /*skipping this as the appointment was in past.*/
                    return;
                } else {
                    newAppointmentEntry.setAppointmentDay(CarePayConstants.DAY_TODAY);
                    Date mSourceAptDate = new SimpleDateFormat(CarePayConstants.DATE_FORMAT,
                            Locale.ENGLISH).parse(mAptTime.replaceAll(CarePayConstants.ATTR_UTC, ""));
                    SimpleDateFormat mSimpleDateFormat_Time = new SimpleDateFormat(
                            CarePayConstants.DATE_FORMAT_AM_PM, Locale.ENGLISH);
                    String parsedDate = mSimpleDateFormat_Time.format(mSourceAptDate);
                    newAppointmentEntry.setAppointmentTime(parsedDate);
                }
            } catch (ParseException ex) {
                Log.e(LOG_TAG, "Parse Exception caught : " + ex.getMessage());
            }

            newAppointmentEntry.setAppointmentDate(model.getAppointmentDate());
            newAppointmentEntry.setPlaceName(model.getPlaceName());
            newAppointmentEntry.setPlaceAddress(model.getPlaceAddress());
            newAppointmentEntry.setPending(true);
            appointmentsItems.add(newAppointmentEntry);

            appointmentListWithHeader = getAppointmentListWithHeader();

            if (appointmentListWithHeader != null && appointmentListWithHeader.size() > 0) {
                appointmentsAdapter = new AppointmentsAdapter(getActivity(),
                        appointmentListWithHeader, appointmentsListFragment);
                appointmentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                appointmentRecyclerView.setAdapter(appointmentsAdapter);
            } else {
                Toast.makeText(getActivity(), "Appointment does not exist!", Toast.LENGTH_LONG).show();
            }
        }
        AppointmentsListFragment.showNewAddedAppointment = false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View appointmentsListView = inflater.inflate(R.layout.fragment_appointments_list, container, false);
        appointmentRecyclerView = (RecyclerView) appointmentsListView.findViewById(R.id.appointments_recycler_view);
        appointmentsListFragment = this;
        aptItem = new AppointmentModel();
        new AsyncListParser().execute();

        FloatingActionButton floatingActionButton = (FloatingActionButton) appointmentsListView.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent appointmentIntent = new Intent(getActivity(), AddAppointmentActivity.class);
                startActivity(appointmentIntent);
            }
        });

        return appointmentsListView;
    }

    private class AsyncListParser extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(getContext());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoading.setMessage("Loading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String json;
                try {
                    InputStream is = getActivity().getAssets().open(CarePayConstants.ASSETS_JSON);
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                    json = new String(buffer, "UTF-8");
                } catch (IOException ex) {
                    Log.e(LOG_TAG, "IO Exception caught : " + ex.getMessage());
                    return null;
                }
                return json;
            } catch (Exception ex) {
                Log.e(LOG_TAG, "Exception caught : " + ex.getMessage());
                return ex.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            pdLoading.dismiss();
            try {
                JSONArray jsonArray = new JSONArray(result);
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        // Creating JSONObject from JSONArray
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        JSONObject jsonObject_Response = jsonObj.getJSONObject(CarePayConstants.ATTR_RESPONSE);

                        // Getting data from individual JSONObject
                        if (jsonObject_Response != null) {
                            for (int j = 0; j < jsonObject_Response.length(); j++) {
                                JSONObject jsonObj_Capture = jsonObject_Response.getJSONObject(CarePayConstants.ATTR_CAPTURE);
                                JSONArray jsonArray_Appointments = jsonObj_Capture.getJSONArray(CarePayConstants.ATTR_APPOINTMENTS);
                                if (jsonArray_Appointments != null && jsonArray_Appointments.length() > 0) {
                                    for (int k = 0; j < jsonArray_Appointments.length(); k++) {
                                        JSONObject jsonObj_Physician = jsonArray_Appointments.getJSONObject(k);
                                        String mAptId = jsonObj_Physician.getString(CarePayConstants.ATTR_APPT_ID);
                                        aptItem.setAptId(mAptId);
                                        String mAptTime = jsonObj_Physician.getString(CarePayConstants.ATTR_TIME);

                                        String mAptDate = "", mAptDateWithoutTime = "";
                                        if (mAptTime != null) {
                                            mAptDate = mAptTime.replaceAll(CarePayConstants.ATTR_UTC, "");
                                            String[] mAptDateArr = mAptDate.split(" ");
                                            mAptDateWithoutTime = mAptDateArr[0];
                                        }

                                        String mAptDay = null;
                                        try {
                                            Calendar c = Calendar.getInstance();
                                            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(CarePayConstants.DATE_FORMAT, Locale.ENGLISH);
                                            String mCurrentDate = mSimpleDateFormat.format(c.getTime());

                                            String mCurrentDateWithoutTime = "";
                                            if (mCurrentDate != null) {
                                                String[] mCurrentDateArr = mCurrentDate.split(" ");
                                                mCurrentDateWithoutTime = mCurrentDateArr[0];
                                            }

                                            String mAptDateFormat = mSimpleDateFormat.format(mSimpleDateFormat.parse(mAptDate));
                                            Date mCurrentConvertedDate = mSimpleDateFormat.parse(mCurrentDate);
                                            Date mConvertedAptDate = mSimpleDateFormat.parse(mAptDateFormat);
                                            aptItem.setAppointmentTime(mAptTime.replaceAll(CarePayConstants.ATTR_UTC, ""));

                                            JSONObject jsonObjectPhysician = jsonObj_Physician.getJSONObject(CarePayConstants.ATTR_PHYSICIAN);
                                            String mDoctorName = jsonObjectPhysician.getString(CarePayConstants.ATTR_NAME);
                                            aptItem.setDoctorName(mDoctorName);

                                            String mDoctorType = jsonObjectPhysician.getString(CarePayConstants.ATTR_TYPE);
                                            aptItem.setAppointmentType(mDoctorType);

                                            if (mConvertedAptDate.after(mCurrentConvertedDate) &&
                                                    !mAptDateWithoutTime.equalsIgnoreCase(mCurrentDateWithoutTime)) {
                                                mAptDay = CarePayConstants.DAY_UPCOMING;
                                                Date mSourceAptDate = new SimpleDateFormat(CarePayConstants.DATE_FORMAT,
                                                        Locale.ENGLISH).parse(mAptTime.replaceAll(CarePayConstants.ATTR_UTC, ""));
                                                SimpleDateFormat mSimpleDateFormat_Time = new SimpleDateFormat(
                                                        CarePayConstants.DATE_TIME_FORMAT, Locale.ENGLISH);
                                                String mUpcomingDate = mSimpleDateFormat_Time.format(mSourceAptDate);
                                                appointmentsItems.add(new AppointmentModel(mAptId, mDoctorName,
                                                        mUpcomingDate, mDoctorType, mAptDay, mAptTime));
                                            } else if (mConvertedAptDate.before(mCurrentConvertedDate)) {
                                                // skipping this date as this appointment was in past.
                                            } else {
                                                mAptDay = CarePayConstants.DAY_TODAY;
                                                Date mSourceAptDate = new SimpleDateFormat(CarePayConstants.DATE_FORMAT,
                                                        Locale.ENGLISH).parse(mAptTime.replaceAll(CarePayConstants.ATTR_UTC, ""));
                                                SimpleDateFormat mSimpleDateFormat_Time = new SimpleDateFormat(
                                                        CarePayConstants.DATE_FORMAT_AM_PM, Locale.ENGLISH);
                                                String parsedDate = mSimpleDateFormat_Time.format(mSourceAptDate);
                                                appointmentsItems.add(new AppointmentModel(mAptId, mDoctorName,
                                                        parsedDate, mDoctorType, mAptDay, mAptTime));
                                            }
                                        } catch (ParseException ex) {
                                            Log.e(LOG_TAG, "Parse Exception caught : " + ex.getMessage());
                                        }
                                        aptItem.setAppointmentHeader(mAptDay);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (JSONException ex) {
                Log.e(LOG_TAG, "JSON Exception caught : " + ex.getMessage());
            }

            appointmentListWithHeader = getAppointmentListWithHeader();
            if (appointmentListWithHeader != null && appointmentListWithHeader.size() > 0) {
                appointmentsAdapter = new AppointmentsAdapter(getActivity(),
                        appointmentListWithHeader, appointmentsListFragment);
                appointmentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                appointmentRecyclerView.setAdapter(appointmentsAdapter);
            } else {
                Toast.makeText(getActivity(), "Appointment does not exist!", Toast.LENGTH_LONG).show();
            }
            checkUpcomingAppointmentForReminder();
        }
    }

    /*Method to return appointmentListWithHeader*/
    private ArrayList<Object> getAppointmentListWithHeader() {
        if (appointmentsItems != null && appointmentsItems.size() > 0) {
            /*To sort appointment list based on appointment time*/
            Collections.sort(appointmentsItems, new Comparator<AppointmentModel>() {
                public int compare(AppointmentModel o1, AppointmentModel o2) {
                    if (o1.getAppointmentDate() != null && o2.getAppointmentDate() != null) {
                        String dateO1 = o1.getAppointmentDate().replaceAll(CarePayConstants.ATTR_UTC, "").trim();
                        String dateO2 = o2.getAppointmentDate().replaceAll(CarePayConstants.ATTR_UTC, "").trim();
                        try {
                            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(CarePayConstants.DATE_FORMAT, Locale.ENGLISH);
                            Date date1 = mSimpleDateFormat.parse(dateO1);
                            Date date2 = mSimpleDateFormat.parse(dateO2);
                            long time1 = date1.getTime();
                            long time2 = date2.getTime();

                            if (time1 < time2) {
                                return -1;
                            } else {
                                return 1;
                            }
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                            Log.e(LOG_TAG, "Parse Exception caught in Comparator : " + ex.getMessage());
                        }
                    }
                    return 0;
                }
            });

            /*To sort appointment list based on today or tomorrow*/
            Collections.sort(appointmentsItems, new Comparator<AppointmentModel>() {
                public int compare(AppointmentModel o1, AppointmentModel o2) {
                    int compare = o1.getAppointmentDay().compareTo(o2.getAppointmentDay());
                    return compare;
                }
            });

            /*To create appointment list data structure along with headers*/
            String previousDay = "";
            appointmentListWithHeader = new ArrayList<Object>();

            for (AppointmentModel appointmentModel : appointmentsItems) {
                if (previousDay.equalsIgnoreCase(appointmentModel.getAppointmentDay())) {
                    appointmentListWithHeader.add(appointmentModel);
                } else {
                    previousDay = appointmentModel.getAppointmentDay();
                    AppointmentSectionHeader appointmentSectionHeader = new AppointmentSectionHeader();
                    appointmentSectionHeader.setAppointmentHeader(previousDay);
                    appointmentListWithHeader.add(appointmentSectionHeader);
                    appointmentListWithHeader.add(appointmentModel);
                }
            }
        }
        return appointmentListWithHeader;
    }
}
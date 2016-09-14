package com.carecloud.carepaylibray.appointments.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.adapters.AppointmentsAdapter;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;
import com.carecloud.carepaylibray.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class AppointmentsListFragment extends Fragment {

    private static final String LOG_TAG = AppointmentsListFragment.class.getSimpleName();
    private ArrayList<AppointmentModel> mAptItems = new ArrayList<>();
    private AppointmentModel aptItem;
    private AppointmentsAdapter appointmentsAdapter, appointmentsAdapterUpcoming;
    private ArrayList<AppointmentModel> todayAppointmentsItems = new ArrayList<AppointmentModel>();
    private ArrayList<AppointmentModel> upcomingAppointmentsItems = new ArrayList<AppointmentModel>();
    private RecyclerView recyclerViewToday, recyclerViewUpcoming;


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.Appointments_title);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_appointments_list, container, false);
        aptItem = new AppointmentModel();
        new AsyncListParser().execute();
        return view;
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
                String json = null;
                try {
                    InputStream is = getActivity().getAssets().open(CarePayConstants.ASSETS_JSON);
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                    json = new String(buffer, "UTF-8");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return null;
                }
                return (json.toString());
                //new FileReader(getContext()).loadJSONFromAsset();
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {

            pdLoading.dismiss();
            try {

                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < jsonArray.length(); i++) {

                    // Creating JSONObject from JSONArray
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    JSONObject jsonObject_Response = jsonObj.getJSONObject(CarePayConstants.ATTR_RESPONSE);
                    // Getting data from individual JSONObject
                    for(int j=0;j<jsonObject_Response.length();j++)
                    {
                        JSONObject jsonObj_Capture = jsonObject_Response.getJSONObject(CarePayConstants.ATTR_CAPTURE);
                        JSONArray jsonArray_Appointments = jsonObj_Capture.getJSONArray(CarePayConstants.ATTR_APPOINTMENTS);
                        for(int k=0;j<jsonArray_Appointments.length();k++)
                        {
                            JSONObject jsonObj_Physician = jsonArray_Appointments.getJSONObject(k);
                            String mAptId = jsonObj_Physician.getString(CarePayConstants.ATTR_APPT_ID);
                            aptItem.setAptId(mAptId);
                            String mAptTime = jsonObj_Physician.getString(CarePayConstants.ATTR_TIME);
                            String mAptDate=mAptTime.replaceAll(CarePayConstants.ATTR_UTC,"");

                            String mAptDay = null;
                            try {
                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(CarePayConstants.DATE_FORMAT, Locale.ENGLISH);
                                String mCurrentDate = mSimpleDateFormat.format(c.getTime());

                                String mAptDateFormat = mSimpleDateFormat.format(mSimpleDateFormat.parse(mAptDate));

                                Date mCurrentConvertedDate = new Date();
                                Date mConvertedAptDate = new Date();

                                mCurrentConvertedDate = mSimpleDateFormat.parse(mCurrentDate);
                                mConvertedAptDate = mSimpleDateFormat.parse(mAptDateFormat);
                                aptItem.setAppointmentTime(mAptTime.replaceAll(CarePayConstants.ATTR_UTC,""));

                                JSONObject jsonObjectPhysician= jsonObj_Physician.getJSONObject(CarePayConstants.ATTR_PHYSICIAN);
                                String mDoctorName = jsonObjectPhysician.getString(CarePayConstants.ATTR_NAME);
                                aptItem.setDoctorName(mDoctorName);

                                String mDoctorType = jsonObjectPhysician.getString(CarePayConstants.ATTR_TYPE);
                                aptItem.setAppointmentType(mDoctorType);
                                if (mConvertedAptDate.after(mCurrentConvertedDate)) {
                                    mAptDay = CarePayConstants.DAY_UPCOMING;
                                    Date mSourceAptDate = new SimpleDateFormat(CarePayConstants.DATE_FORMAT, Locale.ENGLISH).parse(mAptTime.replaceAll(CarePayConstants.ATTR_UTC,""));
                                    SimpleDateFormat mSimpleDateFormat_Time = new SimpleDateFormat(CarePayConstants.DATE_TIME_FORMAT, Locale.ENGLISH);
                                    String mUpcomingDate = mSimpleDateFormat_Time.format(mSourceAptDate);
                                    upcomingAppointmentsItems.add(new AppointmentModel(mAptId,mDoctorName, mUpcomingDate, mDoctorType, mAptDay, mAptTime));
                                } else {
                                    mAptDay = CarePayConstants.DAY_TODAY;
                                    Date mSourceAptDate = new SimpleDateFormat(CarePayConstants.DATE_FORMAT, Locale.ENGLISH).parse(mAptTime.replaceAll(CarePayConstants.ATTR_UTC,""));
                                    SimpleDateFormat mSimpleDateFormat_Time = new SimpleDateFormat(CarePayConstants.DATE_FORMAT_AM_PM, Locale.ENGLISH);
                                    String parsedDate = mSimpleDateFormat_Time.format(mSourceAptDate);
                                    todayAppointmentsItems.add(new AppointmentModel(mAptId, mDoctorName, parsedDate, mDoctorType, mAptDay, mAptTime));

                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            aptItem.setAppointmentHeader(mAptDay);
                        }
                    }
                }

            } catch (JSONException e) {
            }

            TextView mTextViewSectionTitleToday = (TextView) getActivity().findViewById(R.id.appointments_section_title_Today);
            TextView mTextViewSectionTitleUpcoming = (TextView) getActivity().findViewById(R.id.appointments_section_title_Upcoming);
            Utility.setProximaNovaSemiboldTypeface(getContext(),mTextViewSectionTitleToday );
            Utility.setProximaNovaSemiboldTypeface(getContext(),mTextViewSectionTitleUpcoming );

            appointmentsAdapter = new AppointmentsAdapter(getActivity(),todayAppointmentsItems);
            appointmentsAdapterUpcoming = new AppointmentsAdapter(getActivity(),upcomingAppointmentsItems);

            recyclerViewToday = ((RecyclerView)getActivity().findViewById(R.id.appointments_recycler_view_today));
            recyclerViewToday.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerViewToday.setAdapter(appointmentsAdapter);
            recyclerViewUpcoming = ((RecyclerView)getActivity().findViewById(R.id.appointments_recycler_view_upcoming));
            recyclerViewUpcoming.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerViewUpcoming.setAdapter(appointmentsAdapterUpcoming);

        }

    }
}
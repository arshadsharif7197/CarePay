package com.carecloud.carepaylibray.appointments.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.adapters.AppointmentsAdapter;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class AppointmentsListFragment extends Fragment {

    private static final String LOG_TAG = AppointmentsListFragment.class.getSimpleName();
    private KeyboardHolderActivity mActivity;
    ArrayList<AppointmentModel> mAptItems = new ArrayList<>();
    AppointmentModel aptItem;
    RecyclerView todayRecyclerView, upcomingRecyclerView;
    AppointmentsAdapter todayAdapter, upcomingAdapter;
    ArrayList<AppointmentModel> todayItems = new ArrayList<AppointmentModel>();
    ArrayList<AppointmentModel> upcomingItems = new ArrayList<AppointmentModel>();

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Appointments");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_appointments, container, false);
        aptItem = new AppointmentModel();
        // Reading json file from assets folder
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(getActivity().getAssets().open(
                   CarePayConstants.ASSETS_JSON)));
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            JSONArray jsonArray = new JSONArray(sb.toString());

            for (int i = 0; i < jsonArray.length(); i++) {

                // Creating JSONObject from JSONArray
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                JSONObject jsonObject = jsonObj.getJSONObject(CarePayConstants.ATTR_RESPONSE);
                // Getting data from individual JSONObject
                for(int j=0;j<jsonObject.length();j++)
                {
                    JSONObject jsonObj1 = jsonObject.getJSONObject(CarePayConstants.ATTR_CAPTURE);
                    JSONArray jsonArray1 = jsonObj1.getJSONArray(CarePayConstants.ATTR_APPOINTMENTS);
                    for(int k=0;j<jsonArray1.length();k++)
                    {
                        JSONObject objPhysian = jsonArray1.getJSONObject(k);
                        String strAptId = objPhysian.getString(CarePayConstants.ATTR_APPT_ID);
                        aptItem.setAptId(strAptId);
                        String strTime = objPhysian.getString(CarePayConstants.ATTR_TIME);
                        String inputdate=strTime.replaceAll(CarePayConstants.ATTR_UTC,"");
                        aptItem.setAptDate(inputdate);
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(CarePayConstants.DATE_FORMAT,Locale.getDefault());
                        String strDay = null;
                        try {
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat(CarePayConstants.DATE_FORMAT,Locale.getDefault());
                            String strCurrentDate = sdf.format(c.getTime());

                            String date = strCurrentDate;
                            String dateafter = new SimpleDateFormat(CarePayConstants.DATE_FORMAT,Locale.getDefault()).format(simpleDateFormat.parse(inputdate));

                            SimpleDateFormat dateFormat = new SimpleDateFormat(
                                    CarePayConstants.DATE_FORMAT,Locale.getDefault());
                            Date convertedDate = new Date();
                            Date convertedDate2 = new Date();

                            convertedDate = dateFormat.parse(date);
                            convertedDate2 = dateFormat.parse(dateafter);
                            aptItem.setAptTime(strTime.replaceAll(CarePayConstants.ATTR_UTC,""));

                            JSONObject jsonObj3 = objPhysian.getJSONObject(CarePayConstants.ATTR_PHYSICIAN);
                            String strName = jsonObj3.getString(CarePayConstants.ATTR_NAME);
                            aptItem.setDoctorName(strName);

                            String strType = jsonObj3.getString(CarePayConstants.ATTR_TYPE);
                            aptItem.setAptType(strType);
                            if (convertedDate2.after(convertedDate)) {
                                strDay = CarePayConstants.DAY_UPCOMING;
                                Date initDate = new SimpleDateFormat(CarePayConstants.DATE_FORMAT,Locale.getDefault()).parse(strTime.replaceAll(CarePayConstants.ATTR_UTC,""));
                                SimpleDateFormat formatter = new SimpleDateFormat(CarePayConstants.DATE_TIME_FORMAT,Locale.getDefault());
                                String upcomingDate = formatter.format(initDate);
                                upcomingItems.add(new AppointmentModel(strAptId,strName, upcomingDate, strType, strDay,inputdate));
                            } else {
                                strDay = CarePayConstants.DAY_TODAY;
                                Date initDate = new SimpleDateFormat(CarePayConstants.DATE_FORMAT,Locale.getDefault()).parse(strTime.replaceAll(CarePayConstants.ATTR_UTC,""));
                                SimpleDateFormat formatter = new SimpleDateFormat(CarePayConstants.DATE_FORMAT_AM_PM,Locale.getDefault());
                                String parsedDate = formatter.format(initDate);
                                todayItems.add(new AppointmentModel(strAptId, strName, parsedDate, strType, strDay,inputdate));

                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        aptItem.setAptHeader(strDay);
                    }
                }
            }

        } catch (JSONException e) {
            Log.d("Exception ", e+"");

            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Typeface textViewFont_proximanova_semibold = Typeface.createFromAsset(view.getResources().getAssets(), "fonts/proximanova_semibold.otf");
        TextView textView_section_title_Today = (TextView) view.findViewById(R.id.section_title_Today);
        textView_section_title_Today.setTypeface(textViewFont_proximanova_semibold);
        TextView textView_section_title_Upcoming = (TextView) view.findViewById(R.id.section_title_Upcoming);
        textView_section_title_Upcoming.setTypeface(textViewFont_proximanova_semibold);

        todayAdapter = new AppointmentsAdapter(getActivity(),todayItems);
        upcomingAdapter = new AppointmentsAdapter(getActivity(),upcomingItems);

        RecyclerView recyclerView = ((RecyclerView)view.findViewById(R.id.recycler_view_today));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(todayAdapter);
        RecyclerView recyclerView1 = ((RecyclerView)view.findViewById(R.id.recycler_view_upcoming));
        recyclerView1.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView1.setAdapter(upcomingAdapter);

        return view;
    }
}
package com.carecloud.carepaylibray.appointments.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.activities.AddAppointmentActivity;
import com.carecloud.carepaylibray.appointments.activities.AppointmentsActivity;
import com.carecloud.carepaylibray.appointments.adapters.AppointmentsAdapter;
import com.carecloud.carepaylibray.appointments.models.Appointment;
import com.carecloud.carepaylibray.appointments.models.AppointmentAddressModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentSectionHeaderModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.services.AppointmentService;
import com.carecloud.carepaylibray.appointments.utils.CustomPopupNotification;
import com.carecloud.carepaylibray.base.BaseServiceGenerator;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.utils.ApplicationPreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentsListFragment extends Fragment {
    private static final String LOG_TAG = AppointmentsListFragment.class.getSimpleName();

    private AppointmentModel appointmentModel;
    private AppointmentsResultModel appointmentsResultModel;
    private ProgressBar appointmentProgressBar;
    private SwipeRefreshLayout appointmentRefresh;
    private LinearLayout noAppointmentView;

    private AppointmentsAdapter appointmentsAdapter;
    private ArrayList<AppointmentModel> appointmentsItems = new ArrayList<>();
    private ArrayList<Object> appointmentListWithHeader;
    private RecyclerView appointmentRecyclerView;
    private AppointmentsListFragment appointmentsListFragment;
    private Bundle bundle;

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
    @SuppressLint("StringFormatMatches")
    private void checkUpcomingAppointmentForReminder() {
        if (appointmentsItems != null && !appointmentsItems.isEmpty() &&
                !appointmentsItems.get(0).getAppointmentId().equalsIgnoreCase(
                        ApplicationPreferences.Instance.readStringFromSharedPref(
                                CarePayConstants.PREF_LAST_REMINDER_POPUP_APPT_ID))) {

            try {
                String appointmentTimeStr = appointmentsItems.get(0).getAppointmentTime();
                String currentTime = new SimpleDateFormat(CarePayConstants.DATE_FORMAT_AM_PM, Locale.getDefault()).format(new Date());

                SimpleDateFormat format = new SimpleDateFormat(CarePayConstants.DATE_FORMAT_AM_PM, Locale.getDefault());
                Date appointmentDate = format.parse(appointmentTimeStr);
                Date currentDate = format.parse(currentTime);

                long differenceInMilli = appointmentDate.getTime() - currentDate.getTime();
                long differenceInMinutes = TimeUnit.MILLISECONDS.toMinutes(differenceInMilli);

                if (differenceInMinutes <= CarePayConstants.APPOINTMENT_REMINDER_TIME_IN_MINUTES &&
                        differenceInMinutes > 0) {
                    String appointmentInDuration;
                    if (differenceInMinutes == CarePayConstants.APPOINTMENT_REMINDER_TIME_IN_MINUTES) {
                        appointmentInDuration = "2 hours";
                    } else if (differenceInMinutes == 60) {
                        appointmentInDuration = "1 hour";
                    } else if (differenceInMinutes > 60) {
                        appointmentInDuration = " hour and " + (differenceInMinutes - 60) + " minutes";
                    } else {
                        appointmentInDuration = differenceInMinutes + " minutes";
                    }
                    popup = new CustomPopupNotification(getActivity(), getView(), getString(R.string.checkin_early),
                            getString(R.string.dismiss),
                            getString(R.string.apt_popup_message_text, appointmentsItems.get(0).getDoctorName(), appointmentInDuration),
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

            if (appointmentListWithHeader != null) {
                appointmentsAdapter = new AppointmentsAdapter(getActivity(), appointmentListWithHeader, appointmentsListFragment);
                appointmentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                appointmentRecyclerView.setAdapter(appointmentsAdapter);
            }

            if (appointmentsAdapter.getItemCount() == 0) {
                noAppointmentView.setVisibility(View.VISIBLE);
                appointmentRefresh.setVisibility(View.GONE);
            } else {
                noAppointmentView.setVisibility(View.GONE);
                appointmentRefresh.setVisibility(View.VISIBLE);
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

        appointmentRefresh = (SwipeRefreshLayout) appointmentsListView.findViewById(R.id.swipeRefreshLayout);
//        appointmentRefresh.setRefreshing(false);

        appointmentProgressBar = (ProgressBar) appointmentsListView.findViewById(R.id.appointmentProgressBar);
        appointmentProgressBar.setVisibility(View.GONE);
        bundle = getArguments();

        noAppointmentView = (LinearLayout) appointmentsListView.findViewById(R.id.no_appointment_layout);

        FloatingActionButton floatingActionButton = (FloatingActionButton) appointmentsListView.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent appointmentIntent = new Intent(getActivity(), AddAppointmentActivity.class);
                startActivity(appointmentIntent);
            }
        });
        getAppointmentInformation();
      //  onRefresh();

        return appointmentsListView;
    }

    private void getAppointmentInformation() {
        appointmentProgressBar.setVisibility(View.VISIBLE);

        AppointmentService aptService = (new BaseServiceGenerator(getActivity())).createService(AppointmentService.class);
        Call<AppointmentsResultModel> call = aptService.fetchAppointmentInformation();
        call.enqueue(new Callback<AppointmentsResultModel>() {

            @Override
            public void onResponse(Call<AppointmentsResultModel> call, Response<AppointmentsResultModel> response) {
                appointmentsResultModel = response.body();
                appointmentProgressBar.setVisibility(View.GONE);

                if (appointmentsResultModel != null && appointmentsResultModel.getPayload() != null
                        && appointmentsResultModel.getPayload().getAppointments() != null
                        && appointmentsResultModel.getPayload().getAppointments().size() > 0) {

                    noAppointmentView.setVisibility(View.GONE);
                    appointmentRefresh.setVisibility(View.VISIBLE);

                    List<Appointment> appointments = appointmentsResultModel.getPayload().getAppointments();
                    for (Appointment appointment : appointments) {

                        appointmentModel = new AppointmentModel();

                        // Appointment Id
                        String mAptId = appointment.getPayload().getId();
                        appointmentModel.setAptId(mAptId);

                        // Appointment is pending
                        boolean isPending = false, isCancelled = false;
                        if (appointment.getPayload().getAppointmentStatusModel().getId() == 1) {
                            isPending = true;
                            appointmentModel.setPending(isPending);
                        } else if (appointment.getPayload().getAppointmentStatusModel().getId() == 4) {
                            isCancelled = true;
                            appointmentModel.setCancelled(isCancelled);
                        }

                        // Appointment start time
                        String mAptTime = "";
                        String mAptDate = "", mAptDateWithoutTime = "";
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
                        try {
                            Date aptDate = sdf.parse(appointment.getPayload().getStartTime());
                            mAptTime = new SimpleDateFormat(CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT,
                                    Locale.getDefault()).format(aptDate);
                            mAptDate = mAptTime.replaceAll(CarePayConstants.ATTR_UTC, "");
                            mAptDateWithoutTime= new SimpleDateFormat("yyyy-MM-dd",
                                    Locale.getDefault()).format(aptDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        /*//String mAptDate = "", mAptDateWithoutTime = "";
                        if (mAptTime != null) {
                            mAptDate = mAptTime.replaceAll(CarePayConstants.ATTR_UTC, "");
                            String[] mAptDateArr = mAptDate.split(" ");
                            mAptDateWithoutTime = mAptDateArr[0];
                            mAptDateWithoutTime= new SimpleDateFormat("MM-dd-yyyy",
                                    Locale.getDefault()).format(mAptTime);
                        }*/

                        String mAptDay = null;
                        try {
                            Calendar c = Calendar.getInstance();
                            c.set(Calendar.HOUR_OF_DAY,0);
                            c.set(Calendar.MINUTE,0);
                            c.set(Calendar.SECOND,0);
                            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(
                                    "yyyy-MM-dd", Locale.US);
                            String mCurrentDate = mSimpleDateFormat.format(c.getTime());

                            String mCurrentDateWithoutTime = "";
                            if (mCurrentDate != null) {
                                String[] mCurrentDateArr = mCurrentDate.split(" ");
                                mCurrentDateWithoutTime = mCurrentDateArr[0];
                                mCurrentDateWithoutTime=mCurrentDate;
                            }

                            // Appointment Time
                            String mAptDateFormat = mSimpleDateFormat.format(mSimpleDateFormat.parse(mAptDate));
                            Date mCurrentConvertedDate = mSimpleDateFormat.parse(mCurrentDate);
                            Date mConvertedAptDate = mSimpleDateFormat.parse(mAptDateFormat);
                            appointmentModel.setAppointmentTime(mAptTime.replaceAll(CarePayConstants.ATTR_UTC, ""));

                            // Appointment Provider Name
                            String mDoctorName = appointment.getPayload().getProvider().getName();
                            appointmentModel.setDoctorName(mDoctorName);

                            // Appointment Provider type
                            String mDoctorType = appointment.getPayload().getProvider().getSpecialty();
                            appointmentModel.setAppointmentType(mDoctorType);

                            // Appointment Provider phone
                            String mPhoneNumber = appointment.getPayload().getProvider().getPhone();
                            appointmentModel.setPhoneNumber(mPhoneNumber);

                            // Appointment Provider photo
                            String mDoctorPhoto = appointment.getPayload().getProvider().getPhoto();
                            appointmentModel.setPhoto(mDoctorPhoto);

                            // Appointment Place
                            String mPlaceName = appointment.getPayload().getLocation().getName();
                            appointmentModel.setPlaceName(mPlaceName);

                            // Appointment Place address
                            AppointmentAddressModel address = appointment.getPayload().getLocation().getAddress();
                            String line1 = TextUtils.isEmpty(address.getLine1()) ? "" : address.getLine1();
                            String line2 = TextUtils.isEmpty(address.getLine2()) ? "" : address.getLine2();
                            String line3 = (address.getLine3() == null) ? "" : address.getLine3().toString();
                            String city = TextUtils.isEmpty(address.getCity()) ? "" : address.getCity();
                            String zipCode = TextUtils.isEmpty(address.getZipCode()) ? "" : address.getZipCode();
                            String countyName = (address.getCountyName() == null) ? "" : address.getCountyName().toString();
                            String stateName = TextUtils.isEmpty(address.getStateName()) ? "" : address.getStateName();

                            String mPlaceAddress = line1 + " " + line2 + " " + line3 + " " + city
                                    + " " + stateName + " " + zipCode + " " + countyName;
                            appointmentModel.setPlaceAddress(mPlaceAddress);

                            if (mConvertedAptDate.after(mCurrentConvertedDate) &&
                                    !mAptDateWithoutTime.equalsIgnoreCase(mCurrentDateWithoutTime)) {
                                mAptDay = CarePayConstants.DAY_UPCOMING;
                                Date mSourceAptDate = new SimpleDateFormat(CarePayConstants.DATE_FORMAT,
                                        Locale.getDefault()).parse(mAptTime.replaceAll(CarePayConstants.ATTR_UTC, ""));
                                SimpleDateFormat mSimpleDateFormat_Time = new SimpleDateFormat(
                                        CarePayConstants.DATE_TIME_FORMAT, Locale.getDefault());
                                String mUpcomingDate = mSimpleDateFormat_Time.format(mSourceAptDate);

                                AppointmentModel model = new AppointmentModel();
                                model.setAppointmentId(mAptId);
                                model.setDoctorName(mDoctorName);
                                model.setAppointmentType(mDoctorType);
                                model.setPhoto(mDoctorPhoto);
                                model.setAppointmentTime(mUpcomingDate);
                                model.setAppointmentDay(mAptDay);
                                model.setAppointmentDate(mAptTime);
                                model.setPlaceName(mPlaceName);
                                model.setPlaceAddress(mPlaceAddress);
                                model.setPending(isPending);
                                model.setCancelled(isCancelled);
                                model.setPhoneNumber(mPhoneNumber);

                                // Skip cancelled appointments
                                if (!isCancelled) {
                                    appointmentsItems.add(model);
                                }

                            } else if (mConvertedAptDate.before(mCurrentConvertedDate)) {
                                // skipping this date as this appointment was in past.
                                Log.i(LOG_TAG, "Appointment dare already over");
                            } else {
                                mAptDay = CarePayConstants.DAY_TODAY;
                                Date mSourceAptDate = new SimpleDateFormat(CarePayConstants.DATE_FORMAT,
                                        Locale.getDefault()).parse(mAptTime.replaceAll(CarePayConstants.ATTR_UTC, ""));
                                SimpleDateFormat mSimpleDateFormat_Time = new SimpleDateFormat(
                                        CarePayConstants.DATE_FORMAT_AM_PM, Locale.getDefault());
                                String parsedDate = mSimpleDateFormat_Time.format(mSourceAptDate);

                                AppointmentModel model = new AppointmentModel();
                                model.setAppointmentId(mAptId);
                                model.setDoctorName(mDoctorName);
                                model.setAppointmentType(mDoctorType);
                                model.setPhoto(mDoctorPhoto);
                                model.setAppointmentTime(parsedDate);
                                model.setAppointmentDay(mAptDay);
                                model.setAppointmentDate(mAptTime);
                                model.setPlaceName(mPlaceName);
                                model.setPlaceAddress(mPlaceAddress);
                                model.setPending(isPending);
                                model.setPhoneNumber(mPhoneNumber);

                                // Skip cancelled appointments
                                if (!isCancelled) {
                                    appointmentsItems.add(model);
                                }
                            }
//                            appointmentRefresh.setRefreshing(false);
                        } catch (ParseException ex) {
                            Log.e(LOG_TAG, "Parse Exception caught : " + ex.getMessage());
                        }
                        appointmentModel.setAppointmentHeader(mAptDay);
                    }
                }

                appointmentListWithHeader = getAppointmentListWithHeader();
                if (appointmentListWithHeader != null) {
                    if (bundle != null) {
                        AppointmentModel appointmentModel = (AppointmentModel)
                                bundle.getSerializable(CarePayConstants.CHECKED_IN_APPOINTMENT_BUNDLE);

                        if (appointmentModel != null) {
                            // adding checked-in appointment at the top of the list
                            appointmentListWithHeader.add(0, appointmentModel);
                        }
                    }

                    appointmentsAdapter = new AppointmentsAdapter(getActivity(),
                            appointmentListWithHeader, appointmentsListFragment);
                    appointmentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    appointmentRecyclerView.setAdapter(appointmentsAdapter);
                } else {
                    noAppointmentView.setVisibility(View.VISIBLE);
                    appointmentRefresh.setVisibility(View.GONE);
                }

                checkUpcomingAppointmentForReminder();
            }

            @Override
            public void onFailure(Call<AppointmentsResultModel> call, Throwable t) {

            }
        });
    }
  /*  private void onRefresh(){
        appointmentRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (appointmentsResultModel!=(new AppointmentsResultModel())) {
                    AppointmentSectionHeaderModel appointmentSectionHeaderModel = new AppointmentSectionHeaderModel();
                    appointmentListWithHeader.remove(appointmentSectionHeaderModel);
                    appointmentListWithHeader.remove(appointmentModel);

                    getAppointmentInformation();
                }
            }
        });
    }*/

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
                            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(
                                    CarePayConstants.DATE_FORMAT, Locale.getDefault());
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
            appointmentListWithHeader = new ArrayList<>();

            for (AppointmentModel appointmentModel : appointmentsItems) {
                if (previousDay.equalsIgnoreCase(appointmentModel.getAppointmentDay())) {
                    appointmentListWithHeader.add(appointmentModel);
                } else {
                    previousDay = appointmentModel.getAppointmentDay();
                    AppointmentSectionHeaderModel appointmentSectionHeaderModel = new AppointmentSectionHeaderModel();
                    appointmentSectionHeaderModel.setAppointmentHeader(previousDay);
                    appointmentListWithHeader.add(appointmentSectionHeaderModel);
                    appointmentListWithHeader.add(appointmentModel);
                }
            }
        }
        return appointmentListWithHeader;
    }
}
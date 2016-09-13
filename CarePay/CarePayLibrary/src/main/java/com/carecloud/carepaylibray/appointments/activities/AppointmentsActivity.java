package com.carecloud.carepaylibray.appointments.activities;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.fragments.AppointmentsListFragment;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;
import com.carecloud.carepaylibray.utils.Utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppointmentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);
        FragmentManager fm = getSupportFragmentManager();
        AppointmentsListFragment fragment = (AppointmentsListFragment) fm.findFragmentByTag(AppointmentsListFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new AppointmentsListFragment();
        }
        fm.beginTransaction().replace(R.id.appointments_list_frag_holder, fragment,
                AppointmentsListFragment.class.getSimpleName()).commit();
    }

    public  void showAppointmentsDialog(AppointmentModel appointmentModel){
        if(appointmentModel !=null) {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);
            LayoutInflater inflater = (LayoutInflater) this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.dialog_appointments, null);
            alertDialogBuilder.setView(view);
            alertDialogBuilder.setCancelable(false);
            final AlertDialog dialog = alertDialogBuilder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.show();

            TextView dateTextView =  ((TextView) view.findViewById(R.id.appt_date_tv));
            TextView timeTextView = ((TextView) view.findViewById(R.id.appt_time_tv));
            TextView shortNameTextView =  ((TextView) view.findViewById(R.id.appt_shortname_tv));
            TextView nameTextView =   ((TextView) view.findViewById(R.id.appt_name_tv));
            TextView typeTextView = ((TextView) view.findViewById(R.id.appt_type_tv));
            TextView addressTextView =  ((TextView) view.findViewById(R.id.appt_address_tv));

            dateTextView.setText(onDateParseToString(appointmentModel.getAptDate())[0]);
            timeTextView.setText(onDateParseToString(appointmentModel.getAptDate())[1]);
            shortNameTextView.setText(Utility.onShortDrName(appointmentModel.getDoctorName()));
            nameTextView.setText(appointmentModel.getDoctorName());
            typeTextView.setText(appointmentModel.getAptType());
            //addressTextView.setText(appointmentModel.getA());

            Utility.setTypefaceFromAssets(this,"fonts/proximanova_regular.otf",dateTextView);
            Utility.setTypefaceFromAssets(this,"fonts/gotham_rounded_book.otf",timeTextView);
            Utility.setTypefaceFromAssets(this,"fonts/proximanova_regular.otf",shortNameTextView);
            Utility.setTypefaceFromAssets(this,"fonts/proximanova_semibold.otf",nameTextView);
            Utility.setTypefaceFromAssets(this,"fonts/proximanova_regular.otf",typeTextView);
            Utility.setTypefaceFromAssets(this,"fonts/gotham_rounded_medium.otf",addressTextView);

            ((ImageView) view.findViewById(R.id.dialog_appoint_close_id)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.cancel();
                }
            });
            ((ImageView) view.findViewById(R.id.dialog_appoint_location_id)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(AppointmentsActivity.this, "Clicked on location icon,", Toast.LENGTH_LONG).show();
                }
            });
            ((ImageView) view.findViewById(R.id.dialog_appoint_dail_id)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(AppointmentsActivity.this, "Clicked on dial icon,", Toast.LENGTH_LONG).show();
                }
            });
            ((Button) view.findViewById(R.id.btn_check_at_office_id)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(AppointmentsActivity.this, "Clicked on button icon,", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private String[] onDateParseToString(String dateStr){
        String stringDate[] = dateStr.split(" ");
        String formateDate[] = new String[2];
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy",Locale.getDefault());
            Date appointdate = sdf.parse(stringDate[0]);
            formateDate[0] = (String)android.text.format.DateFormat.format("MMMM", appointdate)+" "+(String)android.text.format.DateFormat.format("dd", appointdate);
            formateDate[1] = stringDate[1]+" "+stringDate[2];
        }catch(Exception ex){

        }
                return formateDate;
    }


}

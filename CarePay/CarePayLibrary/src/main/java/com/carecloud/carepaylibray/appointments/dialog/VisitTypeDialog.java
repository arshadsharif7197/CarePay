package com.carecloud.carepaylibray.appointments.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.Appointment;

import java.util.ArrayList;
import java.util.Arrays;

public class VisitTypeDialog extends Dialog {

    // dummy data for now till it get it from JSON file
    String[] items = {"Follow-up", "Annual Physical", "New Patient", "Existing Patient",
            "Back Pain", "Asthma", "Chest Pain"};

    public VisitTypeDialog(Context context, final Appointment model, final OnDialogListItemClickListener listener) {
        super(context);

        // This is the layout XML file that describes your Dialog layout
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_visit_type);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // Load and display list
        ListView visitTypeList = (ListView) findViewById(R.id.visitTypeList);
        VisitTypeListAdapter adapter = new VisitTypeListAdapter(context, new ArrayList<>(Arrays.asList(items)));
        visitTypeList.setAdapter(adapter);

        findViewById(R.id.visitTypeCancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        visitTypeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                model.setNewAppointmentVisitType(adapterView.getItemAtPosition(i).toString());
                listener.onDialogListItemClickListener(model);
                dismiss();
            }
        });
    }

    public interface OnDialogListItemClickListener {
        void onDialogListItemClickListener(Appointment model);
    }

    private class VisitTypeListAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<String> listItems;

        VisitTypeListAdapter(Context c, ArrayList<String> items) {
            this.mContext = c;
            this.listItems = items;
        }

        @Override
        public int getCount() {
            return listItems.size();
        }

        @Override
        public String getItem(int i) {
            return listItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            VisitTypeListHolder pHolder;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(
                        R.layout.dialog_visit_type_list_item, viewGroup, false);

                pHolder = new VisitTypeListHolder();
                pHolder.type = (TextView) view.findViewById(R.id.visitTypeListItem);
                view.setTag(pHolder);
            } else {
                pHolder = (VisitTypeListHolder) view.getTag();
            }

            pHolder.type.setText(getItem(i));
            return view;
        }

        class VisitTypeListHolder {
            TextView type;
        }
    }
}

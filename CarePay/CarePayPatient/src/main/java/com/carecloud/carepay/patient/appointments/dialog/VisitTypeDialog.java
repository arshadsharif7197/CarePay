package com.carecloud.carepay.patient.appointments.dialog;

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
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;

import java.util.ArrayList;
import java.util.List;

public class VisitTypeDialog extends Dialog {

    List<VisitTypeDTO> visitTypesModel;
    
    /**
     * Constructor.
     * @param context context
     * @param model appointment item
     * @param listener Onclick listener
     */
    public VisitTypeDialog(Context context, final AppointmentResourcesDTO model, final OnDialogListItemClickListener listener, AppointmentsResultModel appointmentsResultModel) {
        super(context);

        // This is the layout XML file that describes your Dialog layout
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_visit_type);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView titleView = (TextView) findViewById(R.id.visit_type_header_title);
        titleView.setText(appointmentsResultModel.getMetadata().getLabel().getVisitTypeHeading());

        visitTypesModel = model.getResource().getVisitReasons();

        // Load and display list
        ListView visitTypeList = (ListView) findViewById(R.id.visitTypeList);
        VisitTypeListAdapter adapter = new VisitTypeListAdapter(context, (ArrayList<VisitTypeDTO>) visitTypesModel);
        visitTypeList.setAdapter(adapter);

        findViewById(R.id.visitTypeCancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        visitTypeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                VisitTypeDTO selectedVisitType = visitTypesModel.get(position);
                listener.onDialogListItemClickListener(selectedVisitType);
                dismiss();
            }
        });
    }

    public interface OnDialogListItemClickListener {
        void onDialogListItemClickListener(VisitTypeDTO selectedVisitType);
    }

    private class VisitTypeListAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<VisitTypeDTO> listItems;

        VisitTypeListAdapter(Context context, ArrayList<VisitTypeDTO> items) {
            this.context = context;
            this.listItems = items;
        }

        @Override
        public int getCount() {
            return listItems.size();
        }

        @Override
        public String getItem(int position) {
            return listItems.get(position).getName();
        }

        @Override
        public long getItemId(int position) {
            return listItems.get(position).getId();
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            VisitTypeListHolder holder;
            if (view == null) {
                view = LayoutInflater.from(context).inflate(
                        R.layout.dialog_visit_type_list_item, viewGroup, false);

                holder = new VisitTypeListHolder();
                holder.type = (TextView) view.findViewById(R.id.visitTypeListItem);
                view.setTag(holder);
            } else {
                holder = (VisitTypeListHolder) view.getTag();
            }

            holder.type.setText(getItem(position));
            return view;
        }

        class VisitTypeListHolder {
            TextView type;
        }
    }


}

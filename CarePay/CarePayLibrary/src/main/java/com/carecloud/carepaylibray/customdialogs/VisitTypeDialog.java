package com.carecloud.carepaylibray.customdialogs;

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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VisitTypeDialog extends Dialog {

    private final OnDialogListItemClickListener callback;
    private List<VisitTypeDTO> visitTypeList;

    public interface OnDialogListItemClickListener {
        void onDialogListItemClickListener(VisitTypeDTO selectedVisitType);
    }

    /**
     * Constructor.
     * @param context context
     * @param model appointment item
     * @param callback Onclick listener
     */
    public VisitTypeDialog(Context context,
                           AppointmentResourcesDTO model,
                           OnDialogListItemClickListener callback,
                           AppointmentsResultModel appointmentsResultModel) {
        super(context);
        this.callback = callback;

        // This is the layout XML file that describes your Dialog layout
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_visit_type);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        visitTypeList = model.getResource().getVisitReasons();
        sortVisitTypeListByName();

        initializeViews(context, appointmentsResultModel);
    }

    private void initializeViews(Context context, AppointmentsResultModel appointmentsResultModel) {
        TextView titleView = (TextView) findViewById(R.id.visit_type_header_title);
        titleView.setText(appointmentsResultModel.getMetadata().getLabel().getVisitTypeHeading());

        // Load and display list
        ListView visitTypeListView = (ListView) findViewById(R.id.visitTypeList);
        visitTypeListView.setAdapter(new VisitTypeListAdapter(context, visitTypeList));
        visitTypeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                VisitTypeDTO selectedVisitType = visitTypeList.get(position);
                callback.onDialogListItemClickListener(selectedVisitType);
                dismiss();
            }
        });

        findViewById(R.id.visitTypeCancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private void sortVisitTypeListByName() {
        Collections.sort(visitTypeList, new Comparator<VisitTypeDTO>() {
            @Override
            public int compare(VisitTypeDTO lhs, VisitTypeDTO rhs) {
                if (lhs != null && rhs != null) {
                    return lhs.getName().compareTo(rhs.getName());
                }
                return -1;
            }
        });
    }

    private class VisitTypeListAdapter extends BaseAdapter {

        private Context context;
        private List<VisitTypeDTO> listItems;

        VisitTypeListAdapter(Context context, List<VisitTypeDTO> items) {
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

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

import com.carecloud.carepay.patient.appointments.fragments.ChooseProviderFragment;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentProvidersDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;

import java.util.ArrayList;
import java.util.List;

public class VisitTypeDialog extends Dialog {

    // dummy data for now till it get it from JSON file
    String[] items = {"Follow-up", "Annual Physical", "New Patient", "Existing Patient",
            "Back Pain", "Asthma", "Chest Pain"};
    List<VisitTypeDTO> visitTypesModel;
    
    /**
     * Constructor.
     * @param context context
     * @param model appointment item
     * @param listener Onclick listener
     */
    public VisitTypeDialog(Context context, final AppointmentProvidersDTO model, final OnDialogListItemClickListener listener) {
        super(context);

        // This is the layout XML file that describes your Dialog layout
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_visit_type);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        //temp solution, no visitTypes asociated to provider
        AppointmentsResultModel appointmentsResultModel = ((ChooseProviderFragment)listener).getAppointmentsResultModel();
        visitTypesModel = searchVisitTypes(model, appointmentsResultModel.getPayload().getResources());

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
                VisitTypeDTO selectedVisitType = visitTypesModel.get(position - 1);
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

    /**
     * tmporal search of visit types sice not asociated to provider in json
     * @param provider the provider
     * @param resources the resources to search visit types by provider
     * @return the visitTypes
     */
    private List<VisitTypeDTO> searchVisitTypes(AppointmentProvidersDTO provider, List<AppointmentResourcesDTO> resources){
        List<VisitTypeDTO> result = new ArrayList<>();
        for (AppointmentResourcesDTO resource: resources){
            if(provider.getId().intValue() == resource.getResource().getProvider().getId().intValue()){
                result = resource.getResource().getVisitReasons();
                break;
            }
        }
        return result;
    }

}

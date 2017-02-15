package com.carecloud.carepay.practice.library.payments.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.payments.models.PatientDTO;

import java.util.List;

public class PatientSearchResultAdapter extends RecyclerView.Adapter<PatientSearchResultAdapter.ViewHolder> {

    private Context context;
    private List<PatientDTO> patients;

    public PatientSearchResultAdapter(Context context, List<PatientDTO> patients) {
        this.context = context;
        this.patients = patients;
    }

    @Override
    public PatientSearchResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.patient_search_result_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PatientSearchResultAdapter.ViewHolder holder, int position) {
        PatientDTO patient = patients.get(position);
        holder.name.setText(patient.getFirstName() + " " + patient.getLastName());
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CarePayTextView name;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (CarePayTextView) itemView.findViewById(R.id.search_result_item_text);
        }
    }
}

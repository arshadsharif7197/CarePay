package com.carecloud.carepay.practice.library.payments.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.List;

public class PatientSearchResultAdapter extends RecyclerView.Adapter<PatientSearchResultAdapter.ViewHolder> {

    private Context context;
    private List<PatientModel> patients;
    private OnItemClickedListener clickedListener;

    public interface OnItemClickedListener {
        void onItemClicked(PatientModel patient);
    }

    /**
     * Constructor
     *
     * @param context  context
     * @param patients patient list
     */
    public PatientSearchResultAdapter(Context context, List<PatientModel> patients) {
        this.context = context;
        this.patients = patients;
    }

    public void setClickedListener(OnItemClickedListener clickedListener) {
        this.clickedListener = clickedListener;
    }

    @Override
    public PatientSearchResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.patient_search_result_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PatientSearchResultAdapter.ViewHolder holder, int position) {
        PatientModel patient = patients.get(position);
        holder.name.setText(StringUtil.capitalize(patient.getFullName()));
        holder.itemView.setContentDescription(patient.getFullName());
        holder.patientDOBTextView.setText(patient.getFormattedDateOfBirth());
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CarePayTextView name;
        private CarePayTextView patientDOBTextView;

        /**
         * Constructor
         *
         * @param itemView view
         */
        public ViewHolder(View itemView) {
            super(itemView);

            name = (CarePayTextView) itemView.findViewById(R.id.search_result_item_text);
            patientDOBTextView = (CarePayTextView) itemView.findViewById(R.id.patientDOBTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickedListener.onItemClicked(patients.get(getAdapterPosition()));
        }
    }
}

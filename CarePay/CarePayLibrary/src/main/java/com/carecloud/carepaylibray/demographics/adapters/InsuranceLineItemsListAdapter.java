package com.carecloud.carepaylibray.demographics.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class InsuranceLineItemsListAdapter extends
        RecyclerView.Adapter<InsuranceLineItemsListAdapter.InsuranceDetailsListViewHolder> {

    private Context context;
    private ApplicationMode.ApplicationType applicationType;
    private OnInsuranceEditClickListener listener;
    private List<DemographicInsurancePayloadDTO> insuranceList = new ArrayList<>();

    /**
     * @param context         context
     * @param insuranceList   list of insurances
     * @param listener        the listener
     * @param applicationType the application type
     */
    public InsuranceLineItemsListAdapter(Context context, List<DemographicInsurancePayloadDTO> insuranceList,
                                         OnInsuranceEditClickListener listener,
                                         ApplicationMode.ApplicationType applicationType) {

        this.context = context;
        this.listener = listener;
        this.applicationType = applicationType;
        this.insuranceList = insuranceList;
    }

    @Override
    public InsuranceDetailsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View paymentDetailsListItemView = LayoutInflater.from(context).inflate(
                R.layout.insurance_line_item, parent, false);
        return new InsuranceDetailsListViewHolder(paymentDetailsListItemView);
    }

    @Override
    public int getItemCount() {
        return insuranceList.size();
    }

    @Override
    public void onBindViewHolder(final InsuranceDetailsListViewHolder holder, int position) {
        final DemographicInsurancePayloadDTO lineItem = insuranceList.get(position);
        String plan = lineItem.getInsurancePlan();
        String provider = lineItem.getInsuranceProvider();
        holder.name.setText(provider + " " + (plan != null ? plan : ""));
        if (applicationType == ApplicationMode.ApplicationType.PATIENT) {
            if (hasAnotherOfTheSameType(lineItem)) {
                holder.separator.setBackgroundColor(context.getResources().getColor(R.color.redAlert));
                holder.name.setTextColor(context.getResources().getColor(R.color.redAlert));
            } else if (lineItem.getInsurancePhotos().size() == 0) {
                holder.separator.setBackgroundColor(context.getResources().getColor(R.color.lightning_yellow));
                holder.name.setTextColor(context.getResources().getColor(R.color.lightning_yellow));
                holder.edit.setText(Label.getLabel("demographics_insurance_add_photos_button"));
            } else {
                holder.separator.setBackgroundColor(context.getResources().getColor(R.color.gray_divider));
                holder.name.setTextColor(context.getResources().getColor(R.color.textview_default_textcolor));
                holder.edit.setText(Label.getLabel("practice_checin_edit_clickable_label"));
            }
            String language = ApplicationPreferences.getInstance().getUserLanguage();
            int numeral = 1;
            if (lineItem.getInsuranceType().toLowerCase().equals("primary")) {
                numeral = 1;
            } else if (lineItem.getInsuranceType().toLowerCase().equals("secondary")) {
                numeral = 2;
            } else if (lineItem.getInsuranceType().toLowerCase().equals("tertiary")) {
                numeral = 3;
            } else if (lineItem.getInsuranceType().toLowerCase().equals("quaternary")) {
                numeral = 4;
            }
            holder.type.setText(StringUtil.getOrdinal(language, numeral));
        } else {
            if (hasAnotherOfTheSameType(lineItem)) {
                showAlert(holder, R.drawable.icn_alert_red, R.color.redAlert);
                holder.separator.setBackgroundColor(context.getResources().getColor(R.color.redAlert));
                holder.name.setTextColor(context.getResources().getColor(R.color.redAlert));
            } else if (lineItem.getInsurancePhotos().size() == 0) {
                showAlert(holder, R.drawable.icn_alert, R.color.lightning_yellow);
                holder.separator.setBackgroundColor(context.getResources().getColor(R.color.lightning_yellow));
                holder.name.setTextColor(context.getResources().getColor(R.color.lightning_yellow));
                holder.edit.setText(Label.getLabel("demographics_insurance_add_photos_button"));
            } else {
                holder.alertLayout.setVisibility(View.INVISIBLE);
                holder.separator.setBackgroundColor(context.getResources().getColor(R.color.gray_divider));
                holder.name.setTextColor(context.getResources().getColor(R.color.textview_default_textcolor));
                holder.edit.setText(Label.getLabel("practice_checin_edit_clickable_label"));
            }
            holder.type.setText(StringUtil.capitalize(lineItem.getInsuranceType()));
        }

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEditInsuranceClicked(lineItem);
            }
        });

    }

    private void showAlert(InsuranceDetailsListViewHolder holder, int alertImage, int alertColor) {
        holder.alertLayout.setVisibility(View.VISIBLE);
        holder.alertImage.setImageDrawable(context.getResources().getDrawable(alertImage));
        holder.alertVertical.setBackgroundColor(context.getResources().getColor(alertColor));
        holder.alertHorizontal.setBackgroundColor(context.getResources().getColor(alertColor));
    }

    private boolean hasAnotherOfTheSameType(DemographicInsurancePayloadDTO lineItem) {
        int counter = 0;
        for (DemographicInsurancePayloadDTO insurance : insuranceList) {
            if (lineItem.getInsuranceType().toLowerCase().equals(insurance.getInsuranceType().toLowerCase())) {
                counter++;
            }
        }
        return counter > 1;
    }

    /**
     * @param insuranceList the insurances list
     */
    public void setInsurancesList(List<DemographicInsurancePayloadDTO> insuranceList) {
        this.insuranceList = insuranceList;
        notifyDataSetChanged();
    }

    public interface OnInsuranceEditClickListener {
        void onEditInsuranceClicked(DemographicInsurancePayloadDTO position);
    }

    class InsuranceDetailsListViewHolder extends RecyclerView.ViewHolder {

        CarePayTextView name;
        CarePayTextView type;
        CarePayTextView edit;
        View alertLayout;
        View alertHorizontal;
        View alertVertical;
        ImageView alertImage;
        View separator;

        InsuranceDetailsListViewHolder(View itemView) {
            super(itemView);
            name = (CarePayTextView) itemView.findViewById(R.id.health_insurance_name);
            type = (CarePayTextView) itemView.findViewById(R.id.health_insurance_type);
            edit = (CarePayTextView) itemView.findViewById(R.id.health_insurance_edit);
            alertLayout = itemView.findViewById(R.id.alertLayout);
            alertHorizontal = itemView.findViewById(R.id.horizontal);
            alertVertical = itemView.findViewById(R.id.vertical);
            alertImage = (ImageView) itemView.findViewById(R.id.alertImage);
            separator = itemView.findViewById(R.id.separator);
        }
    }
}

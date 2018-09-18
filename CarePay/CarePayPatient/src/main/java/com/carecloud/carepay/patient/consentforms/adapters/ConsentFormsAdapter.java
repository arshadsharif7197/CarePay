package com.carecloud.carepay.patient.consentforms.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.consentforms.fragments.ConsentFormViewPagerFragment;
import com.carecloud.carepay.patient.consentforms.interfaces.ConsentFormsFormsInterface;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.constants.CustomAssetStyleable;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.DateUtil;

import java.util.List;

/**
 * @author pjohnson on 6/04/18.
 */
public class ConsentFormsAdapter extends RecyclerView.Adapter<ConsentFormsAdapter.ViewHolder> {

    private static final int VIEW_TYPE_LOADING = 1;
    private final List<PracticeForm> forms;
    private ConsentFormsFormsInterface callback;
    private final int mode;
    private boolean isLoading;

    public ConsentFormsAdapter(List<PracticeForm> pendingForms, int mode) {
        this.forms = pendingForms;
        this.mode = mode;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout;
        if (viewType == VIEW_TYPE_LOADING) {
            layout = R.layout.item_loading;
        } else {
            layout = R.layout.item_provider_forms;
        }
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= forms.size()) {
            return VIEW_TYPE_LOADING;
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (position >= forms.size()) {
            return;
        }
        final PracticeForm form = forms.get(position);
        holder.container.setOnClickListener(null);
        holder.formNameTextView.setText(form.getPayload().get("title").getAsString());
        SpannableStringBuilder sb = new SpannableStringBuilder();
        if (form.getLastModifiedDate() != null) {
            holder.formCheckBox.setVisibility(View.GONE);
            sb.append(String.format(Label.getLabel("adhoc_form_date_placeholder"),
                    DateUtil.getInstance().setDateRaw(form.getLastModifiedDate())
                            .toStringWithFormatMmSlashDdSlashYyyy()));
        } else {
            sb.append(Label.getLabel("consentForms.providersFormList.item.status.neverReviewedStatus"));
        }

        if (mode == ConsentFormViewPagerFragment.PENDING_MODE) {
            holder.formDateTextView.setTextColor(holder.formDateTextView.getContext().getResources()
                    .getColor(R.color.cadet_gray));
            holder.formCheckBox.setVisibility(View.VISIBLE);
            holder.formDateTextView.setTextColor(holder.formDateTextView.getContext().getResources()
                    .getColor(R.color.lightning_yellow));

            sb.insert(0, " - ");
            sb.setSpan(new ForegroundColorSpan(holder.formDateTextView.getContext()
                            .getResources().getColor(R.color.cadet_gray)), 0, sb.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.insert(0, Label.getLabel("consentForms.providersFormList.item.status.pendingStatus"));

            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.formCheckBox.setChecked(!form.isSelected());
                }
            });
        } else {
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onFilledFormSelected(form);
                }
            });
        }

        holder.formDateTextView.setText(sb);
        holder.formCheckBox.setOnCheckedChangeListener(null);
        holder.formCheckBox.setChecked(form.isSelected());
        holder.formCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onFormSelected(isChecked, form, holder);
            }
        });
    }

    protected void onFormSelected(boolean isChecked, PracticeForm form, ViewHolder holder) {
        form.setSelected(!form.isSelected());
        callback.onPendingFormSelected(form, isChecked);
        if (form.isSelected()) {
            holder.formNameTextView.setTextColor(holder.formNameTextView.getContext()
                    .getResources().getColor(R.color.colorPrimary));
            holder.formNameTextView.setFontAttribute(CustomAssetStyleable.PROXIMA_NOVA_SEMI_BOLD);
        } else {
            holder.formNameTextView.setTextColor(holder.formNameTextView.getContext()
                    .getResources().getColor(R.color.myHealthTextColor));
            holder.formNameTextView.setFontAttribute(CustomAssetStyleable.PROXIMA_NOVA_REGULAR);
        }
    }

    @Override
    public int getItemCount() {
        if (isLoading && !forms.isEmpty()) {
            return forms.size() + 1;
        }
        return forms.size();
    }

    public void addHistoryList(List<PracticeForm> newItems) {
        this.forms.addAll(newItems);
        notifyDataSetChanged();
    }

    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
        notifyDataSetChanged();
    }

    public void setCallback(ConsentFormsFormsInterface callback) {
        this.callback = callback;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox formCheckBox;
        CarePayTextView formNameTextView;
        TextView formDateTextView;
        View container;

        public ViewHolder(View itemView) {
            super(itemView);
            formCheckBox = (CheckBox) itemView.findViewById(R.id.formCheckBox);
            formNameTextView = (CarePayTextView) itemView.findViewById(R.id.formNameTextView);
            formDateTextView = (TextView) itemView.findViewById(R.id.formDateTextView);
            container = itemView.findViewById(R.id.container);
        }
    }
}

package com.carecloud.carepay.practice.library.payments.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;

/**
 * Created by lmenendez on 3/15/17.
 */

public abstract class PopupPickerAdapter extends RecyclerView.Adapter<PopupPickerAdapter.PopupListViewHolder> {

    private Context context;

    protected BalanceItemDTO selectedBalanceItem;

    public PopupPickerAdapter(Context context) {
        this.context = context;
    }

    public void setSelectedBalanceItem(BalanceItemDTO selectedBalanceItem) {
        this.selectedBalanceItem = selectedBalanceItem;
    }

    public interface PopupPickCallback {
        void pickLocation(LocationDTO location, BalanceItemDTO balanceItem);

        void pickProvider(ProviderDTO provider, BalanceItemDTO balanceItem);
    }

    @Override
    public PopupListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_pick_row, parent, false);
        return new PopupListViewHolder(view);
    }


    public class PopupListViewHolder extends RecyclerView.ViewHolder {

        private TextView name;

        public PopupListViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.pick_list_name);
        }

        public TextView getName() {
            return name;
        }

        public void setName(TextView name) {
            this.name = name;
        }
    }
}

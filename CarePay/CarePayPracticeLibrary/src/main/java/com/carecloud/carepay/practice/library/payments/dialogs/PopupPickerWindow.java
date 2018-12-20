package com.carecloud.carepay.practice.library.payments.dialogs;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.payments.adapter.PopupPickerAdapter;
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;

/**
 * Created by lmenendez on 3/15/17.
 */

public class PopupPickerWindow extends PopupWindow {
    private Context context;
    private RecyclerView popupRecycler;
    private View popupBackgroundView;

    /**
     * Constructor
     *
     * @param context context
     */
    public PopupPickerWindow(Context context) {
        super(context);
        this.context = context;

        initView();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.popup_picker_list, null, false);
        popupBackgroundView = view.findViewById(R.id.popup_container);
        setContentView(view);
        setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(10);
        }
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);

        setWidth(context.getResources().getDimensionPixelSize(R.dimen.popup_picker_width));
        setHeight(context.getResources().getDimensionPixelSize(R.dimen.popup_picker_height));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        popupRecycler = (RecyclerView) view.findViewById(R.id.pick_list_recycler);
        popupRecycler.setLayoutManager(layoutManager);
    }

    public void setAdapter(PopupPickerAdapter adapter) {
        popupRecycler.setAdapter(adapter);
    }

    public PopupPickerAdapter getAdapter() {
        return (PopupPickerAdapter) popupRecycler.getAdapter();
    }

    public void setBalanceItemDTO(BalanceItemDTO balanceItemDTO) {
        ((PopupPickerAdapter) popupRecycler.getAdapter()).setSelectedBalanceItem(balanceItemDTO);
    }

    /**
     * Set the background to top view if necessary
     *
     * @param showOnTop true to show above th anchor view
     */
    public void flipPopup(boolean showOnTop) {
        if (showOnTop) {
            popupBackgroundView.setBackgroundResource(R.drawable.popup_picker_bg_top_new);
        } else {
            popupBackgroundView.setBackgroundResource(R.drawable.popup_picker_bg_new);
        }
    }
}

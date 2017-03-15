package com.carecloud.carepay.practice.library.payments.dialogs;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.payments.adapter.PopupPickerAdapter;

/**
 * Created by lmenendez on 3/15/17.
 */

public class PopupPickerWindow extends PopupWindow {
    private Context context;
    private RecyclerView popupRecycler;

    public PopupPickerWindow(Context context){
        super(context);
        this.context = context;

        initView();
    }

    private void initView(){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.popup_picker_list, null, false);
        setContentView(view);
    }

    @Override
    public void setContentView(View view){
        super.setContentView(view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        popupRecycler = (RecyclerView) view.findViewById(R.id.pick_list_recycler);
        popupRecycler.setLayoutManager(layoutManager);
    }

    public void setAdapter(PopupPickerAdapter adapter){
        popupRecycler.setAdapter(adapter);
    }

    public PopupPickerAdapter getAdapter(){
        return (PopupPickerAdapter) popupRecycler.getAdapter();
    }

}

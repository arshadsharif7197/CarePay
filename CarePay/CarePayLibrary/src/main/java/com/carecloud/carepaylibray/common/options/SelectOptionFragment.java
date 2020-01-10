package com.carecloud.carepaylibray.common.options;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.service.library.base.OptionNameInterface;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BlurDialogFragment;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicsOption;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.List;

/**
 * @author pjohnson on 2019-05-22.
 */
public class SelectOptionFragment extends BlurDialogFragment implements OnOptionSelectedListener {

    private OnOptionSelectedListener callback;
    private List<? extends OptionNameInterface> options;

    public static SelectOptionFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putString("title", title);
        SelectOptionFragment fragment = new SelectOptionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 40);
        dialog.getWindow().setBackgroundDrawable(inset);
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_option, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        OptionsRecyclerAdapter adapter = new OptionsRecyclerAdapter(options);
        adapter.setListener(this);
        RecyclerView optionsRecyclerView = view.findViewById(R.id.optionsRecyclerView);
        optionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        optionsRecyclerView.setAdapter(adapter);

        TextView dialogTitleTextView = view.findViewById(R.id.dialogTitleTextView);
        dialogTitleTextView.setText(StringUtil.capitalize(getArguments().getString("title")));

        view.findViewById(R.id.closeImageView).setOnClickListener(view1 -> dismiss());
    }

    public void setOptions(List<? extends OptionNameInterface> options) {
        this.options = options;
    }

    public void setCallback(OnOptionSelectedListener callback) {
        this.callback = callback;
    }

    @Override
    public void onOptionSelected(DemographicsOption option, int position) {
        callback.onOptionSelected(option, position);
        dismiss();
    }
}

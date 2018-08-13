package com.carecloud.carepay.patient.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;

/**
 * @author pjohnson on 13/08/18.
 */
public class ShimmerFragment extends Fragment {

    private final static int ROWS_PER_SECTION = 5;
    private int rowsNumber = 5;

    public static ShimmerFragment newInstance(@LayoutRes int rowLayoutId) {
        return newInstance(-1, rowLayoutId);
    }

    public static ShimmerFragment newInstance(@LayoutRes int headerLayoutId,
                                              @LayoutRes int rowLayoutId) {
        Bundle args = new Bundle();
        ShimmerFragment fragment = new ShimmerFragment();
        args.putInt("headerLayoutId", headerLayoutId);
        args.putInt("rowLayoutId", rowLayoutId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shimmer, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int headerLayoutId = getArguments().getInt("headerLayoutId");
        int rowLayoutId = getArguments().getInt("rowLayoutId");

        LinearLayoutCompat container = (LinearLayoutCompat) view.findViewById(R.id.container);
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (int i = 0; i < rowsNumber; i++) {
            if ((headerLayoutId != -1) && ((i % ROWS_PER_SECTION) == 0)) {
                inflater.inflate(headerLayoutId, container);
            } else {
                inflater.inflate(rowLayoutId, container);
            }
        }
    }

    public void setRowsNumber(int rowsNumber) {
        this.rowsNumber = rowsNumber;
    }
}

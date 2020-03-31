package com.carecloud.carepay.patient.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import com.carecloud.carepay.patient.R;
import com.google.android.material.tabs.TabLayout;

/**
 * @author pjohnson on 13/08/18.
 */
public class ShimmerFragment extends Fragment {

    private final static int ROWS_PER_SECTION = 5;
    private int rowsNumber = 5;
    private boolean isTabbed = false;
    private String leftTabLabel;
    private String rightTabLabel;

    public static ShimmerFragment newInstance(@LayoutRes int rowLayoutId) {
        return newInstance(-1, rowLayoutId, true);
    }

    public static ShimmerFragment newInstance(@LayoutRes int rowLayoutId, boolean loop) {
        return newInstance(-1, rowLayoutId, loop);
    }

    public static ShimmerFragment newInstance(@LayoutRes int headerLayoutId,
                                              @LayoutRes int rowLayoutId) {
        return newInstance(headerLayoutId, rowLayoutId, true);
    }

    public static ShimmerFragment newInstance(@LayoutRes int headerLayoutId,
                                              @LayoutRes int rowLayoutId, boolean loop) {
        Bundle args = new Bundle();
        ShimmerFragment fragment = new ShimmerFragment();
        args.putInt("headerLayoutId", headerLayoutId);
        args.putInt("rowLayoutId", rowLayoutId);
        args.putBoolean("loop", loop);
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int headerLayoutId = getArguments().getInt("headerLayoutId");
        int rowLayoutId = getArguments().getInt("rowLayoutId");
        boolean loop = getArguments().getBoolean("loop");

        LinearLayoutCompat container = view.findViewById(R.id.container);
        LayoutInflater inflater = LayoutInflater.from(getContext());

        if (isTabbed) {
            manageTabLayout(view);
        }

        if (loop) {
            for (int i = 0; i < rowsNumber; i++) {
                if ((headerLayoutId != -1) && ((i % ROWS_PER_SECTION) == 0)) {
                    inflater.inflate(headerLayoutId, container);
                } else {
                    inflater.inflate(rowLayoutId, container);
                }
            }
        } else {
            if (headerLayoutId != -1) {
                inflater.inflate(headerLayoutId, container);
            }
            inflater.inflate(rowLayoutId, container);
        }
    }

    private void manageTabLayout(View view) {
        TabLayout tabs = view.findViewById(R.id.tabLayout);
        tabs.setVisibility(View.VISIBLE);
        TabLayout.Tab leftTab = tabs.getTabAt(0);
        if (leftTab != null) {
            leftTab.setCustomView(R.layout.page_tab_layout);
            ((TextView) leftTab.getCustomView().findViewById(R.id.tab_title)).setText(leftTabLabel);
        }
        TabLayout.Tab rightTab = tabs.getTabAt(1);
        if (rightTab != null) {
            rightTab.setCustomView(R.layout.page_tab_layout);
            ((TextView) rightTab.getCustomView().findViewById(R.id.tab_title)).setText(rightTabLabel);
        }
    }

    public void setRowsNumber(int rowsNumber) {
        this.rowsNumber = rowsNumber;
    }

    public void setTabbed(boolean tabbed, String leftTabLabel, String rigthTabLabel) {
        isTabbed = tabbed;
        this.leftTabLabel = leftTabLabel;
        this.rightTabLabel = rigthTabLabel;
    }
}

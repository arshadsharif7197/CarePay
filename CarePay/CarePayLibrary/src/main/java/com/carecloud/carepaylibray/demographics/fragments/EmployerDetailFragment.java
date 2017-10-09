package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.EmployerInterface;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.demographics.dtos.payload.EmployerDto;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * @author pjohnson on 5/10/17.
 */

public class EmployerDetailFragment extends BaseFragment {

    private EmployerInterface callback;
    private EmployerDto employer;

    public static EmployerDetailFragment newInstance(EmployerDto employerDto) {
        EmployerDetailFragment fragment = new EmployerDetailFragment();
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, employerDto);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if (context instanceof DemographicsView) {
                callback = ((DemographicsView) context).getPresenter();
            } else {
                callback = (EmployerInterface) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement EmployerInterface");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        employer = DtoHelper.getConvertedDTO(EmployerDto.class, getArguments());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_employer_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbarContainer);
        TextView title = (TextView) toolbar.findViewById(R.id.settings_toolbar_title);
        title.setText(employer.getName());
        toolbar.setNavigationIcon(R.drawable.icn_patient_mode_nav_close);
        callback.setToolbar(toolbar);
        setHasOptionsMenu(true);

        setUpUI(view);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.employer_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.deleteEmployer) {
            callback.addEmployer(null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpUI(View view) {
        Button addOtherEmployerButton = (Button) view.findViewById(R.id.addOtherEmployerButton);
        addOtherEmployerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.displaySearchEmployer();
            }
        });
        if (employer.getAddress() != null) {
            TextView addressTextView = (TextView) view.findViewById(R.id.addressTextView);
            addressTextView.setText(employer.getAddress().getAddress1() + " "
                    + employer.getAddress().getAddress2());

            TextView zipCodeTextView = (TextView) view.findViewById(R.id.zipCodeTextView);
            zipCodeTextView.setText(employer.getAddress().getCity());

            TextView cityTextView = (TextView) view.findViewById(R.id.cityTextView);
            cityTextView.setText(employer.getAddress().getState());

            TextView stateTextView = (TextView) view.findViewById(R.id.stateTextView);
            stateTextView.setText(employer.getAddress().getState());

            TextView phoneTextView = (TextView) view.findViewById(R.id.phoneTextView);
            phoneTextView.setText(employer.getAddress().getPhone());
        }

    }
}

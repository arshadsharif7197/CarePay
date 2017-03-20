package com.carecloud.carepaylibray.customdialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.adapters.VisitTypeListAdapter;
import com.carecloud.carepaylibray.appointments.AppointmentNavigationCallback;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VisitTypeFragmentDialog extends BaseDialogFragment {

    private AppointmentNavigationCallback callback;
    private List<VisitTypeDTO> visitTypeList;
    private String cancelString;
    private AppointmentResourcesDTO model;
    private AppointmentsResultModel appointmentsResultModel;

    @Override
    protected String getCancelString() {
        return cancelString;
    }

    @Override
    protected int getCancelImageResource() {
        return R.drawable.icn_close;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.dialog_visit_type;
    }

    @Override
    protected boolean getCancelable() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    /**
     * Creates a VisitTypeFragmentDialog fragment
     *
     * @param model                   The appointment Resource DTO
     * @param appointmentsResultModel The appointment resource model
     */
    public static VisitTypeFragmentDialog newInstance(AppointmentResourcesDTO model,
                                                      AppointmentsResultModel appointmentsResultModel) {
        // Supply inputs as an argument
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, model);
        DtoHelper.bundleDto(args, appointmentsResultModel);

        VisitTypeFragmentDialog dialog = new VisitTypeFragmentDialog();
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (AppointmentNavigationCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AppointmentNavigationCallback");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        this.cancelString = arguments.getString("cancelString");
        model = DtoHelper.getConvertedDTO(AppointmentResourcesDTO.class, arguments);
        appointmentsResultModel = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, arguments);
        visitTypeList = model.getResource().getVisitReasons();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        sortVisitTypeListByName();
        initializeViews(view);

        return view;
    }

    protected void initializeViews(View view) {
        view.findViewById(R.id.closeViewLayout).setVisibility(View.GONE);
        if (view.findViewById(R.id.visit_type_header_title) != null) {
            TextView title = (TextView) view.findViewById(R.id.visit_type_header_title);
            title.setText(Label.getLabel("visit_type_heading"));
        }

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.respons_toolbar);
        if (toolbar != null) {
            TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
            title.setText(Label.getLabel("visit_type_heading"));
            toolbar.setTitle("");
            ViewGroup.LayoutParams layoutParams = title.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            title.setLayoutParams(layoutParams);
            title.setGravity(Gravity.CENTER_HORIZONTAL);
            view.findViewById(R.id.closeViewLayout).setVisibility(View.VISIBLE);
        }

        // Load and display list
        ListView visitTypeListView = (ListView) view.findViewById(R.id.visitTypeList);
        visitTypeListView.setAdapter(new VisitTypeListAdapter(view.getContext(), visitTypeList));
        visitTypeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                VisitTypeDTO selectedVisitType = visitTypeList.get(position);
                dismiss();
                callback.selectTime(selectedVisitType, model, appointmentsResultModel);

            }
        });
    }

    private void sortVisitTypeListByName() {
        Collections.sort(visitTypeList, new Comparator<VisitTypeDTO>() {
            @Override
            public int compare(VisitTypeDTO lhs, VisitTypeDTO rhs) {
                if (lhs != null && rhs != null) {
                    return lhs.getName().compareTo(rhs.getName());
                }
                return -1;
            }
        });
    }
}

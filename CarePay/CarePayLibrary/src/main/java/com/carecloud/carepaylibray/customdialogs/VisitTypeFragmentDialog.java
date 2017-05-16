package com.carecloud.carepaylibray.customdialogs;

import android.content.Context;
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
import com.carecloud.carepaylibray.appointments.fragments.BaseAppointmentDialogFragment;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VisitTypeFragmentDialog extends BaseAppointmentDialogFragment {

    private AppointmentNavigationCallback callback;
    private List<VisitTypeDTO> visitTypeList;
    private AppointmentResourcesDTO model;
    private AppointmentsResultModel appointmentsResultModel;

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
        attachCallback(context);
    }

    @Override
    protected void attachCallback(Context context) {
        try {
            if(context instanceof AppointmentViewHandler){
                callback = ((AppointmentViewHandler) context).getPresenter();
            }else {
                callback = (AppointmentNavigationCallback) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement AppointmentNavigationCallback");
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(callback == null){
            attachCallback(getContext());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        model = DtoHelper.getConvertedDTO(AppointmentResourcesDTO.class, arguments);
        appointmentsResultModel = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, arguments);
        visitTypeList = model.getResource().getVisitReasons();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_visit_type, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        sortVisitTypeListByName();
        initializeViews(view);
    }

    protected void initializeViews(View view) {
        hideKeyboardOnViewTouch(view);

        View closeView = view.findViewById(R.id.closeViewLayout);
        if(closeView!=null) {
            view.findViewById(R.id.closeViewLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });

        }

        setCancelable(closeView==null);

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

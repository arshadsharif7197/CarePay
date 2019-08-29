package com.carecloud.carepay.patient.messages.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.messages.MessageNavigationCallback;
import com.carecloud.carepay.patient.messages.adapters.MessagesProvidersAdapter;
import com.carecloud.carepay.patient.messages.models.MessagingModel;
import com.carecloud.carepay.patient.messages.models.ProviderContact;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 7/7/17
 */

public class MessagesProvidersFragment extends BaseFragment implements MessagesProvidersAdapter.SelectProviderCallback {

    private RecyclerView providersRecycler;

    private List<ProviderContact> providers = new ArrayList<>();
    private MessageNavigationCallback callback;
    private MessagingModel messagingModel;

    public static Fragment newInstance() {
        return new MessagesProvidersFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (MessageNavigationCallback) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement MessageNavigationCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        messagingModel = (MessagingModel) callback.getDto();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_messages_provider_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        initToolbar(view);

        providersRecycler = view.findViewById(R.id.providers_recycler);
        providersRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        setAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        callback.displayToolbar(false, null);
    }

    private void initToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(Label.getLabel("messaging_providers_title"));

        toolbar.setNavigationIcon(R.drawable.icn_patient_mode_nav_close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

    }

    private void setAdapter() {
        providers = messagingModel.getPayload().getProviderContacts();
        MessagesProvidersAdapter adapter = new MessagesProvidersAdapter(getContext(), providers, this);
        providersRecycler.setAdapter(adapter);
    }


    /**
     * Set or update the list of providers
     *
     * @param providers providers list
     */
    public void updateProvidersList(List<ProviderContact> providers) {
        this.providers = providers;
        setAdapter();
    }

    @Override
    public void onProviderSelected(ProviderContact provider) {
        callback.addFragment(MessagesNewThreadFragment.newInstance(provider), true);
    }

}

package com.carecloud.carepaylibray.signinsignup.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.interfaces.BaseFragmentInterface;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.signinsignup.ResetPasswordViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pjohnson on 03/12/2017
 */
public class ConfirmationResetPasswordFragment extends BaseFragment {

    private FragmentActivityInterface listener;
    private BaseFragmentInterface baseFragmentListener;
    private String email;
    private ResetPasswordViewModel viewModel;

    public ConfirmationResetPasswordFragment() {
    }

    /**
     * @param email the email to receive the link in order to reset the password
     * @return an instance of ConfirmationResetPasswordFragment
     */
    public static ConfirmationResetPasswordFragment newInstance(String email) {
        ConfirmationResetPasswordFragment fragment = new ConfirmationResetPasswordFragment();
        Bundle args = new Bundle();
        args.putString("email", email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (FragmentActivityInterface) context;
            baseFragmentListener = (BaseFragmentInterface) context;

        } catch (ClassCastException e) {
            throw new ClassCastException("Attached Context must implement FragmentActivityInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        email = getArguments().getString("email");
        setUpViewModel();
    }

    private void setUpViewModel() {
        viewModel = new ViewModelProvider(getActivity()).get(ResetPasswordViewModel.class);
        viewModel.getResendPasswordDtoObservable().observe(this, aVoid
                -> listener.showSuccessToast(Label.getLabel("forgot_password_confirmation_success_message")));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_confirmation_reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button goBackButton = view.findViewById(R.id.goBackButton);
        if (goBackButton != null) {
            goBackButton.setVisibility(View.VISIBLE);
            goBackButton.setOnClickListener(view1 -> getActivity().onBackPressed());
        }

        ImageView signInHome = view.findViewById(R.id.signInHome);
        if (signInHome != null) {
            signInHome.setVisibility(View.VISIBLE);
            signInHome.setOnClickListener(view12 -> {
                getActivity().setResult(ResetPasswordFragment.GO_TO_HOME);
                getActivity().finish();
            });
        }

        view.findViewById(R.id.goToMailAppButton).setOnClickListener(view13 -> showMailAppChooserDialog());

        view.findViewById(R.id.backToSignInButton).setOnClickListener(view14
                -> baseFragmentListener.startBaseFragment());
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            listener.setToolbar(toolbar);
            ((TextView) toolbar.findViewById(R.id.toolbar_title))
                    .setText(Label.getLabel("forgot_password_confirmation_screen_title"));
        }

    }

    private void showMailAppChooserDialog() {
        Intent emailIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"));
        PackageManager pm = getActivity().getPackageManager();

        List<ResolveInfo> resInfo = pm.queryIntentActivities(emailIntent, 0);
        if (resInfo.size() > 0) {
            ResolveInfo ri = resInfo.get(0);
            // First create an intent with only the package name of the first registered email app
            // and build a picked based on it
            Intent intentChooser = pm.getLaunchIntentForPackage(ri.activityInfo.packageName);
            if (intentChooser == null) {
                showErrorNotification("No Mail Application Available");
                return;
            }
            Intent openInChooser =
                    Intent.createChooser(intentChooser,
                            Label.getLabel("forgot_password_confirmation_launch_mail_dialog_button"));

            // Then create a list of LabeledIntent for the rest of the registered email apps
            List<LabeledIntent> intentList = new ArrayList<>();
            for (int i = 1; i < resInfo.size(); i++) {
                // Extract the label and repackage it in a LabeledIntent
                ri = resInfo.get(i);
                String packageName = ri.activityInfo.packageName;
                Intent intent = pm.getLaunchIntentForPackage(packageName);
                if (intent != null) {
                    intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
                }
            }

            LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);
            // Add the rest of the email apps to the picker selection
            openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
            startActivity(openInChooser);
        }
    }
}

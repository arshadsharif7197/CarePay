package com.carecloud.carepaylibray.signinsignup.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.signinsignup.dto.SignInDTO;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pjohnson on 03/12/2017
 */
public class ConfirmationResetPasswordFragment extends BaseFragment {

    private FragmentActivityInterface listener;
    private String email;

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
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (FragmentActivityInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Attached Context must implement DTOInterface");
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_confirmation_reset_password, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button goBackButton = (Button) view.findViewById(R.id.goBackButton);
        if (goBackButton != null) {
            goBackButton.setVisibility(View.VISIBLE);
            goBackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().onBackPressed();
                }
            });
        }

        ImageView signInHome = (ImageView) view.findViewById(R.id.signInHome);
        if (signInHome != null) {
            signInHome.setVisibility(View.VISIBLE);
            signInHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().setResult(ResetPasswordFragment.GO_TO_HOME);
                    getActivity().finish();
                }
            });
        }

        view.findViewById(R.id.goToMailAppButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMailAppChooserDialog();
            }
        });

        view.findViewById(R.id.resendMailButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword(((SignInDTO) listener.getDto()).getMetadata().getTransitions()
                        .getForgotPassword(), email);
            }
        });
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            listener.setToolbar(toolbar);
            ((TextView)toolbar.findViewById(R.id.toolbar_title))
                    .setText(Label.getLabel("forgot_password_confirmation_screen_title"));
        }

    }

    private void resetPassword(TransitionDTO resetPasswordTransition, String email) {
        JsonObject jsonObject2 = new JsonObject();
        jsonObject2.addProperty("username", email);
        JsonObject userObject = new JsonObject();
        userObject.add("user", jsonObject2);
        Map<String, String> headers = getWorkflowServiceHelper().getApplicationStartHeaders();
        Map<String, String> queryParams = new HashMap<>();
        getWorkflowServiceHelper().execute(resetPasswordTransition, resetPasswordCallback,
                userObject.toString(), queryParams, headers);
    }

    private WorkflowServiceCallback resetPasswordCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            listener.showSuccessToast(Label.getLabel("forgot_password_confirmation_success_message"));
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            listener.showErrorToast(exceptionMessage);
        }
    };

    private void showMailAppChooserDialog() {
        Intent emailIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"));
        PackageManager pm = getActivity().getPackageManager();

        List<ResolveInfo> resInfo = pm.queryIntentActivities(emailIntent, 0);
        if (resInfo.size() > 0) {
            ResolveInfo ri = resInfo.get(0);
            // First create an intent with only the package name of the first registered email app
            // and build a picked based on it
            Intent intentChooser = pm.getLaunchIntentForPackage(ri.activityInfo.packageName);
            if(intentChooser == null){
                showErrorNotification("No Mail Application Available");
                return;
            }
            Intent openInChooser =
                    Intent.createChooser(intentChooser, Label.getLabel("forgot_password_confirmation_launch_mail_dialog_button"));

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

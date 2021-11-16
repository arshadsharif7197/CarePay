package com.carecloud.carepaylibray.base;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelStore;

import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.base.IApplicationSession;
import com.carecloud.carepay.service.library.cognito.AppAuthorizationHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.WorkFlowRecord;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.CarePayApplication;
import com.carecloud.carepaylibray.common.BaseViewModel;
import com.carecloud.carepaylibray.customcomponents.CarePayButton;
import com.carecloud.carepaylibray.customcomponents.CarePayEditText;
import com.carecloud.carepaylibray.customcomponents.CarePayTextInputLayout;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanDetailsDialogFragment;
import com.carecloud.carepaylibray.utils.CustomPopupNotification;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.google.gson.internal.Primitives;
import java.util.ArrayList;
import java.util.List;
public abstract class BaseActivity extends AppCompatActivity implements ISession {

    private static final int FULLSCREEN_VALUE = 0x1000000;
    private static final long LOGOUT_SESSION_TIMEOUT = 1000 * 60 * 10;//10 minutes
    private static boolean isForeground = false;
    private static Handler handler;
    private static boolean expectingResult = false;

    private Dialog progressDialog;
    private CustomPopupNotification errorNotification;
    protected boolean isVisible = false;
    View rootView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.setFactory2(new CarePayLayoutInflaterFactory(this));
        super.onCreate(savedInstanceState);
        isVisible = true;
        if (handler == null) {
            handler = new Handler();
        }
        setNewRelicInteraction(getClass().getName());

    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
        isForeground = false;
        handler.postDelayed(backgroundLogoutSessionRunnable, LOGOUT_SESSION_TIMEOUT);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (manageSession()&&ev.getAction()==MotionEvent.ACTION_UP)
            ((CarePayApplication) getApplicationContext()).restartSession(this);
        return super.dispatchTouchEvent(ev);
    }
    @Override
    protected void onResume() {
        super.onResume();
        handleFragmentDialogs();
        getAppAuthorizationHelper();
        isVisible = true;
        isForeground = true;
        rootView = findViewById(android.R.id.content);
        rootView.setSoundEffectsEnabled(false);
        rootView.setOnClickListener(view -> {
            SystemUtil.hideSoftKeyboard(BaseActivity.this);
            view.requestFocus();
            onProgressDialogCancel();
        });


        setLastInteraction(System.currentTimeMillis());
        handler.removeCallbacksAndMessages(null);
        expectingResult = false;
    }

    public abstract boolean manageSession();


    private void handleFragmentDialogs() {
        FragmentManager fm = getSupportFragmentManager();
        List<BaseDialogFragment> baseDialogFragmentList = getBaseDialogs(fm.getFragments());
        int stackSize = (BaseDialogFragment.isVisible) ? baseDialogFragmentList.size() - 1 : baseDialogFragmentList.size(); // if any dialog visible not hide that one
        for (int index = 0; index < stackSize; index++) {
            baseDialogFragmentList.get(index).hideDialog();
        }
    }

    private List<BaseDialogFragment> getBaseDialogs(List<Fragment> allFragments) {
        List<BaseDialogFragment> baseDialogFragmentList = new ArrayList<>();
        for (Fragment fragment : allFragments) {
            if ((fragment instanceof BaseDialogFragment) || (fragment instanceof PaymentPlanDetailsDialogFragment)) {
                BaseDialogFragment baseFragment = (BaseDialogFragment) fragment;
                baseDialogFragmentList.add(baseFragment);
            }
        }
        return baseDialogFragmentList;
    }

    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public ApplicationPreferences getApplicationPreferences() {
        return ((IApplicationSession) getApplication()).getApplicationPreferences();
    }

    @Override
    public WorkflowServiceHelper getWorkflowServiceHelper() {
        return ((IApplicationSession) getApplication()).getWorkflowServiceHelper();
    }

    public AppAuthorizationHelper getAppAuthorizationHelper() {
        return ((IApplicationSession) getApplication()).getAppAuthorizationHelper();
    }

    @Override
    public ApplicationMode getApplicationMode() {
        return ((IApplicationSession) getApplication()).getApplicationMode();
    }

    @Override
    public void onAtomicRestart() {
        ((IApplicationSession) getApplication()).onAtomicRestart();
    }

    @Override
    public void setLastInteraction(long systemTime) {
        ((IApplicationSession) getApplication()).setLastInteraction(systemTime);
    }

    @Override
    public long getLastInteraction() {
        return ((IApplicationSession) getApplication()).getLastInteraction();
    }

    @Override
    public void setNewRelicInteraction(String interactionName) {
        ((IApplicationSession) getApplication()).setNewRelicInteraction(interactionName);
    }

    public Context getContext() {
        return this;
    }

    @Override
    public void showProgressDialog() {
        if (!isVisible()) {
            return;
        }

        if (null != progressDialog && progressDialog.isShowing()) {
            return;
        }

        if (null == progressDialog) {
            boolean isPracticeAppPatientMode = getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE;
            progressDialog = new ProgressDialogUtil(isPracticeAppPatientMode, this);
        }

        progressDialog.setOnCancelListener(progressCancelListener);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }


    @Override
    public void hideProgressDialog() {
        if (null != progressDialog && progressDialog.isShowing()) {
            //adding try catch here to prevent case where dialog is not attached to window manager
            try {
                progressDialog.dismiss();
                progressDialog = null;
            } catch (IllegalArgumentException iax) {
                iax.printStackTrace();
            }
        }
    }

    @Override
    public void showErrorNotification(String errorMessage) {
        if (!isVisible()) {
            return;
        }

        if (null != errorNotification && errorNotification.isShowing()) {
            return;
        }
        try {
            if (null == errorNotification) {

                if (!StringUtil.isNullOrEmpty(errorMessage)) {
                    errorNotification = new CustomPopupNotification(getApplicationContext(), getCurrentFocus(),
                            getWindow(), errorMessage, CustomPopupNotification.TYPE_ERROR_NOTIFICATION,
                            errorNotificationSwipeListener());
                } else {
                    errorNotification = new CustomPopupNotification(getApplicationContext(), getCurrentFocus(),
                            getWindow(), CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE,
                            CustomPopupNotification.TYPE_ERROR_NOTIFICATION, errorNotificationSwipeListener());
                }

            }
            if (isVisible()) {
                errorNotification.showPopWindow();
            }
        } catch (Exception e) {
            Log.e("Base Activity", e.getMessage() + "");
        }
    }

    /**
     * Gets cancel reason appointment dialog listener.
     *
     * @return the cancel reason appointment dialog listener
     */
    public CustomPopupNotification.CustomPopupNotificationListener errorNotificationSwipeListener() {
        return swipeDirection -> {
            hideErrorNotification();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat.getColor(getContext(),
                        R.color.colorPrimaryDark));
            }
        };
    }

    @Override
    public void hideErrorNotification() {
        if (null != errorNotification && errorNotification.isShowing()) {
            errorNotification.dismiss();
            errorNotification = null;
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        expectingResult = true;
        super.startActivityForResult(intent, requestCode);
    }

    /**
     * Common WorkflowDTO which will converted to the desire DTO with dtoClass params
     *
     * @param dtoClass class to convert
     * @param <S>      Dynamic class to convert
     * @return Dynamic converted class object
     */
    public <S> S getConvertedDTO(Class<S> dtoClass) {
        Bundle bundle = this.getIntent().getExtras();
        if (bundle == null) {
            return null;
        }

        Object rawWorkflowDTO = bundle.get(WorkflowDTO.class.getName());
        if (rawWorkflowDTO == null) {
            return null;
        }

        Gson gson = new Gson();
        try {
            long id = (Long) rawWorkflowDTO;
            WorkflowDTO workflowDTO = new WorkflowDTO(WorkFlowRecord.findById(getContext(), id));

            if (dtoClass.equals(WorkflowDTO.class)) {
                return Primitives.wrap(dtoClass).cast(workflowDTO);
            }

            return gson.fromJson(workflowDTO.toString(), dtoClass);
        } catch (ClassCastException e) {
            // Object is set as String and not in DB
            return gson.fromJson(bundle.getString(WorkflowDTO.class.getName()), dtoClass);
        }
    }

    /**
     * Common WorkflowDTO which will converted to the desire DTO with dtoClass params
     *
     * @param dtoClass class to convert
     * @param <S>      Dynamic class to convert
     * @return Dynamic converted class object
     */
    public static <S> S getConvertedDTO(Class<S> dtoClass, String jsonString) {

        if (!StringUtil.isNullOrEmpty(jsonString)) {
            Gson gson = new Gson();
            return gson.fromJson(jsonString, dtoClass);
        }
        return null;
    }

    public abstract void navigateToWorkflow(WorkflowDTO workflowDTO);

    /**
     * @param toolbar the toolbar
     */
    public void setToolbar(Toolbar toolbar) {
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.icn_nav_back));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(view -> onBackPressed());
        }
    }

    /**
     * @param fragment       the new fragment
     * @param addToBackStack if true, add to back stack
     */
    public void replaceFragment(int containerId, Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        String tag = fragment.getClass().getName();
        transaction.replace(containerId, fragment, tag);
        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
    }

    /**
     * @param fragment       the new fragment
     * @param addToBackStack if true, addFragment to back stack
     */
    public void addFragment(int containerId, Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        String tag = fragment.getClass().getCanonicalName();
        transaction.add(containerId, fragment, tag);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public void clearFragments() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }

        for (Fragment baseFragment : fm.getFragments()) {
            if (baseFragment instanceof BaseDialogFragment) {
                ((BaseDialogFragment) baseFragment).cancel();
            }
        }
    }

    /**
     * Display a fragment as a Dialog
     *
     * @param fragment       must be a Dialog Fragment
     * @param addToBackStack optional flag to addFragment this transaction to back stack
     */
    public void displayDialogFragment(DialogFragment fragment, boolean addToBackStack) {
        String tag = fragment.getClass().getName();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        if (addToBackStack) {
            ft.addToBackStack(tag);
        }

        fragment.show(ft, tag);
    }


    /**
     * @param errorMessage the error message
     */
    public void showErrorToast(String errorMessage) {
        if (isVisible)
            new CustomMessageToast(this, errorMessage, CustomMessageToast.NOTIFICATION_TYPE_ERROR).show();
    }

    /**
     * @param successMessage the success message
     */
    public void showSuccessToast(String successMessage) {
        if (isVisible)
            new CustomMessageToast(this, successMessage, CustomMessageToast.NOTIFICATION_TYPE_SUCCESS).show();
    }

    /**
     * @param title the action bar title
     */
    public void setActionBarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

    }

    /**
     * Show/Hide system ui like status bar or navigation bar.
     */
    public void setSystemUiVisibility() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    /**
     * Updates layout so in clover and devices with navigation bar is on screen don't hide content
     */
    public void setNavigationBarVisibility() {
        if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE) {

            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE
                    | FULLSCREEN_VALUE;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private Runnable backgroundLogoutSessionRunnable = () -> {
        long now = System.currentTimeMillis();
        if (now - getLastInteraction() > LOGOUT_SESSION_TIMEOUT && !isForeground && !expectingResult && !isFinishing()) {
            getApplicationMode().clearUserPracticeDTO();
            AppAuthorizationHelper authHelper = getAppAuthorizationHelper();
            authHelper.setUser(null);
            authHelper.setAccessToken(null);
            authHelper.setIdToken(null);
            authHelper.setRefreshToken(null);
            restartApp(null, false);
        }
    };

    private DialogInterface.OnCancelListener progressCancelListener = dialog -> onProgressDialogCancel();

    protected void onProgressDialogCancel() {

    }

    protected void setUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> restartApp(throwable, true));

    }

    protected void restartApp(Throwable throwable, boolean crash) {
        onAtomicRestart();
        if (throwable != null) {
            Log.e("CareCloud", "" + throwable.getMessage(), throwable);
        }
        if (crash) {
            Intent intent = new Intent();
            intent.setAction("com.carecloud.carepay.restart");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            intent.putExtra(CarePayConstants.CRASH, crash);
            PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),
                    0, intent, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager mgr = (AlarmManager) getBaseContext()
                    .getSystemService(Context.ALARM_SERVICE);
            if (mgr != null) {
                mgr.set(AlarmManager.RTC, System.currentTimeMillis(), pendingIntent);
            }
        }
        ((CarePayApplication) getApplication()).cancelSession();
        finishAffinity();
        System.exit(crash ? 2 : 0);
    }

    public void setBasicObservers(BaseViewModel viewModel) {
        viewModel.getLoading().observe(this, isLoading -> {
            if (isLoading) {
                showProgressDialog();
            } else {
                hideProgressDialog();
            }
        });
        viewModel.getErrorMessage().observe(this, this::showErrorNotification);
        viewModel.getSuccessMessage().observe(this, this::showSuccessToast);
    }

    protected abstract void stopSessionService();

    public Fragment getTopFragment() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (!fragmentList.isEmpty()) {
            Fragment topFragment = (fragmentList.get(fragmentList.size() - 1).getTag().equals("SupportLifecycleFragmentImpl")) ? fragmentList.get(fragmentList.size() - 2) : fragmentList.get(fragmentList.size() - 1);
            return topFragment;
        }
        return null;
    }

    ArrayList<View> result = new ArrayList<View>();


}

package com.carecloud.carepaylibray.base;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.base.IApplicationSession;
import com.carecloud.carepay.service.library.cognito.AppAuthorizationHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.WorkFlowRecord;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.utils.CustomPopupNotification;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.google.gson.internal.Primitives;

public abstract class BaseActivity extends AppCompatActivity implements ISession {

    private static final int FULLSCREEN_VALUE = 0x10000000;
    private static final long LOGOUT_SESSION_TIMEOUT = 1000 * 60 * 10;
    private static boolean isForeground = false;
    private static Handler handler;

    private Dialog progressDialog;
    private CustomPopupNotification errorNotification;
    protected boolean isVisible = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.setFactory2(new CarePayLayoutInflaterFactory(this));
        super.onCreate(savedInstanceState);
        isVisible = true;
        if (handler == null) {
            handler = new Handler();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
        isForeground = false;
        handler.postDelayed(logoutUserSession, LOGOUT_SESSION_TIMEOUT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAppAuthorizationHelper();
        isVisible = true;
        isForeground = true;
        final View rootView = findViewById(android.R.id.content);
        rootView.setSoundEffectsEnabled(false);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemUtil.hideSoftKeyboard(BaseActivity.this);
                view.requestFocus();
            }
        });
        setLastInteraction(System.currentTimeMillis());
        handler.removeCallbacksAndMessages(null);
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

        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
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
                    errorNotification = new CustomPopupNotification(getContext(), getCurrentFocus(),
                            getWindow(), errorMessage, CustomPopupNotification.TYPE_ERROR_NOTIFICATION,
                            errorNotificationSwipeListener());
                } else {
                    errorNotification = new CustomPopupNotification(getContext(), getCurrentFocus(),
                            getWindow(), CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE,
                            CustomPopupNotification.TYPE_ERROR_NOTIFICATION, errorNotificationSwipeListener());
                }

            }
            errorNotification.showPopWindow();
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
        return new CustomPopupNotification.CustomPopupNotificationListener() {
            @Override
            public void onSwipe(String swipeDirection) {
                hideErrorNotification();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(ContextCompat.getColor(getContext(),
                            R.color.colorPrimaryDark));
                }
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

        Object rawWorkflowDTO = bundle.get(WorkflowDTO.class.getSimpleName());
        if (rawWorkflowDTO == null) {
            return null;
        }

        Gson gson = new Gson();
        try {
            long id = (Long) rawWorkflowDTO;
            WorkflowDTO workflowDTO = new WorkflowDTO(WorkFlowRecord.findById(WorkFlowRecord.class, id));

            if (dtoClass.equals(WorkflowDTO.class)) {
                return Primitives.wrap(dtoClass).cast(workflowDTO);
            }

            return gson.fromJson(workflowDTO.toString(), dtoClass);
        } catch (ClassCastException e) {
            // Object is set as String and not in DB
            return gson.fromJson(bundle.getString(WorkflowDTO.class.getSimpleName()), dtoClass);
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
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
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
        new CustomMessageToast(this, errorMessage, CustomMessageToast.NOTIFICATION_TYPE_ERROR).show();
    }

    /**
     * @param successMessage the success message
     */
    public void showSuccessToast(String successMessage) {
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

    private Runnable logoutUserSession = new Runnable() {
        @Override
        public void run() {
            long now = System.currentTimeMillis();
            if (now - getLastInteraction() > LOGOUT_SESSION_TIMEOUT && !isForeground && !isFinishing()) {
                getApplicationMode().clearUserPracticeDTO();
                AppAuthorizationHelper authHelper = getAppAuthorizationHelper();
                authHelper.setUser(null);
                authHelper.setAccessToken(null);
                authHelper.setIdToken(null);
                authHelper.setRefreshToken(null);
                finishAffinity();
            }
        }
    };

    protected void setUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                onAtomicRestart();
                Log.e("CareCloud", "" + throwable.getMessage(), throwable);
                Intent intent = new Intent();
                intent.setAction("com.carecloud.carepay.restart");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                intent.putExtra(CarePayConstants.CRASH, true);
                PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),
                        0, intent, PendingIntent.FLAG_ONE_SHOT);
                AlarmManager mgr = (AlarmManager) getBaseContext()
                        .getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis(), pendingIntent);
                finishAffinity();
                System.exit(2);
            }
        });

    }

}

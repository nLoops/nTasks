package com.nloops.ntasks.taskslist;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nloops.ntasks.R;
import com.nloops.ntasks.utils.CloudSyncTasks;
import com.nloops.ntasks.widgets.WidgetIntentService;

import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class TasksList extends AppCompatActivity implements EasyPermissions.PermissionCallbacks,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int PERMISSION_REQ_CODE = 225;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public static final int RC_SIGN_IN = 101;
    // Current Auth User to use Firebase Database.
    public static String mUserID;
    // Ref of Shared Preferences
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_list);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.tasks_list_toolbar);
        toolbar.inflateMenu(R.menu.tasks_list_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setLogo(R.mipmap.ic_app_name);
        // get ref of Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        TasksFragment tasksFragment =
                (TasksFragment) getSupportFragmentManager().findFragmentById(R.id.tasks_list_container);
        if (tasksFragment == null) {
            // Create a new instance.
            tasksFragment = TasksFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.tasks_list_container, tasksFragment);
            transaction.commit();
        }

        // update Widget List with data.
        WidgetIntentService.startActionChangeList(this);

        // get Preferences Ref
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);

        // we need to get required permissions to allow app to RECORD AUDIO_NOTES and SAVE FILES
        getPermissions();

        //set Firebase Auth Listener
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    mUserID = user.getUid();
                    SharedPreferences preferences = PreferenceManager.
                            getDefaultSharedPreferences(TasksList.this);
                    preferences.edit().
                            putString(getString(R.string.current_user_firebase), mUserID)
                            .commit();
                    // user signed in
                    if (isSyncEnabled() && !isScheduled()) {
                        CloudSyncTasks.initialize(TasksList.this);
                    }
                } else {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(true)
                                    .setLogo(R.drawable.ic_auth_logo)
                                    .setTheme(R.style.AuthTheme)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()
                                    )).build(), RC_SIGN_IN);
                }

            }
        };
        setupFirstRunGuide(toolbar);

    }

    private void setupFirstRunGuide(Toolbar toolbar) {

        if (isFirstTimeRun()) {
            // get Display Size to Draw Rectangle in the Center of Screen
            // with app Drawable
            final Display display = getWindowManager().getDefaultDisplay();
            final Drawable appDrawable = ContextCompat.getDrawable(this, R.drawable.ic_auth_calendar);
            final Rect drawableRect = new Rect(0, 0, appDrawable.getIntrinsicWidth() * 2, appDrawable.getIntrinsicHeight() * 2);
            drawableRect.offset(display.getWidth() / 2, display.getHeight() / 2);

            // Setup TapTargetView Guide.
            // first we setup Sequence that will our Tutorial will go throw.
            final TapTargetSequence sequence = new TapTargetSequence(this)
                    .targets(
                            TapTarget.forView(findViewById(R.id.tasks_list_fab),
                                    getString(R.string.task_list_fab_title),
                                    getString(R.string.task_list_fab_description))
                                    .cancelable(false)
                                    .drawShadow(true)
                                    .titleTextDimen(R.dimen.title_text_size)
                                    .tintTarget(false)
                                    .id(1),
                            TapTarget.forToolbarMenuItem(
                                    toolbar, R.id.action_settings,
                                    getString(R.string.task_list_settings_title), getString(R.string.task_list_settings_desc))
                                    .cancelable(false)
                                    .id(2),
                            TapTarget.forToolbarOverflow(toolbar, getString(R.string.task_list_overflow_title), getString(R.string.task_list_overflow_desc))
                                    .cancelable(false)
                                    .id(3)
                    ).listener(new TapTargetSequence.Listener() {
                        @Override
                        public void onSequenceFinish() {
                            Snackbar.make(findViewById(R.id.task_list_coordinator),
                                    getString(R.string.sequence_finish_message), Snackbar.LENGTH_LONG).show();
                        }

                        @Override
                        public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                        }

                        @Override
                        public void onSequenceCanceled(TapTarget lastTarget) {

                        }
                    });

            // Second we fire our Guide.
            TapTargetView.showFor(this, TapTarget.forBounds(drawableRect,
                    getString(R.string.task_list_app_title),
                    getString(R.string.task_list_app_desc))
                    .cancelable(false)
                    .icon(appDrawable), new TapTargetView.Listener() {
                @Override
                public void onTargetClick(TapTargetView view) {
                    super.onTargetClick(view);
                    sequence.start();
                }
            });
            preferences.edit()
                    .putBoolean(getString(R.string.str_is_first_run), false)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                Snackbar.make(findViewById(R.id.task_list_coordinator), getString(R.string.firebase_signed_in), Snackbar.LENGTH_LONG).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Snackbar.make(findViewById(R.id.task_list_coordinator), getString(R.string.firebase_signed_out), Snackbar.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    private boolean isSyncEnabled() {
        return preferences.getBoolean(getString(R.string.settings_sync_key),
                getResources().getBoolean(R.bool.sync_data));
    }

    private boolean isScheduled() {
        return preferences.getBoolean(getString(R.string.backup_schedule),
                getResources().getBoolean(R.bool.backup_scheduled));
    }

    private boolean isFirstTimeRun() {
        return preferences.getBoolean(getString(R.string.str_is_first_run),
                getResources().getBoolean(R.bool.first_run));
    }

    /**
     * This Method will check if we have the required permissions to RECORD and SAVE files,
     * if not we will alert USER to get the permissions.
     */
    @TargetApi(23)
    private void getPermissions() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO};
        if (!EasyPermissions.hasPermissions(TasksList.this, permissions)) {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.permissions_required),
                    PERMISSION_REQ_CODE, permissions);
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
// TODO : we need to consider if the USER denied the permissions and need to add an AUIDo note, in this case we need to Display AlertDialog to grant the Permissions.

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.settings_sync_time_key))) {
            CloudSyncTasks.initialize(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        preferences.unregisterOnSharedPreferenceChangeListener(this);

    }
}

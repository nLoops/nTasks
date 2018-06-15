package com.nloops.ntasks.taskslist;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nloops.ntasks.R;
import com.nloops.ntasks.utils.CloudSyncTasks;
import com.nloops.ntasks.utils.GeneralUtils;
import com.nloops.ntasks.widgets.WidgetIntentService;

import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class TasksList extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int PERMISSION_REQ_CODE = 225;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public static final int RC_SIGN_IN = 101;
    // Current Auth User to use Firebase Database.
    public static String mUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tasks_list_toolbar);
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
                    if (isSyncEnabled()) {
                        CloudSyncTasks.initialize(TasksList.this);
                    }
                } else {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(true)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()
                                    )).build(), RC_SIGN_IN);
                }

            }
        };

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
                Toast.makeText(TasksList.this, "Signed In", Toast.LENGTH_LONG).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(TasksList.this, "Signed Out", Toast.LENGTH_LONG).show();
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getBoolean(getString(R.string.settings_sync_key),
                getResources().getBoolean(R.bool.sync_data));
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
}

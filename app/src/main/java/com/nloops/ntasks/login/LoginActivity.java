package com.nloops.ntasks.login;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.nloops.ntasks.R;
import com.nloops.ntasks.taskslist.TasksList;
import com.nloops.ntasks.utils.Constants;
import com.nloops.ntasks.utils.SharedPreferenceHelper;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

  private static final String TAG = LoginActivity.class.getSimpleName();
  // ref of Pattern to detect a Valid Email.
  private final Pattern VALID_EMAIL_ADDRESS_REGEX =
      Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
  // ref of Activity Layout VIEWS
  @BindView(R.id.fab)
  FloatingActionButton fab;
  @BindView(R.id.et_username)
  EditText editTextUsername;
  @BindView(R.id.et_password)
  EditText editTextPassword;
  // ref of Dialog to show info to user.
  private LovelyProgressDialog waitingDialog;
  // ref of Firebase Objects to Auth User.
  private AuthUtils authUtils;
  private FirebaseAuth mAuth;
  private FirebaseAuth.AuthStateListener mAuthListener;
  private FirebaseUser user;
  private boolean firstTimeAccess;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    ButterKnife.bind(this);
    // set first Access flag when app login
    firstTimeAccess = true;
    // Check User Auth.
    initFirebase();

  }

  @Override
  protected void onStart() {
    super.onStart();
    mAuth.addAuthStateListener(mAuthListener);
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (mAuthListener != null) {
      mAuth.removeAuthStateListener(mAuthListener);
    }
  }

  /**
   * Helper Method to init {@link #mAuth} to operate the User functions with the backend, using
   * helper method class {@link #authUtils} added the {@link #mAuthListener} to detect when and what
   * action to operate.
   */
  private void initFirebase() {
    //Define Firebase Auth.
    mAuth = FirebaseAuth.getInstance();
    // inner class that helps with multi operations for user Add,RESET, etc...
    authUtils = new AuthUtils();
    // set the listener to Auth User.
    mAuthListener = new FirebaseAuth.AuthStateListener() {
      @Override
      public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        user = firebaseAuth.getCurrentUser();
        // if current user != null means (exist or signing in)
        if (user != null) {
          // User is signed in
          Constants.UID = user.getUid();
//          sync data
          authUtils.saveUserInfo();
          // if first open app goes to TaskList
          if (firstTimeAccess) {
            startActivity(new Intent(LoginActivity.this, TasksList.class));
            LoginActivity.this.finish();

          }
        }
        // set the flag to false
        firstTimeAccess = false;
      }
    };

    //Define the ref of Dialog.
    waitingDialog = new LovelyProgressDialog(this).setCancelable(false);
  }


  /**
   * This Method will launch {@link RegisterActivity} for a results with beautiful animation if the
   * user device is >= LOLLIPOP or normal transition if under.
   *
   * @param view @{{@link #fab}}
   */
  @SuppressLint("RestrictedApi")
  public void clickRegisterLayout(View view) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().setExitTransition(null);
      getWindow().setEnterTransition(null);

      ActivityOptions options =
          ActivityOptions.makeSceneTransitionAnimation(this, fab, fab.getTransitionName());
      startActivityForResult(new Intent(this, RegisterActivity.class),
          Constants.REQUEST_CODE_REGISTER, options.toBundle());
    } else {
      startActivityForResult(new Intent(this, RegisterActivity.class),
          Constants.REQUEST_CODE_REGISTER);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    // if we got a successful registration then we call createUser helper method
    if (requestCode == Constants.REQUEST_CODE_REGISTER && resultCode == RESULT_OK) {
      authUtils.createUser(data.getStringExtra(Constants.STR_EXTRA_USERNAME),
          data.getStringExtra(Constants.STR_EXTRA_PASSWORD));
    }
  }

  public void clickLogin(View view) {
    String username = editTextUsername.getText().toString();
    String password = editTextPassword.getText().toString();
    if (validate(username, password)) {
      authUtils.signIn(username, password);
    } else {
      Toast.makeText(this, getString(R.string.str_msg_invaild_email), Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    setResult(RESULT_CANCELED, null);
    finish();
  }

  private boolean validate(String emailStr, String password) {
    Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
    return (password.length() > 0 || password.equals(";")) && matcher.find();
  }

  public void clickResetPassword(View view) {
    String username = editTextUsername.getText().toString();
    if (validate(username, ";")) {
      authUtils.resetPassword(username);
    } else {
      Toast.makeText(this, getString(R.string.str_msg_reset), Toast.LENGTH_SHORT).show();
    }
  }

  /**
   * This helper class will help {@link FirebaseAuth} to Create,Reset,SignIn as well add userData to
   * {@link FirebaseDatabase}
   */
  class AuthUtils {

    /**
     * Action register
     */
    void createUser(String email, String password) {
      waitingDialog
          .setTitle(getString(R.string.str_msg_loading))
          .setTopColorRes(R.color.colorPrimary)
          .show();
      mAuth.createUserWithEmailAndPassword(email, password)
          .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
              waitingDialog.dismiss();
              saveUserInfo();
              // If sign in fails, display a message to the user. If sign in succeeds
              // the auth state listener will be notified and logic to handle the
              // signed in user can be handled in the listener.
              if (!task.isSuccessful()) {
                new LovelyInfoDialog(LoginActivity.this) {
                  @Override
                  public LovelyInfoDialog setConfirmButtonText(String text) {
                    findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm)
                        .setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View view) {
                            dismiss();
                          }
                        });
                    return super.setConfirmButtonText(text);
                  }
                }
                    .setTopColorRes(R.color.colorAccent)
                    .setIcon(R.drawable.ic_add_friend)
                    .setTitle(getString(R.string.str_msg_false))
                    .setMessage(getString(R.string.str_msg_email_exist))
                    .setConfirmButtonText(getString(R.string.str_dialog_ok))
                    .setCancelable(false)
                    .show();
              } else {
                // if true we upload user data to server.
                initNewUserInfo();
                Toast.makeText(LoginActivity.this, getString(R.string.str_login_sucess),
                    Toast.LENGTH_SHORT)
                    .show();
                startActivity(new Intent(LoginActivity.this, TasksList.class));
                LoginActivity.this.finish();
              }
            }
          })
          .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              waitingDialog.dismiss();
            }
          })
      ;
    }


    /**
     * Action Login
     */
    void signIn(String email, String password) {
      waitingDialog
          .setTitle(getString(R.string.str_login_process))
          .setTopColorRes(R.color.colorPrimary)
          .show();
      mAuth.signInWithEmailAndPassword(email, password)
          .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
              // If sign in fails, display a message to the user. If sign in succeeds
              // the auth state listener will be notified and logic to handle the
              // signed in user can be handled in the listener.
              waitingDialog.dismiss();
              if (!task.isSuccessful()) {
                new LovelyInfoDialog(LoginActivity.this) {
                  @Override
                  public LovelyInfoDialog setConfirmButtonText(String text) {
                    findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm)
                        .setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View view) {
                            dismiss();
                          }
                        });
                    return super.setConfirmButtonText(text);
                  }
                }
                    .setTopColorRes(R.color.colorAccent)
                    .setTitle(getString(R.string.str_login_failed))
                    .setMessage(getString(R.string.str_login_email_wrong))
                    .setCancelable(false)
                    .setConfirmButtonText(getString(R.string.str_dialog_ok))
                    .show();
              } else {
                // if we success get sign in we save current user data into SharedPreferences.
                saveUserInfo();
                Intent intent = new Intent(LoginActivity.this, TasksList.class);
                intent.putExtra(Constants.EXTRAS_SIGN_IN_INTENT, "logged");
                startActivity(intent);
                LoginActivity.this.finish();
              }
            }
          })
          .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              waitingDialog.dismiss();
            }
          });
    }

    /**
     * Action reset password
     */
    void resetPassword(final String email) {
      mAuth.sendPasswordResetEmail(email)
          .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
              new LovelyInfoDialog(LoginActivity.this) {
                @Override
                public LovelyInfoDialog setConfirmButtonText(String text) {
                  findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm)
                      .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                          dismiss();
                        }
                      });
                  return super.setConfirmButtonText(text);
                }
              }
                  .setTopColorRes(R.color.colorPrimary)
                  .setTitle(getString(R.string.str_reset_okay))
                  .setMessage(getString(R.string.str_reset_okay_message) + " " + email)
                  .setConfirmButtonText(getString(R.string.str_dialog_ok))
                  .show();
            }
          })
          .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              new LovelyInfoDialog(LoginActivity.this) {
                @Override
                public LovelyInfoDialog setConfirmButtonText(String text) {
                  findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm)
                      .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                          dismiss();
                        }
                      });
                  return super.setConfirmButtonText(text);
                }
              }
                  .setTopColorRes(R.color.colorAccent)
                  .setTitle(getString(R.string.str_reset_failed_message))
                  .setMessage(getString(R.string.str_reset_failed_message) + " " + email)
                  .setConfirmButtonText(getString(R.string.str_dialog_ok))
                  .show();
            }
          });
    }

    /**
     * Get User Data from Server and Save it into Shared Preferences.
     */
    void saveUserInfo() {
      try {
        LocalUser newLocalUser = new LocalUser();
        newLocalUser.email = user.getEmail();
        newLocalUser.name = user.getEmail().substring(0, user.getEmail().indexOf("@"));
        SharedPreferenceHelper.getInstance(LoginActivity.this).saveUserInfo(newLocalUser);
      } catch (Exception e) {
        Log.i(TAG, "saveUserInfo: " + e.getMessage());
      }
    }

    /**
     * Push new User Data to {@link FirebaseDatabase} under node /users/user-uid
     */
    void initNewUserInfo() {
      LocalUser newLocalUser = new LocalUser();
      newLocalUser.email = user.getEmail();
      newLocalUser.name = user.getEmail().substring(0, user.getEmail().indexOf("@"));
      FirebaseDatabase.getInstance().getReference().child(Constants.USERS_DATABASE_REFERENCE)
          .child(user.getUid())
          .setValue(newLocalUser);
    }
  }
}

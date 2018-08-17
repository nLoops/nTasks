package com.nloops.ntasks.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.nloops.ntasks.R;
import com.nloops.ntasks.utils.Constants;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

  // passing data from Login Activity.
  public static final String STR_EXTRA_ACTION_REGISTER = "register";
  // this pattern will check the user input.
  private final Pattern VALID_EMAIL_ADDRESS_REGEX =
      Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
  // Activity Views Ref.
  @BindView(R.id.cv_add)
  CardView cvAdd;
  @BindView(R.id.fab)
  FloatingActionButton fab;
  @BindView(R.id.et_username)
  EditText editTextUsername;
  @BindView(R.id.et_password)
  EditText editTextPassword;
  @BindView(R.id.et_repeatpassword)
  EditText editTextRepeatPassword;
  @BindView(R.id.bt_go)
  Button mRegButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);
    ButterKnife.bind(this);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      ShowEnterAnimation();
    }
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          animateRevealClose();
        } else {
          finish();
        }
      }
    });

    // set register button listener
    mRegButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        clickRegister();
      }
    });
  }


  @TargetApi(VERSION_CODES.LOLLIPOP)
  private void ShowEnterAnimation() {
    Transition transition = TransitionInflater.from(this)
        .inflateTransition(R.transition.fabtransition);
    getWindow().setSharedElementEnterTransition(transition);

    transition.addListener(new Transition.TransitionListener() {
      @Override
      public void onTransitionStart(Transition transition) {
        cvAdd.setVisibility(View.GONE);
      }

      @Override
      public void onTransitionEnd(Transition transition) {
        transition.removeListener(this);
        animateRevealShow();
      }

      @Override
      public void onTransitionCancel(Transition transition) {

      }

      @Override
      public void onTransitionPause(Transition transition) {

      }

      @Override
      public void onTransitionResume(Transition transition) {

      }


    });
  }

  @RequiresApi(api = VERSION_CODES.LOLLIPOP)
  public void animateRevealShow() {
    Animator mAnimator = ViewAnimationUtils
        .createCircularReveal(cvAdd, cvAdd.getWidth() / 2, 0, fab.getWidth() / 2,
            cvAdd.getHeight());
    mAnimator.setDuration(500);
    mAnimator.setInterpolator(new AccelerateInterpolator());
    mAnimator.addListener(new AnimatorListenerAdapter() {

      @Override
      public void onAnimationStart(Animator animation) {
        cvAdd.setVisibility(View.VISIBLE);
        super.onAnimationStart(animation);
      }
    });
    mAnimator.start();
  }

  @TargetApi(VERSION_CODES.LOLLIPOP)
  public void animateRevealClose() {
    Animator mAnimator = ViewAnimationUtils
        .createCircularReveal(cvAdd, cvAdd.getWidth() / 2, 0, cvAdd.getHeight(),
            fab.getWidth() / 2);
    mAnimator.setDuration(500);
    mAnimator.setInterpolator(new AccelerateInterpolator());
    mAnimator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        cvAdd.setVisibility(View.INVISIBLE);
        super.onAnimationEnd(animation);
        fab.setImageResource(R.drawable.ic_signup);
        RegisterActivity.super.onBackPressed();
      }

    });
    mAnimator.start();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      animateRevealClose();
    } else {
      finish();
    }
  }

  private void clickRegister() {
    String username = editTextUsername.getText().toString();
    String password = editTextPassword.getText().toString();
    String repeatPassword = editTextRepeatPassword.getText().toString();
    if (validate(username, password, repeatPassword)) {
      Intent data = new Intent();
      data.putExtra(Constants.STR_EXTRA_USERNAME, username);
      data.putExtra(Constants.STR_EXTRA_PASSWORD, password);
      data.putExtra(Constants.STR_EXTRA_ACTION, STR_EXTRA_ACTION_REGISTER);
      setResult(RESULT_OK, data);
      finish();
    } else {
      Toast.makeText(this, "Invalid email or not match password", Toast.LENGTH_SHORT).show();
    }
  }

  /**
   * Validate email, pass == re_pass
   *
   * @param emailStr passed Email.
   * @param password passed Password.
   * @return boolean if the input Data Validated or not.
   */
  private boolean validate(String emailStr, String password, String repeatPassword) {
    Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
    return password.length() > 0 && repeatPassword.equals(password) && matcher.find();
  }
}

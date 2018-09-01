package com.nloops.ntasks.UI;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.nloops.ntasks.R;
import com.nloops.ntasks.login.LocalUser;
import com.nloops.ntasks.utils.Constants;
import com.nloops.ntasks.utils.SharedPreferenceHelper;

/**
 * From Library Author Tutorial
 *
 * @NonReusable is annotation in PlaceHolderView, to be used in cases where we want to release all
 * the resources and references if removed from the list and won’t use the same object in addView()
 * method in PlaceHolderView.
 * @layout is used to bind the layout with this class
 * @View is used to bind the views in this layout we want to refer to
 * @Resolve is used to operate on the view references obtained from @View, in short if we want to
 * define any operation on the views it should be put in a method and annotated with @Resolve
 */
@NonReusable
@Layout(R.layout.drawer_header)
public class DrawerHeader {

  private final Context mContext;
  @View(R.id.profileImageView)
  private ImageView mImageView;
  @View(R.id.nameTxt)
  private TextView mUserName;
  @View(R.id.email_address)
  private TextView mEmailAddress;

  public DrawerHeader(Context context) {
    this.mContext = context;
  }

  @Resolve
  private void onResolved() {
    LocalUser user = SharedPreferenceHelper.getInstance(mContext).getUserInfo();
    mUserName.setText(user.name.equals("") ? Constants.CURRENT_USERNAME : user.name);
    mEmailAddress.setText(user.email.equals("") ? Constants.CURRENT_USEREMAIL : user.email);
  }
}

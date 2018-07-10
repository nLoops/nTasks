package com.nloops.ntasks.UI;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.nloops.ntasks.R;

/**
 * This Class will hold {@link DrawerLayout} items,
 * we can here define a interface with listener to items clicks
 */
@Layout(R.layout.drawer_item)
public class DrawerMenuItem {

    public static final int DRAWER_MENU_ITEM_CALENDER_VIEW = 1;

    private int mMenuPosition;
    private Context mContext;
    private DrawerCallBack mCallBack;

    @View(R.id.itemNameTxt)
    private TextView itemNameTxt;

    @View(R.id.itemIcon)
    private ImageView itemIcon;

    public DrawerMenuItem(Context context, int menuPosition) {
        mContext = context;
        mMenuPosition = menuPosition;
    }

    @Resolve
    private void onResolved() {
        switch (mMenuPosition) {
            case DRAWER_MENU_ITEM_CALENDER_VIEW:
                itemNameTxt.setText(mContext.getString(R.string.drawer_calender_view));
                itemIcon.setImageDrawable(mContext.getResources().
                        getDrawable(R.drawable.ic_empty_calender_small));
                break;
        }
    }

    @Click(R.id.mainView)
    private void onMenuItemClick() {
        switch (mMenuPosition) {
            case DRAWER_MENU_ITEM_CALENDER_VIEW:
                Toast.makeText(mContext, "Calender View", Toast.LENGTH_LONG).show();
                if (mCallBack != null) mCallBack.onCaldenderViewSelected();
                break;
        }
    }

    public void setDrawerCallBack(DrawerCallBack callBack) {
        mCallBack = callBack;
    }

    public interface DrawerCallBack {
        void onCaldenderViewSelected();

    }


}

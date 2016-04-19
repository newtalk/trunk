package net.shopnc.shop.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import net.shopnc.shop.R;
import net.shopnc.shop.ncinterface.INCOnDialogConfirm;

/**
 * Created by dqw on 2015/6/30.
 */
public class NCDialog {
    Context context;

    View popupView;
    PopupWindow mPopupWindow;
    TextView tv1;
    TextView tv2;
    Button btnCancel;
    Button btnConfirm;

    INCOnDialogConfirm incOnDialogConfirm;

    public NCDialog(Context context) {
        this.context = context;

        popupView = LayoutInflater.from(context).inflate(R.layout.nc_dialog, null);
        mPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));

        FrameLayout flBack = (FrameLayout) popupView.findViewById(R.id.flBack);
        flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });

        tv1 = (TextView) popupView.findViewById(R.id.tvText1);
        tv2 = (TextView) popupView.findViewById(R.id.tvText2);

        btnCancel = (Button) popupView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });

        btnConfirm = (Button) popupView.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incOnDialogConfirm.onDialogConfirm();
                mPopupWindow.dismiss();
            }
        });

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }

    public void setOnDialogConfirm(INCOnDialogConfirm incOnDialogConfirm) {
        this.incOnDialogConfirm = incOnDialogConfirm;
    }

    public void setText1(String text1){
        tv1.setText(text1);
    }

    public void setText2(String text2){
        tv2.setText(text2);
    }

    public void showPopupWindow(){
        mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }
}

package com.xiaomizuche.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaomizuche.R;

/**
 * 支付弹出框
 */
public class PaymentDialog implements OnClickListener {
    private Context context;
    private Display display;
    private Dialog dialog;
    private TextView feeView;
    private ImageView wxpayView;
    private ImageView alipayView;
    private ImageView unionpayView;
    private PayCallBack payCallBack;

    public PaymentDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public PaymentDialog builder() {
        //初始化显示的view
        View view = LayoutInflater.from(context).inflate(
                R.layout.view_payment_dialog, null);
        feeView = (TextView) view.findViewById(R.id.tv_fee);
        wxpayView = (ImageView) view.findViewById(R.id.iv_wxpay);
        alipayView = (ImageView) view.findViewById(R.id.iv_alipay);
        unionpayView = (ImageView) view.findViewById(R.id.iv_unionpay);

        wxpayView.setOnClickListener(this);
        alipayView.setOnClickListener(this);
        unionpayView.setOnClickListener(this);

        //创建dialog
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        lp.width = display.getWidth();
        dialogWindow.setAttributes(lp);
        return this;
    }

    public PaymentDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public PaymentDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    public void show() {
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        dialog.dismiss();
        switch (view.getId()) {
            case R.id.iv_wxpay:
                payCallBack.onPay("wxpay");
                break;
            case R.id.iv_alipay:
                payCallBack.onPay("alipay");
                break;
            case R.id.iv_unionpay:
                payCallBack.onPay("unionpay");
                break;
            default:
                break;
        }
    }

    public void setFee(String fee) {
        feeView.setText("支付年费:￥" + fee);
    }

    public interface PayCallBack {
        void onPay(String payMode);
    }

    public void setPayCallBack(PayCallBack payCallBack) {
        this.payCallBack = payCallBack;
    }
}

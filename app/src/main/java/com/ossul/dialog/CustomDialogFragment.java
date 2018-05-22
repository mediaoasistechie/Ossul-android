package com.ossul.dialog;

import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ossul.R;
import com.ossul.activities.BaseActivity;
import com.ossul.appconstant.AppConstants;
import com.ossul.utility.AppUtilsMethod;
import com.ossul.utility.Validator;


/**
 * * Fragment for custom dialog used in app
 */
public class CustomDialogFragment extends Dialog {

    private static CustomDialogFragment customDialogFragment;
    private static int dialogTypeValue = -1;
    private BaseActivity mContext;
    private OnDialogClickListener onClickListener;

    public CustomDialogFragment(final BaseActivity context, String title, String message1, String message2, String leftButton, String rightButton, int dialogType, final OnDialogClickListener onClickListener) {
        super(context, R.style.PickerAnim);
        this.mContext = context;
        this.onClickListener = onClickListener;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialogType == 0) {
            setContentView(R.layout.fragment_custom_dialog);
            TextView titleTV = (TextView) findViewById(R.id.tv_title);
            TextView message1TV = (TextView) findViewById(R.id.tv_message1);
            TextView message2TV = (TextView) findViewById(R.id.tv_message2);
            TextView cancelTV = (TextView) findViewById(R.id.tv_cancel);
            TextView okTV = (TextView) findViewById(R.id.tv_ok);

            if (!Validator.isEmptyString(title))
                titleTV.setText(title);
            if (!Validator.isEmptyString(message1))
                message1TV.setText(message1);
            if (!Validator.isEmptyString(message2)) {
                message2TV.setText(message2);
                message2TV.setVisibility(View.VISIBLE);
            } else
                message2TV.setVisibility(View.GONE);
            if (!Validator.isEmptyString(leftButton))
                cancelTV.setText(leftButton);
            if (!Validator.isEmptyString(rightButton))
                okTV.setText(rightButton);


            okTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener != null) {
                        onClickListener.onClickOk();
                    }
                    dismiss();
                }
            });

            cancelTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener != null) {
                        onClickListener.onClickCancel();
                    }
                    dismiss();
                }
            });
        }else  if (dialogType == AppConstants.APP_UPDATE) {
            setContentView(R.layout.update_dialog);
            TextView titleTV = (TextView) findViewById(R.id.tv_title);
            TextView message1TV = (TextView) findViewById(R.id.tv_message1);
            TextView message2TV = (TextView) findViewById(R.id.tv_message2);
            TextView cancelTV = (TextView) findViewById(R.id.tv_cancel);
            TextView okTV = (TextView) findViewById(R.id.tv_ok);

            if (!Validator.isEmptyString(title))
                titleTV.setText(title);
            if (!Validator.isEmptyString(message1))
                message1TV.setText(message1);
            if (!Validator.isEmptyString(message2)) {
                message2TV.setText(message2);
                message2TV.setVisibility(View.VISIBLE);
            } else
                message2TV.setVisibility(View.GONE);
            if (!Validator.isEmptyString(leftButton))
                cancelTV.setText(leftButton);
            if (!Validator.isEmptyString(rightButton))
                okTV.setText(rightButton);


            okTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener != null) {
                        onClickListener.onClickOk();
                    }
                    dismiss();
                }
            });

            cancelTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener != null) {
                        onClickListener.onClickCancel();
                    }
                    dismiss();
                }
            });
        }
        else if (dialogType == 1) {
            setContentView(R.layout.layout_custom_dialog);
            TextView messageTV = (TextView) findViewById(R.id.tv_message);
            messageTV.setText(message1);
            TextView okTV = (TextView) findViewById(R.id.tv_ok);
            if (leftButton != null && leftButton.length() > 0) {
                if (leftButton.contains("finish") && leftButton.split("-").length > 0) {
                    okTV.setText(leftButton.split("-")[0]);
                    this.setCanceledOnTouchOutside(false);
                    this.setCancelable(false);
                } else
                    okTV.setText(leftButton);
            }
            okTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener != null) {
                        onClickListener.onClickOk();
                    }
                    dismiss();
                }
            });
        } else if (dialogType == AppConstants.CREATE_ECATALOGUE) {
            setContentView(R.layout.dialog_create_ecatalogue);
            TextView titleTV = (TextView) findViewById(R.id.tv_title);
            TextView message1TV = (TextView) findViewById(R.id.tv_message1);
            final TextView eNameET = (TextView) findViewById(R.id.et_e_name);
            TextView cancelTV = (TextView) findViewById(R.id.tv_cancel);
            TextView okTV = (TextView) findViewById(R.id.tv_ok);

            if (!Validator.isEmptyString(title))
                titleTV.setText(title);
            if (!Validator.isEmptyString(message1))
                message1TV.setText(message1);
            if (!Validator.isEmptyString(leftButton))
                cancelTV.setText(leftButton);
            if (!Validator.isEmptyString(rightButton))
                okTV.setText(rightButton);


            okTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener != null) {
                        onClickListener.onClickOk();
                    }
                    dismiss();
                }
            });

            cancelTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        } else if (dialogType == AppConstants.SHARED_SUCCESS) {
            setContentView(R.layout.layout_shared_dialog);
            TextView messageTV = (TextView) findViewById(R.id.tv_message);
            messageTV.setText(message1);
            TextView okTV = (TextView) findViewById(R.id.tv_ok);
            if (leftButton != null && leftButton.length() > 0) {
                if (leftButton.contains("finish") && leftButton.split("-").length > 0) {
                    okTV.setText(leftButton.split("-")[0]);
                    this.setCanceledOnTouchOutside(false);
                    this.setCancelable(false);
                } else
                    okTV.setText(leftButton);
            }
            okTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener != null) {
                        onClickListener.onClickOk();
                    }
                    dismiss();
                }
            });
        }


        if (!(mContext).isFinishing()) {
            show();
        }
    }

    public static CustomDialogFragment getInstance() {
        return customDialogFragment;
    }

    public static CustomDialogFragment getInstance(BaseActivity context, String title, String message1, String message2, String leftButton, String rightButton, int dialogType, final OnDialogClickListener onClickListener) {
        if (customDialogFragment == null) {
            dialogTypeValue = dialogType;
            customDialogFragment = new CustomDialogFragment(context, title, message1, message2, leftButton, rightButton, dialogType, onClickListener);
            return customDialogFragment;
        }

        if (!(dialogTypeValue == dialogType && customDialogFragment.isShowing())) {
            dialogTypeValue = dialogType;
            if (customDialogFragment.isShowing()) {
                customDialogFragment.dismiss();
            }
            customDialogFragment = new CustomDialogFragment(context, title, message1, message2, leftButton, rightButton, dialogType, onClickListener);
        }
        return customDialogFragment;
    }

    /*public static void showNewsDetailPopup(final BaseActivity activity) {

        Dialog dialog = new PopupWindow(activity, R.style.PickerAnim);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_property_details);
        dialog.
        RelativeLayout mainRL = (RelativeLayout) dialog.findViewById(R.id.rl_main);
        mainRL.setBackground(activity.getResources().getDrawable(R.drawable.property_pupup_bg));
        mainRL.setPadding(AppUtilsMethod.dpToPx(activity, 15), AppUtilsMethod.dpToPx(activity, 10), AppUtilsMethod.dpToPx(activity, 15), AppUtilsMethod.dpToPx(activity, 10));
        ImageView closeIV = (ImageView) dialog.findViewById(R.id.iv_close);
        dialog.show();
    }

*/
    public interface OnDialogClickListener {
        void onClickOk();

        void onClickCancel();
    }
}

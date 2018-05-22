package com.ossul.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ossul.R;
import com.ossul.apppreferences.AppPreferences;
import com.ossul.utility.AppUtilsMethod;
import com.ossul.utility.Validator;

public class DialogManager extends Dialog {

    private final Activity context;

    public DialogManager(Activity context) {
        super(context);
        AppUtilsMethod.hideSoftKeyboard(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_progress_indicator);
        TextView textView = (TextView) findViewById(R.id.tv_text);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        if (!Validator.isEmptyString(AppPreferences.get().getPrefLang()) && AppPreferences.get().getPrefLang().equalsIgnoreCase("ar"))
            textView.setText(context.getResources().getString(R.string.loading));
        progressBar.setIndeterminate(true);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFFFFFFFF, android.graphics.PorterDuff.Mode.MULTIPLY);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }
}

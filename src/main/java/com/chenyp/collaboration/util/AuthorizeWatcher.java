package com.chenyp.collaboration.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by change on 2015/11/6.
 */
public class AuthorizeWatcher implements TextWatcher {

    //监听改变的文本框
    private EditText et;

    public AuthorizeWatcher(EditText et) {
        this.et = et;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String text = et.getText().toString();
        if (!ValidateUtil.isAuthorizeValid(text)) {
            et.setText(text.substring(0, start));
            et.setSelection(start);
            Toast.makeText(et.getContext(), "格式为:id号,id号…", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}

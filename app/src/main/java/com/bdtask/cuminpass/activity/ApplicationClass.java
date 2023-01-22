package com.bdtask.cuminpass.activity;

import android.text.TextUtils;

import com.bdtask.cuminpass.utils.CryptLib;

public class ApplicationClass {

    public static String encryptMessage(String message,String key) {
        String encryptedMsg = "";
        if (message != null && !message.equals("")) {
            try {
                CryptLib cryptLib = new CryptLib();
                encryptedMsg = cryptLib.encryptPlainTextWithRandomIV(message, key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return encryptedMsg;
    }

    public static String decryptMessage(String message,String key) {
        if (message != null && !TextUtils.isEmpty(message)) {
            try {
                CryptLib cryptLib = new CryptLib();
                message = cryptLib.decryptCipherTextWithRandomIV(message, key);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return message;
        }
        return "";
    }

}

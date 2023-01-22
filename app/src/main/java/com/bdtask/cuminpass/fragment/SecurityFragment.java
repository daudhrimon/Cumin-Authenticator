package com.bdtask.cuminpass.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.bdtask.cuminpass.R;
import com.bdtask.cuminpass.activity.MainActivity;
import com.bdtask.cuminpass.utils.SharedPref;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

public class SecurityFragment extends Fragment {

    private OtpView otpView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_security, container, false);


        otpView = view.findViewById(R.id.otp_view);
        showSoftKeyBoard(otpView);
        otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {

               if (otp.equals(SharedPref.read("pinNumber",""))){
                   hideSoftKeyBoard(otpView);
                   Intent intent = new Intent(getActivity(), MainActivity.class);
                   startActivity(intent);
                   getActivity().finish();
               }else {
                   otpView.setText("");
                   Toast.makeText(getActivity(), "Wrong PIN Number", Toast.LENGTH_SHORT).show();
               }
            }
        });


        return view;
    }


    // code to hide soft keyboard
    public void hideSoftKeyBoard(OtpView otpView) {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(otpView.getWindowToken(), 0);
    }


    // code to show soft keyboard
    private void showSoftKeyBoard(OtpView otpView) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        otpView.requestFocus();
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}
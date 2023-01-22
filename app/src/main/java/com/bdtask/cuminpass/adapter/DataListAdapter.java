package com.bdtask.cuminpass.adapter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import com.bdtask.cuminpass.R;
import com.bdtask.cuminpass.activity.MainActivity;
import com.bdtask.cuminpass.model.UserData;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DataListAdapter extends RecyclerView.Adapter<DataListAdapter.MyViewHolder>{
    static final String DATE_FORMAT = "yyyyMMddHHmm";
    Context context;
    List<UserData> dataList;
    SimpleDateFormat simpleDateFormat;
    String finalText = "";


    public DataListAdapter(Context context, List<UserData> dataList) {
        this.context = context;
        this.dataList = dataList;
        simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.custom_layout,viewGroup,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.name.setText(dataList.get(position).getAccountName());
        finalText  = getTimeStamp(dataList.get(position).getAccountKey());
        Log.wtf("finalCode",finalText);

        runAllHandler(holder,position);
        Log.wtf("hello",dataList.toString());

        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.layout.setBackgroundResource(R.color.cardBg);
                holder.name.setTextColor(context.getResources().getColor(R.color.colorWhite));
                holder.code.setTextColor(context.getResources().getColor(R.color.colorWhite));
                ((MainActivity)context).itemClick(dataList.get(position).getId(),dataList.get(position).getAccountName(),dataList.get(position).getAccountKey());

                return true;
            }
        });

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.code.getText().toString().isEmpty()){
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("info", holder.code.getText().toString());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, "copied to clipboard", Toast.LENGTH_SHORT).show();
                }

            }
        });




    }

    private void runAllHandler(MyViewHolder holder, int position) {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {

                        if(dataList != null && dataList.size() > 0)
                        {
                            Date currentTime = Calendar.getInstance().getTime();
                            SimpleDateFormat sdf = new SimpleDateFormat("ss");
                            Log.wtf("hello",sdf.format(currentTime));
                            holder.progressBar.setProgress(Integer.parseInt(sdf.format(currentTime)));
                            if (Integer.parseInt(sdf.format(currentTime)) > 40){
                                holder.progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
                            }else {
                                holder.progressBar.setProgressTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAccent)));
                            }
                            finalText = getTimeStamp(dataList.get(position).getAccountKey());
                            String hashed = getSha256(finalText);
                            Log.wtf("Hashed", hashed);
                            Log.wtf("HashedToDecimal", String.valueOf(new BigInteger(hashed,16)));
                            String codeValue = String.valueOf(new BigInteger(hashed,16));
                            holder.code.setText(codeValue.substring(0,6));
                        }



                        handler.postDelayed(this,2000);

                    }
                },2000);


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static String getSha256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));

            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public String getTimeStamp(String keyCode){


        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());
        Log.wtf("testTime",utcTime);

        return keyCode+utcTime;

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name,code;
        ProgressBar progressBar;
        LinearLayout layout;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name         = itemView.findViewById(R.id.codeName);
            code         = itemView.findViewById(R.id.code);
            progressBar  = itemView.findViewById(R.id.circularProgressbar);
            layout       = itemView.findViewById(R.id.linearLayout2);
        }
    }
}


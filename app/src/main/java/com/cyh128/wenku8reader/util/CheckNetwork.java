package com.cyh128.wenku8reader.util;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.cyh128.wenku8reader.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class CheckNetwork extends BroadcastReceiver {
    private Dialog dialog;
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo !=null && networkInfo.isAvailable()){
            if (dialog != null) {
                dialog.dismiss();
            }
        }else {
            Dialog(context);
        }
    }
    private void Dialog(final Context context) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setIcon(R.drawable.nointernet);
        builder.setTitle("无网络");
        builder.setMessage("请您正确设置网络连接");
        builder.setCancelable(false);
        dialog = builder.show();
    }
}



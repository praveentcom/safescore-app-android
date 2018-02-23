package io.praveen.safescore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SafeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, SafeService.class);
        context.startService(i);
    }
}
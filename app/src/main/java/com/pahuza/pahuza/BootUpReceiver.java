package com.pahuza.pahuza;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pahuza.pahuza.services.LocationService;

/**
 * Created by baryariv on 29/12/2016.
 */

public class BootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        /****** For Start Activity *****/
//        Intent i = new Intent(context, MyActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(i);

        /***** For start Service  ****/
        Intent myIntent = new Intent(context, LocationService.class);
        context.startService(myIntent);
    }

}

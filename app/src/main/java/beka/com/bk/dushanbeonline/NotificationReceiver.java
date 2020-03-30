package beka.com.bk.dushanbeonline;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equalsIgnoreCase("beka.com.bk.dushanbeonline.INFORM_NOTIFICATION")){
            System.out.println("abab receiver inform");
            Intent i = new Intent(context,NotificationActivity.class);
            context.startActivity(i);

        }
        if(action.equalsIgnoreCase("com.google.firebase.MESSAGING_EVENT")){
            System.out.println("abab receiver mess");
            Intent i = new Intent(context,NotificationActivity.class);
            context.startActivity(i);

        }
    }
}

package iooojik.casein.background.process;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WebSocketsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentService = new Intent(context, WebSocketsService.class);
        context.startService(intentService);
    }

}

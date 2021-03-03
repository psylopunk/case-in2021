package iooojik.casein.background.process;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;


public class WebSocketsService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        try {
            openConnection();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // данный метод открыает соединение
    public void openConnection() throws InterruptedException {
        try {
            // WatchData - это класс, с помощью которого мы передадим параметры в
            // создаваемый поток
            WatchData data = new WatchData("message", getApplicationContext());
            // создаем новый поток для сокет-соединения
            WatchSocket watchSocket = new WatchSocket();
            watchSocket.execute(data);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}

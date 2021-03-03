package iooojik.casein.background.process;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import iooojik.casein.LogMessages;
import iooojik.casein.SocketEvents;
import iooojik.casein.localData.AppDatabase;
import iooojik.casein.localData.chatRooms.ChatRoomModel;
import iooojik.casein.StaticVars;
import iooojik.casein.custom.notification.CustomNotification;
import iooojik.casein.localData.childs.ChildModel;
import iooojik.casein.localData.messages.MessageLocalModel;

public class WatchSocket extends AsyncTask<WatchData , Integer, Integer> {

    Context mCtx;
    Socket mySock;
    CustomNotification notification;
    SharedPreferences preferences;
    LogMessages logMessages = new LogMessages();
    SocketEvents socketEvents = new SocketEvents();

    protected void onProgressUpdate(Integer... progress) {

    }

    protected void onPostExecute(Integer result) {
        // Это выполнится после завершения работы потока
    }


    protected Integer doInBackground(WatchData... param) {

        mCtx = param[0].ctx;
        String messageText = param[0].message;

        try {
            while(true) {

                mySock = IO.socket(new StaticVars().getSOCKET_URL());

                // открываем сокет-соединение
                SocketData data = new SocketData();
                data.ctx = mCtx;
                data.sock = mySock;
                preferences = mCtx.getSharedPreferences(new StaticVars().getPreferencesName(), Context.MODE_PRIVATE);
                mySock.connect();
                Log.i(logMessages.getSYSTEM_MESSAGE(), "SOCKET CONNECTION EST");

                AppDatabase database = AppDatabase.Companion.getAppDataBase(mCtx);

                if(database != null) {
                    List<ChildModel> models = database.childModelDao().getAll();

                    for (ChildModel model : models) {
                        mySock.emit(socketEvents.getEVENT_JOIN(), model.getModelId());
                    }

                    Log.i(logMessages.getSYSTEM_MESSAGE(), "SOCKET CONNECTED TO CHAT ROOMS");

                }

                mySock.on(socketEvents.getEVENT_NOTIFICATION(),  args -> {
                    JSONObject message = null;
                    try {
                        message = new JSONObject(args[0].toString());
                        notification = new CustomNotification("Уведомление", message.getString("message"), mCtx);

                        MessageLocalModel messageLocalModel = new MessageLocalModel(
                                null,
                                message.getString("message"),
                                message.getString("unique_room_id"),
                                message.getString("sender")
                        );
                        database.messageDao().insert(messageLocalModel);

                        Log.i(logMessages.getSYSTEM_MESSAGE(), "RECEIVED NOTIFICATION");
                        notification.makeNotification();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

                // Поток, который принимает входящие сообщения

                GetPacket pack = new GetPacket();
                AsyncTask<SocketData, Integer, Integer> running = pack.execute(data);

                // Следим за потоком, принимающим сообщения
                while(running.getStatus().equals(Status.RUNNING)) {

                }

                // Если поток закончил принимать сообщения - это означает,
                // что соединение разорвано (других причин нет).
                // Это означает, что нужно закрыть сокет
                // и открыть его опять в бесконечном цикле
                try{
                    mySock.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            return -1;
        }
    }

    public static boolean isForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}


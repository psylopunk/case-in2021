package iooojik.casein.background.process;

import android.content.Context;
import android.os.AsyncTask;
import io.socket.client.Socket;

public class GetPacket extends AsyncTask<SocketData, Integer, Integer> {

    protected void onPostExecute(Integer result) {
        // Это выполнится после завершения работы потока
    }

    protected void onProgressUpdate(Integer... progress) {

    }


    protected Integer doInBackground(SocketData... param) {

        return 0;
    }
}

class SocketData {
    Socket sock;
    Context ctx;
}
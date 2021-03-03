package iooojik.casein.background.process;

import android.content.Context;

public class WatchData {
    String message;
    Context ctx;

    public WatchData(String message, Context ctx) {
        this.message = message;
        this.ctx = ctx;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }
}

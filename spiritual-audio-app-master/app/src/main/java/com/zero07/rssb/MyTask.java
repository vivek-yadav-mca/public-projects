package dummydata;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class MyTask extends AsyncTask<Void, Void, Void> {
    ProgressDialog dialog;
    Context context;
    public MyTask(Context context)
    {
        this.context=context;

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }

    protected void onPreExecute() {
        dialog=new ProgressDialog(context);
        dialog.setMessage("Loading...");
        dialog.show();
    }

    protected void onPostExecute(Void unused) {
    }
}

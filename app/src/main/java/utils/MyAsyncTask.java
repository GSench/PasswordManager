package utils;

/**
 * Created by grish on 18.03.2017.
 */

public class MyAsyncTask {

    Thread mThread;
    function onBackground;
    function onPostExecute;

    public MyAsyncTask(final function onBackground, function onPostExecute){
        this.onBackground=onBackground;
        this.onPostExecute=onPostExecute;
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                onBackground.run();
            }
        });
    }
}

package demo.atlysmmp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.net.Uri;
import android.widget.MediaController;
import android.widget.VideoView;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Environment;
import android.view.MotionEvent;
import android.util.Log;
import android.view.View;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import android.view.Window;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.content.Intent;

import org.apache.http.util.EntityUtils;




public class MainActivity extends ActionBarActivity {

    // testing on Emulator:
    private static final String TAG = MainActivity.class.getSimpleName();
    List<String> schedulearray = new ArrayList<String>();

    public int currentvid = 0;


    //public String atlysmmpschedulekey = getResources().getString(R.string.atlysmmp_schedule_key);
    // testing on Emulator:


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (android.os.Build.VERSION.SDK_INT > 9) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()

                    .permitAll().build();

            StrictMode.setThreadPolicy(policy);

        }


        FetchURL();

        VideoView vidView = (VideoView)findViewById(R.id.myVideo);


        Log.d(TAG, "started and seeked!");


        vidView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "stopped!");
                currentvid++;

                String nextvideo = schedulearray.get(currentvid).toString();

                FetchFileUrl(nextvideo);
            }
        });



        vidView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "dont touch me!");
                return true;
            }
        });

    }




    @Override
    public void onBackPressed() {
        System.exit(0);
    }

    @Override
    protected void onStop()
    {
        System.exit(0);
    }



    public void FetchURL() {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://atlysmmp.com/api/schedule/bP06XXdLa8koBUE7lZ11jyt5F0i9");

        try {
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();

            //
            // Read the contents of an entity and return it as a String.
            //
            String content = EntityUtils.toString(entity);
            System.out.println(content);
            LoadJSON(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public void LoadJSON(String response){

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonScheduleArray = jsonObject.getJSONArray("schedule");
            Log.d(TAG, "JSON LOADED");
            for (int i = 0; i < jsonScheduleArray.length(); i++) {
                String scheduleset = jsonScheduleArray.get(i).toString();
                schedulearray.add(scheduleset);
                //JSONObject youValue = jsonArray.getJSONObject(i);
                System.out.println(scheduleset);
                //String idModule = youValue.getString("id_module");
                // Use the same for remaining values



                // set the first video!
                if (i==0){
                    String firstvideo = scheduleset;
                    System.out.println("FIRST VIDEO");
                    System.out.println(firstvideo);
                    FetchFileUrl(firstvideo);
                } else {

                }
            }

           // StartAtlysschedule();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }





    public void FetchFileUrl(String fid) {
        String completeurl = "http://atlysmmp.com/api/file/"+fid;
        System.out.println(completeurl);

        // brake up start and end times
        String[] separated = fid.split("#");
        String[] separatedtime = separated[1].split("~");
        String endtime = separatedtime[1];
        String start = separatedtime[0].substring(2);


        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(completeurl);

        try {
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity);
            ObjtoVideo(content, start);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void ObjtoVideo(String response, String starttime){

            String scheduleset = response.substring(3);
            String newscheduleset = scheduleset.substring(0, scheduleset.length()-2);

            Log.d(TAG, "OBJECT TO VIDEO PLAYER");
            System.out.println(newscheduleset);

        int startseconds = new Integer(starttime.toString());
        int millistart = startseconds * 1000;

        System.out.println(millistart);

        VideoView vidView = (VideoView)findViewById(R.id.myVideo);
        Uri uri = Uri.parse(newscheduleset); //Declare your url here.
        vidView.setVideoURI(uri);
        vidView.requestFocus();
        vidView.start();
        vidView.seekTo(millistart);

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package tk.urbantaxi.dtxi.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import tk.urbantaxi.dtxi.Vehicles;

import static android.content.Context.MODE_PRIVATE;
import static tk.urbantaxi.dtxi.Vehicles.SHARED_PREFERENCE;

/**
 * Created by steph on 6/20/2017.
 */

public class Requestor {

    public String url;
    public Boolean isRunning = false;
    public Map<String, Object> param;
    public Context context;
    public Boolean asynchronus = false;

    public Requestor(String url, Map<String, Object> param, Context context){
        this.url = url;
        this.param = param;
        this.context = context;
    }

    public void execute(){
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE);
        String userDetails = prefs.getString("userDetails", null);
        if (userDetails != null) {
            try {
                JSONObject userObject = new JSONObject(userDetails);
                String username = userObject.getString("username");
                String password = userObject.getString("password");
                param.put("username", username);
                param.put("password", password);
                Boolean network = isNetworkAvailable();
                if (network == true) {
                    if (isRunning == true && asynchronus == true) {
                        new Ajaxer().execute();
                    }else{
                        new Ajaxer().execute();
                    }
                } else {
                    onNetworkError();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void onNetworkError(){
        Toast.makeText(context, "Something went wrong.", Toast.LENGTH_LONG);
    }

    public void preExecute(){

    }

    public void postExecute(Boolean cancelled, String result){

    }

    public void cancelled(){
        Toast.makeText(context, "Something went wrong.", Toast.LENGTH_LONG);
    }

    public class Ajaxer extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isRunning = true;
            preExecute();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            isRunning = false;
            cancelled();
            postExecute(true, "");
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                byte[] postDataBytes = urlParams(param);
                java.net.URL urlj = new URL(url);
                connection = (HttpURLConnection) urlj.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                connection.setDoOutput(true);
                connection.getOutputStream().write(postDataBytes);
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                String finalJson = buffer.toString().trim();
                return finalJson;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {

                cancel(true);
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }
                try {
                    if(reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            isRunning = false;
            postExecute(false, s);
        }
    }

    public static byte[] urlParams(Map<String, Object> params) throws UnsupportedEncodingException {
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");
        return postDataBytes;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

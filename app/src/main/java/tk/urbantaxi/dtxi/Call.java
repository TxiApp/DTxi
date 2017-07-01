package tk.urbantaxi.dtxi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import tk.urbantaxi.dtxi.classes.Requestor;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Call extends AppCompatActivity implements View.OnClickListener {
    private TextView tvName;
    private TextView tvAddress;
    private Button btnStart;
    private Button btnReject;
    public Intent intent;
    public CountDownTimer countDownTimer;
    private ProgressDialog dialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");

        tvName = (TextView)findViewById(R.id.tvName);
        tvAddress = (TextView)findViewById(R.id.tvAddress);
        btnStart = (Button)findViewById(R.id.btnStart);
        btnReject = (Button)findViewById(R.id.btnReject);

        intent = getIntent();
        tvName.setText(intent.getStringExtra("name"));
        tvAddress.setText(intent.getStringExtra("address") + " to " + intent.getStringExtra("destination"));

        btnReject.setOnClickListener(this);
        btnStart.setOnClickListener(this);

        countDownTimer = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Map<String, Object> param = new LinkedHashMap<>();
                param.put("id", intent.getStringExtra("id"));
                Requestor failed = new Requestor("failedrequest", param, getApplicationContext()){
                    @Override
                    public void onNetworkError(){
                        Log.e("Error", "No internet connection.");
                    }

                    @Override
                    public void postExecute(Boolean cancelled, String result){
                        finish();
                    }
                };
                failed.execute();
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnReject:
                Map<String, Object> param = new LinkedHashMap<>();
                param.put("id", intent.getStringExtra("id"));
                Requestor reject = new Requestor("rejectrequest", param, getApplicationContext()){
                    @Override
                    public void onNetworkError(){
                        showDialog("Network Error", "Please check your internet connection.");
                    }
                    @Override
                    public void preExecute(){
                        countDownTimer.cancel();
                        dialog.show();
                    }
                    @Override
                    public void postExecute(Boolean cancelled, String result){
                        if(cancelled){
                            Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }else{
                            try {
                                JSONObject object = new JSONObject(result);
                                String res = object.getString("result");
                                if(res.equals("success")){
                                    finish();
                                }else{
                                    Toast.makeText(getApplicationContext(), "Request is already expired.", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                reject.execute();
                break;
            case R.id.btnStart:
                Map<String, Object> param2 = new LinkedHashMap<>();
                param2.put("id", intent.getStringExtra("id"));
                Requestor accept = new Requestor("acceptrequest", param2, getApplicationContext()){
                    @Override
                    public void onNetworkError(){
                        showDialog("Network Error", "Please check your internet connection.");
                    }
                    @Override
                    public void preExecute(){
                        countDownTimer.cancel();
                        dialog.show();
                    }
                    @Override
                    public void postExecute(Boolean cancelled, String result){
                        if(cancelled){
                            Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }else{
                            try {
                                JSONObject object = new JSONObject(result);
                                String res = object.getString("result");
                                if(res.equals("success")){
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(getApplicationContext(), "Request is already expired.", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                accept.execute();
                break;
        }
    }
}

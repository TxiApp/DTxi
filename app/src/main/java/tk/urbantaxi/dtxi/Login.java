package tk.urbantaxi.dtxi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.LinkedHashMap;
import java.util.Map;

import tk.urbantaxi.dtxi.classes.Requestor;
import tk.urbantaxi.dtxi.services.LocationService;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static tk.urbantaxi.dtxi.classes.Constants.SHARED_PREFERENCE;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog dialog;
    private EditText etUsername;
    private EditText etPassword;
    private Button btnSubmit;
    private Requestor requestor;
    private Map<String, Object> param;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(this);

        param = new LinkedHashMap<>();
        requestor = new Requestor("login", param, this){
            @Override
            public void onNetworkError(){
                showDialog("Network Error", "Please check your internet connection and try again later.");
            }

            @Override
            public void preExecute(){
                dialog.show();
            }

            @Override
            public void postExecute(Boolean cancelled, String result){
                dialog.dismiss();
                try {
                    JSONObject response = new JSONObject(result);
                    String r = response.getString("result");
                    if (r.equals("success")) {
                        JSONObject details = response.getJSONObject("details");
                        String stringDetails = details.toString();
                        SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE).edit();
                        editor.putString("userDetails", stringDetails);
                        editor.commit();

                        Intent intentService = new Intent(getApplicationContext(), LocationService.class);
                        startService(intentService);

                        Boolean inSession = response.getBoolean("session");
                        Intent intent = null;
                        if(inSession){
                            intent = new Intent(getApplicationContext(), MainActivity.class);
                        }else{
                            intent = new Intent(getApplicationContext(), Menuoptions.class);
                        }

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        showDialog("Error", "Invalid username or password.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE);
        String userDetails = prefs.getString("userDetails", null);
        if (userDetails != null) {
            param.put("relogin", "relogin");
            requestor.execute();
        }
    }

    @Override
    public void onClick(View v) {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            param.put("username", username);
            param.put("password", password);
            requestor.execute();
    }
}

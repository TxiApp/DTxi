package tk.urbantaxi.dtxi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.MapFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static tk.urbantaxi.dtxi.classes.Constants.SHARED_PREFERENCE;

public class Session extends AppCompatActivity {

    public TextView tvSessionDate;
    public TextView tvName;
    public TextView tvCompany;
    public TextView tvCode;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        tvSessionDate = (TextView)findViewById(R.id.tvSessionDate);
        tvName = (TextView)findViewById(R.id.tvName);
        tvCompany = (TextView)findViewById(R.id.tvCompany);
        tvCode = (TextView)findViewById(R.id.tvCode);

        SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE);
        String userDetails = prefs.getString("userDetails", null);

        JSONObject userObject = null;
        try {
            userObject = new JSONObject(userDetails);
            tvName.setText(userObject.getString("name"));
            tvCompany.setText(userObject.getString("company_name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tvCode.setText("");

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("E d");
        SimpleDateFormat hourFormat = new SimpleDateFormat("KK:mm a");

        tvSessionDate.setText(dateFormat.format(date) + " at " + hourFormat.format(date));
    }
    public void goToMap (View view){
        Intent intent = new Intent (this, MainActivity.class);
        startActivity(intent);
    }
}

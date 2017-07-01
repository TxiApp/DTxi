package tk.urbantaxi.dtxi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static tk.urbantaxi.dtxi.classes.Constants.SHARED_PREFERENCE;

public class Menuoptions extends AppCompatActivity {
    public TextView tvName;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menuoptions);

        SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE);
        String userDetails = prefs.getString("userDetails", null);

        tvName = (TextView)findViewById(R.id.tvName);

        try {
            JSONObject userObject = new JSONObject(userDetails);
            tvName.setText(userObject.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void goToVehicles (View view){
        Intent intent = new Intent (this, Vehicles.class);
        startActivity(intent);
    }
}

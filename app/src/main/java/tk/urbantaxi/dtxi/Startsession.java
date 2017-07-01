package tk.urbantaxi.dtxi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import tk.urbantaxi.dtxi.classes.Requestor;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static tk.urbantaxi.dtxi.Vehicles.VEHICLE_URL;
import static tk.urbantaxi.dtxi.classes.Constants.SHARED_PREFERENCE;

public class Startsession extends AppCompatActivity implements View.OnClickListener {
    public final static String URL = "startsession";

    public TextView tvName;
    public ImageView ivTaxiImage;
    public TextView tvTaxiCode;
    public Button btnSubmit;

    public ImageLoader imageLoader = ImageLoader.getInstance();
    public Requestor requestor;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startsession);

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        SharedPreferences prefs = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE);
        String userDetails = prefs.getString("userDetails", null);

        tvName = (TextView)findViewById(R.id.tvName);
        tvTaxiCode = (TextView)findViewById(R.id.tvTaxiCode);
        ivTaxiImage = (ImageView)findViewById(R.id.ivTaxiImage);
        btnSubmit = (Button)findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(this);

        JSONObject userObject = null;
        try {
            userObject = new JSONObject(userDetails);
            tvName.setText(userObject.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        intent = getIntent();
        tvTaxiCode.setText(intent.getStringExtra("code"));
        imageLoader.displayImage(VEHICLE_URL + intent.getStringExtra("image"), ivTaxiImage);

        Map<String, Object> param = new LinkedHashMap<>();
        param.put("code", intent.getStringExtra("code"));
        requestor = new Requestor(URL, param, this){
            @Override
            public void preExecute(){
                btnSubmit.setText("Please wait...");
            }

            @Override
            public void postExecute(Boolean cancelled, String result){
                JSONObject object = null;
                try {
                    object = new JSONObject(result);
                    String r = object.getString("result");
                    if(r.equals("success")){
                        Intent intent2 = new Intent (getApplicationContext(), Session.class);
                        intent2.putExtra("code", intent.getStringExtra("code"));
                        startActivity(intent2);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

    }
    public void goToSession (View view){
        Intent intent = new Intent (this, Session.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnSubmit){
            requestor.execute();
        }
    }
}

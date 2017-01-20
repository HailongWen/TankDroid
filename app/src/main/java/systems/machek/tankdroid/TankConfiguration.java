package systems.machek.tankdroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class TankConfiguration extends AppCompatActivity {

    private TextView ipAddressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tank_configuration);

        ipAddressView = (TextView)findViewById(R.id.ipAddress);
        ipAddressView.setText(getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE).getString(Constants.CONFIG_IP_ADDRESS, Constants.DEFAULT_IP_ADDRESS));
    }


    public void saveConfiguration(View view) {
        SharedPreferences sharedPref = getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(Constants.CONFIG_IP_ADDRESS, ipAddressView.getText().toString());
        editor.commit();
    }

}

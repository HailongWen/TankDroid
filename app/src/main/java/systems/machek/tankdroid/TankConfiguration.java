package systems.machek.tankdroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.security.keystore.UserNotAuthenticatedException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import systems.machek.tankdroid.systems.machek.tankdroid.network.PingChecker;

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


    public void pingTest(View view) {
        PingChecker pc = new PingChecker(ipAddressView.getText().toString());
        Thread t = new Thread(pc);
        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!pc.isReachable()) {
            alertDialog("Failed", "Host not reachable");

        } else {
            alertDialog("OK", "Host is reachable");

        }
    }

    public void alertDialog(String title, String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(TankConfiguration.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

}

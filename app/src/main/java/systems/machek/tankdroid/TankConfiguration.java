package systems.machek.tankdroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import systems.machek.tankdroid.network.JsonTester;
import systems.machek.tankdroid.network.PingChecker;

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

    public void jsonTest(View view) {
        JSONObject command = new JSONObject();
        try {
            command.put("method", "test");
            String json = command.toString();

            JsonTester sender = new JsonTester(ipAddressView.getText().toString(), json);

            Thread t = new Thread(sender);
            t.start();
            t.join();

            if (sender.isAllWentFine()) {
                alertDialog("JSON Test OK", "Your tank says: " + sender.getResult());
            } else {
                alertDialog("JSON Test FAILED", "Error: " + sender.getErrorMessage());
            }



        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void moveTest(View view) {
        JSONObject command = new JSONObject();
        JSONObject args = new JSONObject();
        try {
            command.put("method", "move");

            args.put("move_l", 0);
            args.put("move_r", 0);
            args.put("move_t", 0);

            command.put("params", args);

            String json = command.toString();

            JsonTester sender = new JsonTester(ipAddressView.getText().toString(), json);

            Thread t = new Thread(sender);
            t.start();
            t.join();

            if (sender.isAllWentFine()) {
                alertDialog("JSON Test OK", "Your tank says: " + sender.getResult());
            } else {
                alertDialog("JSON Test FAILED", "Error: " + sender.getErrorMessage());
            }



        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
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

package systems.machek.tankdroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

import systems.machek.tankdroid.network.JsonTester;
import systems.machek.tankdroid.network.PingChecker;

public class TankConfiguration extends AppCompatActivity {

    private TextView ipAddressView;
    private String enteredURL;
    // private CheckBox fpsBox;
    private Set<String> mjpegUrls;

    private SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tank_configuration);

        prefs = getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);


        ipAddressView = (TextView)findViewById(R.id.ipAddress);
        ipAddressView.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        ipAddressView.setText(prefs.getString(Constants.CONFIG_IP_ADDRESS, Constants.DEFAULT_IP_ADDRESS));
        mjpegUrls = new HashSet<String>(prefs.getStringSet(Constants.CONFIG_MJPEG_URLS, new HashSet<String>()));

        //fpsBox = (CheckBox) findViewById(R.id.fpsBox);
        //fpsBox.setSelected(prefs.getBoolean(Constants.CONFIG_SHOW_FPS, false));

        displayMjpegUrls();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }


    public void saveConfiguration(View view) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(Constants.CONFIG_MJPEG_URLS, mjpegUrls);
        editor.putString(Constants.CONFIG_IP_ADDRESS, ipAddressView.getText().toString());
        // editor.putBoolean(Constants.CONFIG_SHOW_FPS, fpsBox.isSelected());
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


    public void openStreamAddDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter MJPEG stream URL");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        input.setText("http://" + ipAddressView.getText());
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enteredURL = input.getText().toString();
                addStreamURL();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void addStreamURL() {
        mjpegUrls.add(enteredURL);
        displayMjpegUrls();
    }

    private void displayMjpegUrls() {
        TableLayout l = (TableLayout) findViewById(R.id.urlListLayout);
        TableLayout.LayoutParams p = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.MATCH_PARENT);

        l.removeAllViews();

        for (String url : mjpegUrls) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));


            EditText urlEdit = new EditText(this);
            urlEdit.setText(url);
            Button deleteButton = new Button(this);
            deleteButton.setText("Delete");
            deleteButton.setOnClickListener((View v) -> {
                mjpegUrls.remove(url);
                displayMjpegUrls();
            });

            //urlEdit.setLayoutParams(p);
            row.addView(urlEdit);
            //deleteButton.setLayoutParams(p);
            row.addView(deleteButton);

            l.addView(row);
        }
    }

    public void restartCamServices(View v) {

        JSONObject command = new JSONObject();
        try {
            command.put("method", "restart_cams");
            String json = command.toString();

            JsonTester sender = new JsonTester(ipAddressView.getText().toString(), json);

            Thread t = new Thread(sender);
            t.start();
            t.join();

            if (sender.isAllWentFine()) {
                alertDialog("Cam services restarted", "Your tank says: " + sender.getResult());
            } else {
                alertDialog("Failed to restart cam services", "Error: " + sender.getErrorMessage());
            }



        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void rebootTank(View v) {

        JSONObject command = new JSONObject();
        try {
            command.put("method", "reboot");
            String json = command.toString();

            JsonTester sender = new JsonTester(ipAddressView.getText().toString(), json);

            Thread t = new Thread(sender);
            t.start();
            t.join();

            if (sender.isAllWentFine()) {
                alertDialog("Reboot initiated", "Your tank says: " + sender.getResult());
            } else {
                alertDialog("Failed to restart cam services", "Error: " + sender.getErrorMessage());
            }



        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

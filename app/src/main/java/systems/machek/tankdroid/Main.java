package systems.machek.tankdroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
     }

    public void launchTabbedCockpit(View view) {
        Intent intent = new Intent(this, LiveCockpit.class);
        startActivity(intent);
    }

    public void launchConfiguration(View view) {
        Intent intent = new Intent(this, TankConfiguration.class);
        startActivity(intent);
    }
}


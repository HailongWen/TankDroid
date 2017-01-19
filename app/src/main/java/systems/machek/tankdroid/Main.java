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

    public void launchFullScreen(View view) {
        Intent intent = new Intent(this, FullScreenCockpit.class);
        startActivity(intent);
    }


    public void launchFullScreenH264(View view) {
        Intent intent = new Intent(this, FullScreenCockpitH264.class);
        startActivity(intent);
    }

    public void launchTabbedCockpit(View view) {
        Intent intent = new Intent(this, CockpitTabbed.class);
        startActivity(intent);
    }

    public void launchTest(View view) {
        Intent intent = new Intent(this, Test.class);
        startActivity(intent);
    }
}


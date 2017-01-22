package systems.machek.tankdroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

import org.json.JSONException;
import org.json.JSONObject;

import systems.machek.tankdroid.systems.machek.tankdroid.network.JsonSender;
import systems.machek.tankdroid.widgets.MjpegFragment;

public class LiveCockpit extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_live_cockpit);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            String ip = getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE).getString(Constants.CONFIG_IP_ADDRESS, Constants.DEFAULT_IP_ADDRESS);

            switch (position) {
                case 0: return MjpegFragment.newInstance(Constants.FRONT_MJPEG_CAM_URL.replace("xxipxx", ip), Constants.FRONT_MJPEG_CAM_NAME);
                case 1: return MjpegFragment.newInstance(Constants.FRONT_MJPEG_CAM_URL.replace("xxipxx", ip), Constants.TURRET_MJPEG_CAM_NAME);
            }

            return null;

        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return Constants.FRONT_MJPEG_CAM_NAME;
                case 1:
                    return Constants.TURRET_MJPEG_CAM_NAME;
            }
            return null;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        return true;
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent ev) {

        String ip = getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE).getString(Constants.CONFIG_IP_ADDRESS, Constants.DEFAULT_IP_ADDRESS);

        float throttleLeft = ev.getAxisValue(MotionEvent.AXIS_Y);
        float throttleRight = ev.getAxisValue(MotionEvent.AXIS_RZ);


        JSONObject json = new JSONObject();

        try {
            json.put("method", "move");
            JSONObject args = new JSONObject();
            args.put("move_l", throttleLeft);
            args.put("move_r", throttleRight);
            args.put("move_t", 0.0);
            json.put("params", args);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonSender command = new JsonSender(ip, json.toString());
        Thread t = new Thread(command);
        t.start();



        return true;
    }
}

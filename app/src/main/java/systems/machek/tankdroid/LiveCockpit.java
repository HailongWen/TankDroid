    package systems.machek.tankdroid;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.webkit.WebView;
import android.webkit.WebViewFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

import systems.machek.tankdroid.widgets.MjpegFragment;

public class LiveCockpit extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private String[] urls;

    private TankController tankController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_cockpit);

        urls = getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE).getStringSet(Constants.CONFIG_MJPEG_URLS, new HashSet<String>()).toArray(new String[0]);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tankController = new TankController(getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE).getString(Constants.CONFIG_IP_ADDRESS, Constants.DEFAULT_IP_ADDRESS));


        try {
            Thread t = new Thread(tankController);
            t.start();
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (urls.length > 0) {
                String ip = getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE).getString(Constants.CONFIG_IP_ADDRESS, Constants.DEFAULT_IP_ADDRESS);
                return MjpegFragment.newInstance(urls[position], urls[position]);
            } else {
                return new BlankFragment();
            }

        }

        @Override
        public int getCount() {
            return urls.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return urls[position];
            // TODO
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent ev) {

        float throttleLeft = 0.0f, throttleRight = 0.0f, turret = 0.0f;

        String ip = getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE).getString(Constants.CONFIG_IP_ADDRESS, Constants.DEFAULT_IP_ADDRESS);

        float steering = ev.getAxisValue(MotionEvent.AXIS_Z);
        float throttle = ev.getAxisValue(MotionEvent.AXIS_Y);
        turret = ev.getAxisValue(MotionEvent.AXIS_HAT_X);

        throttleLeft = throttle - steering;
        throttleRight = throttle + steering;

        /*
        if (steering < 0.0f) {
            // drive left
            if (throttleLeft < 1.0f) {
                float delta = -throttleLeft - 1.0f;
                throttleLeft += delta;
                throttleRight += delta;

            } else if (throttleRight > 1.0f) {
                float delta = throttleRight - 1.0f;
                throttleRight -= delta;
                throttleLeft -= delta;
            }
        } else if (steering > 0.0f) {
            // drive right
            if (throttleLeft > 1.0f) {
                float delta = throttleLeft - 1.0f;
                throttleLeft -= delta;
                throttleRight -= delta;
            } else if (throttleRight < 1.0f) {
                float delta = -throttleRight - 1.0f;
                throttleRight += delta;
                throttleLeft += delta;
            }
        }
        */

        JSONObject json = new JSONObject();

        try {
            json.put("method", "move");
            JSONObject args = new JSONObject();
            args.put("move_l", throttleLeft);
            args.put("move_r", throttleRight);
            args.put("move_t", turret);
            json.put("params", args);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            String result = tankController.sendCommand(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }


        return true;
    }
}

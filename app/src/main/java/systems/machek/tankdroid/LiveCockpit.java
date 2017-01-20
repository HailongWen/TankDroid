package systems.machek.tankdroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

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
                case 0: return MjpegFragment.newInstance(Constants.FRONT_MJPEG_CAM_URL, Constants.FRONT_MJPEG_CAM_NAME.replace("xxipxx", ip));
                case 1: return MjpegFragment.newInstance(Constants.FRONT_MJPEG_CAM_URL, Constants.TURRET_MJPEG_CAM_NAME.replace("xxipxx", ip));
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
}

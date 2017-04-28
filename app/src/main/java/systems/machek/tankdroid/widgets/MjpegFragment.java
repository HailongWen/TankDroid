package systems.machek.tankdroid.widgets;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegView;

import java.net.URL;

import systems.machek.tankdroid.R;


public class MjpegFragment extends Fragment {

    private static final String URL_KEY = "url";
    private static final String CAM_KEY = "cam";


    private MjpegView mjpegView;
    private TextView desc;

    public MjpegFragment() { }

    public static MjpegFragment newInstance(String url, String name) {
        MjpegFragment fragment = new MjpegFragment();
        Bundle args = new Bundle();

        args.putString(URL_KEY, url);
        args.putString(CAM_KEY, name);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_mjpeg, container, false);

        mjpegView = (MjpegView)v.findViewById(R.id.mjpegView);
        desc = (TextView)v.findViewById(R.id.desc);

        desc.setText(getArguments().getString(CAM_KEY));

        Mjpeg.newInstance(Mjpeg.Type.DEFAULT)
                .open(getArguments().getString(URL_KEY))
                .subscribe(inputStream -> {
                    mjpegView.setSource(inputStream);
                    mjpegView.setDisplayMode(DisplayMode.BEST_FIT);
                    mjpegView.showFps(true);
                });

        return v;
    }

    @Override
    public void onPause() {
        // Avoid NPE when LiveCockpit is put to background or closed.
        mjpegView.stopPlayback();
        super.onPause();
    }
}

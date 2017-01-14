package systems.machek.tankdroid.widgets;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegView;

import systems.machek.tankdroid.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MjpegFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MjpegFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MjpegFragment extends Fragment {

    private String url;

    private MjpegView mjpegView;


    public MjpegFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MjpegFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MjpegFragment newInstance() {
        MjpegFragment fragment = new MjpegFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_mjpeg, container, false);
        mjpegView = (MjpegView)v.findViewById(R.id.mjpegView);
        return v;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;

        Mjpeg.newInstance(Mjpeg.Type.DEFAULT)
                .open(url)
                .subscribe(inputStream -> {
                    mjpegView.setSource(inputStream);
                    mjpegView.setDisplayMode(DisplayMode.BEST_FIT);
                    mjpegView.showFps(true);
                });
    }
}

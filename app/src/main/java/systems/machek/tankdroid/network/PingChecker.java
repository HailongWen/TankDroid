package systems.machek.tankdroid.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by bm on 21.01.2017.
 */

public class PingChecker implements Runnable {

    private String address;
    private boolean reachable = false;

    public PingChecker(String address) {
        this.address = address;
    }

    @Override
    public void run() {
        try {
            InetAddress ip = InetAddress.getByName(address);
            reachable = ip.isReachable(5000);
        } catch (UnknownHostException e) {
            reachable = false;
            e.printStackTrace();
        } catch (IOException ioe) {
            reachable = false;
            ioe.printStackTrace();
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isReachable() {
        return reachable;
    }

    public void setReachable(boolean reachable) {
        this.reachable = reachable;
    }
}

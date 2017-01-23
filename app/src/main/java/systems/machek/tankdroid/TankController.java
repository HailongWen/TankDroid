package systems.machek.tankdroid;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import systems.machek.tankdroid.Constants;

/**
 * Created by bm on 22.01.2017.
 */

public class TankController implements Runnable {

    private DataOutputStream tankCommandSender;
    private BufferedReader tankResultReceiver;
    private Socket tankSocket = null;

    private String ip;

    public TankController(String ip) {
        this.ip = ip;
    }

    @Override
    public void run() {

        try {
            tankSocket = new Socket(ip, Constants.JSON_TCP_PORT);
            tankCommandSender = new DataOutputStream(tankSocket.getOutputStream());
            tankResultReceiver = new BufferedReader(new InputStreamReader(tankSocket.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String sendCommand(String jsonCommand) {

        try {
            TankCommandTransceiver tcrcv = new TankCommandTransceiver(jsonCommand);
            Thread t = new Thread(tcrcv);
            t.start();
            t.join();

            String jsonResult = tcrcv.getResult();
            JSONObject responseObject = new JSONObject(jsonResult);
            return responseObject.getString("result");

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException jsone) {
            jsone.printStackTrace();
        }

        return "failed";
    }

    private class TankCommandTransceiver implements Runnable {
        private String command, result;

        public TankCommandTransceiver(String command) {
            this.command = command;
        }

        @Override
        public void run() {
            try {
                tankCommandSender.writeBytes(command);

                String line;
                StringBuffer responseStringBuffer = new StringBuffer();
                boolean dataIsComing = true;
                while (dataIsComing) {
                    line = tankResultReceiver.readLine();
                    if (line.equals("}")) {
                        dataIsComing = false;
                    }
                    responseStringBuffer.append(line);
                }

                result = responseStringBuffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String getResult() {
            return result;
        }
    }
}

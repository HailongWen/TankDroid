package systems.machek.tankdroid.systems.machek.tankdroid.network;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;

import systems.machek.tankdroid.Constants;

/**
 * Created by bm on 21.01.2017.
 */

public class JsonSender implements Runnable {

    private String ip, command;

    private String result, errorMessage;
    private boolean allWentFine = true;

    public JsonSender(String ip, String command) {
        this.ip = ip;
        this.command = command;
    }

    @Override
    public void run() {
        try {
            StringBuffer sb = new StringBuffer();

            Socket tankSocket = new Socket(ip, Constants.JSON_TCP_PORT);
            DataOutputStream outToTank = new DataOutputStream(tankSocket.getOutputStream());
            outToTank.writeBytes(command);

            BufferedReader inFromTank = new BufferedReader(new InputStreamReader(tankSocket.getInputStream()));

            String line;
            boolean dataIsComing = true;
            while (dataIsComing) {
                line = inFromTank.readLine();
                if (line.equals("}")) {
                    dataIsComing = false;
                }
                sb.append(line);
            }

            try {
                JSONObject responseObject = new JSONObject(sb.toString());
                result = responseObject.getString("result");

            } catch (JSONException jse) {
                errorMessage = jse.getMessage();
                allWentFine = false;
                jse.printStackTrace();
            }

            tankSocket.close();

        } catch (IOException e) {
            errorMessage = e.getMessage();
            allWentFine = false;
            e.printStackTrace();
        }

        allWentFine = true;
    }

    public String getResult() {
        return result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isAllWentFine() {
        return allWentFine;
    }

}

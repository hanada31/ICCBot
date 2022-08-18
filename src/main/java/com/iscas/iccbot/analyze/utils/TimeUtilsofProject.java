package com.iscas.iccbot.analyze.utils;

import com.iscas.iccbot.MyConfig;
import com.iscas.iccbot.client.BaseClient;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class TimeUtilsofProject {
    /**
     * Analyze will terminate when timeout.
     *
     * @param client
     */
    public static void setTotalTimer(BaseClient client) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(MyConfig.getInstance().getClient() + " time = "
                        + MyConfig.getInstance().getTimeLimit() + " minutes, timeout!");
                MyConfig.getInstance().setStopFlag(true);
                try {
                    client.clientOutput();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        }, MyConfig.getInstance().getTimeLimit() * 60 * 1000);

    }
}

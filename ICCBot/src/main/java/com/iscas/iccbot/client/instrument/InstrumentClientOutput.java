package com.iscas.iccbot.client.instrument;

import com.iscas.iccbot.Global;
import com.iscas.iccbot.analyze.utils.AndroidUtils;
import com.iscas.iccbot.analyze.utils.output.FileUtils;
import soot.Unit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * output analyze result
 *
 * @author 79940
 */
public class InstrumentClientOutput {

    /**
     * sign apk file
     */
    public static void signApk(String fileName) {
        if (new File(fileName).exists()) {
            AndroidUtils.signAPK(fileName);
        }
    }

    public static void writeInstrumentFile(String dir, String file) {
        FileUtils.createFolder(dir);
        FileUtils.createFile(dir + file);
        File f = new File(dir + file);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(f));
            for (Unit u : Global.v().getInstrumentList())
                writer.write(u + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

package com.iscas.iccbot.client.soot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iscas.iccbot.Main;
import com.iscas.iccbot.MyConfig;
import com.iscas.iccbot.SummaryLevel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author hanada
 * @Date 2022/3/11 15:27
 * @Version 1.0
 */
@Slf4j
public class IROutputClientTest {

    @Test
    public void testConfig() {
        setArgs();
        setMySwitch();
        Main.startAnalyze();
        System.out.println("Finish...\n");
        System.exit(0);
    }

    private void setArgs() {
        String path;
        path = "../apk";
        String name;
        name = "yokuu";
        name = "appinventor.ai_jorgeguimaraesnet.ccsantosferreira_8.0";
        name = "com.lifeplus.diveplus_20.0";
        name = "com.whatsapp.w4b_220572003.0";

        String client = "IROutputClient";

        MyConfig.getInstance().setAppName(name + ".apk");
        MyConfig.getInstance().setAppPath(path + File.separator);
        MyConfig.getInstance().setClient(client);
        MyConfig.getInstance().setMaxPathNumber(30);
        MyConfig.getInstance().setMaxFunctionExpandNumber(5); //10?
        MyConfig.getInstance().setMaxObjectSummarySize(100);
        MyConfig.getInstance().setResultWrapperFolder("../results/" + File.separator);
        MyConfig.getInstance().setResultFolder(MyConfig.getInstance().getResultWrapperFolder()+ "output" + File.separator);
        MyConfig.getInstance().setTimeLimit(10);
        MyConfig.getInstance().setAndroidJar("../lib/platforms");


        // Load Analyze Config
        String analyzeConfigPath = "../config/config.json";
        Path fPath = Paths.get(analyzeConfigPath);
        JSONObject analyzeConfig = null;
        try {
            analyzeConfig = JSON.parseObject(String.join("\n", Files.readAllLines(fPath)));
            MyConfig.getInstance().setAnalyzeConfig(analyzeConfig);
        } catch (IOException e) {
            log.error("Failed to load analyze config json: IOException", e);
            System.exit(0);
        }
    }

    /**
     * analyze parameters for evaluation
     */
    private static void setMySwitch() {
        MyConfig.getInstance().getMySwitch().setDummyMainSwitch(false);
        MyConfig.getInstance().getMySwitch().setCallBackSwitch(true);
        MyConfig.getInstance().getMySwitch().setFunctionExpandSwitch(true);

        MyConfig.getInstance().getMySwitch().setAsyncMethodSwitch(true);
        MyConfig.getInstance().getMySwitch().setPolymSwitch(true);

        MyConfig.getInstance().getMySwitch().setAdapterSwitch(true);
        MyConfig.getInstance().getMySwitch().setStringOpSwitch(true);
        MyConfig.getInstance().getMySwitch().setStaticFieldSwitch(true);

        MyConfig.getInstance().getMySwitch().setFragmentSwitch(true);
        MyConfig.getInstance().getMySwitch().setLibCodeSwitch(true);
        MyConfig.getInstance().getMySwitch().setWrapperAPISwitch(true);

        MyConfig.getInstance().getMySwitch().setImplicitLaunchSwitch(true);
        MyConfig.getInstance().getMySwitch().setDynamicBCSwitch(true);

        MyConfig.getInstance().getMySwitch().setSummaryStrategy(SummaryLevel.object);
        MyConfig.getInstance().getMySwitch().setVfgStrategy(true);
        MyConfig.getInstance().getMySwitch().setCgAnalyzeGroupedStrategy(false);
    }
}

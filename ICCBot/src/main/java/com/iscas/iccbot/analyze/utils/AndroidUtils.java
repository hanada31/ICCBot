package com.iscas.iccbot.analyze.utils;

import com.iscas.iccbot.MyConfig;

import java.io.*;
import java.util.Arrays;

public class AndroidUtils {
    /**
     * signAPK IO
     *
     * @author 79940
     */
    private static class ReadStream implements Runnable {
        String name;
        InputStream is;
        Thread thread;

        public ReadStream(String name, InputStream is) {
            this.name = name;
            this.is = is;
        }

        public void start() {
            thread = new Thread(this);
            thread.start();
        }

        @Override
        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                while (true) {
                    String s = br.readLine();
                    if (s == null)
                        break;
                    System.out.println("[" + name + "] " + s);
                }
                is.close();
            } catch (Exception ex) {
                System.out.println("Problem reading stream " + name + "... :" + ex);
                ex.printStackTrace();
            }
        }
    }

    /**
     * sign apk on apkPath
     *
     * @param apkPath APK Path
     */
    public static void signAPK(String apkPath) {
        Runtime r = Runtime.getRuntime();
        String keystoreUrl = "template" + File.separator + "debug.keystore";

        String[] signCmd = Arrays.asList(
                "jarsigner", "-verbose", "-sigalg", "SHA1withRSA", "-digestalg", "SHA1",
                "-storepass", "android", "-keystore", keystoreUrl, apkPath, "androiddebugkey"
            ).toArray(new String[0]);
        try {
            Process p = r.exec(signCmd);

            ReadStream s1 = new ReadStream("stdin", p.getInputStream());
            ReadStream s2 = new ReadStream("stderr", p.getErrorStream());
            s1.start();
            s2.start();

            p.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Finish Sign Apk.");
    }

    /**
     * create android project projectName with pkg name on path param_result_dir
     *
     * @param pkg Package name
     * @param param_result_dir Result directory
     * @param projectName Project name
     */
    public static void creatProject(String pkg, String param_result_dir, String projectName) {
        ExecuteUtils.exec("android create project --name " + projectName + " --target "
                + MyConfig.getInstance().getAndroidVersion() + " --path " + param_result_dir + " --package "
                + ConstantUtils.APKFLAGPREFIX + pkg + ConstantUtils.APKFLAGSUFFIX + " --activity "
                + ConstantUtils.ACTIVITY + "_");
        System.out.println("android create project --name " + projectName + " --target "
                + MyConfig.getInstance().getAndroidVersion() + " --path " + param_result_dir + " --package "
                + ConstantUtils.APKFLAGPREFIX + pkg + ConstantUtils.APKFLAGSUFFIX + " --activity "
                + ConstantUtils.ACTIVITY + "_");
    }

    /**
     * buildApp on path projectPath
     *
     * @param projectPath Project path
     */
    public static void buildApp(String projectPath) {
        System.out.println("ant debug -buildfile " + projectPath + "/build.xml");
        ExecuteUtils.exec("ant debug -buildfile " + projectPath + "/build.xml");
    }

}

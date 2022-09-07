package com.iscas.iccbot.client.soot;

import com.alibaba.fastjson.JSONArray;
import com.iscas.iccbot.Analyzer;
import com.iscas.iccbot.Global;
import com.iscas.iccbot.MyConfig;
import com.iscas.iccbot.analyze.utils.ConstantUtils;
import lombok.extern.slf4j.Slf4j;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.Transform;
import soot.options.Options;

import java.io.File;
import java.util.Collections;
import java.util.List;

@Slf4j
public class SootAnalyzer extends Analyzer {
    public SootAnalyzer() {
        super();
    }

    /**
     * analyze using soot 1) set setActiveBody 2) def-use analyze
     */
    @Override
    public void analyze() {
        if (appModel.getAppName() == null) return;
        long startMS = System.currentTimeMillis();

        sootInit();
        sootTransform();
        sootEnd();

        if (Global.v().getAppModel().getApplicationClassNames().size() == 0) {
            for (SootClass sc : Scene.v().getApplicationClasses()) {
                Global.v().getAppModel().addApplicationClassNames(sc.getName());
            }
        }
        log.info(String.format("SootAnalyzer finished in %.2f seconds", (System.currentTimeMillis() - startMS) / 1000.0));
        MyConfig.getInstance().setSootAnalyzeFinish(true);
    }

    /**
     * initialize soot
     */
    public static void sootInit() {
        soot.G.reset();
        Options.v().set_android_jars(MyConfig.getInstance().getAndroidJar());
        Options.v().set_process_dir(Collections.singletonList(Global.v().getAppModel().getAppPath()));
        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_process_multiple_dex(true);
        if (MyConfig.getInstance().isJimple())
            Options.v().set_output_format(Options.output_format_jimple);
        else
            Options.v().set_output_format(Options.output_format_shimple);
        String out = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName() + File.separator
                + ConstantUtils.SOOTOUTPUT;
        Options.v().set_output_dir(out);
        Options.v().set_src_prec(Options.src_prec_apk);
        Options.v().allow_phantom_refs();
        Options.v().set_whole_program(true);
        setExcludePackage();
    }

    /**
     * add transforms for analyzing
     */
    private void sootTransform() {
        String pack = "jtp";
        if (!MyConfig.getInstance().isJimple())
            pack = "stp";
        // set setActiveBody
        ActiveBodyTransformer abTran = new ActiveBodyTransformer();
        Transform t1 = new Transform(pack + ".bt", abTran);
        PackManager.v().getPack(pack).add(t1);
    }

    /**
     * end setting of soot
     */
    public static void sootEnd() {
        soot.Main.v().autoSetOptions();
        Scene.v().loadNecessaryClasses();
        Scene.v().loadBasicClasses();
        PackManager.v().runPacks();
    }

    /**
     * packages refuse to be analyzed
     */
    public static void setExcludePackage() {
        System.out.println(MyConfig.getInstance().getAnalyzeConfig());
        JSONArray excArr = MyConfig.getInstance().getAnalyzeConfig().getJSONArray("SootAnalyzer.excludePackages");
        if (excArr == null) return;
        List<String> excList = excArr.toJavaList(String.class);
        log.info("Loaded {} exclude packages from analyze config", excList.size());
        Options.v().set_exclude(excList);
    }
}

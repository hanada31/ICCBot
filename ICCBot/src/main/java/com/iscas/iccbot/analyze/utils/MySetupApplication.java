package com.iscas.iccbot.analyze.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParserException;
import soot.G;
import soot.SootClass;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.callbacks.AndroidCallbackDefinition;
import soot.jimple.infoflow.sourcesSinks.definitions.ISourceSinkDefinitionProvider;
import soot.util.MultiMap;

import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MySetupApplication extends SetupApplication {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Method[] parentMethods;
    private Class<?>[] parentClasses;

    public MySetupApplication(String androidJar, String apkFileLocation) {
        super(androidJar, apkFileLocation);
        getMethods();
    }

    public Method getParentPrivateMethod(String name) {
        return getParentPrivateMethod(name, 0);
    }

    protected Method getParentPrivateMethod(String name, int paramsCnt) {
        for (Method m : parentMethods) {
            if (m.getName().equals(name) && m.getParameterCount() == paramsCnt) {
                return m;
            }
        }
        return null;
    }

    public Object getParentPrivateClassInstance(String name) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        for (Class<?> c : parentClasses) {
            if (c.getSimpleName().equals(name)) {
                Constructor<?> constructor;
                try {
                    constructor = c.getDeclaredConstructor();
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
                constructor.setAccessible(true);
                return constructor.newInstance();
            }
        }
        return null;
    }

    public void getMethods() {
        Class<?> superClass = this.getClass().getSuperclass();
        parentMethods = superClass.getDeclaredMethods();
        AccessibleObject.setAccessible(parentMethods, true);
        parentClasses = superClass.getDeclaredClasses();
    }

    public void runInfoflow_dummy() throws InvocationTargetException, IllegalAccessException, InstantiationException {
        config.setCallgraphAlgorithm(InfoflowConfiguration.CallgraphAlgorithm.AutomaticSelection);
//        config.setCallgraphAlgorithm(InfoflowConfiguration.CallgraphAlgorithm.SPARK);
        config.setMergeDexFiles(true);
        config.setOneComponentAtATime(false);
        //modify the setting of CallbackAnalyzer if the flowdroid can not finish.
//        config.getCallbackConfig().setCallbackAnalyzer(InfoflowAndroidConfiguration.CallbackAnalyzer.Fast);
        config.getCallbackConfig().setCallbackAnalyzer(InfoflowAndroidConfiguration.CallbackAnalyzer.Default);
        config.getCallbackConfig().setCallbackAnalysisTimeout(120);

        runInfoflow_dummy(null);
    }

    public void runInfoflow_dummy(ISourceSinkDefinitionProvider sourcesAndSinks)
             throws InvocationTargetException, IllegalAccessException, InstantiationException {
        // Reset our object state
        this.collectedSources = null;
        this.collectedSinks = null;
        this.sourceSinkProvider = sourcesAndSinks;
        this.infoflow = null;

        // Start a new Soot instance
        if (config.getSootIntegrationMode() == InfoflowAndroidConfiguration.SootIntegrationMode.CreateNewInstance) {
            G.reset();
            getParentPrivateMethod("initializeSoot").invoke(this);
        }

        // Perform basic app parsing
        try {
            parseAppResources();
        } catch (IOException | XmlPullParserException e) {
            logger.error("Parse app resource failed", e);
            throw new RuntimeException("Parse app resource failed", e);
        }

        Object resultAggregator = getParentPrivateClassInstance("MultiRunResultAggregator");

        // We need at least one entry point
        if (entrypoints == null || entrypoints.isEmpty()) {
            logger.warn("No entry points");
            return;
        }
        processEntryPoint_dummy(sourcesAndSinks, resultAggregator);
    }

    /**
     * Runs the data flow analysis on the given entry point class
     *
     * @param sourcesAndSinks
     *            The sources and sinks on which to run the data flow analysis
     * @param resultAggregator
     *            An object for aggregating the results from the individual data
     *            flow runs
     */
    protected void processEntryPoint_dummy(
            ISourceSinkDefinitionProvider sourcesAndSinks,
            Object resultAggregator) throws InvocationTargetException, IllegalAccessException {
        // Get rid of leftovers from the last entry point
        Class<?> clazz = resultAggregator.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().equals("clearLastResults")) {
                m.setAccessible(true);
                m.invoke(resultAggregator);
            }
        }

        // Perform basic app parsing
        long callbackDuration = System.nanoTime();
        try {
            if (config.getOneComponentAtATime())
                getParentPrivateMethod("calculateCallbacks", 2).invoke(this, sourcesAndSinks, null);
            else
                getParentPrivateMethod("calculateCallbacks", 1).invoke(this, sourcesAndSinks);
        } catch (Exception e) {
            logger.error("Callgraph construction failed: " + e.getMessage(), e);
            throw new RuntimeException("Callgraph construction failed", e);
        }
        callbackDuration = System.nanoTime() - callbackDuration;
        logger.info(String.format("Collecting callbacks and building a callgraph took %.2f seconds",
                callbackDuration / 1e9));
    }

    public MultiMap<SootClass, AndroidCallbackDefinition> getCallbackMethods() {
        return callbackMethods;
    }

    public MultiMap<SootClass, SootClass> getFragmentClasses() {
        return fragmentClasses;
    }
}

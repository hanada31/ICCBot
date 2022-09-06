package com.iscas.iccbot;

import com.alibaba.fastjson.JSONObject;

/**
 * config information for current run
 *
 * @author 79940
 */
public class MyConfig {

    private MyConfig() {
    }

    private boolean isJimple = true;
    private boolean testGeneration = false;
    private boolean writeSootOutput = false;
    private JSONObject analyzeConfig;
    private String androidVersion;
    private String resultFolder;
    private String resultWrapperFolder;
    private String srcFolder;
    private String appName;
    private String appPath;
    private String client;
    private String gatorClient;
    private String callGraphAlgorithm;
    private int timeLimit;
    private int maxPathNumber;
    private int maxFunctionExpandNumber;
    private int maxObjectSummarySize;
    private String androidJar;
    private boolean stopFlag = false;

    private boolean isSootAnalyzeFinish;
    private boolean isManifestClientFinish;
    private boolean isFragmentClientFinish;
    private boolean isCallGraphClientFinish;
    private boolean isStaticValueAnalyzeFinish;
    private boolean isOracleConstructionClientFinish;
    private Switch mySwitch = new Switch();

    private static class SingletonInstance {
        private static final MyConfig INSTANCE = new MyConfig();
    }

    public static MyConfig getInstance() {
        return SingletonInstance.INSTANCE;
    }

    /**
     * @return the mySwitch
     */
    public Switch getMySwitch() {
        return mySwitch;
    }

    /**
     * @param mySwitch the mySwitch to set
     */
    public void setMySwitch(Switch mySwitch) {
        this.mySwitch = mySwitch;
    }

    public JSONObject getAnalyzeConfig() {
        return analyzeConfig;
    }

    public void setAnalyzeConfig(JSONObject analyzeConfig) {
        this.analyzeConfig = analyzeConfig;
    }

    public boolean isJimple() {
        return isJimple;
    }

    public void setJimple(boolean isJimple) {
        this.isJimple = isJimple;
    }

    public boolean isTestGeneration() {
        return testGeneration;
    }

    public void setTestGeneration(boolean testGeneration) {
        this.testGeneration = testGeneration;
    }

    public boolean isWriteSootOutput() {
        return writeSootOutput;
    }

    public void setWriteSootOutput(boolean writeSootOutput) {
        this.writeSootOutput = writeSootOutput;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }

    public String getResultFolder() {
        return resultFolder;
    }

    public void setResultFolder(String resultFolder) {
        this.resultFolder = resultFolder;
    }

    public String getSrcFolder() {
        return srcFolder;
    }

    public void setSrcFolder(String srcFolder) {
        this.srcFolder = srcFolder;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppPath() {
        return appPath;
    }

    public void setAppPath(String appPath) {
        this.appPath = appPath;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    /**
     * @return the androidJar
     */
    public String getAndroidJar() {
        return androidJar;
    }

    /**
     * @param androidJar the androidJar to set
     */
    public void setAndroidJar(String androidJar) {
        this.androidJar = androidJar;
    }

    /**
     * @return the maxPathNumber
     */
    public int getMaxPathNumber() {
        return maxPathNumber;
    }

    /**
     * @param maxPathNumber the maxPathNumber to set
     */
    public void setMaxPathNumber(int maxPathNumber) {
        this.maxPathNumber = maxPathNumber;
    }

    /**
     * @return the isManifestAnalyzeFinish
     */
    public boolean isManifestAnalyzeFinish() {
        return isManifestClientFinish;
    }

    /**
     * @param isManifestAnalyzeFinish the isManifestAnalyzeFinish to set
     */
    public void setManifestAnalyzeFinish(boolean isManifestAnalyzeFinish) {
        this.isManifestClientFinish = isManifestAnalyzeFinish;
    }

    /**
     * @return the isFragementAnalyzeFinish
     */
    public boolean isFragmentAnalyzeFinish() {
        return isFragmentClientFinish;
    }

    /**
     * @param isFragmentAnalyzeFinish the isFragmentAnalyzeFinish to set
     */
    public void setFragmentAnalyzeFinish(boolean isFragmentAnalyzeFinish) {
        this.isFragmentClientFinish = isFragmentAnalyzeFinish;
    }

    /**
     * @return the isCallGraphAnalyzeFinish
     */
    public boolean isCallGraphAnalyzeFinish() {
        return isCallGraphClientFinish;
    }

    /**
     * @param isCallGraphAnalyzeFinish the isCallGraphAnalyzeFinish to set
     */
    public void setCallGraphAnalyzeFinish(boolean isCallGraphAnalyzeFinish) {
        this.isCallGraphClientFinish = isCallGraphAnalyzeFinish;
    }

    /**
     * @return the isOracleConstructionClientFinish
     */
    public boolean isOracleConstructionClientFinish() {
        return isOracleConstructionClientFinish;
    }

    /**
     * @param isOracleConstructionClientFinish the isOracleConstructionClientFinish to set
     */
    public void setOracleConstructionClientFinish(boolean isOracleConstructionClientFinish) {
        this.isOracleConstructionClientFinish = isOracleConstructionClientFinish;
    }

    /**
     * @return the isStaticValueAnalyzeFinish
     */
    public boolean isStaticValueAnalyzeFinish() {
        return isStaticValueAnalyzeFinish;
    }

    /**
     * @param isStaticValueAnalyzeFinish the isStaticValueAnalyzeFinish to set
     */
    public void setStaticValueAnalyzeFinish(boolean isStaticValueAnalyzeFinish) {
        this.isStaticValueAnalyzeFinish = isStaticValueAnalyzeFinish;
    }

    /**
     * @return the isSootAnalyzeFinish
     */
    public boolean isSootAnalyzeFinish() {
        return isSootAnalyzeFinish;
    }

    /**
     * @param isSootAnalyzeFinish the isSootAnalyzeFinish to set
     */
    public void setSootAnalyzeFinish(boolean isSootAnalyzeFinish) {
        this.isSootAnalyzeFinish = isSootAnalyzeFinish;
    }

    /**
     * @return the stopFlag
     */
    public boolean isStopFlag() {
        return stopFlag;
    }

    /**
     * @param stopFlag the stopFlag to set
     */
    public void setStopFlag(boolean stopFlag) {
        this.stopFlag = stopFlag;
    }

    /**
     * @return the resultWrapperFolder
     */
    public String getResultWrapperFolder() {
        return resultWrapperFolder;
    }

    /**
     * @param resultWrapperFolder the resultWrapperFolder to set
     */
    public void setResultWrapperFolder(String resultWrapperFolder) {
        this.resultWrapperFolder = resultWrapperFolder;
    }

    /**
     * @return the gatorClient
     */
    public String getGatorClient() {
        return gatorClient;
    }

    /**
     * @param gatorClient the gatorClient to set
     */
    public void setGatorClient(String gatorClient) {
        this.gatorClient = gatorClient;
    }

    /**
     * @return the callGraphAlgorithm
     */
    public String getCallGraphAlgorithm() {
        return callGraphAlgorithm;
    }

    /**
     * @param callGraphAlgorithm the callGraphAlgorithm to set
     */
    public void setCallGraphAlgorithm(String callGraphAlgorithm) {
        this.callGraphAlgorithm = callGraphAlgorithm;
    }

    /**
     * @return the maxFunctionExpandNumber
     */
    public int getMaxFunctionExpandNumber() {
        return maxFunctionExpandNumber;
    }

    /**
     * @param maxFunctionExpandNumber the maxFunctionExpandNumber to set
     */
    public void setMaxFunctionExpandNumber(int maxFunctionExpandNumber) {
        this.maxFunctionExpandNumber = maxFunctionExpandNumber;
    }

    /**
     * @return the maxObjectSummarySize
     */
    public int getMaxObjectSummarySize() {
        return maxObjectSummarySize;
    }

    /**
     * @param maxObjectSummarySize the maxObjectSummarySize to set
     */
    public void setMaxObjectSummarySize(int maxObjectSummarySize) {
        this.maxObjectSummarySize = maxObjectSummarySize;
    }
}
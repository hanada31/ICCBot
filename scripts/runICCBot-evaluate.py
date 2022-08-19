import os
import sys
import shutil

def analyzeApk(apkPath, resPath, sdk, tag):

    logDir = resPath+"/logs"+tag
    outputDir = resPath+"/output"+tag
    if(not os.path.exists(logDir)): 
        os.makedirs(logDir) 
    if(not os.path.exists(outputDir)): 
        os.makedirs(outputDir) 
        
    if(os.path.exists(apkPath)): 
        apks = os.listdir(apkPath)
        for apk in apks:
            if apk[-4:] ==".apk":
                print("java -jar ICCBot.jar -path "+ apkPath +" -name "+apk+" -androidJar "+ sdk +"/platforms -time 30 -maxPathNumber 100 -client ToolEvaluateClient -outputDir "+outputDir+" >> "+logDir+"/"+apk[:-4]+".txt")
                os.system("java -jar ICCBot.jar  -path "+ apkPath +" -name "+apk+" -androidJar "+ sdk +"/platforms -time 60 -maxPathNumber 100 -client ToolEvaluateClient -outputDir "+outputDir+" >> "+logDir+"/"+apk[:-4]+".txt")


if __name__ == '__main__' :
    apkPath = sys.argv[1]
    resPath = sys.argv[2]
    tag = ""
    
    jarFile = "ICCBot.jar"

    if not os.path.exists(jarFile):
        print("ICCBot.jar not found! Please run \"scripts/mvn.py\" to build ICCBot first!")

    sdk = "lib/"    
    analyzeApk(apkPath, resPath, sdk,tag)

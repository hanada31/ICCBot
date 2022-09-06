import os
import sys
import shutil

def analyzeApk(apkPath, resPath, sdk):
    id=0
    logDir = resPath+"/logs"
    outputDir = resPath+"/output"
    if(not os.path.exists(logDir)): 
        os.makedirs(logDir) 
    if(not os.path.exists(outputDir)): 
        os.makedirs(outputDir) 
        
    if(os.path.exists(apkPath)): 
        apks = os.listdir(apkPath)
        extraArgs = "" #"-noLibCode "# 
        for apk in apks:
            if apk[-4:] ==".apk":
                id+=1
                print ("\n\n\nThis is the "+str(id)+"th app " +apk)
                resFile = logDir+"/"+apk[:-4]+".txt"
                if(not os.path.exists(resFile)):
                    cmd = "java -jar "+jarFile+" -path "+ apkPath +" -name "+apk+" -androidJar "+ sdk +"/platforms  "+ extraArgs +" -time 60 -maxPathNumber 100 -client ICCSpecClient -outputDir "+outputDir+" >> "+logDir+"/"+apk[:-4]+".txt"
                    os.system(cmd)

if __name__ == '__main__' :
    apkPath = sys.argv[1]
    resPath = sys.argv[2]
    jarFile = "ICCBot.jar"

    if not os.path.exists(jarFile):
        print("ICCBot.jar not found! Please run \"scripts/mvn.py\" to build ICCBot first!")

    sdk = "lib/"    
    analyzeApk(apkPath, resPath, sdk)

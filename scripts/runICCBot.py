import os
import sys
import shutil

jarFile = "ICCBot.jar"
logDir = "../results/logs"
outputDir = "../results/output"
def analyzeApk(path, sdk):
    if(os.path.exists(path)): 
        apks = os.listdir(path)
        for apk in apks:
            if apk[-4:] =="_ins.apk":
                continue
            if apk[-4:] ==".apk":
                print("java -jar "+jarFile+"  -path "+ path +" -name "+apk+" -androidJar "+ sdk +"/platforms -time 30 -maxPathNumber 100 -client MainClient    -outputDir "+outputDir+" >> "+logDir+"/"+apk+".txt")
                os.system("java -jar "+jarFile+"  -path "+ path +" -name "+apk+" -androidJar "+ sdk +"/platforms -time 60 -maxPathNumber 100 -client MainClient -outputDir "+outputDir+" >> "+logDir+"/"+apk+".txt")


if __name__ == '__main__' :
    path = "apk2/"
    sdk = "lib/"    
    #os.system("mvn -f pom.xml package ")
    #shutil.copy("target/ICCBot.jar", jarFile)
    if(not os.path.exists(logDir)): 
        os.makedirs(logDir) 
    analyzeApk(path, sdk)
    

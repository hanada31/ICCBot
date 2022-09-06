import os
import sys
import shutil

if __name__ == '__main__' :
    print("Checking update for soot-dev submodule...")
    os.system("git submodule update --init soot-dev")
    print("Building ICCBot, please wait...")
    os.system("mvn -f pom.xml clean package -q -DskipTests")
    if os.path.exists("target/ICCBot-jar-with-dependencies.jar"):
        print("Successfully built! generate jar-with-dependencies in folder target/")
        shutil.copy("target/ICCBot-jar-with-dependencies.jar", "ICCBot.jar")
        print("copy jar to the root directory.")
    else:
        print("Fail to build! Please run \"mvn -f pom.xml package\" " + 
              "or \"mvn -f pom.xml package -DskipTests\" (skip Soot tests) " + 
              "to see the detail info.")

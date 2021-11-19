import os
import sys
import shutil

if __name__ == '__main__' :
    suffix = ""
    if len(sys.argv) == 2:
        suffix = "-"+sys.argv[1]
    os.system("mvn -f pom.xml package -q")
    if os.path.exists("target/ICCBot.jar"):
        print("Successfully build! generate jar-with-dependencies in folder target/")
        shutil.copy("target/ICCBot.jar", "ICCBot"+suffix+".jar")
        print("copy jar to the root directory.")
    else:
        print("Fail to build! Please run \"mvn -f pom.xml package\" to see the detail info.")
import os
import sys
import shutil

if __name__ == '__main__' :
    suffix = ""
    if len(sys.argv) == 2:
        suffix = "-"+sys.argv[1]
    os.system("mvn -f pom.xml clean package")
    shutil.copy("target/target/ICCBot-1.0-SNAPSHOT-jar-with-dependencies", "ICCBot"+suffix+".jar")

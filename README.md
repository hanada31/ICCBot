# ICCBot
An Key Characteristic-directed and Efficient-friendly ICC Resolution tool.

build *ICCBot*：

```
mvn -f pom.xml clean package
cp target/ICCBot-1.0-SNAPSHOT.one-jar.jar ../ICCBot.jar
```

test *ICCBot*:

```
python .\scripts\runICCBot.py
```



run *ICCBot*:

```
java –jar ICCBot.jar –h

usage: java -jar ICCBot.jar [options] [-path] [-name] [-androidJar] [-outputDir][-client]
 
 -h                     -h: Show the help information.
 -name <arg>            -name: Set the name of the apk under analysis.
 -path <arg>            -path: Set the path to the apk under analysis.
 -outputDir <arg>       -outputDir: Set the output folder of the apk.
 -client <arg>          -client [default:ICTGClient]: Set the analyze client.
                        [IROutputClient: Output soot IR files.
                        ManifestClient: Output manifest.xml file.
                        CallGraphClient: Output call graph files.
                       	MainClient: Resolve ICC Links.]
                        
 -androidJar <arg>      -androidJar: Set the path of android.jar.                
 -version <arg>         -version [default:23]: Version of Android SDK.
 -maxPathNumber <arg>   -maxPathNumber [default:10000]: Set the max number of paths.
 -time <arg>            -time [default:90]: Set the max running time (min).

 -noAdapter             -noAdapter: exclude super simple adapter model
 -noAsyncMethod         -noAsyncMethod: exclude async method call edge
 -noCallBackEntry       -noCallBackEntry: exclude the call back methods
 -noDynamicBC           -noDynamicBC: exclude dynamic broadcast receiver matching
 -noFragment            -noFragment: exclude fragment operation model
 -noFunctionExpand      -noFunctionExpand: do not inline function with useful contexts
 -noImplicit            -noImplicit: exclude implict matching
 -noLibCode             -noLibCode: exclude the activities not declared in app's package
 -noPolym               -noPolym: exclude polymorphism methods
 -noStaticField         -noStaticField: exclude string operation model
 -noStringOp            -noStringOp: exclude string operation model
 -noWrapperAPI          -noWrapperAPI: exclude RAICC model
 -onlyDummyMain         -onlyDummyMain: limit the entry scope

 -debug                 -debug: use debug mode.
```



example:

```
java -jar ICCBot.jar -path apk/ -name IntentFlowBench.apk -outputDir results  -androidJar lib/platforms
```



Input：apk File

Output：ICC resolution results, CTG graph, etc.

Output Files: 

+ CallGraphInfo
+ DummyMainInfo
+ ManifestInfo
+ fragmentInfo
  + fragment loading graph
+ CTGResult
  + component transition graph





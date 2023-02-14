# ICCBot

ICCBot: A Fragment-Aware and Context-Sensitive ICC Resolution Tool for Android Applications.

<p align="center">
<img src="overview/ICCBot.png" width="100%">
</p>

## Publication ##
The paper PDF can be found at https://hanada31.github.io/pdf/icse22_iccbot.pdf
```latex
@inproceedings{icse22_iccbot,
  author    = {Jiwei Yan and
               Shixin Zhang and
	       Yepang Liu and
               Jun Yan and
               Jian Zhang},
  title     = {ICCBot: Fragment-Aware and Context-Sensitive ICC Resolution for {Android} Applications},
  booktitle = {The 44th International Conference on Software Engineering, {ICSE} 2022 (Demo Track)},
  year      = {2022},
}
```

## Build and run ICCBot

### General Requirements

1. Python 3+

2. Java 1.8+

3. Install GraphViz (http://www.graphviz.org/download/) 

### Build ICCBot

```bash
# Initialize soot-dev submodule
git submodule update --init soot-dev

# Use -DskipTests to skip tests of soot (make build faster)
mvn -f pom.xml clean package -DskipTests

# Copy jar to root directory
cp target/ICCBot-jar-with-dependencies.jar ICCBot.jar
```

or use the build script provide by us:

```shell
python scripts/mvn.py
```

### Run ICCBot

```bash
java -jar ICCBot.jar  -path apk/ -name ICCBotBench.apk -androidJar lib/platforms -time 30 -maxPathNumber 100 -client CTGClient -outputDir results/output
```

or analyze apks under given folder with Python script:

```bash
python scripts/runICCBot.py [apkPath] [resultPath]
```

### Usage of ICCBot

```
java -jar ICCBot.jar -h

usage: java -jar ICCBot.jar [options] [-path] [-name] [-androidJar] [-outputDir][-client]
 
 -h                     -h: Show the help information.
 -name <arg>            -name: Set the name of the apk under analysis.
 -path <arg>            -path: Set the path to the apk under analysis.
 -outputDir <arg>       -outputDir: Set the output folder of the apk.
 -client <arg>          -client 
                         "CallGraphClient: Output call graph files."
                         "ManifestClient: Output manifest.xml file."
                         "IROutputClient: Output soot IR files."
                         "FragmentClient: Output the fragment loading results."
                         "CTGClient/MainClient: Resolve ICC and generate CTG."
                         "ICCSpecClient:  Report ICC specification for each component."
                        
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

+ Input: apk file

+ Output: ICC resolution results, CTG graph, etc.
  + CallGraphInfo
    + Generated extended call graph
      + [appName]_cg.txt: the original call graph edges.
      + cg.txt: the formatted call graph edges.
  + ManifestInfo
    + Extracted AndroidManifest file
      + AndroidManifest.txt
  + FragmentInfo
    + Generated fragment loading graph
      + [appName]_fragLoad.dot: the fragment loading graph in dot format
      + [appName]_fragLoad.pdf: the fragment loading graph
  + CTGResult
    + Generated component transition graph
      + [appName]_CTG.dot: the component transition graph in dot format
      + [appName]_CTG.pdf: the component transition graph
      + [appName]_CTGwithFragment.dot: the component transition and fragment loading graph in dot format
      + [appName]_CTGwithFragmentG.pdf: the component transition and fragment loading graph
      + componentInfo.xml: the information for each component, including, how the component is declared in the manifest file (<manifest>), the sent ICC information from current component (<sender>), and the received ICC information by current component (<receiver>).
      + CTG.xml: give the destination list for each source component.
      + ICCBotResult.xml: for each component, give a list of intent summaries. Each intentSummary corresponds to an intent object. This file gives its source, destination and other ICC data (<sendICCInfo>), the method that the ICC is sent out, the ICC-sending-related method call trace (<methodtrace>) and each ICC-sending-related unit (<nodes>).
  + ICCSpecification
    + Generated ICC specification JSON file

      + ComponentModel.json: Give a list of components. For each one, it lists the className, the type of the component (a for Activity, s for Service, p for ContentProvider, and r for BroadcastReceiver), and the fullValueSet. The fullValueSet gives all the ICC data we obtained by static analysis, for each ICC data (e.g., action, category, and other extra data items), we give the declared data value in the manifest file (<manifest>), the ICC-related values that will be created and sent to the current component by other components (<sendIntent>), and the data values that will be used in comparison with the received ICC message form outside (<recvIntent>).
      + ComponentModel_typeValue.json: the values for each type that can be used for further test generation.
      + objectSummary_entry.xml: for each component, give a list of intent summaries. Each intentSummary corresponds to an intent object. This file gives its source, destination, and other ICC data (<sendICCInfo>), the method that the ICC is sent out, the ICC-sending-related method call trace (<methodtrace>), and each ICC-sending-related unit (<nodes>).
      + pathSummary_entry.xml: for each component, give a list of path summaries. Each pathSummary corresponds to a path and may involve more than one intent object.  Just using the file objectSummary_entry.xml is fine.
  + SootIRInfo
    + Generated Soot Jimple IR files

### Troubleshooting

Q: ICCBot provides error "`Failed to load analyze config json: File not exist`"

A: Make sure you have the folder `config` under current directory,
with `config/config.json` file specifying `excludePackages` and `AndroidCallbacks`.




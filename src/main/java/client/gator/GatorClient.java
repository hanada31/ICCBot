package main.java.client.gator;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.dom4j.DocumentException;

import brut.common.BrutException;
import brut.util.OS;
import main.java.Global;
import main.java.MyConfig;
import main.java.analyze.utils.ConstantUtils;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.BaseClient;

public class GatorClient  extends BaseClient {
	String gatorTmpFolder;
	PrintStream out = null;
	@Override
	protected void clientAnalyze() {
		gatorTmpFolder = "gator_tmp";
		//invoke apktool
		invokeApkTool();
		invokeGator();
		
	}

	private void invokeApkTool() {
		String[] args= {"decode","-f","--no-src", "print2stdout", "--output", gatorTmpFolder, Global.v().getAppModel().getAppPath()};
		try {
			brut.apktool.Main.main(args);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrutException e) {
			e.printStackTrace();
		}
	}
	

	private void invokeGator() {
		String clearCmd[] = {"rm", "/tmp/"+Global.v().getAppModel().getAppName()+"-*.xml"};
		try {
			OS.exec(clearCmd);
		} catch (BrutException e) {
			System.out.println("no file to be deleted");
		}
		try{
			String[] args = {"-sootandroidDir", "lib",
			           "-sdkDir", "lib",
			           "-listenerSpecFile", "data/listeners.xml",
			           "-wtgSpecFile", "data/wtg.xml",
			           "-resourcePath", gatorTmpFolder+"/res",
			           "-manifestFile", gatorTmpFolder+"/AndroidManifest.xml",
			           "-project"
			           , Global.v().getAppModel().getAppPath(),
			           "-apiLevel", "android-1",
			           "-guiAnalysis",
			           "-benchmarkName", Global.v().getAppModel().getAppName(),
			           "-libraryPackageListFile", "data/libPackages.txt",
			           "-android", "lib/platforms/android-1/android.jar",
			           "-client", MyConfig.getInstance().getGatorClient()};
			presto.android.Main.main(args);
		}catch(RuntimeException e){
			System.err.println("gator fails");
		}
		out = System.out;
		FileUtils.delAllFile(gatorTmpFolder);
		//on linux
	}


	@Override
	public void clientOutput() throws IOException, DocumentException {
		String client =MyConfig.getInstance().getGatorClient();
		switch (client) {
		case "GUIHierarchyPrinterClient":
			String summary_app_dir = MyConfig.getInstance().getResultFolder() + Global.v().getAppModel().getAppName()
					+ File.separator;
			String gatorDir = summary_app_dir + ConstantUtils.GATORFOLDER+client+File.separator;
			FileUtils.createFolder(gatorDir);
			String copyCmd[] = {"cp", "/tmp/"+Global.v().getAppModel().getAppName()+"-*.xml", gatorDir+Global.v().getAppModel().getAppName()+"-"+client+".xml"};
			try {
				OS.exec(copyCmd);
			} catch (BrutException e) {
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
		
	}

}

package main.java.client.gator;

import java.io.IOException;

import org.dom4j.DocumentException;

import brut.common.BrutException;
import main.java.Global;
import main.java.analyze.utils.output.FileUtils;
import main.java.client.BaseClient;

public class GatorClient  extends BaseClient {
	String gatorTmpFolder;
	@Override
	protected void clientAnalyze() {
		gatorTmpFolder = "gator_tmp";
		//invoke apktool
		invokeApkTool();
		invokeGator();
		
	}

	private void invokeApkTool() {
		String[] args= {"decode","-f","--no-src", "--output", gatorTmpFolder, Global.v().getAppModel().getAppPath()};
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
		String[] args = {"-sootandroidDir", "lib",
		           "-sdkDir", "lib",
		           "-listenerSpecFile", "data/listeners.xml",
		           "-wtgSpecFile", "data/wtg.xml",
		           "-resourcePath", gatorTmpFolder+"/res",
		           "-manifestFile", gatorTmpFolder+"/AndroidManifest.xml",
		           "-project", Global.v().getAppModel().getAppPath(),
		           "-apiLevel", "android-1",
		           "-guiAnalysis",
		           "-benchmarkName", Global.v().getAppModel().getAppName(),
		           "-libraryPackageListFile", "data/libPackages.txt",
		           "-android", "lib/platforms/android-1/android.jar",
					"-client", "GUIHierarchyPrinterClient"};
		presto.android.Main.main(args);
		FileUtils.delAllFile(gatorTmpFolder);
		//on linux
	}


	@Override
	public void clientOutput() throws IOException, DocumentException {
		// TODO Auto-generated method stub
		
	}

}

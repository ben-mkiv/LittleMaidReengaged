package net.blacklab.lmr.util;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import net.blacklab.lmr.LittleMaidReengaged;
import net.minecraftforge.fml.relauncher.FMLInjectionData;

public class FileList {

    public static class CommonClassLoaderWrapper extends URLClassLoader{

        public CommonClassLoaderWrapper(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        @Override
        public void addURL(URL url) {
            if (new ArrayList(Arrays.asList(getURLs())).contains(url)) return;
            super.addURL(url);
        }

    }

    public static File dirMinecraft;
    public static File dirMods;
    public static File dirModsVersion;
    public static File dirDevClasses;
    public static File dirDevClassAssets;
    public static List<File> dirDevIncludeClasses = new ArrayList<File>();
//	public static File[] dirDevIncludeAssets = new File[]{};

    public static List<File> files;
    public static String dirMinecraftPath	= "";
    //	public static File   minecraftJar	= null;	// minecraft.jarを見に行くのは昔の仕様？
    public static String assetsDir		= "";	// mods/LittleMaidX/assets
    public static boolean isDevdir;
    public static Map<String,List<File>>    fileList = new HashMap<String, List<File>>();

    public static CommonClassLoaderWrapper COMMON_CLASS_LOADER;

    private static File devPathClasses(){
        String dirProjectPath = FileClassUtil.getParentDir(dirMinecraftPath);
        File path;

        switch(DevMode.DEVMODE){
            case DEVMODE_ECLIPSE:
                path = new File(dirProjectPath.concat("/bin"));
                if(path.exists() && path.isDirectory())
                    return path;

                break;

            default:
                path = new File(dirProjectPath + "/build/classes/main");
                if(path.exists() && path.isDirectory())
                    return path;

                path = new File(dirProjectPath + "/build/classes");
                if(path.exists() && path.isDirectory())
                    return path;

                break;
        }

        throw new IllegalStateException("Could not get dev class path.");
    }

    public static void initClassLoader(){
        List<URL> urls = new ArrayList<URL>();
        try {
            urls.add(FileList.dirMods.toURI().toURL());
        } catch (MalformedURLException e1) {
            LittleMaidReengaged.Debug("malformed URL");
        }
        if(DevMode.DEVMODE==DevMode.DEVMODE_ECLIPSE){
            for(File f:FileList.dirDevIncludeClasses){
                try {
                    urls.add(f.toURI().toURL());
                } catch (MalformedURLException e) {
                    LittleMaidReengaged.Debug("malformed URL");
                }
            }
        }

        FileList.COMMON_CLASS_LOADER = new FileList.CommonClassLoaderWrapper(urls.toArray(new URL[]{}), LittleMaidReengaged.class.getClassLoader());
    }

    private static File devPathAssets(){
        String dirProjectPath = FileClassUtil.getParentDir(dirMinecraftPath);

        if(new File(dirProjectPath + "/build/resources/main").exists())
            return new File(dirProjectPath + "/build/resources/main");

        throw new IllegalStateException("Could not get dev assets path.");
    }

    private static HashSet<File> devClassesEclipse(){
        String dirProjectPath = FileClassUtil.getParentDir(dirMinecraftPath);
        HashSet<File> list = new HashSet<>();

        for(String includeProject : DevMode.INCLUDEPROJECT)
            list.add(new File(FileClassUtil.getParentDir(dirProjectPath)+"/"+includeProject+"/bin"));

        return list;
    }

    static {
        Object[] injectionData = FMLInjectionData.data();
        dirMinecraft = (File) FMLInjectionData.data()[6];
        dirMinecraftPath = FileClassUtil.getLinuxAntiDotName(dirMinecraft.getAbsolutePath());
        if (dirMinecraftPath.endsWith("/")) {
            dirMinecraftPath = dirMinecraftPath.substring(0, dirMinecraftPath.lastIndexOf("/"));
        }
        dirMods = new File(dirMinecraft, "mods");

        if(!DevMode.DEVMODE.equals(DevMode.NOT_IN_DEV)){
            dirDevClasses = devPathClasses();
            dirDevClassAssets = devPathAssets();

            if (DevMode.DEVMODE.equals(DevMode.DEVMODE_ECLIPSE))
                dirDevIncludeClasses.addAll(devClassesEclipse());

        }
        dirModsVersion = new File(dirMods, (String)injectionData[4]);
        LittleMaidReengaged.Debug("init FileManager.");
    }

    // TODO 今後使用しなさそう
	/*
	public static void setSrcPath(File file)
	{
		assetsDir = file.getPath() + "/assets";
		LittleMaidReengaged.Debug("mods path =" + dirMods.getAbsolutePath());

		// eclipseの環境の場合、eclipseフォルダ配下のmodsを見に行く
		isDevdir = file.getName().equalsIgnoreCase("bin");
		if(isDevdir)
		{
			dirMods = new File(file.getParent()+"/eclipse/mods");
		}
		else
		{
			dirMods = new File(file.getParent());
		}
	}
	*/

    /**
     * modsディレクトリに含まれるファイルを全て返す。<br>
     * バージョンごとの物も含む。
     * @return
     */
	/*
	public static List<File> getAllmodsFiles() {
		List<File> llist = new ArrayList<File>();
		if (dirMods.exists()) {
			for (File lf : dirMods.listFiles()) {
				llist.add(lf);
			}
		}
		if (dirModsVersion.exists()) {
			for (File lf : dirModsVersion.listFiles()) {
				llist.add(lf);
			}
		}
		files = llist;
		return llist;
	}
	public static List<File> getAllmodsFiles(ClassLoader pClassLoader) {
		List<File> llist = new ArrayList<File>();
		if (pClassLoader instanceof URLClassLoader ) {
			for (URL lurl : ((URLClassLoader)pClassLoader).getURLs()) {
				try {
					String ls = lurl.toString();
					if (ls.endsWith("/bin/") || ls.indexOf("/mods/") > -1) {
						llist.add(new File(lurl.toURI()));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		files = llist;
		return llist;
	}
	*/
    public static List<File> getAllmodsFiles(ClassLoader pClassLoader, boolean pFlag) {
        List<File> llist = new ArrayList<File>();
        if (pClassLoader instanceof URLClassLoader ) {
            for (URL lurl : ((URLClassLoader)pClassLoader).getURLs()) {
                try {
                    String ls = lurl.toString();
                    if (ls.endsWith("/bin/") || ls.indexOf("/mods/") > -1) {
                        llist.add(new File(lurl.toURI()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (pFlag) {
            if (dirMods.exists()) {
                for (File lf : dirMods.listFiles()) {
                    addList(llist, lf);
                }
            }
            if (dirModsVersion.exists()) {
                for (File lf : dirModsVersion.listFiles()) {
                    addList(llist, lf);
                }
            }
        }

        files = llist;
        return llist;
    }

    protected static boolean addList(List<File> pList, File pFile) {
        for (File lf : pList) {
            try {
                if (pFile.getCanonicalPath().compareTo(lf.getCanonicalPath()) == 0) {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        pList.add(pFile);
        return true;
    }

    public static List<File> getModFile(String pname, String pprefix) {
        List<File> llist;
        if (fileList.containsKey(pname)) {
            llist = fileList.get(pname);
        } else {
            llist = new ArrayList<File>();
            fileList.put(pname, llist);
        }

        LittleMaidReengaged.Debug("getModFile:[%s]:%s", pname, dirMods.getAbsolutePath());
        if(DevMode.DEVMODE != DevMode.NOT_IN_DEV){
            llist.add(dirDevClasses);

            switch(DevMode.DEVMODE){
                case DEVMODE_ECLIPSE:
                    llist.addAll(dirDevIncludeClasses);
                    break;

                    case DEVMODE_NO_IDE:
                    llist.add(dirDevClassAssets);
                    break;
            }
        }
        try {
            if (dirMods.isDirectory()) {
                LittleMaidReengaged.Debug("getModFile-get:%d.", dirMods.list().length);
                for (File t : dirMods.listFiles()) {
                    if (t.getName().indexOf(pprefix) != -1) {
                        if (t.getName().endsWith(".zip") || t.getName().endsWith(".jar")) {
                            llist.add(t);
                            LittleMaidReengaged.Debug("getModFile-file:%s", t.getName());
                        } else if (t.isDirectory()) {
                            llist.add(t);
                            LittleMaidReengaged.Debug("getModFile-file:%s", t.getName());
                        }
                    }
                }
                LittleMaidReengaged.Debug("getModFile-files:%d", llist.size());
            } else {
                LittleMaidReengaged.Debug("getModFile-fail.");
            }
        }
        catch (Exception exception) {
            LittleMaidReengaged.Debug("getModFile-Exception.");
        }

        return llist;
    }

    public static void debugPrintAllFileList() {
        for(String key : fileList.keySet()) {
            List<File> list = fileList.get(key);
            for(File f : list) {
                System.out.println("MMMLib-AllFileList ### " + key + " : " + f.getPath());
            }
        }
    }

    public static List<File> getFileList(String pname) {
        return fileList.get(pname);
    }
}
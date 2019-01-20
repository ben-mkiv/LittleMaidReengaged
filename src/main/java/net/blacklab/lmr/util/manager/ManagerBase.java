package net.blacklab.lmr.util.manager;


import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.blacklab.lmr.LittleMaidReengaged;
import net.blacklab.lmr.util.DevMode;
import net.blacklab.lmr.util.FileClassUtil;
import net.blacklab.lmr.util.FileList;
import net.minecraftforge.fml.relauncher.FMLInjectionData;

public abstract class ManagerBase {

    protected abstract String getPreFix();
    /**
     * 追加処理の本体
     */
    protected abstract boolean append(Class pclass);


    protected void load() {

        if(!DevMode.DEVMODE.equals(DevMode.NOT_IN_DEV)){
            startSearchDev(FileList.dirDevClasses);

            for(File f:FileList.dirDevIncludeClasses)
                startSearchDev(f);
        }

        startSearch(FileList.dirMods);
    }

    private void startSearchDev(File root){
        if (root.isDirectory()) {
            LittleMaidReengaged.Debug("startSearchDev path: '"+root.toString()+"'");
            decodeDirectory(root, root);
        }
        else if(root.isFile()) {
            LittleMaidReengaged.Debug("startSearchDev file: '" + root.toString() + "'");
            decodeZip(root);
        }
        else
            LittleMaidReengaged.Debug("couldnt load mode from '"+root.toString()+"'");
    }

    private void startSearch(File root){
        // mods]
        String mcv = (String) FMLInjectionData.data()[4];
        LittleMaidReengaged.Debug("MC %s", mcv);
        LittleMaidReengaged.Debug("START SEARCH MODS FOLDER");
        decodeDirectory(root, root);
        for (File lf : root.listFiles()) {
            if (lf.isFile() && (lf.getName().endsWith(".zip") || lf.getName().endsWith(".jar"))) {
                decodeZip(lf);
            } else if (lf.isDirectory()) {
                // ディレクトリの解析
                String md = FileClassUtil.getLinuxAntiDotName(lf.getAbsolutePath());
                if (md.endsWith("/")) {
                    md = md.substring(0, md.length()-1);
                }

                LittleMaidReengaged.Debug("DIR SEARCH %s", md);
                String mf = FileClassUtil.getFileName(md);
                LittleMaidReengaged.Debug(" SPLICE %s", mf);
                if (mf.equals(mcv)) {
                    LittleMaidReengaged.Debug("DEBUG START SEARCH DIVIDED FOLDER");
                    startSearch(lf);
                }
            }
        }
    }

    private void decodeDirectory(File pfile, File pRoot) {
        try {
            FileList.COMMON_CLASS_LOADER.addURL(pRoot.toURI().toURL());
        } catch (MalformedURLException e) {
            LittleMaidReengaged.Debug("malformed path? " + pfile.toString() + ", " + pRoot.toString());
            return;
        }
        // ディレクトリ内のクラスを検索
        for (File lf : pfile.listFiles()) {
            if (lf.isDirectory()) {
                decodeDirectory(lf, pRoot);
                continue;
            }

            if(lf.isFile()){
                String lname = lf.getName();
                if (lname.contains(getPreFix()) && lname.endsWith(".class")) {
                    loadClass(FileClassUtil.getClassName(
                            FileClassUtil.getLinuxAntiDotName(lf.getAbsolutePath()),
                            FileClassUtil.getLinuxAntiDotName(pRoot.getAbsolutePath())));
                }
            }
        }
    }

    private void decodeZip(File pfile) {
        // zipファイルを解析
        try {
            // 多分いらんと思う…
            FileList.COMMON_CLASS_LOADER.addURL(pfile.toURI().toURL());
        } catch (MalformedURLException e) {
            return;
        }
        try {
            FileInputStream fileinputstream = new FileInputStream(pfile);
            ZipInputStream zipinputstream = new ZipInputStream(fileinputstream);
            ZipEntry zipentry;

            do {
                zipentry = zipinputstream.getNextEntry();
                if(zipentry == null) {
                    break;
                }
                if (!zipentry.isDirectory()) {
                    String lname = zipentry.getName();
                    if (lname.indexOf(getPreFix()) >= 0 && lname.endsWith(".class")) {
                        loadClass(zipentry.getName());
                    }
                }
            } while(true);

            zipinputstream.close();
            fileinputstream.close();
        }
        catch (Exception exception) {
            LittleMaidReengaged.Debug("add%sZip-Exception.", getPreFix());
        }

    }

    private void loadClass(String pname) {
        String lclassname = "";
        try {
            Package lpackage = LittleMaidReengaged.class.getPackage();
            lclassname = pname.endsWith(".class") ? pname.substring(0, pname.lastIndexOf(".class")) : pname;
            Class lclass;
            if(lpackage != null) {
                //lclassname = lpackage.getName() + "." + lclassname;
                lclassname = lclassname.replace("/", ".").replace("java.main.", "");
// LMM_EntityModeManager でしか使ってないので暫定
                lclass = FileList.COMMON_CLASS_LOADER.loadClass(lclassname);
            } else {
                lclass = Class.forName(lclassname);
            }
            if (Modifier.isAbstract(lclass.getModifiers())) {
                return;
            }
            if (append(lclass)) {
                LittleMaidReengaged.Debug("get%sClass-done: %s", getPreFix(), lclassname);
            } else {
                LittleMaidReengaged.Debug("get%sClass-fail: %s", getPreFix(), lclassname);
            }
			/*
            if (!(MMM_ModelStabilizerBase.class).isAssignableFrom(lclass) || Modifier.isAbstract(lclass.getModifiers())) {
            	LittleMaidReengaged.Debug(String.format(String.format("get%sClass-fail: %s", pprefix, lclassname)));
                return;
            }

            MMM_ModelStabilizerBase lms = (MMM_ModelStabilizerBase)lclass.newInstance();
            pmap.put(lms.getName(), lms);
            LittleMaidReengaged.Debug(String.format("get%sClass-done: %s[%s]", pprefix, lclassname, lms.getName()));
            */
        }
        catch (Exception exception) {
            LittleMaidReengaged.Debug("get%sClass-Exception.(%s)", getPreFix(), lclassname);
            if(DevMode.DEVELOPMENT_DEBUG_MODE && LittleMaidReengaged.cfg_PrintDebugMessage) exception.printStackTrace();
        }
        catch (Error error) {
            LittleMaidReengaged.Debug("get%sClass-Error: %s", getPreFix(), lclassname);
            if(DevMode.DEVELOPMENT_DEBUG_MODE && LittleMaidReengaged.cfg_PrintDebugMessage) error.printStackTrace();
        }

    }


}
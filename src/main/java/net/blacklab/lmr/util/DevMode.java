package net.blacklab.lmr.util;

public enum DevMode {
	NOT_IN_DEV,
	DEVMODE_NO_IDE,
	DEVMODE_ECLIPSE;

	/* note for intellij idea dev mode users:
	*  1st run the gradle build task to deploy all the classes/assets to the build directory
	* */

	public static DevMode DEVMODE = DevMode.DEVMODE_NO_IDE;

	public static final boolean DEVELOPMENT_DEBUG_MODE = false;

	public static final String[] INCLUDEPROJECT = new String[]{};
}

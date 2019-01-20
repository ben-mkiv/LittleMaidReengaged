package net.blacklab.lmr.util;

public enum DevMode {
	NOT_IN_DEV,
	DEVMODE_NO_IDE,
	DEVMODE_ECLIPSE;

	public static DevMode DEVMODE = DevMode.DEVMODE_NO_IDE;

	public static final boolean DEVELOPMENT_DEBUG_MODE = true;

	public static final String[] INCLUDEPROJECT = new String[]{};
}

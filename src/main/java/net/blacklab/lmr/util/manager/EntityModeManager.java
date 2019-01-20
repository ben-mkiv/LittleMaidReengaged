package net.blacklab.lmr.util.manager;

import java.util.ArrayList;
import java.util.List;

import net.blacklab.lmr.LittleMaidReengaged;
import net.blacklab.lmr.entity.EntityLittleMaid;
import net.blacklab.lmr.entity.mode.EntityModeBase;
import net.blacklab.lmr.util.FileList;
import org.apache.logging.log4j.Level;

public class EntityModeManager extends ManagerBase {

	public static final String prefix = "EntityMode";
	public static List<EntityModeBase> maidModeList = new ArrayList<>();


	public static void init() {
		FileList.getModFile("EntityMode", prefix);
	}

	public static void loadEntityMode() {
		new EntityModeManager().load();
	}

	@Override
	protected String getPreFix() {
		return prefix;
	}

	@Override
	protected boolean append(Class pclass) {
		if (!EntityModeBase.class.isAssignableFrom(pclass)) {
			return false;
		}

		try {
			EntityModeBase lemb;
			lemb = (EntityModeBase)pclass.getConstructor(EntityLittleMaid.class).newInstance((EntityLittleMaid)null);
			lemb.init();

			if(maidModeList.contains(lemb))
				return false;

			if (maidModeList.isEmpty() || lemb.priority() >= maidModeList.get(maidModeList.size() - 1).priority()) {
				maidModeList.add(lemb);
			} else {
				for (int li = 0; li < maidModeList.size(); li++) {
					if (lemb.priority() < maidModeList.get(li).priority()) {
						maidModeList.add(li, lemb);
						break;
					}
				}
			}

			return true;
		} catch (Exception e) {
			LittleMaidReengaged.Debug("couldnt append mode: " + pclass.getName());
			return false;
		}
	}

	/**
	 * AI追加用のリストを獲得。
	 */
	public static List<EntityModeBase> getModeList(EntityLittleMaid pentity) {
		List<EntityModeBase> llist = new ArrayList<EntityModeBase>();
		for (EntityModeBase lmode : maidModeList) {
			try {
				llist.add(lmode.getClass().getConstructor(EntityLittleMaid.class).newInstance(pentity));
			} catch (Exception e) {
				LittleMaidReengaged.Debug("getModeList failed for Entity " + pentity.getName());
			}
		}

		return llist;
	}

	/**
	 * ロードされているモードリストを表示する。
	 */
	public static void showLoadedModes() {
		LittleMaidReengaged.Debug(Level.INFO, "Loaded Entity Modes (%d)", maidModeList.size());
		for (EntityModeBase lem : maidModeList) {
			LittleMaidReengaged.Debug(Level.INFO, "%04d : %s", lem.priority(), lem.getClass().getSimpleName());
		}
	}

}
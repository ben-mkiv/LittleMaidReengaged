package net.blacklab.lmr.entity.mode;

import net.blacklab.lmr.LittleMaidReengaged;
import net.blacklab.lmr.entity.EntityLittleMaid;
import net.blacklab.lmr.item.ItemSpawnEgg;
import net.blacklab.lmr.util.DevMode;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class EntityMode_Debug extends EntityMode_Basic {

	public static final int mmode_Debug = 0x00f0;

	public EntityMode_Debug(EntityLittleMaid pEntity) {
		super(pEntity);
		isAnytimeUpdate = true;
	}

	@Override
	public int priority() {
		return 0;
	}

	@Override
	public void init() {
	}

	@Override
	public void addEntityMode(EntityAITasks pDefaultMove, EntityAITasks pDefaultTargeting) {
		EntityAITasks[] ltasks = new EntityAITasks[2];
		ltasks[0] = pDefaultMove;
		ltasks[1] = pDefaultTargeting;

		if(DevMode.DEVELOPMENT_DEBUG_MODE) owner.addMaidMode(ltasks, "Debug", mmode_Debug);
	}

	@Override
	public boolean changeMode(EntityPlayer pentityplayer) {
		if(!DevMode.DEVELOPMENT_DEBUG_MODE) return false;
		ItemStack litemstackl0 = owner.maidInventory.getStackInSlot(17);
		ItemStack litemstackl1 = owner.maidInventory.getStackInSlot(16);
		if (!litemstackl0.isEmpty() && !litemstackl1.isEmpty()) {
			if (ItemStack.areItemStackTagsEqual(litemstackl0, ItemSpawnEgg.DEFAULT_STACK) && litemstackl1.getItem() == Item.getItemFromBlock(Blocks.BARRIER)) {
				owner.setMaidMode("Debug");
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean setMode(int pMode) {
		if(!DevMode.DEVELOPMENT_DEBUG_MODE) return false;
		switch (pMode) {
		case mmode_Debug :
			owner.setBloodsuck(false);
			owner.aiAttack.setEnable(false);
			owner.aiShooting.setEnable(false);
			return true;
		}

		return false;
	}

	@Override
	public void updateAITick(int pMode) {
		super.updateAITick(pMode);
		if (pMode == mmode_Debug) {
			owner.addMaidExperience(10f);
			/*
			try {
				int limit = (Integer) ObfuscationReflectionHelper.getPrivateValue(LMM_EntityLittleMaid.class, owner, "maidContractLimit");
				limit-=5000;
				if (limit<0) {
					limit = 0;
				}
				ObfuscationReflectionHelper.setPrivateValue(LMM_EntityLittleMaid.class, owner, limit, "maidContractLimit");
			} catch (SecurityException e) {
			} catch (IllegalArgumentException e) {
			}
			*/
		}
	}

}

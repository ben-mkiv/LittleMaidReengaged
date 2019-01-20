package net.blacklab.lmr.util;

import java.util.HashMap;
import java.util.Map;

import net.blacklab.lmr.entity.EntityLittleMaid;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;

public class EntityCaps extends EntityCapsLiving {

	private EntityLittleMaid owner;
	private static Map<String, Integer> caps;

	static {
		caps = new HashMap<String, Integer>();
		caps.putAll(getStaticModelCaps());
		caps.put("isBloodsuck", caps_isBloodsuck);
		caps.put("isFreedom", caps_isFreedom);
		caps.put("isTracer", caps_isTracer);
		caps.put("isPlaying", caps_isPlaying);
		caps.put("isLookSuger", caps_isLookSuger);
		caps.put("isBlocking", caps_isBlocking);
		caps.put("isWait", caps_isWait);
		caps.put("isWaitEX", caps_isWaitEX);
		caps.put("isOpenInv", caps_isOpenInv);
		caps.put("isWorking", caps_isWorking);
		caps.put("isWorkingDelay", caps_isWorkingDelay);
		caps.put("isContract", caps_isContract);
		caps.put("isContractEX", caps_isContractEX);
		caps.put("isRemainsC", caps_isRemainsC);
		caps.put("isClock", caps_isClock);
		caps.put("isMasked", caps_isMasked);
		caps.put("isCamouflage", caps_isCamouflage);
		caps.put("isPlanter", caps_isPlanter);
		caps.put("isOverdrive", caps_isOverdrive);
		caps.put("isOverdriveDelay", caps_isOverdriveDelay);
		caps.put("entityIdFactor", caps_entityIdFactor);
		caps.put("height", caps_height);
		caps.put("width", caps_width);
		caps.put("YOffset", caps_YOffset);
		caps.put("mountedYOffset", caps_mountedYOffset);
		caps.put("dominantArm", caps_dominantArm);
		caps.put("HeadMount", caps_HeadMount);
		caps.put("stabiliser", caps_stabiliser);
		caps.put("Items", caps_Items);
		caps.put("Actions", caps_Actions);
		caps.put("Grounds", caps_Grounds);
		caps.put("Ground", caps_Ground);
		caps.put("Inventory", caps_Inventory);
		caps.put("interestedAngle", caps_interestedAngle);
		caps.put("currentArmor", caps_currentArmor);
		caps.put("currentEquippedItem", caps_currentEquippedItem);
	}

	public EntityCaps(EntityLittleMaid pOwner) {
		super(pOwner);
		owner = pOwner;
	}

	@Override
	public Map<String, Integer> getModelCaps() {
		return caps;
	}

	@Override
	public Object getCapsValue(int pIndex, Object ...pArg) {
		int li = 0;

		switch (pIndex) {
		case caps_isBloodsuck:
			return owner.isBloodsuck();
		case caps_isFreedom:
			return owner.isFreedom();
		case caps_isTracer:
			return owner.isTracer();
		case caps_isPlaying:
			return owner.isPlaying();
		case caps_isLookSuger:
			return owner.isLookSuger();
		case caps_isBlocking:
			return owner.isBlocking();
		case caps_isWait:
			return owner.isMaidWait();
		case caps_isWaitEX:
			return owner.isMaidWaitEx();
		case caps_isOpenInv:
			return owner.isOpenInventory();
		case caps_isWorking:
			return owner.isWorking();
		case caps_isWorkingDelay:
			return owner.isWorkingDelay();
		case caps_isContract:
			return owner.isContract();
		case caps_isContractEX:
			return owner.isContractEX();
		case caps_isRemainsC:
			return owner.isRemainsContract();
		case caps_isClock:
			return owner.isClockMaid();
		case caps_isMasked:
			return owner.isMaskedMaid();
		case caps_isCamouflage:
			return owner.isCamouflage();
		case caps_isPlanter:
			return owner.isPlanter();
		case caps_isOverdrive:
			return owner.getMaidOverDriveTime().isEnable();
		case caps_isOverdriveDelay:
			return owner.getMaidOverDriveTime().isDelay();
		case caps_height:
			return owner.textureData.textureBox[0] == null ? null : owner.textureData.textureBox[0].getHeight(this);
		case caps_width:
			return owner.textureData.textureBox[0] == null ? null : owner.textureData.textureBox[0].getWidth(this);
		case caps_YOffset:
			return owner.textureData.textureBox[0] == null ? null : owner.textureData.textureBox[0].getYOffset(this);
		case caps_mountedYOffset:
			return owner.textureData.textureBox[0] == null ? null : owner.textureData.textureBox[0].getMountedYOffset(this);
		case caps_dominantArm:
			return owner.getDominantArm();
		case caps_HeadMount:
			return owner.maidInventory.armorInventory.get(3);
		case caps_stabiliser:
			return owner.maidStabilizer;
		case caps_Items:
			ItemStack[] lstacks = new ItemStack[owner.mstatSwingStatus.length];
			for (SwingStatus ls : owner.mstatSwingStatus) {
				lstacks[li++] = ls.getItemStack(owner);
			}
			return lstacks;
		case caps_Actions:
			EnumAction[] lactions = new EnumAction[owner.mstatSwingStatus.length];
			for (SwingStatus ls : owner.mstatSwingStatus) {
				lactions[li++] = ls.isUsingItem() ? ls.getItemStack(owner).getItemUseAction() : null;
			}
			return lactions;
		case caps_Grounds:
			float[] lgrounds = new float[owner.mstatSwingStatus.length];
			for (SwingStatus ls : owner.mstatSwingStatus) {
				lgrounds[li++] = ls.onGround;
			}
			return lgrounds;
		case caps_Ground:
			if (owner.mstatSwingStatus.length < (Integer)pArg[0]) {
				return pArg[1];
			}
			return owner.mstatSwingStatus[(Integer)pArg[0]].onGround;
		case caps_Inventory:
			return owner.maidInventory;
		case caps_interestedAngle:
			return owner.getInterestedAngle((Float)pArg[0]);
		case caps_PartsVisible:
			return owner.textureData.selectValue;
		case caps_textureData:
			return owner.textureData;
		}

		return super.getCapsValue(pIndex, pArg);
	}

	@Override
	public boolean setCapsValue(int pIndex, Object... pArg) {
		switch (pIndex) {
		case caps_PartsVisible:
			owner.textureData.selectValue = (Integer)pArg[0];
		}
		return super.setCapsValue(pIndex, pArg);
	}

}

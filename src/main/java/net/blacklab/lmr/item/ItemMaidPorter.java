package net.blacklab.lmr.item;

import java.util.List;
import javax.annotation.Nullable;

import net.blacklab.lmr.LittleMaidReengaged;
import net.blacklab.lmr.entity.EntityLittleMaid;
import net.blacklab.lmr.entity.experience.ExperienceUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemMaidPorter extends Item {
	public final static String NAME = "maidporter";
	public static ItemMaidPorter DEFAULT_ITEM;

	public ItemMaidPorter() {
		setMaxStackSize(1);
		setUnlocalizedName(NAME);
		setRegistryName(NAME);
	}

	@Override
	public boolean isDamageable() {
		return true;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		NBTTagCompound stackTag = stack.getTagCompound();
		if (stackTag != null) {
			String customName = stackTag.getString(LittleMaidReengaged.MODID + ":MAID_NAME");
			float experience = stackTag.getFloat(LittleMaidReengaged.MODID + ":EXPERIENCE");

			if (!customName.isEmpty()) {
				tooltip.add("Name: ".concat(customName));
			}
			tooltip.add(String.format("Level: %3d", ExperienceUtil.getLevelFromExp(experience)));
		}
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) {
			return EnumActionResult.PASS;
		}
		ItemStack stack = playerIn.getHeldItem(hand);
		NBTTagCompound tagCompound = stack.getTagCompound();
		if (tagCompound != null) {
			if (worldIn.isAirBlock(pos.add(0, 1, 0)) && worldIn.isAirBlock(pos.add(0, 2, 0))) {
				String customName = tagCompound.getString(LittleMaidReengaged.MODID + ":MAID_NAME");
				float experience = tagCompound.getFloat(LittleMaidReengaged.MODID + ":EXPERIENCE");

				EntityLittleMaid lMaid = new EntityLittleMaid(worldIn) {
					@Deprecated
					public EntityLittleMaid addMaidExperienceWithoutEvent(float value) {
						maidExperience += value;
						return this;
					}
				}.addMaidExperienceWithoutEvent(experience);
				lMaid.setLocationAndAngles(pos.getX(), pos.getY()+1, pos.getZ(), 0, 0);
				worldIn.spawnEntity(lMaid);
				lMaid.processInteract(playerIn, EnumHand.MAIN_HAND);

				if (!customName.isEmpty()) {
					lMaid.setCustomNameTag(customName);
				}
				lMaid.maidInventory.clear();
				lMaid.maidInventory.readFromNBT(tagCompound.getTagList(LittleMaidReengaged.MODID + ":MAID_INVENTORY", 10));

				lMaid.setTextureNameMain(tagCompound.getString(LittleMaidReengaged.MODID + ":MAIN_MODEL_NAME"));
				lMaid.setTextureNameArmor(tagCompound.getString(LittleMaidReengaged.MODID + ":ARMOR_MODEL_NAME"));
				lMaid.setColor(tagCompound.getInteger(LittleMaidReengaged.MODID + ":MAID_COLOR"));
			} else {
				return EnumActionResult.PASS;
			}
		}
		playerIn.setItemStackToSlot(hand==EnumHand.OFF_HAND ? EntityEquipmentSlot.OFFHAND : EntityEquipmentSlot.MAINHAND, null);
		return EnumActionResult.SUCCESS;
	}
}

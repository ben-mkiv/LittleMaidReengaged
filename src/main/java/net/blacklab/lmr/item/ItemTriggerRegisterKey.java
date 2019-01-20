package net.blacklab.lmr.item;

import java.util.List;

import javax.annotation.Nullable;

import net.blacklab.lmr.LittleMaidReengaged;
import net.blacklab.lmr.util.TriggerSelect;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;

import static net.blacklab.lmr.LittleMaidReengaged.MODID;

public class ItemTriggerRegisterKey extends Item {
	public final static String NAME = "registerkey";
	public static ItemTriggerRegisterKey DEFAULT_ITEM;

	public static final String RK_MODE_TAG = MODID + ":RK_MODE";
	public static final String RK_COUNT = MODID + ":RK_COUNT";

	public static final int RK_MAX_COUNT = 32;

	public ItemTriggerRegisterKey() {
		setUnlocalizedName(NAME);
		setRegistryName(NAME);
		setCreativeTab(LittleMaidReengaged.creativeTab);
	}

	public static void registerRecipe(RegistryEvent.Register<IRecipe> event){
		NonNullList<Ingredient> ingredients = new NonNullList<Ingredient>(){};
		ingredients.add(Ingredient.fromItem(Items.EGG));
		ingredients.add(Ingredient.fromItem(Items.SUGAR));
		ingredients.add(Ingredient.fromItem(Items.NETHER_WART));
		event.getRegistry().register(new ShapelessRecipes(MODID, new ItemStack(ItemTriggerRegisterKey.DEFAULT_ITEM, 1), ingredients).setRegistryName(ItemTriggerRegisterKey.DEFAULT_ITEM.getUnlocalizedName()));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn,
			EntityPlayer playerIn, EnumHand pHand) {
		ItemStack itemStackIn = playerIn.getHeldItem(pHand);
		NBTTagCompound tagCompound = itemStackIn.getTagCompound();
		if(tagCompound==null) {
			tagCompound = new NBTTagCompound();
			itemStackIn.setTagCompound(tagCompound);
		}

		int index = 0;
		String modeString = tagCompound.getString(RK_MODE_TAG);

		// 登録モードを切り替える．
		index = TriggerSelect.selector.indexOf(modeString) + 1;
		if(index >= TriggerSelect.selector.size()) index = 0;

		modeString = index < TriggerSelect.selector.size() ? TriggerSelect.selector.get(index) : "no selectors available";
		tagCompound.setString(RK_MODE_TAG, modeString);

		if(!worldIn.isRemote)
			playerIn.sendMessage(new TextComponentTranslation("littleMaidMob.chat.text.changeregistermode", modeString));

		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		NBTTagCompound tagCompound = stack.getTagCompound();
		if(tagCompound != null) {
			tooltip.add("Mode: "+tagCompound.getString(RK_MODE_TAG));
			tooltip.add("Remains: " + (RK_MAX_COUNT-tagCompound.getInteger(RK_COUNT)));
		}
	}

	@Override
	public boolean isDamageable() {
		return true;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if(!stack.hasTagCompound()){
			NBTTagCompound t = new NBTTagCompound();
			if(!TriggerSelect.selector.isEmpty())
				t.setString(RK_MODE_TAG, TriggerSelect.selector.get(0));
			stack.setTagCompound(t);
		}
	}

}

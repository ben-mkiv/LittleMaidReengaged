package net.blacklab.lmr.achievements;

import net.blacklab.lmr.LittleMaidReengaged;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public class AchievementsLMRE {
	public static void grantAdvancement(EntityPlayer player, String advancementName) {
		if (!(player instanceof EntityPlayerMP))
			return;
		
		AdvancementManager manager = player.world.getMinecraftServer().getAdvancementManager();
		Advancement advancement = manager.getAdvancement(new ResourceLocation(LittleMaidReengaged.MODID, advancementName));
		if (advancement == null)
			return;
		
		((EntityPlayerMP)player).getAdvancements().grantCriterion(advancement, "done");
	}
}

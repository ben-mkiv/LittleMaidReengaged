package net.blacklab.lmr.network;

import net.blacklab.lmr.LittleMaidReengaged;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.FMLCommonHandler;
public class ProxyCommon
{
	public void init(){

	}
	public int OFFSET_COUNT = 0;
	public void onItemPickup(EntityPlayer lmm_EntityLittleMaidAvatar,Entity entity, int i) {}
	public void onCriticalHit(EntityPlayer pAvatar, Entity par1Entity) {}
	public void onEnchantmentCritical(EntityPlayer pAvatar, Entity par1Entity) {}
	public EntityPlayer getClientPlayer(){ return null; }
	public void loadSounds(){}
	public void runCountThread() {}
	public void onClientCustomPayLoad(LMRMessage lmrMessage) {};
	
	public boolean isSinglePlayer()
	{
		return FMLCommonHandler.instance().getMinecraftServerInstance().isSinglePlayer();
	}
	public void playLittleMaidSound(World world, double posX, double posY,
			double posZ, String s, float soundVolume, float lpitch, boolean b) {
		
	}
	public Object getClientGuiElement(int iD, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	public void printEntitySpawnBiomeRegistrationInfo(String name, Biome biome){
		LittleMaidReengaged.Debug("Registering "+name+" to spawn in " + biome.getRegistryName());
	}
}

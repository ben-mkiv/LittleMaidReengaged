package net.blacklab.lmr.network;

import java.util.Arrays;

import net.blacklab.lmr.LittleMaidReengaged;
import net.blacklab.lmr.client.gui.GuiIFF;
import net.blacklab.lmr.client.gui.inventory.GuiMaidInventory;
import net.blacklab.lmr.client.resource.OldZipTexturesWrapper;
import net.blacklab.lmr.client.resource.SoundResourcePack;
import net.blacklab.lmr.client.sound.SoundLoader;
import net.blacklab.lmr.entity.EntityLittleMaid;
import net.blacklab.lmr.util.EnumSound;
import net.blacklab.lmr.util.IFF;
import net.blacklab.lmr.util.helper.CommonHelper;
import net.blacklab.lmr.util.helper.NetworkHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleItemPickup;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

/**
 * クライアント専用処理。
 * マルチ用に分離。
 * 分離しとかないとNoSuchMethodで落ちる。
 */
public class ProxyClient extends ProxyCommon {

	@Override
	public void init(){
		loadSounds();

		SimpleReloadableResourceManager resourceManager = ((SimpleReloadableResourceManager) Minecraft.getMinecraft().getResourceManager());
		resourceManager.reloadResourcePack(new SoundResourcePack());
		resourceManager.reloadResourcePack(new OldZipTexturesWrapper());

		Minecraft.getMinecraft().getSoundHandler().onResourceManagerReload(resourceManager);
	}



	/* 呼び出し箇所なし
	public GuiContainer getContainerGUI(EntityClientPlayerMP var1, int var2,
			int var3, int var4, int var5) {
		Entity lentity = var1.world.getEntityByID(var3);
		if (lentity instanceof LMM_EntityLittleMaid) {
			LMM_GuiInventory lgui = new LMM_GuiInventory(var1, (LMM_EntityLittleMaid)lentity);
//			var1.openContainer = lgui.inventorySlots;
			return lgui;
		} else {
			return null;
		}
	}
	*/

// Avatarr

	public void onItemPickup(EntityPlayer pAvatar, Entity entity, int i) {
		// アイテム回収のエフェクト
		// TODO:こっちを使うか？
//		mc.effectRenderer.addEffect(new EntityPickupFX(mc.theWorld, entity, avatar, -0.5F));
		CommonHelper.mc.effectRenderer.addEffect(new ParticleItemPickup(CommonHelper.mc.world, entity, pAvatar, 0.1F));
	}

	// TODO いらん？
	public void onCriticalHit(EntityPlayer pAvatar, Entity par1Entity) {
		//1.8後回し
//		MMM_Helper.mc.effectRenderer.addEffect(new EntityCrit2FX(MMM_Helper.mc.theWorld, par1Entity));
	}

	public void onEnchantmentCritical(EntityPlayer pAvatar, Entity par1Entity) {
		//1.8後回し
//		EntityCrit2FX entitycrit2fx = new EntityCrit2FX(MMM_Helper.mc.theWorld, par1Entity, "magicCrit");
//		MMM_Helper.mc.effectRenderer.addEffect(entitycrit2fx);
	}


	public EntityPlayer getClientPlayer()
	{
		return Minecraft.getMinecraft().player;
	}

	/* 呼び出し箇所なし
	public static void setAchievement() {
// MinecraftクラスからstatFileWriterが消えてる
//		MMM_Helper.mc.statFileWriter.readStat(mod_LMM_littleMaidMob.ac_Contract, 1);
	}
	*/

	public void loadSounds()
	{
		// 音声の解析
//		LMM_SoundManager.instance.init();
		// サウンドパック
//		LMM_SoundManager.instance.loadDefaultSoundPack();
		SoundLoader.load();
	}

	public boolean isSinglePlayer()
	{
		return Minecraft.getMinecraft().isSingleplayer();
	}

	@Override
	public void runCountThread(){
	}

	@Override
	public void playLittleMaidSound(World par1World, double x, double y, double z, String s, float v, float p, boolean b) {
/*
		if(!par1World.isRemote) return;
//		if(LMM_LittleMaidMobNX.proxy.OFFSET_COUNT==0){
//			LMM_LittleMaidMobNX.proxy.OFFSET_COUNT=2;
			if(soundCount<=1){
				soundCount++;
				try{
					par1World.playSound(x, y, z, s, v, p, b);
				}catch(Exception exception){
					Minecraft.getMinecraft().getSoundHandler().update();
				}
			}
//		}
*/
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		Object o = null;
		switch(ID) {
			case GuiHandler.GUI_ID_INVVENTORY:
				if(GuiHandler.maidClient!=null)
				{
					o = new GuiMaidInventory(player, GuiHandler.maidClient);
					GuiHandler.maidClient = null;
				}
				break;

			case GuiHandler.GUI_ID_IFF:
				o = new GuiIFF(world, player, GuiHandler.maidClient);
				break;
		}
		return o;
	}

	@Override
	public void onClientCustomPayLoad(LMRMessage pPayload) {
		EnumPacketMode lmode = EnumPacketMode.getEnumPacketMode(pPayload.data[0]);
		if (lmode == null) return;
		LittleMaidReengaged.Debug("MODE: %s", lmode.toString());
		EntityLittleMaid lemaid = null;
		if (lmode.withEntity) {
			lemaid = LMRNetwork.getLittleMaid(pPayload.data, 1, Minecraft.getMinecraft().world);
			if (lemaid == null) return;
			LMRNetwork.syncPayLoad(lmode, lemaid, Arrays.copyOfRange(pPayload.data, 5, pPayload.data.length));
		}
		clientPayLoad(lmode, lemaid, Arrays.copyOfRange(pPayload.data, lmode.withEntity?5:1, pPayload.data.length));
	}

	private static void clientPayLoad(EnumPacketMode pMode, EntityLittleMaid lemaid, byte[] contents) {
		switch (pMode) {
		case CLIENT_SWINGARM :
			// 腕振り
			byte larm = contents[0];
			EnumSound lsound = EnumSound.getEnumSound(NetworkHelper.getIntFromPacket(contents, 1));
			lemaid.setSwinging(larm, lsound, NetworkHelper.getIntFromPacket(contents, 5)==1);
//			mod_LMM_littleMaidMob.Debug(String.format("SwingSound:%s", lsound.name()));
			break;

		case CLIENT_RESPOND_IFF :
			// IFFの設定値を受信
			int lval = contents[0];
			int lindex = NetworkHelper.getIntFromPacket(contents, 1);
			String lname = (String)IFF.DefaultIFF.keySet().toArray()[lindex];
			LittleMaidReengaged.Debug("setIFF-CL %s(%d)=%d", lname, lindex, lval);
			IFF.setIFFValue(null, lname, lval);
			break;

		case CLIENT_PLAY_SOUND :
			// 音声再生
			EnumSound lsound9 = EnumSound.getEnumSound(NetworkHelper.getIntFromPacket(contents, 0));
			LittleMaidReengaged.Debug(String.format("playSound:%s", lsound9.name()));
			lemaid.playSound(lsound9, contents[4]==1);
			break;
		case CLIENT_ONDEATH :
			lemaid.manualOnDeath();
			break;
		default:
			break;
		}
	}

	public int soundCount = 0;

	public static class CountThread extends Thread{

		public boolean isRunning = true;

		@Override
		public void run() {
			while(isRunning){
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}
				if(((ProxyClient)LittleMaidReengaged.proxy).soundCount>0){
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
					}
					((ProxyClient)LittleMaidReengaged.proxy).soundCount--;
				}
			}
		}

		public void cancel(){
			isRunning = false;
		}
	}

	public void printEntitySpawnBiomeRegistrationInfo(String name, Biome biome){
		LittleMaidReengaged.Debug("Registering "+ name +" to spawn in " + biome.getBiomeName());
	}
}

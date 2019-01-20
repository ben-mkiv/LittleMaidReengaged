package net.blacklab.lmr;

import java.util.*;

import net.blacklab.lmr.client.entity.EntityLittleMaidForTexSelect;
import net.blacklab.lmr.config.Config;
import net.blacklab.lmr.entity.EntityLittleMaid;
import net.blacklab.lmr.entity.EntityLittleMaidAvatarMP;
import net.blacklab.lmr.entity.renderfactory.RenderFactoryLittleMaid;
import net.blacklab.lmr.entity.renderfactory.RenderFactoryModelSelect;
import net.blacklab.lmr.item.ItemMaidPorter;
import net.blacklab.lmr.item.ItemSpawnEgg;
import net.blacklab.lmr.item.ItemTriggerRegisterKey;
import net.blacklab.lmr.network.GuiHandler;
import net.blacklab.lmr.network.LMRNetwork;
import net.blacklab.lmr.network.ProxyCommon;
import net.blacklab.lmr.util.DevMode;
import net.blacklab.lmr.util.FileList;
import net.blacklab.lmr.util.IFF;
import net.blacklab.lmr.util.manager.EntityModeManager;
import net.blacklab.lmr.util.manager.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;

@Mod(modid = LittleMaidReengaged.MODID, name = "LittleMaidReengaged", version = LittleMaidReengaged.VERSION,
		acceptedMinecraftVersions=LittleMaidReengaged.ACCEPTED_MCVERSION, dependencies = LittleMaidReengaged.DEPENDENCIES)
public class LittleMaidReengaged {

	public static final String MODID = "lmreengaged";
	public static final String VERSION = "8.0.1.66";
	public static final String ACCEPTED_MCVERSION = "[1.12.2]";
	public static final String DEPENDENCIES = "required-after:forge@[1.12.2-14.23.0.2517,)";

	public static final CreativeTab creativeTab = new CreativeTab(MODID);

	@SidedProxy(clientSide = "net.blacklab.lmr.network.ProxyClient", serverSide = "net.blacklab.lmr.network.ProxyCommon")
	public static ProxyCommon proxy;

	@Instance(MODID)
	public static LittleMaidReengaged instance;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		FileList.initClassLoader();

		ModelManager.init();

		Config.init();

		// Item preinit
		ItemTriggerRegisterKey.DEFAULT_ITEM = new ItemTriggerRegisterKey();
		ItemMaidPorter.DEFAULT_ITEM = new ItemMaidPorter();
		
		// AI Tasks
		EntityModeManager.init();

		// Mod Network
		LMRNetwork.init(MODID);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
		MinecraftForge.EVENT_BUS.register(new EntityLittleMaid.MaidEvents());

		proxy.init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		// init Entity Modes/Jobs
		EntityModeManager.loadEntityMode();
		EntityModeManager.showLoadedModes();

		// IFFのロード
		IFF.loadIFFs();
	}

	@Mod.EventBusSubscriber
	public static class ObjectRegistryHandler {
		@SubscribeEvent
		public static void registerRecipes(RegistryEvent.Register<IRecipe> event){
			ItemTriggerRegisterKey.registerRecipe(event);

			if(Config.cfg_enableSpawnEgg)
				ItemSpawnEgg.registerRecipe(event);
		}

		@SubscribeEvent
		public static void addItems(RegistryEvent.Register<Item> event) {
			event.getRegistry().register(ItemTriggerRegisterKey.DEFAULT_ITEM);
			event.getRegistry().register(ItemMaidPorter.DEFAULT_ITEM);
		}

		@SubscribeEvent
		public static void registerEntities(RegistryEvent.Register<EntityEntry> event){
			EntityRegistry.registerModEntity(new ResourceLocation(MODID, EntityLittleMaid.NAME), EntityLittleMaid.class, EntityLittleMaid.NAME, 0, instance, 80, 3, true, 0x000000, 0xD800FF);
			EntityRegistry.registerModEntity(new ResourceLocation(MODID, EntityLittleMaidAvatarMP.NAME), EntityLittleMaidAvatarMP.class, EntityLittleMaidAvatarMP.NAME, 1, instance, 80, 3, false);

			if (Config.cfg_spawnWeight > 0) {
				HashSet<BiomeDictionary.Type> spawnBiomes = new HashSet<>();
				spawnBiomes.add(BiomeDictionary.Type.HOT);
				spawnBiomes.add(BiomeDictionary.Type.DRY);
				spawnBiomes.add(BiomeDictionary.Type.SAVANNA);
				spawnBiomes.add(BiomeDictionary.Type.CONIFEROUS);
				spawnBiomes.add(BiomeDictionary.Type.MUSHROOM);
				spawnBiomes.add(BiomeDictionary.Type.FOREST);
				spawnBiomes.add(BiomeDictionary.Type.PLAINS);
				spawnBiomes.add(BiomeDictionary.Type.SANDY);
				spawnBiomes.add(BiomeDictionary.Type.BEACH);
				//spawnBiomes.add(BiomeDictionary.Type.COLD);
				//spawnBiomes.add(BiomeDictionary.Type.SNOWY);
				//spawnBiomes.add(BiomeDictionary.Type.LUSH);

				for(Biome biome : Biome.REGISTRY)
					if(biome != null)
						for(BiomeDictionary.Type biomeType : spawnBiomes)
							if(BiomeDictionary.hasType(biome, biomeType))
								EntityLittleMaid.registerSpawnBiome(biome);

			}
		}

		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		public static void onRegisterModels(ModelRegistryEvent event) {
			ModelLoader.setCustomModelResourceLocation(ItemMaidPorter.DEFAULT_ITEM, 0, new ModelResourceLocation(ItemMaidPorter.DEFAULT_ITEM.getRegistryName().toString()));
			ModelLoader.setCustomModelResourceLocation(ItemTriggerRegisterKey.DEFAULT_ITEM, 0, new ModelResourceLocation(ItemTriggerRegisterKey.DEFAULT_ITEM.getRegistryName().toString()));

			RenderingRegistry.registerEntityRenderingHandler(EntityLittleMaid.class, new RenderFactoryLittleMaid());
			RenderingRegistry.registerEntityRenderingHandler(EntityLittleMaidForTexSelect.class, new RenderFactoryModelSelect());
		}
	}

	public static class CreativeTab extends CreativeTabs {
		CreativeTab(String unlocalizedName) {
			super(unlocalizedName);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public @Nonnull ItemStack getTabIconItem() {
			return new ItemStack(Items.SUGAR, 1);
		}
	}

	// Debug helpers
	public static void Debug(String pText, Object... pVals) {
		if(Config.cfg_PrintDebugMessage || DevMode.DEVELOPMENT_DEBUG_MODE)
			Debug(Level.DEBUG, pText, pVals);
	}

	public static void Debug(boolean isRemote, String format, Object... pVals) {
		Debug("Side=%s; ".concat(format), isRemote, pVals);
	}

	public static void Debug(Level errorLevel, String pText, Object... pVals) {
		System.out.println(String.format(MODID+": " + pText, pVals));
	}
}

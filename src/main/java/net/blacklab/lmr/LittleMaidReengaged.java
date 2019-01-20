package net.blacklab.lmr;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import net.blacklab.lmr.client.entity.EntityLittleMaidForTexSelect;
import net.blacklab.lmr.config.Config;
import net.blacklab.lmr.entity.EntityLittleMaid;
import net.blacklab.lmr.entity.EntityMarkerDummy;
import net.blacklab.lmr.entity.renderfactory.RenderFactoryLittleMaid;
import net.blacklab.lmr.entity.renderfactory.RenderFactoryMarkerDummy;
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
import net.blacklab.lmr.util.helper.CommonHelper;
import net.blacklab.lmr.util.manager.EntityModeManager;
import net.blacklab.lmr.util.manager.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.ModelRegistryEvent;
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
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(
		modid = LittleMaidReengaged.MODID,
		name = "LittleMaidReengaged",
		version = LittleMaidReengaged.VERSION,
		acceptedMinecraftVersions=LittleMaidReengaged.ACCEPTED_MCVERSION,
		dependencies = LittleMaidReengaged.DEPENDENCIES)
public class LittleMaidReengaged {

	public static final String MODID = "lmreengaged";
	public static final String VERSION = "8.0.1.66";
	public static final String ACCEPTED_MCVERSION = "[1.12.2]";
	public static final String DEPENDENCIES = "required-after:forge@[1.12.2-14.23.0.2517,)";

	public static final CreativeTab creativeTab = new CreativeTab(MODID);

	// @MLProp(info="Relative spawn weight. The lower the less common. 10=pigs. 0=off")
	public static int cfg_spawnWeight = 5;
	// @MLProp(info="Maximum spawn count in the World.")
	public static int cfg_spawnLimit = 20;
	// @MLProp(info="Minimum spawn group count.")
	public static int cfg_minGroupSize = 1;
	// @MLProp(info="Maximum spawn group count.")
	public static int cfg_maxGroupSize = 3;
	// @MLProp(info="It will despawn, if it lets things go. ")
	public static boolean cfg_canDespawn = false;
	// @MLProp(info="At local, make sure the name of the owner. ")
	public static boolean cfg_checkOwnerName = false;
	// @MLProp(info="Not to survive the doppelganger. ")
	public static boolean cfg_antiDoppelganger = true;
	// @MLProp(info="Enable LMM SpawnEgg Recipe. ")
	public static boolean cfg_enableSpawnEgg = true;

	// @MLProp(info="LittleMaid Voice distortion.")
	public static boolean cfg_VoiceDistortion = false;

	// @MLProp(info="Print Debug Massages.")
	public static boolean cfg_PrintDebugMessage = false;
	// @MLProp(info="Print Death Massages.")
	public static boolean cfg_DeathMessage = true;
	// @MLProp(info="Spawn Anywhere.")
	public static boolean cfg_Dominant = false;
	// アルファブレンド
	public static boolean cfg_isModelAlphaBlend = false;
	// 野生テクスチャ
	public static boolean cfg_isFixedWildMaid = false;

	// LivingSoundRate
	public static double cfg_voiceRate = 0.1d;

	public static boolean cfg_Aggressive = true;
	public static int cfg_maidOverdriveDelay = 64;

	@SidedProxy(clientSide = "net.blacklab.lmr.network.ProxyClient", serverSide = "net.blacklab.lmr.network.ProxyCommon")
	public static ProxyCommon proxy;

	@Instance(MODID)
	public static LittleMaidReengaged instance;


	public static void Debug(String pText, Object... pVals) {
		// デバッグメッセージ
		if (cfg_PrintDebugMessage || DevMode.DEVELOPMENT_DEBUG_MODE) {
			System.out.println(String.format("littleMaidMob-" + pText, pVals));
		}
	}

	public static void Debug(boolean isRemote, String format, Object... pVals) {
		Debug("Side=%s; ".concat(format), isRemote, pVals);
	}

	public String getName() {
		return "LittleMaidReengaged";
	}

	public static Random randomSoundChance;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		initClassLoader();

		//is this even used?
		//StabilizerManager.init();


		// テクスチャパックの構築
		ModelManager.instance.init();
		ModelManager.instance.loadTextures();
		// ロード
		if (CommonHelper.isClient) {
			// テクスチャパックの構築
//			MMM_TextureManager.loadTextures();
//			MMM_StabilizerManager.loadStabilizer();
			// テクスチャインデックスの構築
			Debug("Localmode: InitTextureList.");
			ModelManager.instance.initTextureList(true);
		} else {
			ModelManager.instance.loadTextureServer();
		}

		randomSoundChance = new Random();

		// Config
		// エラーチェックのため試験的にimportしない形にしてみる
		Config.preInit();
		cfg_Aggressive = Config.getConfig().getCategory("general").get("Aggressive").getBoolean();
		cfg_antiDoppelganger = Config.getConfig().getCategory("general").get("antiDoppelganger").getBoolean();
		cfg_canDespawn = Config.getConfig().getCategory("general").get("canDespawn").getBoolean();
		cfg_checkOwnerName = Config.getConfig().getCategory("general").get("checkOwnerName").getBoolean();
		cfg_DeathMessage = Config.getConfig().getCategory("general").get("DeathMessage").getBoolean();

		cfg_VoiceDistortion = Config.getConfig().getCategory("general").get("VoiceDistortion").getBoolean();
		cfg_Dominant = Config.getConfig().getCategory("general").get("Dominant").getBoolean();
		cfg_enableSpawnEgg = Config.getConfig().getCategory("general").get("enableSpawnEgg").getBoolean();

		cfg_maxGroupSize = Config.getConfig().getCategory("general").get("maxGroupSize").getInt();
		cfg_minGroupSize = Config.getConfig().getCategory("general").get("minGroupSize").getInt();
		cfg_spawnLimit = Config.getConfig().getCategory("general").get("spawnLimit").getInt();
		cfg_spawnWeight = Config.getConfig().getCategory("general").get("spawnWeight").getInt();

		cfg_PrintDebugMessage = Config.getConfig().getCategory("general").get("PrintDebugMessage").getBoolean();
		cfg_isModelAlphaBlend = Config.getConfig().getCategory("general").get("isModelAlphaBlend").getBoolean();
		cfg_isFixedWildMaid = Config.getConfig().getCategory("general").get("isFixedWildMaid").getBoolean();

		cfg_voiceRate = Config.getConfig().getCategory("general").get("voiceRate").getDouble();
		cfg_maidOverdriveDelay = Config.getConfig().getCategory("general").get("maidOverdriveDelay").getInt();

		ItemTriggerRegisterKey.DEFAULT_ITEM = new ItemTriggerRegisterKey();
		ItemMaidPorter.DEFAULT_ITEM = new ItemMaidPorter();
		
		// AIリストの追加
		EntityModeManager.init();

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

		if (cfg_spawnWeight > 0) {
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
							registerSpawnBiome(biome);

		}

		// init Entity Modes
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

			if(cfg_enableSpawnEgg)
				ItemSpawnEgg.registerRecipe(event);
		}

		@SubscribeEvent
		public static void addItems(RegistryEvent.Register<Item> event) {
			event.getRegistry().register(ItemTriggerRegisterKey.DEFAULT_ITEM);
			event.getRegistry().register(ItemMaidPorter.DEFAULT_ITEM);
		}

		@SubscribeEvent
		public static void registerEntities(RegistryEvent.Register<EntityEntry> event){
			EntityRegistry.registerModEntity(new ResourceLocation(MODID, EntityLittleMaid.NAME), EntityLittleMaid.class, EntityLittleMaid.NAME, 2, instance, 80, 3, true, 0x36A8FF, 0xFF2626);
		}

		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		public static void onRegisterModels(ModelRegistryEvent event) {
			ModelLoader.setCustomModelResourceLocation(ItemMaidPorter.DEFAULT_ITEM, 0, new ModelResourceLocation(ItemMaidPorter.DEFAULT_ITEM.getRegistryName().toString()));
			ModelLoader.setCustomModelResourceLocation(ItemTriggerRegisterKey.DEFAULT_ITEM, 0, new ModelResourceLocation(ItemTriggerRegisterKey.DEFAULT_ITEM.getRegistryName().toString()));

			RenderingRegistry.registerEntityRenderingHandler(EntityLittleMaid.class, new RenderFactoryLittleMaid());
			RenderingRegistry.registerEntityRenderingHandler(EntityLittleMaidForTexSelect.class, new RenderFactoryModelSelect());
			RenderingRegistry.registerEntityRenderingHandler(EntityMarkerDummy.class, new RenderFactoryMarkerDummy());
		}
	}

	static void registerSpawnBiome(Biome biome){
		EntityRegistry.addSpawn(EntityLittleMaid.class, cfg_spawnWeight, cfg_minGroupSize, cfg_maxGroupSize, EnumCreatureType.CREATURE, biome);
		Debug("Registering maids to spawn in " + biome.getBiomeName());
	}

	public static class CreativeTab extends CreativeTabs {
		public CreativeTab(String unlocalizedName) {
			super(unlocalizedName);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public ItemStack getTabIconItem() {
			return new ItemStack(Items.SUGAR, 1);
		}
	}

	private void initClassLoader(){
		List<URL> urls = new ArrayList<URL>();
		try {
			urls.add(FileList.dirMods.toURI().toURL());
		} catch (MalformedURLException e1) {
			Debug("malformed URL");
		}
		if(DevMode.DEVMODE==DevMode.DEVMODE_ECLIPSE){
			for(File f:FileList.dirDevIncludeClasses){
				try {
					urls.add(f.toURI().toURL());
				} catch (MalformedURLException e) {
					Debug("malformed URL");
				}
			}
		}

		FileList.COMMON_CLASS_LOADER = new FileList.CommonClassLoaderWrapper(urls.toArray(new URL[]{}), LittleMaidReengaged.class.getClassLoader());
	}

}

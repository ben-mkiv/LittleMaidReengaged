package net.blacklab.lmr.item;

import net.blacklab.lmr.LittleMaidReengaged;
import net.blacklab.lmr.entity.EntityLittleMaid;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.oredict.ShapedOreRecipe;

import static net.blacklab.lmr.LittleMaidReengaged.MODID;

/* this isnt really an Item, but a helper to create the correct spawnegg for the crafting recipe */

public class ItemSpawnEgg {
    public static ItemStack DEFAULT_STACK;

    public static void init(){
        ResourceLocation name = EntityList.getKey(EntityLittleMaid.class);
        if (name != null && EntityList.ENTITY_EGGS.containsKey(name)){
            DEFAULT_STACK = new ItemStack(net.minecraft.init.Items.SPAWN_EGG);
            net.minecraft.item.ItemMonsterPlacer.applyEntityIdToItemStack(DEFAULT_STACK, name);
        }
        else {
            LittleMaidReengaged.Debug("couldnt init spawnEgg, Entity wasn't found");
        }
    }

    public static void registerRecipe(RegistryEvent.Register<IRecipe> event){
        // initialize ItemStack for LittleMaid Entity
        init();

        event.getRegistry().register(new ShapedOreRecipe(new ResourceLocation(MODID + ":spawnegg"), ItemSpawnEgg.DEFAULT_STACK,
                "scs",
                "sbs",
                " e ",
                's', Items.SUGAR, 'c', new ItemStack(Items.DYE, 1, 3),	'b', Items.SLIME_BALL, 'e', Items.EGG).setRegistryName(new ResourceLocation(MODID + ":spawnegg")));
    }
}


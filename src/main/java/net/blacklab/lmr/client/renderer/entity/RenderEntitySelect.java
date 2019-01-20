package net.blacklab.lmr.client.renderer.entity;

import net.blacklab.lmr.config.Config;
import org.lwjgl.opengl.GL11;

import net.blacklab.lmr.client.entity.EntityLittleMaidForTexSelect;
import net.blacklab.lmr.entity.maidmodel.IModelEntity;
import net.blacklab.lmr.entity.maidmodel.ModelBaseDuo;
import net.blacklab.lmr.util.helper.RendererHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;

public class RenderEntitySelect extends RenderModelMulti {

	public class MMMLayerArmor extends LayerArmorBase{

		public RenderLivingBase p1;
		public ModelBaseDuo mmodel;
		public EntityLittleMaidForTexSelect lmm;

		public MMMLayerArmor(RenderLivingBase p_i46125_1_) {
			super(p_i46125_1_);
			p1 = p_i46125_1_;
			mmodel = modelFATT;
		}

		@Override
		protected void initArmor() {
			this.modelLeggings = mmodel;
			this.modelArmor = mmodel;
		}

		@Override
		protected void setModelSlotVisible(ModelBase paramModelBase, EntityEquipmentSlot paramInt) {
			ModelBaseDuo model = (ModelBaseDuo) paramModelBase;
			model.showArmorParts(paramInt.getIndex());
		}

		@Override
		public void doRenderLayer(EntityLivingBase par1EntityLiving,
				float par2, float par3, float par4,
				float par5, float par6, float par7,
				float par8) {
			lmm = (EntityLittleMaidForTexSelect) par1EntityLiving;
			if(!lmm.modeArmor) return;
			// TODO もっと手っ取り早い方法ない？
			render(par1EntityLiving, par2, par3, par4, par6, par7, par8, 3);
			render(par1EntityLiving, par2, par3, par4, par6, par7, par8, 2);
			render(par1EntityLiving, par2, par3, par4, par6, par7, par8, 1);
			render(par1EntityLiving, par2, par3, par4, par6, par7, par8, 0);
		}

		public void setModelValues(EntityLivingBase par1EntityLiving) {
			if (par1EntityLiving instanceof IModelEntity) {
				IModelEntity ltentity = (IModelEntity)par1EntityLiving;
				mmodel.modelInner = ltentity.getModelConfigCompound().textureModel[1];
				mmodel.modelOuter = ltentity.getModelConfigCompound().textureModel[2];
				mmodel.textureInner = ltentity.getTextures(1);
				mmodel.textureOuter = ltentity.getTextures(2);
				mmodel.textureInnerLight = ltentity.getTextures(3);
				mmodel.textureOuterLight = ltentity.getTextures(4);
			}
			mmodel.setRender(RenderEntitySelect.this);
			mmodel.showAllParts();
			mmodel.isAlphablend = true;
		}

		public void render(Entity par1Entity, float par2, float par3, float par4, float par5, float par6, float par7, int renderParts) {
			this.setModelValues(lmm);

			mmodel.showArmorParts(renderParts);

			//Inner
			INNER:{
				if(mmodel.modelInner==null) break INNER;
				ResourceLocation texInner = mmodel.textureInner[renderParts];
				if(texInner!=null) try{
					Minecraft.getMinecraft().getTextureManager().bindTexture(texInner);
				}catch(Exception e){}

//				mmodel.modelInner.setLivingAnimations(lmm.maidCaps, par2, par3, lmm.ticksExisted);
//				mmodel.modelInner.setRotationAngles(par2, par3, lmm.ticksExisted, par5, par6, 0.0625F, lmm.maidCaps);
				mmodel.modelInner.mainFrame.render(0.0625F);
				//mmodel.modelOuter.mainFrame.render(0.0625F, true);
			}

			// 発光Inner
			INNERLIGHT: if (mmodel.modelInner!=null) {
				ResourceLocation texInnerLight = mmodel.textureInnerLight[renderParts];
				if (texInnerLight != null) {
					try{
						Minecraft.getMinecraft().getTextureManager().bindTexture(texInnerLight);
					}catch(Exception e){ break INNERLIGHT; }
					GlStateManager.enableBlend();
					GlStateManager.enableAlpha();
					GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
					GlStateManager.depthFunc(GL11.GL_LEQUAL);

					RendererHelper.setLightmapTextureCoords(0x00f000f0);//61680
					if (mmodel.textureLightColor == null) {
						GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					} else {
						//発光色を調整
						GlStateManager.color(
								mmodel.textureLightColor[0],
								mmodel.textureLightColor[1],
								mmodel.textureLightColor[2],
								mmodel.textureLightColor[3]);
					}
					mmodel.modelInner.mainFrame.render(0.0625F);
					RendererHelper.setLightmapTextureCoords(mmodel.lighting);
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					GlStateManager.disableBlend();
					GlStateManager.disableAlpha();
					//GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				}
			}

			//Outer
			if(Config.cfg_isModelAlphaBlend) GL11.glEnable(GL11.GL_BLEND);
			OUTER:{
				if(mmodel.modelOuter==null) break OUTER;
				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				ResourceLocation texOuter = mmodel.textureOuter[renderParts];
				if(texOuter!=null) try{
					Minecraft.getMinecraft().getTextureManager().bindTexture(texOuter);
				}catch(Exception e){}

				mmodel.modelOuter.mainFrame.render(0.0625F);
			}

			// 発光Outer
			OUTERLIGHT: if (mmodel.modelOuter!=null) {
				ResourceLocation texOuterLight = mmodel.textureOuterLight[renderParts];
				if (texOuterLight != null) {
					try{
						Minecraft.getMinecraft().getTextureManager().bindTexture(texOuterLight);
					}catch(Exception e){ break OUTERLIGHT; }
					GlStateManager.enableBlend();
					GlStateManager.enableAlpha();
					GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
					GlStateManager.depthFunc(GL11.GL_LEQUAL);

					RendererHelper.setLightmapTextureCoords(0x00f000f0);//61680
					if (mmodel.textureLightColor == null) {
						GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					} else {
						//発光色を調整
						GlStateManager.color(
								mmodel.textureLightColor[0],
								mmodel.textureLightColor[1],
								mmodel.textureLightColor[2],
								mmodel.textureLightColor[3]);
					}
					mmodel.modelOuter.mainFrame.render(0.0625F);
					RendererHelper.setLightmapTextureCoords(mmodel.lighting);
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					GlStateManager.disableBlend();
					GlStateManager.disableAlpha();
				}
				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			}

			//カウントインクリメント
//			renderCount++;
//			if(renderCount>=500) renderCount=0;
		}
	}


	public RenderEntitySelect(RenderManager manager, float pShadowSize) {
		super(manager, pShadowSize);
		addLayer(new MMMLayerArmor(this));
	}


	@Override
	public void doRender(EntityLiving par1EntityLiving, double par2,
			double par4, double par6, float par8, float par9) {
		super.doRender(par1EntityLiving, par2, par4, par6, par8, par9);
	}

}

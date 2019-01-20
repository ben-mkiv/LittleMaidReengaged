package net.blacklab.lmr.entity.ai;

import net.blacklab.lmr.entity.EntityLittleMaid;
import net.blacklab.lmr.util.helper.MaidHelper;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EntityAILMFindBlock extends EntityAIBase implements IEntityAI {

	protected boolean isEnable;
	protected EntityLittleMaid theMaid;
	
	
	public EntityAILMFindBlock(EntityLittleMaid pEntityLittleMaid) {
		theMaid = pEntityLittleMaid;
		isEnable = true;
		
		setMutexBits(3);
	}
	
	@Override
	public boolean shouldExecute() {
		if (!isEnable || theMaid.isMaidWait() || theMaid.getMaidActiveModeClass() == null) {
			return false;
		}
		if (!theMaid.getMaidActiveModeClass().isSearchBlock()) {
			return theMaid.getMaidActiveModeClass().shouldBlock(theMaid.maidMode);
		}
		
		// ターゲットをサーチ
		int lx = MathHelper.floor(theMaid.posX);
		int ly = MathHelper.floor(theMaid.posY);
		int lz = MathHelper.floor(theMaid.posZ);
		int vt = MathHelper.floor(((theMaid.rotationYawHead * 4F) / 360F) + 2.5F) & 3;
		int xx = lx;
		int yy = ly;
		int zz = lz;

		// CW方向に検索領域を広げる 
		for (int d = 0; d < 4; d++) {
			for (int a = 0; a < 18; a += 2) {
				int del = a / 2;
				if (vt == 0) {
					xx = lx - del;
					zz = lz - del;
				} 
				else if (vt == 1) { 
					xx = lx + del;
					zz = lz - del;
				} 
				else if (vt == 2) { 
					xx = lx + del;
					zz = lz + del;
				} 
				else if (vt == 3) { 
					xx = lx - del;
					zz = lz + del;
				}

				int b = 0;
				do {
					for (int c = 0; c < 3; c++) {
						yy = ly + (c == 2 ? -1 : c);
						if (theMaid.getMaidActiveModeClass().checkBlock(theMaid.maidMode, xx, yy, zz)) {
							if (theMaid.getMaidActiveModeClass().outrangeBlock(theMaid.maidMode, xx, yy, zz)) {
								theMaid.setTilePos(xx, yy, zz);
							}
						}
					}

					if (vt == 0) {
						xx++;
					} 
					else if (vt == 1) { 
						zz++;
					} 
					else if (vt == 2) { 
						xx--;
					} 
					else if (vt == 3) { 
						zz--;
					}
					
				} while(++b < a);
			}
			vt = (vt + 1) & 3;
		}
		if (theMaid.getMaidActiveModeClass().overlooksBlock(theMaid.maidMode)) {
			TileEntity ltile = theMaid.maidTileEntity;
			if (ltile != null) {
				lx = ltile.getPos().getX();
				ly = ltile.getPos().getY();
				lz = ltile.getPos().getZ();
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean shouldContinueExecuting() {
		theMaid.getMaidActiveModeClass().updateBlock();
		// 移動中は継続
		if (!theMaid.getNavigator().noPath()) return true;
		
		double ld = theMaid.getDistanceTilePos();
		
		// Too far or over tracking range
		if (ld > 100.0D || !MaidHelper.isTargetReachable(theMaid, new Vec3d(theMaid.getCurrentTilePos()), 0)) {
			// 索敵範囲外
			theMaid.getActiveModeClass().farrangeBlock();
			return false;
		} else if (ld > 5.0D) {
			// 射程距離外
			return theMaid.getActiveModeClass().outrangeBlock(theMaid.maidMode);
		} else {
			// 射程距離
			return theMaid.getActiveModeClass().executeBlock(theMaid.maidMode);
		}
	}

	@Override
	public void startExecuting() {
		theMaid.getMaidActiveModeClass().startBlock(theMaid.maidMode);
	}

	@Override
	public void resetTask() {
		theMaid.getMaidActiveModeClass().resetBlock(theMaid.maidMode);
	}

	@Override
	public void updateTask() {
		theMaid.looksTilePos();
	}


	@Override
	public void setEnable(boolean pFlag) {
		isEnable = pFlag;
	}

	@Override
	public boolean getEnable() {
		return isEnable;
	}

}

package amp.awec.mixin;

import amp.awec.data.ClientPlayerData;
import amp.awec.data.PlayerData;
import amp.awec.data.PlayerDataManager;
import amp.awec.util.PosHelper;
import amp.awec.util.WandHelper;
import amp.awec.volume.CuboidVolume;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.camera.ICamera;
import net.minecraft.client.render.renderer.BlendFactor;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.Shaders;
import net.minecraft.client.render.renderer.State;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.joml.primitives.AABBd;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = WorldRenderer.class)
public class RenderSelectionMixin {
	@Unique
	private static final double EXPAND_AMOUNT = 0.002;
	@Unique
	private static final double SMALL_BOX_SIZE = 0.25;
	@Unique
	private static final double SMALL_OFFSET_1 = (1.0 - SMALL_BOX_SIZE) / 2.0;
	@Unique
	private static final double SMALL_OFFSET_2 = 1.0 - SMALL_OFFSET_1;

	@Unique
	private static final Vector4d OUTLINE_COLOR = new Vector4d(0.749, 0.906, 0.988, 1.0);
	@Unique
	private static final Vector4d CORNER1_COLOR = new Vector4d(1.0, 0.478, 0.478, 1.0);
	@Unique
	private static final Vector4d CORNER2_COLOR = new Vector4d(0.678, 1.0, 0.478, 1.0);
	@Unique
	private static final Vector4d TARGET_BLOCK_COLOR = new Vector4d(0.988, 0.894, 0.522, 1.0);

	@Shadow
	public Minecraft mc;

	@Inject(
		method = "renderWorld",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/render/WorldRenderer;renderHand(F)V"
		)
	)
	public void renderWorld(float partialTicks, long updateRenderersUntil, CallbackInfo ci) {
		PlayerData playerData = PlayerDataManager.getPlayerData(mc.thePlayer.uuid);

		if (!ClientPlayerData.drawSelections) {
			return;
		}

		World world = mc.thePlayer.world;

		CuboidVolume selection = playerData.getSelection(world);

		drawVolume(selection, partialTicks);
	}

	@Unique
	private AABBd getAABB(ICamera camera, float partialTicks, Vector3d minCorner, Vector3d maxCorner) {
		AABBd box = new AABBd(minCorner, maxCorner);
		box.translate(-camera.getX(partialTicks), -camera.getY(partialTicks), -camera.getZ(partialTicks));
		return box;
	}

	@Unique
	private void drawSmallBox(TilePos pos, Vector4d color, float partialTicks) {
		Vector3d minCorner = new Vector3d(pos.vec()).add(SMALL_OFFSET_1, SMALL_OFFSET_1, SMALL_OFFSET_1);
		Vector3d maxCorner = new Vector3d(pos.vec()).add(SMALL_OFFSET_2, SMALL_OFFSET_2, SMALL_OFFSET_2);
		AABBd corner1Box = getAABB(mc.activeCamera, partialTicks, minCorner, maxCorner);
		GLRenderer.setColor4dv(color);
		mc.renderGlobal.drawOutlinedBoundingBox(corner1Box);
	}

	@Unique
	private void drawVolume(CuboidVolume volume, float partialTicks) {
		GLRenderer.pushFrame();
		GLRenderer.setShader(Shaders.LINES);
		GLRenderer.disableState(State.DEPTH_TEST);
		GLRenderer.setLineWidth(3.0F);

		TilePos corner1 = volume.getCorner1();
		TilePos corner2 = volume.getCorner2();

		if (corner1 != null) {
			drawSmallBox(corner1, CORNER1_COLOR, partialTicks);
		}
		if (corner2 != null) {
			drawSmallBox(corner2, CORNER2_COLOR, partialTicks);
		}

		if (volume.isComplete()) {
			Vector3d minCorner = new Vector3d(volume.getMinCorner().vec());
			Vector3d maxCorner = new Vector3d(volume.getMaxCorner().vec());

			AABBd mainBox = getAABB(mc.activeCamera, partialTicks, minCorner, maxCorner.add(1, 1, 1));

			mainBox.minX -= EXPAND_AMOUNT;
			mainBox.minY -= EXPAND_AMOUNT;
			mainBox.minZ -= EXPAND_AMOUNT;
			mainBox.maxX += EXPAND_AMOUNT;
			mainBox.maxY += EXPAND_AMOUNT;
			mainBox.maxZ += EXPAND_AMOUNT;

			GLRenderer.setColor4dv(OUTLINE_COLOR);
			mc.renderGlobal.drawOutlinedBoundingBox(mainBox);
		}

		if (WandHelper.isHoldingWand(mc.thePlayer)) {
			TilePos targetPos = WandHelper.getTargetedPos(mc.thePlayer, partialTicks);
			drawSmallBox(targetPos, TARGET_BLOCK_COLOR, partialTicks);
		}

		// TODO: REMOVE THIS (debug)
		drawSmallBox(PosHelper.getPlayerTilePos(mc.thePlayer), TARGET_BLOCK_COLOR, partialTicks);

		GLRenderer.popFrame();
	}
}

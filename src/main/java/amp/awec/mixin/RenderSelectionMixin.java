package amp.awec.mixin;

import amp.awec.data.ClientPlayerData;
import amp.awec.data.PlayerData;
import amp.awec.data.PlayerDataManager;
import amp.awec.util.Vec3i;
import amp.awec.util.WandHelper;
import amp.awec.volume.CuboidVolume;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.camera.ICamera;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;
import org.lwjgl.opengl.GL11;
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
	private static final double[] OUTLINE_COLOR = {0.749, 0.906, 0.988, 1.0};
	@Unique
	private static final double[] CORNER1_COLOR = {1.0, 0.478, 0.478, 1.0};
	@Unique
	private static final double[] CORNER2_COLOR = {0.678, 1.0, 0.478, 1.0};
	@Unique
	private static final double[] TARGET_BLOCK_COLOR = {0.988, 0.894, 0.522, 1.0};

	@Shadow
	public Minecraft mc;

	@Inject(
		method = "renderWorld",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/util/debug/Debug;change(Ljava/lang/String;)V",
			ordinal = 8
		)
	)
	public void renderWorld(float partialTicks, long updateRenderersUntil, CallbackInfo ci) {
		PlayerData playerData = PlayerDataManager.getPlayerData(mc.thePlayer.uuid);

		if (!ClientPlayerData.drawSelections) {
			return;
		}

		World world = mc.thePlayer.world;
		if (world == null) {
			return;
		}

		CuboidVolume selection = playerData.getSelection(world);

		drawVolume(selection, partialTicks);
	}

	@Unique
	private AABB getAABB(ICamera camera, float partialTicks, Vec3 minCorner, Vec3 maxCorner) {
		AABB box = AABB.getTemporaryBB(minCorner.x, minCorner.y, minCorner.z, maxCorner.x, maxCorner.y, maxCorner.z);
		box.move(-camera.getX(partialTicks), -camera.getY(partialTicks), -camera.getZ(partialTicks));
		return box;
	}

	@Unique
	private void drawSmallBox(Vec3i pos, double[] color, float partialTicks) {
		Vec3 v = pos.asMCVector();
		AABB corner1Box = getAABB(mc.activeCamera, partialTicks,
			v.add(SMALL_OFFSET_1, SMALL_OFFSET_1, SMALL_OFFSET_1),
			v.add(SMALL_OFFSET_2, SMALL_OFFSET_2, SMALL_OFFSET_2)
		);
		GL11.glColor4dv(color);
		mc.renderGlobal.drawOutlinedBoundingBox(corner1Box);
	}

	@Unique
	private void drawVolume(CuboidVolume volume, float partialTicks) {
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glLineWidth(3.0F);

		Vec3i corner1 = volume.getCorner1();
		Vec3i corner2 = volume.getCorner2();

		if (corner1 != null) {
			drawSmallBox(corner1, CORNER1_COLOR, partialTicks);
		}
		if (corner2 != null) {
			drawSmallBox(corner2, CORNER2_COLOR, partialTicks);
		}

		if (volume.isComplete()) {
			Vec3 minCorner = volume.getMinCorner().asMCVector();
			Vec3 maxCorner = volume.getMaxCorner().asMCVector();

			AABB mainBox = getAABB(mc.activeCamera, partialTicks, minCorner, maxCorner.add(1, 1, 1));
			mainBox = mainBox.grow(EXPAND_AMOUNT, EXPAND_AMOUNT, EXPAND_AMOUNT);

			GL11.glColor4dv(OUTLINE_COLOR);
			mc.renderGlobal.drawOutlinedBoundingBox(mainBox);
		}

		if (WandHelper.isHoldingWand(mc.thePlayer)) {
			Vec3i targetPos = WandHelper.getTargetedPos(mc.thePlayer, partialTicks);
			drawSmallBox(targetPos, TARGET_BLOCK_COLOR, partialTicks);
		}


		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
	}
}

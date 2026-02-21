package amp.awec.mixin;

import amp.awec.WorldEditMod;
import amp.awec.data.ClientPlayerData;
import amp.awec.data.PlayerData;
import amp.awec.data.PlayerDataManager;
import amp.awec.util.Vec3i;
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

import java.awt.*;


@Environment(EnvType.CLIENT)
@Mixin(value = WorldRenderer.class, remap = false)
public class RenderSelectionMixin {
	@Unique
	private static final double EXPAND_AMOUNT = 0.002;
	@Unique
	private static final double CORNER_BOX_SIZE = 0.25;

	@Unique
	private static final double[] OUTLINE_COLOR = {0.749, 0.906, 0.988, 1.0};
	@Unique
	private static final double[] MINCORNER_COLOR = {1.0, 0.478, 0.478, 1.0};
	@Unique
	private static final double[] MAXCORNER_COLOR = {0.678, 1, 0.478, 1.0};

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

		if (selection.isComplete()) {
			Vec3 minCorner = selection.getMinCorner().asVec3();
			Vec3 maxCorner = selection.getMaxCorner().asVec3();
			Vec3 corner1 = selection.getCorner1().asVec3();
			Vec3 corner2 = selection.getCorner2().asVec3();

			AABB mainBox = getAABB(mc.activeCamera, partialTicks, minCorner, maxCorner.add(1, 1, 1));
			mainBox = mainBox.grow(EXPAND_AMOUNT, EXPAND_AMOUNT, EXPAND_AMOUNT);

			double boxOffset1 = (1.0 - CORNER_BOX_SIZE) / 2.0;
			double boxOffset2 = 1.0 - boxOffset1;

			AABB corner1Box = getAABB(mc.activeCamera, partialTicks, corner1.add(boxOffset1, boxOffset1, boxOffset1), corner1.add(boxOffset2, boxOffset2, boxOffset2));
			AABB corner2Box = getAABB(mc.activeCamera, partialTicks, corner2.add(boxOffset1, boxOffset1, boxOffset1), corner2.add(boxOffset2, boxOffset2, boxOffset2));

			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			GL11.glLineWidth(3.0F);
			GL11.glColor4dv(OUTLINE_COLOR);
			mc.renderGlobal.drawOutlinedBoundingBox(mainBox);

			GL11.glColor4dv(MINCORNER_COLOR);
			mc.renderGlobal.drawOutlinedBoundingBox(corner1Box);

			GL11.glColor4dv(MAXCORNER_COLOR);
			mc.renderGlobal.drawOutlinedBoundingBox(corner2Box);

			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_LINE_SMOOTH);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_ALPHA_TEST);

		}
	}

	private AABB getAABB(ICamera camera, float partialTicks, Vec3 minCorner, Vec3 maxCorner) {
		AABB box = AABB.getTemporaryBB(minCorner.x, minCorner.y, minCorner.z, maxCorner.x, maxCorner.y, maxCorner.z);
		box.move(-camera.getX(partialTicks), -camera.getY(partialTicks), -camera.getZ(partialTicks));
		return box;
	}
}

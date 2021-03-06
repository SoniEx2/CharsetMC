package pl.asie.charset.lib.render;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import pl.asie.charset.lib.utils.ClientUtils;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.util.EnumMap;

public abstract class CharsetBakedModel implements IPerspectiveAwareModel {
    private final EnumMap<ItemCameraTransforms.TransformType, TRSRTransformation> transformMap = new EnumMap(ItemCameraTransforms.TransformType.class);
    private final ResourceLocation particle;

    public CharsetBakedModel(ResourceLocation particle) {
        this.particle = particle;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        return ImmutablePair.of(this,
                transformMap.containsKey(cameraTransformType) ? transformMap.get(cameraTransformType).getMatrix() : null);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return ClientUtils.textureGetter.apply(particle);
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }

    public void addTransformation(ItemCameraTransforms.TransformType type, TRSRTransformation transformation) {
        transformMap.put(type, TRSRTransformation.blockCornerToCenter(transformation));
    }

    public void addThirdPersonTransformation(TRSRTransformation transformation) {
        addTransformation(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, transformation);
        addTransformation(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND,  toLeftHand(transformation));
    }

    public void addFirstPersonTransformation(TRSRTransformation transformation) {
        addTransformation(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, transformation);
        addTransformation(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND,  toLeftHand(transformation));
    }

    // ForgeBlockStateV1 transforms

    private static final TRSRTransformation flipX = new TRSRTransformation(null, null, new Vector3f(-1, 1, 1), null);

    protected static TRSRTransformation toLeftHand(TRSRTransformation transform) {
        return TRSRTransformation.blockCenterToCorner(flipX.compose(TRSRTransformation.blockCornerToCenter(transform)).compose(flipX));
    }

    protected static TRSRTransformation getTransformation(float tx, float ty, float tz, float ax, float ay, float az, float s) {
        return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
                new Vector3f(tx / 16, ty / 16, tz / 16),
                TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)),
                new Vector3f(s, s, s),
                null));
    }

    public void addDefaultBlockTransforms() {
        TRSRTransformation thirdperson = getTransformation(0, 2.5f, 0, 75, 45, 0, 0.375f);
        addTransformation(ItemCameraTransforms.TransformType.GUI,                     getTransformation(0, 0, 0, 30, 225, 0, 0.625f));
        addTransformation(ItemCameraTransforms.TransformType.GROUND,                  getTransformation(0, 3, 0, 0, 0, 0, 0.25f));
        addTransformation(ItemCameraTransforms.TransformType.FIXED,                   getTransformation(0, 0, 0, 0, 0, 0, 0.5f));
        addThirdPersonTransformation(thirdperson);
        addTransformation(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, getTransformation(0, 0, 0, 0, 45, 0, 0.4f));
        addTransformation(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND,  getTransformation(0, 0, 0, 0, 255, 0, 0.4f));
    }

    public void addDefaultItemTransforms() {
        TRSRTransformation thirdperson = getTransformation(0, 3, 1, 0, 0, 0, 0.55f);
        TRSRTransformation firstperson = getTransformation(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f);
        addTransformation(ItemCameraTransforms.TransformType.GROUND,                  getTransformation(0, 2, 0, 0, 0, 0, 0.5f));
        addTransformation(ItemCameraTransforms.TransformType.HEAD,                    getTransformation(0, 13, 7, 0, 180, 0, 1));
        addThirdPersonTransformation(thirdperson);
        addFirstPersonTransformation(firstperson);
    }

    public void addDefaultToolTransforms() {
        addTransformation(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, getTransformation(0, 4, 0.5f,         0, -90, 55, 0.85f));
        addTransformation(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND,  getTransformation(0, 4, 0.5f,         0, 90, -55, 0.85f));
        addTransformation(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, getTransformation(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f));
        addTransformation(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND,  getTransformation(1.13f, 3.2f, 1.13f, 0, 90, -25, 0.68f));
    }
}

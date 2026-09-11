package com.mndk.bteterrarenderer.core.tile.ogc3dtiles;

import com.mndk.bteterrarenderer.core.graphics.PreBakedModel;
import com.mndk.bteterrarenderer.dep.terraplusplus.projection.GeographicProjection;
import com.mndk.bteterrarenderer.dep.terraplusplus.projection.OutOfProjectionBoundsException;
import com.mndk.bteterrarenderer.mcconnector.util.math.McCoord;
import com.mndk.bteterrarenderer.ogc3dtiles.math.Spheroid3;
import com.mndk.bteterrarenderer.ogc3dtiles.math.SpheroidCoordinatesConverter;
import com.mndk.bteterrarenderer.util.IOUtil;
import com.mndk.bteterrarenderer.util.Loggers;
import de.javagl.jgltf.model.*;
import lombok.RequiredArgsConstructor;
import org.joml.*;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

@RequiredArgsConstructor
public abstract class AbstractMpmConverter {

    private final Matrix4d transform;
    private final GeographicProjection projection;
    private final SpheroidCoordinatesConverter coordConverter;

    protected abstract PreBakedModel convert() throws Exception;

    private McCoord transformEarthCoordToGame(Vector3d earthCartesian) throws OutOfProjectionBoundsException {
        Vector3d transformed = this.transform.transformPosition(earthCartesian, new Vector3d());

        Spheroid3 s3 = coordConverter.toSpheroid(transformed);
        double[] posXY = this.projection.fromGeo(s3.getLongitudeDegrees(), s3.getLatitudeDegrees());
        return new McCoord(posXY[0], (float) s3.getHeight(), posXY[1]);
    }

    ParsedPoint toParsedPoint(
            Vector3d pos, @Nullable Matrix4d posTransform,
            @Nullable Vector3d normal, @Nullable Matrix3d normalTransform,
            @Nullable Vector2f tex
    ) throws OutOfProjectionBoundsException {
        if (posTransform != null) pos = posTransform.transformPosition(pos, new Vector3d());
        McCoord gamePos = this.transformEarthCoordToGame(pos);

        McCoord gameNormal = null;
        if (normal != null) {
            if (normalTransform != null) normal = normalTransform.transform(normal, new Vector3d());
            gameNormal = this.transformEarthCoordToGame(pos.add(normal, new Vector3d())).subtract(gamePos).normalized();
        }

        return new ParsedPoint(gamePos, tex, gameNormal);
    }

    public static BufferedImage readMaterialModel(MaterialModel materialModel) {
        BufferedImage image = null;
        if (materialModel instanceof TechniqueMaterialModel) {
            throw new UnsupportedOperationException("material model v1 not supported");
        } else if (materialModel instanceof PbrMaterialModel) {
            PbrMaterialModel pbrMaterialModel = (PbrMaterialModel) materialModel;

            TextureModel textureModel = pbrMaterialModel.getBaseColorTextureModel();
            if (textureModel != null) image = readImageModel(textureModel.getImageModel());
            // TODO: read mag/minFilter, wrapS/T from texture
            // TODO: read emissive, normal, occlusion, and roughness texture from material
        }
        return image;
    }

    public static BufferedImage readImageModel(ImageModel imageModel) {
        try {
            ByteBuffer byteBuffer = imageModel.getImageData();
            byte[] data = IOUtil.readAllBytes(byteBuffer);
            InputStream stream = new ByteArrayInputStream(data);

            // TODO: Add compressedImage3DTiles extension

            return ImageIO.read(stream);
        } catch (IOException e) {
            Loggers.get().error("Could not read image model", e);
            return null;
        }
    }
}

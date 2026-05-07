package com.mndk.bteterrarenderer.mcconnector.client.graphics;

import com.mndk.bteterrarenderer.mcconnector.client.graphics.shape.GraphicsQuad;
import com.mndk.bteterrarenderer.mcconnector.client.graphics.vertex.GraphicsVertex;
import com.mndk.bteterrarenderer.mcconnector.util.math.McCoordTransformer;
import lombok.Getter;

@Getter
public abstract class QuadBufferBuilderWrapper<V extends GraphicsVertex> implements BufferBuilderWrapper<GraphicsQuad<V>> {
    // late init
    private McCoordTransformer transformer = null;

    @Override
    public final void setTransformer(McCoordTransformer transformer) {
        this.transformer = transformer;
    }

    @Override
    public abstract void nextShape(GraphicsQuad<V> shape);
}

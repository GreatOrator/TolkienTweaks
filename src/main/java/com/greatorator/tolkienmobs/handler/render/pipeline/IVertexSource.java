package com.greatorator.tolkienmobs.handler.render.pipeline;

import com.greatorator.tolkienmobs.handler.render.CCRenderState;
import com.greatorator.tolkienmobs.handler.render.pipeline.attribute.AttributeKey;
import com.greatorator.tolkienmobs.handler.vec.Vertex5;
import org.jetbrains.annotations.Nullable;

public interface IVertexSource {

    Vertex5[] getVertices();

    default int getVertexCount() {
        return getVertices().length;
    }

    @Nullable
    <T> T getAttribute(AttributeKey<T> attr);

    boolean hasAttribute(AttributeKey<?> attr);

    void prepareVertex(CCRenderState ccrs);
}

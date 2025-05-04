package com.greatorator.tolkienmobs.handler.render.pipeline;

import com.greatorator.tolkienmobs.handler.render.pipeline.attribute.AttributeKey;

public abstract class VertexAttribute<T> implements IVertexOperation {

    public boolean active = false;
    private final AttributeKey<T> key;

    public VertexAttribute(AttributeKey<T> key) {
        this.key = key;
    }

    @Override
    public int operationID() {
        return key.operationIndex;
    }
}

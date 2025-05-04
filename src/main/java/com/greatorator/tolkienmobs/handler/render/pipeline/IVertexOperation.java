package com.greatorator.tolkienmobs.handler.render.pipeline;


import com.greatorator.tolkienmobs.handler.render.CCRenderState;

public interface IVertexOperation {

    static int registerOperation() {
        return VertexOperationRegistry.nextOperationIndex();
    }

    static int operationCount() {
        return VertexOperationRegistry.nextOperationIndex;
    }

    boolean load(CCRenderState ccrs);

    void operate(CCRenderState ccrs);

    int operationID();

    class VertexOperationRegistry {

        static int nextOperationIndex;

        private static synchronized int nextOperationIndex() {
            return VertexOperationRegistry.nextOperationIndex++;
        }
    }
}
package com.greatorator.tolkienmobs.handler.vec;

import com.greatorator.tolkienmobs.handler.render.CCRenderState;
import com.greatorator.tolkienmobs.handler.render.pipeline.IVertexOperation;

public abstract class Transformation extends ITransformation<Vector3, Transformation> implements IVertexOperation {

    public static final int operationIndex = IVertexOperation.registerOperation();

    public abstract void applyN(Vector3 normal);

    public abstract void apply(Matrix4 mat);

    public Transformation at(Vector3 point) {
        return new TransformationList(new Translation(-point.x, -point.y, -point.z), this, point.translation());
    }

    public TransformationList with(Transformation t) {
        return new TransformationList(this, t);
    }

    @Override
    public boolean load(CCRenderState ccrs) {
        ccrs.pipeline.addRequirement(ccrs.normalAttrib.operationID());
        return !isRedundant();
    }

    @Override
    public void operate(CCRenderState ccrs) {
        apply(ccrs.vert.vec);
        if (ccrs.normalAttrib.active) {
            applyN(ccrs.normal);
        }
    }

    @Override
    public int operationID() {
        return operationIndex;
    }
}

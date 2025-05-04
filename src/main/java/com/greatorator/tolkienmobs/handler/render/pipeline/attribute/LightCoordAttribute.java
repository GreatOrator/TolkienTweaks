package com.greatorator.tolkienmobs.handler.render.pipeline.attribute;

import com.greatorator.tolkienmobs.handler.render.CCRenderState;
import com.greatorator.tolkienmobs.handler.render.lighting.LC;
import com.greatorator.tolkienmobs.handler.render.pipeline.VertexAttribute;
import com.greatorator.tolkienmobs.handler.vec.Transformation;
import com.greatorator.tolkienmobs.handler.vec.Vector3;
import org.jetbrains.annotations.Nullable;

public class LightCoordAttribute extends VertexAttribute<LC[]> {

    public static final AttributeKey<LC[]> attributeKey = AttributeKey.create("light_coord", LC[]::new);

    private final Vector3 vec = new Vector3(); // for computation

    private LC @Nullable [] lcRef;

    public LightCoordAttribute() {
        super(attributeKey);
    }

    @Override
    public boolean load(CCRenderState ccrs) {
        assert ccrs.model != null;

        lcRef = ccrs.model.getAttribute(attributeKey);
        if (ccrs.model.hasAttribute(attributeKey)) {
            return lcRef != null;
        }

        ccrs.pipeline.addDependency(ccrs.sideAttrib);
        ccrs.pipeline.addRequirement(Transformation.operationIndex);
        return true;
    }

    @Override
    public void operate(CCRenderState ccrs) {
        if (lcRef != null) {
            ccrs.lc.set(lcRef[ccrs.vertexIndex]);
        } else {
            ccrs.lc.compute(vec.set(ccrs.vert.vec), ccrs.side);
        }
    }
}

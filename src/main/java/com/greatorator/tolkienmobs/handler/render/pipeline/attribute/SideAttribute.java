package com.greatorator.tolkienmobs.handler.render.pipeline.attribute;

import com.greatorator.tolkienmobs.handler.render.CCRenderState;
import com.greatorator.tolkienmobs.handler.render.pipeline.VertexAttribute;
import com.greatorator.tolkienmobs.util.VectorUtils;
import org.jetbrains.annotations.Nullable;

public class SideAttribute extends VertexAttribute<int[]> {

    public static final AttributeKey<int[]> attributeKey = AttributeKey.create("side", int[]::new);

    private int @Nullable [] sideRef;

    public SideAttribute() {
        super(attributeKey);
    }

    @Override
    public boolean load(CCRenderState ccrs) {
        assert ccrs.model != null;

        sideRef = ccrs.model.getAttribute(attributeKey);
        if (ccrs.model.hasAttribute(attributeKey)) {
            return sideRef != null;
        }

        ccrs.pipeline.addDependency(ccrs.normalAttrib);
        return true;
    }

    @Override
    public void operate(CCRenderState ccrs) {
        if (sideRef != null) {
            ccrs.side = sideRef[ccrs.vertexIndex];
        } else {
            ccrs.side = VectorUtils.findSide(ccrs.normal);
        }
    }
}

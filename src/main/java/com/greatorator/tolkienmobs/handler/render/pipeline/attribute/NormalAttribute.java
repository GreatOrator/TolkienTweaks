package com.greatorator.tolkienmobs.handler.render.pipeline.attribute;

import com.greatorator.tolkienmobs.handler.render.CCRenderState;
import com.greatorator.tolkienmobs.handler.render.pipeline.VertexAttribute;
import com.greatorator.tolkienmobs.handler.vec.Rotation;
import com.greatorator.tolkienmobs.handler.vec.Vector3;
import org.jetbrains.annotations.Nullable;

public class NormalAttribute extends VertexAttribute<Vector3[]> {

    public static final AttributeKey<Vector3[]> attributeKey = AttributeKey.create("normal", Vector3[]::new);

    private Vector3 @Nullable [] normalRef;

    public NormalAttribute() {
        super(attributeKey);
    }

    @Override
    public boolean load(CCRenderState ccrs) {
        assert ccrs.model != null;

        normalRef = ccrs.model.getAttribute(attributeKey);
        if (ccrs.model.hasAttribute(attributeKey)) {
            return normalRef != null;
        }

        if (ccrs.model.hasAttribute(SideAttribute.attributeKey)) {
            ccrs.pipeline.addDependency(ccrs.sideAttrib);
            return true;
        }
        throw new IllegalStateException("Normals requested but neither normal or side attrutes are provided by the model");
    }

    @Override
    public void operate(CCRenderState ccrs) {
        if (normalRef != null) {
            ccrs.normal.set(normalRef[ccrs.vertexIndex]);
        } else {
            ccrs.normal.set(Rotation.axes[ccrs.side]);
        }
    }
}

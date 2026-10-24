package com.greatorator.tolkienmobs.item.custom.render;

import com.greatorator.tolkienmobs.item.custom.CatalystItem;
import com.greatorator.tolkienmobs.item.custom.model.CatalystModel;
import com.geckolib.renderer.GeoItemRenderer;

public class CatalystRender extends GeoItemRenderer<CatalystItem> {
    public CatalystRender() {
        super(new CatalystModel());
    }
}
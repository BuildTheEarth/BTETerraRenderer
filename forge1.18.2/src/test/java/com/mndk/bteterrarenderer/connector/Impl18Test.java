package com.mndk.bteterrarenderer.connector;

import com.mndk.bteterrarenderer.core.config.BTETerraRendererConfig;


public class Impl18Test {

    @Test
    public void testImplFinder() {
        Assertions.assertEquals("osm", BTETerraRendererConfig.GENERAL.getMapServiceId());
    }

}

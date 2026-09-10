package com.mndk.bteterrarenderer.core.util;

import com.mndk.bteterrarenderer.util.StringUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class BTRUtilTest {

    @Test
    public void testDoubleFormatter() {
        Assertions.assertEquals("0" + new DecimalFormatSymbols(Locale.getDefault()).getDecimalSeparator() + "250", StringUtil.formatDoubleNicely(0.25, 3));
        Assertions.assertEquals("0" + new DecimalFormatSymbols(Locale.getDefault()).getDecimalSeparator() + "500", StringUtil.formatDoubleNicely(0.5, 3));
        Assertions.assertEquals("1", StringUtil.formatDoubleNicely(1, 3));
    }

}

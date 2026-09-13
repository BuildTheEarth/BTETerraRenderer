package com.mndk.bteterrarenderer.datatype;

import com.mndk.bteterrarenderer.datatype.number.UByte;
import com.mndk.bteterrarenderer.datatype.number.UInt;
import com.mndk.bteterrarenderer.datatype.number.ULong;
import com.mndk.bteterrarenderer.datatype.number.UShort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NumberDataTypeTest {

    @Test
    public void givenNumber_testToString() {
        // byte
        Assertions.assertEquals("10", DataType.int8().toString((byte) 10));
        Assertions.assertEquals("-6", DataType.int8().toString((byte) -6));
        Assertions.assertEquals("10", DataType.uint8().toString(UByte.of(10)));
        Assertions.assertEquals("250", DataType.uint8().toString(UByte.of(-6)));

        // short
        Assertions.assertEquals("10", DataType.int16().toString((short) 10));
        Assertions.assertEquals("-6", DataType.int16().toString((short) -6));
        Assertions.assertEquals("10", DataType.uint16().toString(UShort.of(10)));
        Assertions.assertEquals("65530", DataType.uint16().toString(UShort.of(-6)));

        // int
        Assertions.assertEquals("10", DataType.int32().toString(10));
        Assertions.assertEquals("-6", DataType.int32().toString(-6));
        Assertions.assertEquals("10", DataType.uint32().toString(UInt.of(10)));
        Assertions.assertEquals("4294967290", DataType.uint32().toString(UInt.of(-6)));

        // long
        Assertions.assertEquals("10", DataType.int64().toString(10L));
        Assertions.assertEquals("-6", DataType.int64().toString(-6L));
        Assertions.assertEquals("10", DataType.uint64().toString(ULong.of(10L)));
        Assertions.assertEquals("18446744073709551610", DataType.uint64().toString(ULong.of(-6L)));
    }

    @Test
    public void givenTwoNumbers_whenDivide_thenCorrect() {
        // byte
        Assertions.assertEquals((byte) 5, DataType.int8().div(Byte.valueOf((byte) 10), Byte.valueOf((byte) 2)).byteValue());
        Assertions.assertEquals((byte) -3, DataType.int8().div(Byte.valueOf((byte) -6), Byte.valueOf((byte) 2)).byteValue());
        Assertions.assertEquals((byte) 5, DataType.uint8().div(UByte.of(10), UByte.of(2)).byteValue());
        Assertions.assertEquals((byte) 125, DataType.uint8().div(UByte.of(-6), UByte.of(2)).byteValue());

        // short
        Assertions.assertEquals((short) 5, DataType.int16().div(Short.valueOf((short) 10), Short.valueOf((short) 2)).shortValue());
        Assertions.assertEquals((short) -3, DataType.int16().div(Short.valueOf((short) -6), Short.valueOf((short) 2)).shortValue());
        Assertions.assertEquals((short) 5, DataType.uint16().div(UShort.of(10), UShort.of(2)).shortValue());
        Assertions.assertEquals((short) 32765, DataType.uint16().div(UShort.of(-6), UShort.of(2)).shortValue());

        // int
        Assertions.assertEquals(5, DataType.int32().div(Integer.valueOf(10), Integer.valueOf(2)).intValue());
        Assertions.assertEquals(-3, DataType.int32().div(Integer.valueOf(-6), Integer.valueOf(2)).intValue());
        Assertions.assertEquals(5, DataType.uint32().div(UInt.of(10), UInt.of(2)).intValue());
        Assertions.assertEquals(2147483645, DataType.uint32().div(UInt.of(-6), UInt.of(2)).intValue());

        // long
        Assertions.assertEquals(5L, DataType.int64().div(Long.valueOf(10L), Long.valueOf(2L)).longValue());
        Assertions.assertEquals(-3L, DataType.int64().div(Long.valueOf(-6L), Long.valueOf(2L)).longValue());
        Assertions.assertEquals(5L, DataType.uint64().div(ULong.of(10L), ULong.of(2L)).longValue());
        Assertions.assertEquals(9223372036854775805L, DataType.uint64().div(ULong.of(-6L), ULong.of(2L)).longValue());
    }


}

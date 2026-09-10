/*
 * Copyright (C) 2024 The Draco Authors (for providing the original C++ code)
 * Copyright (C) 2024 m4ndeokyi (for translating the code into Java)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mndk.bteterrarenderer.draco.compression.entropy;

import com.mndk.bteterrarenderer.datatype.number.UInt;
import com.mndk.bteterrarenderer.datatype.pointer.Pointer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ShannonEntropyTest {

    @Test
    public void testBinaryEntropy() {
        Assertions.assertEquals(0, ShannonEntropyTracker.computeBinary(UInt.of(0), UInt.of(0)), 0);
        Assertions.assertEquals(0, ShannonEntropyTracker.computeBinary(UInt.of(10), UInt.of(0)), 0);
        Assertions.assertEquals(0, ShannonEntropyTracker.computeBinary(UInt.of(10), UInt.of(10)), 0);
        Assertions.assertEquals(1.0, ShannonEntropyTracker.computeBinary(UInt.of(10), UInt.of(5)), 1e-4);
    }

    @Test
    public void testStreamEntropy() {
        int[] symbols = new int[] { 1, 5, 1, 100, 2, 1 };
        Pointer<UInt> symbolsPointer = Pointer.wrapUnsigned(symbols);

        ShannonEntropyTracker entropyTracker = new ShannonEntropyTracker();
        Assertions.assertEquals(0, entropyTracker.getNumberOfDataBits());

        int maxSymbol = 0;
        for (int i = 0; i < symbols.length; ++i) {
            if (symbols[i] > maxSymbol) {
                maxSymbol = symbols[i];
            }
            ShannonEntropyTracker.EntropyData entropyData = entropyTracker.push(symbolsPointer.add(i), 1);

            long streamEntropyBits = entropyTracker.getNumberOfDataBits();
            Assertions.assertEquals(ShannonEntropyTracker.getNumberOfDataBits(entropyData), streamEntropyBits);

            long expectedEntropyBits = ShannonEntropyTracker.compute(symbolsPointer, i + 1, maxSymbol, null);
            Assertions.assertEquals(expectedEntropyBits, streamEntropyBits, 2);
        }

        ShannonEntropyTracker entropyTracker2 = new ShannonEntropyTracker();
        entropyTracker2.push(symbolsPointer, symbols.length);
        long stream2EntropyBits = entropyTracker2.getNumberOfDataBits();
        Assertions.assertEquals(entropyTracker.getNumberOfDataBits(), stream2EntropyBits);

        entropyTracker2.peek(symbolsPointer, 1);

        Assertions.assertEquals(stream2EntropyBits, entropyTracker2.getNumberOfDataBits());
    }

}

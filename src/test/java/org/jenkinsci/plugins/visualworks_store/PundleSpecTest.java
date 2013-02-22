/*
 * The MIT License
 *
 * Copyright (c) 2013. Randy Coulman
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkinsci.plugins.visualworks_store;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PundleSpecTest {

    private PundleSpec spec;

    @Before
    public void setUp() throws Exception {
        spec = new PundleSpec(PundleType.PACKAGE, "MySpec");
    }

    @Test
    public void isEqualToSelf() {
        assertEquals(spec, spec);
    }

    @Test
    public void isEqualIfTypesAndNamesMatch() {
        PundleSpec sameName = new PundleSpec(PundleType.PACKAGE, "MySpec");

        assertEquals(spec, sameName);
    }

    @Test
    public void notEqualToNull() {
        assertFalse(spec.equals(null));
    }

    @Test
    public void notEqualIfNamesDiffer() {
        PundleSpec differentName =
                new PundleSpec(PundleType.PACKAGE, "differentName");

        assertFalse(spec.equals(differentName));
    }

    @Test
    public void notEqualIfTypesDiffer() {
        PundleSpec differentType =
                new PundleSpec(PundleType.BUNDLE, "MySpec");

        assertFalse(spec.equals(differentType));
    }
}

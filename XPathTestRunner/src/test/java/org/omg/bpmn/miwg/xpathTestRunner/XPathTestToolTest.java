/**
 * The MIT License (MIT)
 * Copyright (c) 2013 OMG BPMN Model Interchange Working Group
 *
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
 * 
 */

package org.omg.bpmn.miwg.xpathTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.Collection;

import org.junit.Test;
import org.omg.bpmn.miwg.testresult.Output;

public class XPathTestToolTest {

    private static final String RPT_FOLDER = "target";

    @Test
    public void test() {
        XPathTestTool xPathTestTool = new XPathTestTool("A.1.0", RPT_FOLDER);
        InputStream actualBpmnXml = null;
        try {
            actualBpmnXml = getClass().getResourceAsStream(
                    "/Yaoqiang BPMN Editor 3.0.1/A.1.0-roundtrip.bpmn");
            assertNotNull("Cannot find resource to test", actualBpmnXml);
            Collection<? extends Output> outputs = xPathTestTool
                    .getSignificantDifferences(null, actualBpmnXml);
            System.out.println(outputs);
            assertEquals(6, outputs.size());
            int idx = 1; 
            for (Output output : outputs) {
                System.out.println(idx + ": " + output.getDescription());
                idx++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            try {
                actualBpmnXml.close();
            } catch (Exception e) {
                ;
            }
        }
    }

}

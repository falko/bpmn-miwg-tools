/**
 * The MIT License (MIT)
 * Copyright (c) 2013 Signavio, OMG BPMN Model Interchange Working Group
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

package org.omg.bpmn.miwg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.NodeDetail;
import org.omg.bpmn.miwg.api.TestTool;
import org.omg.bpmn.miwg.bpmn2_0.comparison.Bpmn20ConformanceChecker;
import org.omg.bpmn.miwg.bpmn2_0.comparison.Bpmn20DiffConfiguration;
import org.omg.bpmn.miwg.configuration.BpmnCompareConfiguration;
import org.omg.bpmn.miwg.input.BpmnFileFilter;
import org.omg.bpmn.miwg.output.Detail;
import org.omg.bpmn.miwg.output.DetailedOutput;
import org.omg.bpmn.miwg.testresult.Output;
import org.omg.bpmn.miwg.testresult.OutputType;
import org.omg.bpmn.miwg.testresult.Test;
import org.omg.bpmn.miwg.testresult.TestResults;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class TestRunner implements TestTool {

    private static final String FILE_EXTENSION = "bpmn";
    private static Bpmn20ConformanceChecker checker;
    private static FilenameFilter bpmnFileFilter = new BpmnFileFilter();

    /**
     * First argument path to folder containing the reference bpmn xml files
     * Second argument path to folder containing the bpmn files to compare with
     *
     * @param args
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     *
     */
    public static void main(String[] args) throws SAXException, IOException,
            ParserConfigurationException {

        System.out.println("Running BPMN 2.0 XML Compare Test...");
        String result = runXmlCompareTest(args[0], args[1],
                Variant.valueOf(args[2]));

        if (args.length > 3) {
            File outputFile = new File(args[3]);
            FileUtils.writeStringToFile(outputFile, result);
            System.out.println("Output printed to: \n"
                    + outputFile.getAbsolutePath());
        } else {
            System.out.println(result);
        }

        System.out.println("Finished BPMN 2.0 XML Compare Test");
    }

    public static String runXmlCompareTest(String refFolderPath,
            String testFolderPath, Variant variant)
            throws ParserConfigurationException, SAXException, IOException {
        return runXmlCompareTest(refFolderPath, testFolderPath, variant, null);
    }

    /**
     * Performs the BPMN 2.0 XML Compare test and prints out the resulting XML
     * structure
     *
     * @param referenceFolderPath
     *            Path to the folder containing the reference files
     * @param testFolderPath
     *            Path to the folder containing the test file of a specific tool
     * @param variant
     *            Test variant either export or roundtrip
     * @param confName
     *            configuration of what differences to ignore (denote same
     *            semantic)
     *
     * @return Outputs the XML structure
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static String runXmlCompareTest(String refFolderPath,
            String testFolderPath, Variant variant, String confName)
            throws ParserConfigurationException, SAXException, IOException {
        File refFolder = new File(refFolderPath);
        File testFolder = new File(testFolderPath);

        if (!refFolder.isDirectory() || !testFolder.isDirectory()) {
            throw new IllegalArgumentException("Invalid path to folder");
        }
        TestRunner runnner = new TestRunner();
        initChecker(confName);

        TestResults results = new TestResults();

        for (File bpmnFile : refFolder.listFiles(bpmnFileFilter)) {
            File compareFile = getCompareFile(bpmnFile, testFolder, variant);
            if (compareFile.exists()) {
                // Building test output structure
                FileInputStream bpmnStream = null;
                FileInputStream compareStream = null;
                try {
                    bpmnStream = new FileInputStream(bpmnFile);
                    compareStream = new FileInputStream(compareFile);
                    Test test = results.addTool(testFolder.getName()).addTest(
                            bpmnFile.getName(), variant.name());
                    test.addAll(runnner.getSignificantDifferences(bpmnStream,
                            compareStream));
                } finally {
                    try {
                        bpmnStream.close();
                    } catch (Exception e) {
                        ;
                    }
                    try {
                        compareStream.close();
                    } catch (Exception e) {
                        ;
                    }
                }
            } else {
                results.addTool(testFolder.getName())
                        .addTest(bpmnFile.getName(), variant.name())
                        .addOutput(
                                OutputType.info,
                                "Missing reference file: "
                                        + compareFile.getCanonicalPath());
            }
        }

        return results.toString();
    }

    private static void initChecker(String confName) throws JsonParseException,
            JsonMappingException, IOException, ParserConfigurationException {
        BpmnCompareConfiguration conf = BpmnCompareConfiguration
                .loadConfiguration(confName);
        Bpmn20DiffConfiguration.setConf(conf);
        checker = new Bpmn20ConformanceChecker();
    }

    private static Bpmn20ConformanceChecker getChecker()
            throws JsonParseException, JsonMappingException, IOException,
            ParserConfigurationException {
        if (checker == null) {
            initChecker(null/* rely on default configuration */);
        }
        return checker;
    }

    public static DetailedOutput describeDifference(Difference difference) {
        DetailedOutput dOut = new DetailedOutput();

        // Reference xpath/value
        dOut.addDetail(printDifferenceDetail(difference.getControlNodeDetail(),
                "reference"));

        // Vendor xpath/value
        dOut.addDetail(printDifferenceDetail(difference.getTestNodeDetail(),
                "vendor"));

        return dOut;
    }

    private static Detail printDifferenceDetail(NodeDetail detail, String type) {
        Detail d = new Detail();
        d.setMessage(detail.getXpathLocation() + " :\t" + detail.getValue());
        d.setType(type);
        d.setXpath(detail.getXpathLocation());
        return d;
    }

    private static File getCompareFile(File refFile, File testFolder,
            Variant variant) {
        int i = refFile.getName().lastIndexOf(".");
        String fName = refFile.getName().substring(0, i) + "-"
                + variant.toString() + "." + FILE_EXTENSION;

        return new File(testFolder, fName);
    }

    @Override
    public Collection<? extends Output> getSignificantDifferences(
            InputStream expectedBpmnXml, InputStream actualBpmnXml)
            throws JsonParseException, JsonMappingException,
            IOException, ParserConfigurationException {
        List<Difference> diffs = getChecker().getSignificantDifferences(
                expectedBpmnXml, actualBpmnXml);
        List<Output> outputs = new ArrayList<Output>();
        for (Difference diff : diffs) {
            DetailedOutput detail = describeDifference(diff);
            // System.out.println("Detail: " + detail.getDescription());
            Output output = new Output(OutputType.finding, detail);
            output.setDescription(String.format(
                    "Difference found in %1$s (id:%2$s)",
                    diff.getDescription(), diff.getId()));
            outputs.add(output);
        }
        return outputs;
    }

    public String getName() {
        return "xml-compare";
    }
}

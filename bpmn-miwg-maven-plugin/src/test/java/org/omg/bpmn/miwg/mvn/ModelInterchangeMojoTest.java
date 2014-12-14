package org.omg.bpmn.miwg.mvn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.maven.model.Resource;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class ModelInterchangeMojoTest {

    private static final File REPORT_BASE_FOLDER = new File("target"
, "site");
    private static final String W4_MODELER_ID = "W4 BPMN+ Composer V.9.0";
	private static final String CAMUNDA_MODELER_ID = "camunda Modeler 2.4.0";
	private static final String CAMUNDA_JS_ID = "camunda-bpmn.js c906a7c941b82dbb832ed9be62989c171c724199";
	private static final String ECLIPSE_MODELER_ID = "Eclipse BPMN2 Modeler 0.2.6";
	private static final String IBM_MODELER_ID = "IBM Process Designer 8.0.1";
	private static final String MID_MODELER_ID = "MID Innovator 11.5.2.30413";
	private static final String SIGNAVIO_MODELER_ID = "Signavio Process Editor 7.8.1";
	private static final String YAOQIANG_2_MODELER_ID = "Yaoqiang BPMN Editor 2.2.6";
	private static final String YAOQIANG_3_MODELER_ID = "Yaoqiang BPMN Editor 3.0.1";
	private static ModelInterchangeMojo mojo;
	private static XPathExpression testOverviewExpr;
	private static File overview;
	private static DocumentBuilder docBuilder;
    private static XPath xPath;

    @BeforeClass
    public static void setUpClass() {
        mojo = new ModelInterchangeMojo();
        mojo.outputDirectory = REPORT_BASE_FOLDER;
        mojo.resources = new ArrayList<Resource>();

//        overview = HTMLAnalysisOutputWriter
//                .getOverviewFile(mojo.outputDirectory);

        try {
            docBuilder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();

            XPathFactory xPathFactory = XPathFactory.newInstance();
            xPath = xPathFactory.newXPath();
            testOverviewExpr = xPath.compile("//div[@class=\"test\"]");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getClass().getName() + ":" + e.getMessage());
        }
    }

    @Test
    public void testScanResourcesForAllBpmnFiles() {
        List<File> bpmnFiles = new ArrayList<File>();
        File dir = new File("src" + File.separator + "test" + File.separator
                + "resources");
        mojo.scanForBpmn(dir, bpmnFiles);
        assertEquals(16, bpmnFiles.size());
    }

    @Test
    public void testScanResourcesForSpecificVendorBpmnFiles() {
        List<File> bpmnFiles = new ArrayList<File>();
        File dir = new File("src" + File.separator + "test" + File.separator
                + "resources");
        mojo.scanForBpmn(dir, bpmnFiles, W4_MODELER_ID);
        assertEquals(9, bpmnFiles.size());
    }

    @Test
    public void testMojo() {
        try {
            mojo.outputDirectory = REPORT_BASE_FOLDER;
            mojo.resources = new ArrayList<Resource>();
            Resource res = new Resource();
            res.setDirectory("src" + File.separator + "test" + File.separator
                    + "resources");
            mojo.resources.add(res);
            mojo.execute();
            File overview = new File(mojo.outputDirectory, "overview.html");
            assertTrue(overview.exists());

            // assert structure and content of overview file
            assertTrue(overview.exists());
            Document document = docBuilder.parse(overview);
            NodeList nodes = (NodeList) testOverviewExpr.evaluate(document,
                    XPathConstants.NODESET);
            // count of .bpmn in W4 & ITP dirs, now including C test suite
            assertEquals(14, nodes.getLength());

            // report files for each tool
            assertHtmlReportsExist(new File(REPORT_BASE_FOLDER,
                    ModelInterchangeMojo.XML_COMPARE_TOOL_ID));
            // assertHtmlReportsExist(new File(REPORT_BASE_FOLDER,
            // "xsd"));
            assertHtmlReportsExist(new File(REPORT_BASE_FOLDER,
                    ModelInterchangeMojo.XPATH_TOOL_ID));

            // assert structure of individual results file
            File xpathResult = new File(REPORT_BASE_FOLDER + File.separator
                    + ModelInterchangeMojo.XPATH_TOOL_ID + File.separator
                    + W4_MODELER_ID + File.separator + W4_MODELER_ID
                    + "-A.1.0-roundtrip.html");
            System.out.println("Checking file: " + xpathResult);
            assertTrue(xpathResult.exists());
            document = docBuilder.parse(xpathResult);
            nodes = (NodeList) xPath.compile(
                    "//body/div[@class=\"testresults\"]")
                    .evaluate(document, XPathConstants.NODESET);
            assertTrue("Did not find result element", nodes.getLength() == 1);
            nodes = (NodeList) xPath.compile(
                    "//body/div[@class=\"testresults\"]/div[@class=\"tool\"]")
                    .evaluate(document, XPathConstants.NODESET);
            assertTrue("Did not find tool element", nodes.getLength() == 1);
            nodes = (NodeList) xPath
                    .compile(
                            "//body/div[@class=\"testresults\"]/div[@class=\"tool\"]/div[@class=\"test\"]")
                    .evaluate(document, XPathConstants.NODESET);
            assertTrue("Did not find test element", nodes.getLength() >= 1);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getClass() + ":" + e.getMessage());
		}
	}

    private void assertHtmlReportsExist(File toolFldr) {
        File w4Fldr = new File(toolFldr, W4_MODELER_ID);
        assertTrue("Tool folder " + toolFldr.getAbsolutePath() + " not found",
                toolFldr.exists());
        assertTrue("Tool report for W4 A.1.0 export not found", new File(
                w4Fldr, W4_MODELER_ID + "-A.1.0-export.html").exists());
        assertTrue("Tool report for W4 A.1.0 roundtrip not found", new File(
                w4Fldr, W4_MODELER_ID + "-A.1.0-roundtrip.html").exists());
        assertTrue("Tool report for W4 A.2.0 export not found", new File(
                w4Fldr, W4_MODELER_ID + "-A.2.0-export.html").exists());
        assertTrue("Tool report for W4 A.2.0 roundtrip not found", new File(
                w4Fldr, W4_MODELER_ID + "-A.2.0-roundtrip.html").exists());
        assertTrue("Tool report for W4 A.3.0 export not found", new File(
                w4Fldr, W4_MODELER_ID + "-A.3.0-export.html").exists());
        assertTrue("Tool report for W4 A.3.0 roundtrip not found", new File(
                w4Fldr, W4_MODELER_ID + "-A.3.0-roundtrip.html").exists());
        assertTrue("Tool report for W4 A.4.0 export not found", new File(
                w4Fldr, W4_MODELER_ID + "-A.4.0-export.html").exists());
        assertTrue("Tool report for W4 A.4.0 roundtrip not found", new File(
                w4Fldr, W4_MODELER_ID + "-A.4.0-roundtrip.html").exists());
        assertTrue("Tool report for W4 A.4.1 roundtrip not found", new File(
                w4Fldr, W4_MODELER_ID + "-A.4.1-roundtrip.html").exists());
    }
    
    @Test
    public void testInferTestNames() {
        assertEquals("A.1.0",
                mojo.inferTestName(new File("A.1.0-export.bpmn")));
        assertEquals("B.1.0",
                mojo.inferTestName(new File("B.1.0-export.bpmn")));
        assertEquals("A.1.0", mojo.inferTestName(new File(W4_MODELER_ID,
                "A.1.0-export.bpmn")));
        assertEquals(
                "A.1.0",
                mojo.inferTestName(new File(ModelInterchangeMojo.SUITE_A_PATH
                        + File.separator + W4_MODELER_ID, "A.1.0-export.bpmn")));
    }

    @Test
    public void testFindReference() {
        File b = new File("A.1.0-export.bpmn");
        assertNotNull(mojo.findReference("A.1.0", b));
        b = new File("src" + File.separator + "test" + File.separator
                + "resources" + File.separator + "A.1.0-export.bpmn");
        assertNotNull(mojo.findReference("A.1.0", b));
    }
    
    @Test
    public void testApplicationsParsing() {
        List<String> applications = mojo.getApplications();
        assertEquals(17, applications.size());
        assertTrue(applications.contains(CAMUNDA_MODELER_ID));
        assertTrue(applications.contains(CAMUNDA_JS_ID));
        assertTrue(applications.contains(ECLIPSE_MODELER_ID));
        assertTrue(applications.contains(IBM_MODELER_ID));
        assertTrue(applications.contains(MID_MODELER_ID));
        assertTrue(applications.contains(SIGNAVIO_MODELER_ID));
        assertTrue(applications.contains(YAOQIANG_2_MODELER_ID));
        assertTrue(applications.contains(YAOQIANG_3_MODELER_ID));
        assertTrue(applications.contains(W4_MODELER_ID));
    }
}

package org.omg.bpmn.miwg.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.omg.bpmn.miwg.output.DetailedOutput;
import org.omg.bpmn.miwg.testresult.Output;
import org.omg.bpmn.miwg.testresult.OutputType;
import org.omg.bpmn.miwg.xpath.base.testEntries.AbstractCheckEntry;
import org.omg.bpmn.miwg.xpath.base.testEntries.FindingAssertionEntry;
import org.omg.bpmn.miwg.xpath.base.testEntries.InfoEntry;
import org.omg.bpmn.miwg.xpath.base.testEntries.OKAssertionEntry;
import org.omg.bpmn.miwg.xpath.common.CheckContext;
import org.w3c.dom.Document;

public abstract class AbstractCheck implements Check {

    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    protected Document doc;
    private int numIssues = 0;
	private int numOK = 0;
	protected CheckOutput out;
    protected List<Output> outputs = new ArrayList<Output>();

	protected void ok(AbstractCheckEntry entry) {
		numOK++;
		out.println(entry);
        DetailedOutput details = new DetailedOutput();
        details.setDescription(entry.toLine());
        outputs.add(new Output(OutputType.ok, entry.toLine()));
	}
	
	protected void ok(String assertion, String message) {
		ok(new OKAssertionEntry(assertion, message, null));
	}

	protected void finding(AbstractCheckEntry entry) {
		numIssues++;
		out.println(entry);
        DetailedOutput details = new DetailedOutput();
        details.setDescription(entry.toLine());
        outputs.add(new Output(OutputType.finding, entry.toLine()));
	}
	
	protected void finding(String assertion, String message) {
		finding(new FindingAssertionEntry(assertion, message, (CheckContext)null));
	}

	protected void info(String message) {
		info(new InfoEntry(message));
	}
	
	protected void info(AbstractCheckEntry entry) {
		out.println(entry);
        DetailedOutput details = new DetailedOutput();
        details.setDescription(entry.toLine());
        outputs.add(new Output(OutputType.info, entry.toLine()));
	}
	

	@Override
	public int resultsOK() {
		return numOK;
	}

	@Override
	public int resultsFinding() {
		return numIssues;
	}

	@Override
	public void init(CheckOutput out) {
        factory = null;
        builder = null;
        doc = null;
        numIssues = 0;
		numOK = 0;
		this.out = out;
        this.outputs.clear();
	}

    protected void loadFile(File file) throws Throwable {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            loadResource(is);
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                ;
            }
        }
    }

    protected void loadResource(InputStream is) throws Throwable {
        factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        builder = factory.newDocumentBuilder();
        doc = builder.parse(is);
    }

    protected void addIssues(int number) {
		numIssues += number;
	}

    public List<? extends Output> getOutputs() {
        return Collections.unmodifiableList(outputs);
    }
}
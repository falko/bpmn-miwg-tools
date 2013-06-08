package org.omg.bpmn.miwg.xpathTestRunner.tests;

import java.io.File;

import org.omg.bpmn.miwg.xpathTestRunner.testBase.AbstractXpathTest;
import org.w3c.dom.Node;

public class A_3_0_Test extends AbstractXpathTest {

	@Override
	public String getName() {
		return "A.3.0";
	}

	@Override
	public boolean isApplicable(File file) {
		String fn = file.getName();
		return fn.equals("A.3.0-roundtrip.bpmn") || fn.equals("A.3.0.bpmn");
	}

	@Override
	public void execute(File file) throws Throwable {

		{
			loadFile(file);

			selectElement("/bpmn:definitions/bpmn:process");

			navigateElement("bpmn:startEvent", "Start Event");

			navigateFollowingElement("bpmn:task", "Task 1");

			Node n1 = navigateFollowingElement("bpmn:subProcess",
					"Collapsed Sub-Process");

			navigateBoundaryEvent("Boundary Intermediate Event Non-Interrupting Message");
			checkMessageEvent();
			checkCancelActivity(false);

			navigateFollowingElement("bpmn:task", "Task 3");
			
			navigateFollowingElement("bpmn:endEvent", "End Event 1");

			navigateElement(n1);

			navigateFollowingElement("bpmn:task", "Task 2");

			navigateFollowingElement("bpmn:endEvent", "End Event 1");
			
			navigateElement(n1);
			
			navigateBoundaryEvent("Boundary Intermediate Event Interrupting Escalation");
			checkEscalationEvent();
			checkCancelActivity(true);
			
			navigateFollowingElement("bpmn:task", "Task 4");
			
			navigateFollowingElement("bpmn:endEvent", "End Event 2");

			pop();

		}
	}

}

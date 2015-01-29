BPMN MIWG Tools
===============

Tools for validating and comparing [test results](https://github.com/bpmn-miwg/bpmn-miwg-test-suite) collected by the BPMN Model Interchange Workging Group (BPMN MIWG) at the OMG.

[Test Reports](http://bpmn-miwg.github.io/bpmn-miwg-tools):

[<img height="400" src="http://bpmn-miwg.github.io/bpmn-miwg-tools/bpmn-tools-tested-for-model-interchange-screenshot.png">](http://bpmn-miwg.github.io/bpmn-miwg-tools/)

This work is licensed under the MIT License (see LICENSE.txt).

Running the tools on a private set of vendor files
--------------------------------------------------
Sometimes vendors may wish to run the tools privately before publishing a submission to the group. If this is what you want please read this alternative [README](https://github.com/bpmn-miwg/bpmn-miwg-tools/blob/master/bpmn-miwg-maven-plugin/README.md)

Publishing new results
----------------------
If you are one of the team that maintains the tools and the web site of results this is the checklist of what is involved. 

1. Ensure you have Maven installed
2. Ensure you have the latest tools in your local Maven repository (this is a temporary step until we set up a stable maven repo for the tools). If you have not done this previously then follow these commands: 
```
   git clone https://github.com/bpmn-miwg/bpmn-miwg-tools.git
   cd bpmn-miwg-tools
   mvn clean install 
```
3. Clone / Pull the latest test suite (reference and vendor files) from this repo: https://github.com/bpmn-miwg/bpmn-miwg-test-suite
4. Run all the tools, generate reports and push to the Maven repo with this Maven command:
```
   # cd to the dir you just cloned the repo to
   mvn clean deploy 
```
5. Pull the reports into the site which is hosted on the gh-pages branch of this repo. 
```
   mvn process-resources
```
6. Update the json files that hold statistics for the table displayed on the site. NOTE: This currently only works on Linux and the Mac command line tools are not compatible.
```
   . ./update-json-data.sh
```
7. Commit and push the latest site.

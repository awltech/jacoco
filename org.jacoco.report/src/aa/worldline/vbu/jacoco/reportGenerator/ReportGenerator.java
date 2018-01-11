/*******************************************************************************
 * Copyright (C) 2014-2017 Atos Worldline
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Vincent Blondeau
 *    
 *******************************************************************************/
package aa.worldline.vbu.jacoco.reportGenerator;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.tools.ExecFileLoader;
import org.jacoco.report.DirectorySourceFileLocator;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.xml.XMLFormatter;

/**
 * This example creates a HTML report for eclipse like projects based on a
 * single execution data store called jacoco.exec. The report contains no
 * grouping information.
 * 
 * The class files under test must be compiled with debug information, otherwise
 * source highlighting will not work.
 */
public class ReportGenerator {

	private final String title;

	private final File executionDataFile;
	private final File classesDirectory;
	private final File sourceDirectory;
	private final File reportDirectory;
	private final String reportFileName;

	static String root;

	// String classDir =
	// "C:/Data/Cedicam/mavenrepo/net/atos/wlp/cedicam/opc/wlp-cedicam-opc-provider/5.5.2.024.022-SNAPSHOT/wlp-cedicam-opc-provider-5.5.2.024.022-SNAPSHOT.jar";
	String sources = "src";
	String classpath = "classpath.cp";
	String compiledClasses = "target";

	private ExecFileLoader execFileLoader;

	/**
	 * Create a new generator based for the given project.
	 * 
	 * @param projectDirectory
	 * @param execFile
	 */
	public ReportGenerator(final File projectDirectory, final String execFile) {
		this.title = projectDirectory.getName();
		this.executionDataFile = new File(projectDirectory, "/EXEC_REPORTS/"
				+ execFile);
		this.classesDirectory = new File(projectDirectory, compiledClasses);
		this.sourceDirectory = new File(projectDirectory, sources);
		reportFileName = execFile;
		this.reportDirectory = new File(projectDirectory, "/XML_REPORTS/");
		this.reportDirectory.mkdir();
	}

	/**
	 * Create the report.
	 * 
	 * @throws IOException
	 */
	public void create() throws IOException {

		// Read the jacoco.exec file. Multiple data files could be merged
		// at this point
		loadExecutionData();

		// Run the structure analyzer on a single class folder to build up
		// the coverage model. The process would be similar if your classes
		// were in a jar file. Typically you would create a bundle for each
		// class folder and each jar you want in your report. If you have
		// more than one bundle you will need to add a grouping node to your
		// report
		final IBundleCoverage bundleCoverage = analyzeStructure();

		createReport(bundleCoverage);

	}

	private void createReport(final IBundleCoverage bundleCoverage)
			throws IOException {

		// Create a concrete report visitor based on some supplied
		// configuration. In this case we use the defaults
		final XMLFormatter xmlFormatter = new XMLFormatter();
		final IReportVisitor visitor = xmlFormatter
				.createVisitor(new BufferedOutputStream(new FileOutputStream(
						root + "/XML_REPORTS/" + reportFileName + ".xml")));

		// Initialize the report with all of the execution and session
		// information. At this point the report doesn't know about the
		// structure of the report being created
		visitor.visitInfo(execFileLoader.getSessionInfoStore().getInfos(),
				execFileLoader.getExecutionDataStore().getContents());

		// Populate the report structure with the bundle coverage information.
		// Call visitGroup if you need groups in your report.
		visitor.visitBundle(bundleCoverage, new DirectorySourceFileLocator(
				sourceDirectory, "utf-8", 4));

		// Signal end of structure information to allow report to write all
		// information out
		visitor.visitEnd();

	}

	private void loadExecutionData() throws IOException {
		execFileLoader = new ExecFileLoader();
		execFileLoader.load(executionDataFile);
	}

	private IBundleCoverage analyzeStructure() throws IOException {
		final CoverageBuilder coverageBuilder = new CoverageBuilder();
		final Analyzer analyzer = new Analyzer(
				execFileLoader.getExecutionDataStore(), coverageBuilder);

		/*
		 * Scanner scanner = new Scanner(aLine); scanner.useDelimiter("="); if
		 * (scanner.hasNext()){ //assumes the line has a certain structure
		 * String name = scanner.next(); String value = scanner.next();
		 * log("Name is : " + quote(name.trim()) + ", and Value is : " +
		 * quote(value.trim())); } else {
		 * log("Empty or invalid line. Unable to process."); } }
		 */
		final File cp = new File(root, classpath);
		if (!cp.exists()) {
			System.out
					.println("Jacoco report: the file .classpath.cp doesn't exists!!");
			System.exit(10);
		}
		;
		Scanner aScanner = null;
		try {
			aScanner = new Scanner(cp).useDelimiter(File.pathSeparator);
			while (aScanner.hasNext()) {
				final String jar = aScanner.next();
				try {
					analyzer.analyzeAll(new File(jar));
				} catch (final IOException e) {
					// next item
					System.out.println("[Jacoco error]Bypass " + jar + "-"
							+ e.getMessage());
				}
			}
		} finally {
			aScanner.close();
		}

		analyzer.analyzeAll(classesDirectory);
		return coverageBuilder.getBundle(title);
	}

	/**
	 * Starts the report generation process
	 * 
	 * @param args
	 *            Arguments to the application. This will be the location of the
	 *            eclipse projects that will be used to generate reports for
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		// first arg : project module directory: should contains:
		// in EXEC_REPORTS all the items resulting of the tests analysis from
		// jacocoagent
		// in classpath.cp the classpath used to build the application
		// in src the sources of the programm
		// The results will be published in XML_REPORTS
		System.out.println("[Jacoco report]Begin treatment");
		root = args[0];
		System.out.println("[Jacoco report]The directory used is:" + root);
		final File[] files = new File(root + "/EXEC_REPORTS").listFiles();
		if (files != null) {
			for (final File f : files) {
				System.out.println("[Jacoco report]File:" + f);
				final ReportGenerator generator = new ReportGenerator(new File(
						root), f.getName());
				generator.create();
			}
		}
	}
}

/*******************************************************************************
 * Copyright (c) 2009, 2014 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Evgeny Mandrikov - initial API and implementation
 *
 *******************************************************************************/
package org.jacoco.maven;

import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import aa.worldline.vbu.jacoco.reportGenerator.ReportGenerator;

/**
 * @author a577142
 * @goal ReportJacocoExperiement
 * @phase package
 */
public class ReportJacocoMojo extends AbstractJacocoMojo {

	/**
	 * File with execution data.
	 * 
	 * @parameter property="jacoco.projectFolder"
	 *            default-value="${project.basedir}"
	 */
	String projectFolder;

	/**
	 * @return
	 */
	public String getProjectFolder() {
		return projectFolder;
	}

	/**
	 * @param projectFolder
	 */
	public void setProjectFolder(final String projectFolder) {
		this.projectFolder = projectFolder;
	}

	@Override
	protected void executeMojo() throws MojoExecutionException,
			MojoFailureException {
		final String[] args = { projectFolder };
		try {
			ReportGenerator.main(args);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}

/*******************************************************************************
 * Copyright (c) 2005, 2016 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.ui.examples;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;

import org.eclipse.text.edits.TextEdit;

import org.eclipse.jface.text.Document;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

import org.eclipse.jdt.launching.JavaRuntime;

import org.eclipse.jdt.internal.corext.dom.IASTSharedValues;

public class ASTRewriteSnippet extends TestCase {

	public void testASTRewriteExample() throws Exception {
		// create a new project
		IProject project= ResourcesPlugin.getWorkspace().getRoot().getProject("Test");
		project.create(null);
		project.open(null);
		try {
			// set the Java nature and Java build path
			IProjectDescription description = project.getDescription();
			description.setNatureIds(new String[] { JavaCore.NATURE_ID } );
			project.setDescription(description, null);

			IJavaProject javaProject= JavaCore.create(project);

			// build path is: project as source folder and JRE container
			IClasspathEntry[] cpentry= new IClasspathEntry[] {
					JavaCore.newSourceEntry(javaProject.getPath()),
					JavaRuntime.getDefaultJREContainerEntry()
			};
			javaProject.setRawClasspath(cpentry, javaProject.getPath(), null);
			Map<String, String> options= new HashMap<>();
			options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, JavaCore.SPACE);
			options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, "4");
			javaProject.setOptions(options);

			// create a test file
			IPackageFragmentRoot root= javaProject.getPackageFragmentRoot(project);
			IPackageFragment pack1= root.createPackageFragment("test1", false, null);
			StringBuffer buf= new StringBuffer();
			buf.append("package test1;\n");
			buf.append("public class E {\n");
			buf.append("    public void foo(int i) {\n");
			buf.append("        while (--i > 0) {\n");
			buf.append("            System.beep();\n");
			buf.append("        }\n");
			buf.append("    }\n");
			buf.append("}\n");
			ICompilationUnit cu= pack1.createCompilationUnit("E.java", buf.toString(), false, null);

			// create an AST
			ASTParser parser= ASTParser.newParser(IASTSharedValues.SHARED_AST_LEVEL);
			parser.setSource(cu);
			parser.setResolveBindings(false);
			CompilationUnit astRoot= (CompilationUnit) parser.createAST(null);
			AST ast= astRoot.getAST();

			// create the descriptive ast rewriter
			ASTRewrite rewrite= ASTRewrite.create(ast);

			// get the block node that contains the statements in the method body
}}}
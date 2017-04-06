package com.aj.jeez.codegen;

import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletContext;

import com.aj.jeez.codegen.exceptions.ClassPathScannerNotConfiguredException;

public class ClassPathScanner {	 

	static String rootPackageName;
	static String classesPath;
	
	static Set<String> getClassesQualifiedNames(
			String packagePath,
			ServletContext context,
			Set<String> classesQNSet
			)throws ClassPathScannerNotConfiguredException {
		
		isConfigured();
		
		String rootPackageRelativePath = rootPackageName.replace(".", "/");

		Set<String> resourceSet = context.getResourcePaths(packagePath);
		System.out.println("ClassPathScanner/getClassFiles::resourceSet('"+classesPath+rootPackageName+"') = "+resourceSet);//debug
		if (resourceSet != null) 
			for (Iterator<String> iterator = resourceSet.iterator(); iterator.hasNext();) {
				String resourcePath = (String) iterator.next();
				if (resourcePath.endsWith(".class")){
					String classRelativePath = resourcePath.substring(resourcePath.indexOf(rootPackageRelativePath));
					classesQNSet.add(classRelativePath.replace("/", ".").substring(0,classRelativePath.indexOf(".class")));
				}else 
					getClassesQualifiedNames(resourcePath, context, classesQNSet);	
			}
		return classesQNSet;
	}

	static void isConfigured() throws ClassPathScannerNotConfiguredException {
		if(classesPath==null)
		throw new ClassPathScannerNotConfiguredException("classesPath is not configured");
		if(rootPackageName==null)
			throw new ClassPathScannerNotConfiguredException("packageName is not configured");
	}
	
	static void configure(
			String classesPath,
			String packageName
			) {
		ClassPathScanner.classesPath=classesPath;
		ClassPathScanner.rootPackageName=packageName;
	}

}

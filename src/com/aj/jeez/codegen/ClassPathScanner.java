package com.aj.jeez.codegen;

import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletContext;

public class ClassPathScanner {	 

	static String rootPackageName;
	static String classesPath;
	
	static Set<String> getClasses(
			String rootPackageAbsolutePath,
			ServletContext context,
			Set<String> finalList
			)throws ClassPathScannerNotConfiguredException {
		
		isConfigured();
		
		String rootPackageRelativePath = rootPackageName.replace(".", "/");

		Set<String> resourceSet = context.getResourcePaths(rootPackageAbsolutePath);
		System.out.println("ClassPathScanner/getClassFiles::resourceSet('"
		+classesPath+rootPackageName+"') = "+resourceSet);
		if (resourceSet != null) 
			for (Iterator<String> iterator = resourceSet.iterator(); iterator.hasNext();) {
				String resourcePath = (String) iterator.next();
				if (resourcePath.endsWith(".class")){
					String classRelativePath=resourcePath.substring(resourcePath.indexOf(rootPackageRelativePath));
					finalList.add(
							classRelativePath
							.replace("/", ".")
							.substring(0, classRelativePath.indexOf(".class"))
							);
				}else 
					getClasses(resourcePath, context, finalList);	
			}
		return finalList;
	}

	private static void isConfigured() throws ClassPathScannerNotConfiguredException {
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

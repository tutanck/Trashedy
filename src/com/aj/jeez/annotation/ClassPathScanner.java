package com.aj.jeez.annotation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletContext;

import com.aj.jeez.annotation.exceptions.ClassPathScannerNotConfiguredException;

/**
 * The ClassPathScanner is configurable a built class (.class extension) finder.
 * @author ANAGBLA Joan */
public class ClassPathScanner {	 

	private static String classPath;
	private static String rootPackageQN; //ex: com.aj.mood
	private static String rootPackagePath; //ex: com/aj/mood
	
	/**
	 * Return all the classes qualified names 
	 * found in the {classPath}/{rootPackageName}
	 * @param packagePath
	 * @param context
	 * @param classesQNSet
	 * @return
	 * @throws ClassPathScannerNotConfiguredException */
	static Set<String> getClassesQualifiedNames(
			ServletContext context
			)throws ClassPathScannerNotConfiguredException {
		
		isConfigured();
		
		System.out.println("ClassPathScanner/getClassesQualifiedNames:: start browsing classes... ");//debug
		Set<String> clssStrSet= getClassesQNRec(classPath+rootPackageQN, context, new HashSet<>());
		System.out.println("ClassPathScanner/getClassesQualifiedNames:: found "+clssStrSet.size()+" class files. ");//debug
		return clssStrSet;
	}
	
	
	
	/**
	 * Browse recursively then return all the classes qualified names 
	 * found in the {classPath}/{rootPackageName}
	 * @param packagePath
	 * @param context
	 * @param classesQNSet
	 * @return */
	private static Set<String> getClassesQNRec(
			String packagePath,
			ServletContext context,
			Set<String> classesQNSet){
		Set<String> resourceSet = context.getResourcePaths(packagePath);
		System.out.println("ClassPathScanner/getClassesQNRec::resourceSet('"+packagePath+"') = "+resourceSet);//debug
		if (resourceSet != null) 
			for (Iterator<String> iterator = resourceSet.iterator(); iterator.hasNext();) {
				String resourcePath = (String) iterator.next();
				if (resourcePath.endsWith(".class")){
					String classRelativePath = resourcePath.substring(resourcePath.indexOf(rootPackagePath));
					classesQNSet.add(classRelativePath.replace("/", ".").substring(0,classRelativePath.indexOf(".class")));
				}else 
					getClassesQNRec(resourcePath, context, classesQNSet);	
			}
		return classesQNSet;
	}
	
		

	/**
	 * Allow to know if the ClassPathScanner is initialized :
	 * i.e. if the {classPath} and {rootPackageName} 
	 * attributes are both initialized(not null)
	 * @throws ClassPathScannerNotConfiguredException */
	static void isConfigured() throws ClassPathScannerNotConfiguredException {
		if(classPath==null || classPath.length()==0)
		throw new ClassPathScannerNotConfiguredException("classPath is not configured");
		if(rootPackageQN==null || rootPackageQN.length()==0)
			throw new ClassPathScannerNotConfiguredException("rootPackageName is not configured");
	}
	
	/**
	 * Allow to configure the ClassPathScanner by setting
	 * both the {classPath} and {rootPackageName} attributes
	 * @param classPath
	 * @param rootPackageName */
	static void configure(
			String classPath,
			String rootPackageName
			) {
		ClassPathScanner.classPath=classPath;
		ClassPathScanner.rootPackageQN=rootPackageName;
		ClassPathScanner.rootPackagePath = 
				ClassPathScanner.rootPackageQN.replace(".", "/");
	}

}

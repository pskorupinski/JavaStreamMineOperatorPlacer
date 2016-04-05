package org.microcloud.manager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Factory {

	private static Factory factory = null;
	private static Properties properties = null;
	private static Properties templates = null;

	public static boolean createFactory(Properties prop) {
		Factory.properties = prop;
		if(factory != null) return false;
		else {
			Factory.factory = new Factory();
			Factory.readTemplateProps();
			return true;
		}
	}
	
	public static boolean createFactory() {
		if(factory != null) return false;
		else {
			Factory.factory = new Factory();
			Factory.readTemplateProps();
			return true;
		}
	}
	
	public static boolean changeFactoryProperties(Properties prop) {
		Factory.properties = prop;
		return true;
	}
	
	public static Factory getInstance() {
		return Factory.factory;
	}
	
	private static void readTemplateProps() {
		InputStream is;
		try {
			is = Factory.class.
					getResourceAsStream("config/template.properties");
			templates = new Properties();
			templates.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Factory() {
		// TODO Auto-generated constructor stub
	}
	
    public Object newInstance(String abstractName, Class<?>[] paramTypes, Object[] params) {
        Object obj = null;

        try {
            String changeWord = (String) Factory.properties.get(abstractName);
            String templateName = (String) Factory.templates.get(abstractName);
            String classPath = templateName.replaceAll("Templname", changeWord);
            
            Class<?> cls = Class.forName(classPath);
            if (cls == null) {
                throw new RuntimeException("No class registered under " +
                        abstractName);
            }

            Constructor<?> ctor = cls.getConstructor(paramTypes);
            obj = ctor.newInstance(params);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return obj;
    }
    
    public List<Object> newInstances(String abstractName, Class<?>[] paramTypes, Object[] params) {
        List<Object> objs = new ArrayList<>();

        try {
            String implementationString = (String) Factory.properties.get(abstractName);
            String [] implementations = implementationString.split(",");

            String templateName = (String) Factory.templates.get(abstractName);
            
            for(String changeWord : implementations) {           
	            String classPath = templateName.replaceAll("Templname", changeWord);
	            
	            Class<?> cls = Class.forName(classPath);
	            if (cls == null) {
	                throw new RuntimeException("No class registered under " +
	                        abstractName);
	            }
	
	            Constructor<?> ctor = cls.getConstructor(paramTypes);
	            objs.add(ctor.newInstance(params));
	        }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return objs;
    }   
    
//    public Integer getImplementationsNumber(String abstractName) {
//    	String implementationString = (String) Factory.properties.get(abstractName); 
//    	return countOccurrences(implementationString, ',') + 1;
//    }
    
    public String getConstant(String constantName) {
    	String constant = (String) Factory.properties.get(constantName);
    	
    	return constant;
    }
    
    public boolean getBoolean(String propertyName) {
    	String value = (String) Factory.properties.getProperty(propertyName, "false");
    	return Boolean.parseBoolean(value);
    }
    
//    private int countOccurrences(String haystack, char needle) {
//        int count = 0;
//        for (int i=0; i < haystack.length(); i++)
//        {
//            if (haystack.charAt(i) == needle)
//            {
//                 count++;
//            }
//        }
//        return count;
//    }

}

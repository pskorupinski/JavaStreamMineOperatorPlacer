package org.microcloud.manager.logger;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

public class MyLogger {
	
	private static MyLogger mylogger = null;
	
	static Logger logger4jD;
	static Logger logger4jI;

	public static void newInstance(String name) {
		try {
			mylogger = new MyLogger(name);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("File for logger not opened! Probably you have not enough rights.");
		}
	}
	
	public static MyLogger getInstance() {
		if(mylogger == null)
			try {
				mylogger = new MyLogger(null);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		
		return mylogger;
	}
	
	FileOutputStream fileOutputStream = null;
	FileOutputStream fileOutputStreamMain = null;
	
	public MyLogger(String name) throws FileNotFoundException {
		if(name != null){
			DateFormat df = new SimpleDateFormat("yyMMdd-HHmmss");
			Date today = Calendar.getInstance().getTime();
			String now = df.format(today);
			
//			SimpleLayout layout = new SimpleLayout();    
//		    FileAppender appender;
//			try {
//				appender = new FileAppender(layout,"/data/logs/log-" + now + "-" + name,false);
//			    logger4j.addAppender(appender);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				System.exit(2);
//			}    
			
			System.setProperty("logfile.name",  "/data/logs/log-" + now + "-" + name);
			System.setProperty("logfile.name-m","/data/logs/log-" + now + "-" + name + "-m");
			
			logger4jD = Logger.getLogger("DebugLogger");
			
//			fileOutputStream = new FileOutputStream("/data/logs/log-" + now + "-" + name, true);
//			fileOutputStreamMain = new FileOutputStream("/data/logs/log-" + now + "-" + name + "-m", true);
		}
	}
	
	public void log(Object log) {
		
		logger4jD.trace(log);	

//		System.out.println(log);
//		if(fileOutputStream != null) {
//			try {
//				String fullLog;
//				String strLog = log.toString().replace("\n", "\n\t\t");
//				if(log.equals(""))
//					fullLog = "";
//				else
//					fullLog = new Date().toString() + "\t" + strLog.toString() + "\n";
//				fileOutputStream.write(fullLog.getBytes());
//				fileOutputStream.flush();
//			} catch (IOException e) {
//				e.printStackTrace();
//				System.exit(2);
//			}
//		}
	}
	
	public void logm(Object log) {
		
		logger4jD.info(log);
		
//		System.err.println(log);
//		
//		String fullLog;
//		String strLog = log.toString().replace("\n", "\n\t\t");
//		if(log.equals(""))
//			fullLog = "";
//		else
//			fullLog = new Date().toString() + "\t" + strLog.toString() + "\n";
//		
//		try {
//			if(fileOutputStream != null) {
//				fileOutputStream.write(fullLog.getBytes());
//				fileOutputStream.flush();
//			}
//			
//			if(fileOutputStreamMain != null) {
//				fileOutputStreamMain.write(fullLog.getBytes());
//				fileOutputStreamMain.flush();
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//			System.exit(2);
//		}
	}	
	
	public void close() {
		if(fileOutputStream != null) {
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(2);
			}
		}
		
		if(fileOutputStreamMain != null) {
			try {
				fileOutputStreamMain.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(2);
			}
		}
	}
}

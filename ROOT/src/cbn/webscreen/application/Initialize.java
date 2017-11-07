package cbn.webscreen.application;

import java.io.File;
import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import cbn.webscreen.data.persistance.entitymanager.LoginAccessEntityManager;
import cbn.webscreen.data.persistance.entitymanager.LoginEntityManager;
import cbn.webscreen.job.ScreenCleanScheduler;
import cbn.webscreen.job.UpdateCleanScheduler;

@WebListener
public class Initialize implements ServletContextListener {

	private static Logger logger = Logger.getLogger(Initialize.class);
	
	static {
		
		setServerHome();
		
		initLogger();
		
		createTables();
	}
	
    @Override
    public final void contextInitialized(final ServletContextEvent sce) {
    	ScreenCleanScheduler.init();
    	UpdateCleanScheduler.init();
    }


	@Override
    public final void contextDestroyed(final ServletContextEvent sce) {
		ScreenCleanScheduler.destroy();
    	UpdateCleanScheduler.destroy();
    }

	private static void setServerHome() {
		
		String serverHome = null;
		
//		// custom
//		serverHome = "/myserver";
		
		// support tomcat
		if (serverHome == null) {
			serverHome = System.getProperty("catalina.base");
		}
		
//		// support other
//		if (serverHome == null) {
//			serverHome = System.getProperty("some.home");
//		}
		
		if (serverHome == null) {
			serverHome = System.getProperty("user.home");
		}
		
		System.setProperty("server.home", serverHome);
		
	}
    
    private static void initLogger() {
    	
    	// creates pattern layout
    	PatternLayout layout = new PatternLayout();
    	String conversionPattern = "%-7p %d [%t] %c - %m%n";
    	layout.setConversionPattern(conversionPattern);
    	
    	// creates console appender
    	ConsoleAppender consoleAppender = new ConsoleAppender();
    	consoleAppender.setLayout(layout);
    	consoleAppender.activateOptions();
    	
    	// creates file appender
    	FileAppender fileAppender = new FileAppender();
    	fileAppender.setFile(System.getProperty("server.home") 
    			+ File.separator + "logs" 
    			+ File.separator + "server.log");
    	fileAppender.setLayout(layout);
    	fileAppender.activateOptions();
    	
    	// configures the root logger
    	Logger rootLogger = Logger.getRootLogger();
    	rootLogger.setLevel(Level.INFO);
    	rootLogger.addAppender(consoleAppender);
    	rootLogger.addAppender(fileAppender);
    }
    
    private static void createTables() {
    	try {
    		Class.forName("org.sqlite.JDBC");
    		
			LoginEntityManager.createTable();
			LoginAccessEntityManager.createTable();
			
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
		}
    }
}

package cbn.webscreen.job;

import org.apache.log4j.Logger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class ScreenCleanScheduler {
	
	private static Logger logger = Logger.getLogger(ScreenCleanScheduler.class);
	
	private static Scheduler scheduler = null;
	
	private static boolean running = false;
	
	public static void init() {
		
		if (running) {
			return;
		}
		
		running = true;
		
		try {
			
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			
			Trigger trigger = TriggerBuilder
					.newTrigger()
					.withSchedule(SimpleScheduleBuilder.repeatSecondlyForever())
					.build();
			
			JobDetail job = JobBuilder.newJob(ScreenCleanJob.class).build();
			
			scheduler.scheduleJob(job, trigger);
			
			scheduler.start();
			
			
		} catch (SchedulerException e) {
			logger.error(e);
		}
		
	}
	
	public static void destroy() {
		try {
			if (scheduler != null) {
				scheduler.shutdown(true);
				running = false;
			}
		} catch (SchedulerException e) {
			logger.error(e);
		}
	}

}

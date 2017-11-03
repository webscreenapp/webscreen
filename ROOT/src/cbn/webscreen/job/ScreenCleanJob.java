package cbn.webscreen.job;

import org.apache.log4j.Logger;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

import cbn.webscreen.data.inmemory.ScreenData;
import cbn.webscreen.data.inmemory.Updates;

public class ScreenCleanJob implements InterruptableJob {
	
	private final long SCREEN_TIMEOUT = 10000;

	Thread thread = null;
	
	Logger logger = Logger.getLogger(ScreenCleanJob.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		for (String screenId : ScreenData.screenData.keySet()) {
			
			ScreenData.Screen screen = ScreenData.screenData.get(screenId);
			if (screen.aliveAt < System.currentTimeMillis() - SCREEN_TIMEOUT) {
				ScreenData.screenData.remove(screenId);
				Updates.addScreenWebUpdate(screenId, "screen.close");
			}
		}
		
	}
	
	@Override
	public void interrupt() throws UnableToInterruptJobException {
		thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new UnableToInterruptJobException(e);
        }
	}
	
}

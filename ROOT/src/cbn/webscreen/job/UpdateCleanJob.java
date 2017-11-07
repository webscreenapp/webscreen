package cbn.webscreen.job;

import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

import cbn.webscreen.data.inmemory.Updates;
import cbn.webscreen.data.inmemory.Updates.Update;

public class UpdateCleanJob implements InterruptableJob {

	private final long UPDATE_TIMEOUT = 10000;
	
	private Thread thread = null;
	
	private static Logger logger = Logger.getLogger(UpdateCleanJob.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		for (Update update : Updates.globalWebUpdates) {
			if (update.timestamp < System.currentTimeMillis() - UPDATE_TIMEOUT) {
				Updates.globalWebUpdates.remove(update);
			}
		}
		
		for (String login : Updates.loginWebUpdates.keySet()) {
			List<Update> list = Updates.loginWebUpdates.get(login);
			for (Update update : list) {
				if (update.timestamp < System.currentTimeMillis() - UPDATE_TIMEOUT) {
					list.remove(update);
				}
			}
		}
		
		for (String screenId : Updates.screenWebUpdates.keySet()) {
			List<Update> list = Updates.screenWebUpdates.get(screenId);
			for (Update update : list) {
				if (update.timestamp < System.currentTimeMillis() - UPDATE_TIMEOUT) {
					list.remove(update);
				}
			}
		}

		for (String screenId : Updates.screenAppUpdates.keySet()) {
			List<Update> list = Updates.screenAppUpdates.get(screenId);
			for (Update update : list) {
				if (update.timestamp < System.currentTimeMillis() - UPDATE_TIMEOUT) {
					list.remove(update);
				}
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

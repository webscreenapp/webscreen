package cbn.webscreen.job;

import org.apache.log4j.Logger;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

import cbn.webscreen.data.inmemory.Updates;

public class UpdateCleanJob implements InterruptableJob {

	private Thread thread = null;
	
	private static Logger logger = Logger.getLogger(UpdateCleanJob.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		Updates.cleanUpdates();
		
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

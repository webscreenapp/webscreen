package cbn.webscreen.job;

import org.apache.log4j.Logger;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

public class UpdateCleanJob implements InterruptableJob {

	Thread thread = null;
	
	Logger logger = Logger.getLogger(UpdateCleanJob.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.warn("Not implemented.");
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

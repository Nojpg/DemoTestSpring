package packageTest;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;

/**
 * Created by Nojpg on 15.09.17.
 */
public class StepExecutionStatsListener extends StepExecutionListenerSupport {
    @Override
    public ExitStatus afterStep(StepExecution stepExecution){
        System.out.println("Wrote " + stepExecution.getWriteCount() + " items in step: " + stepExecution.getStepName());
        return null;
    }
}

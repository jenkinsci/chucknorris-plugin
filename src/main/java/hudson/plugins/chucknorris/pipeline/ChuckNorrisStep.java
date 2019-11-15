package hudson.plugins.chucknorris.pipeline;

import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;

public class ChuckNorrisStep extends Step {

	@DataBoundConstructor
	public ChuckNorrisStep() {
	}

	@Override
	public StepExecution start(StepContext stepContext) throws Exception {
		return stepContext.get(ChuckNorrisStepExecution.class);
	}

	@Override
	public StepDescriptor getDescriptor() {
		return new ChuckNorrisStepDescriptor();
	}

}
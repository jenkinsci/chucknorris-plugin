package hudson.plugins.chucknorris.pipeline;

import hudson.Extension;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;

public class ChuckNorrisStep extends Step {

	@DataBoundConstructor
	public ChuckNorrisStep() {
	}

	@Override
	public StepExecution start(StepContext stepContext) throws Exception {
		return stepContext.get(ChuckNorrisStepExecution.class);
	}

	@Extension
	public static class ChuckNorrisStepDescriptor extends StepDescriptor {

		@Override
		public Set<? extends Class<?>> getRequiredContext() {
			return Collections.singleton(ChuckNorrisStepExecution.class);
		}

		@Override
		public String getFunctionName() {
			return "chuckNorris";
		}

		@Nonnull
		@Override
		public String getDisplayName() {
			return "Submit to Chuck Norris' will";
		}

	}

}
package hudson.plugins.chucknorris.pipeline;

import com.google.common.collect.ImmutableSet;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.util.Set;

public class ChuckNorrisStep extends Step {

	@DataBoundConstructor
	public ChuckNorrisStep() {
		//DataBoundConstructor
	}

	@Override
	public StepExecution start(StepContext stepContext) throws Exception {
		return new ChuckNorrisStepExecution(stepContext);
	}

	@Override
	public StepDescriptor getDescriptor() {
		return new ChuckNorrisStepDescriptor();
	}

	@Extension
	public static class ChuckNorrisStepDescriptor extends StepDescriptor {

		public ChuckNorrisStepDescriptor() {
			//Descriptor constructor
		}

		@Override
		public Set<? extends Class<?>> getRequiredContext() {
			return ImmutableSet.of(Run.class, TaskListener.class);
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
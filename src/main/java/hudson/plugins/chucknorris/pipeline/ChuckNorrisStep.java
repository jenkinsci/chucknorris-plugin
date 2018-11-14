package hudson.plugins.chucknorris.pipeline;

import javax.annotation.Nonnull;

import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

public class ChuckNorrisStep extends AbstractStepImpl {

	@DataBoundConstructor
	public ChuckNorrisStep() {
	}

	@Extension
	public static class DescriptorImpl extends AbstractStepDescriptorImpl {
		public DescriptorImpl() {
			super(ChuckNorrisStepExecution.class);
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
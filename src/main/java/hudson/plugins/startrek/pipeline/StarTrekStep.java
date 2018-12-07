package hudson.plugins.startrek.pipeline;

import javax.annotation.Nonnull;

import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

public class StarTrekStep extends AbstractStepImpl {

	@DataBoundConstructor
	public StarTrekStep() {
	}

	@Extension
	public static class DescriptorImpl extends AbstractStepDescriptorImpl {
		public DescriptorImpl() {
			super(StarTrekStepExecution.class);
		}

		@Override
		public String getFunctionName() {
			return "StarTrek";
		}

		@Nonnull
		@Override
		public String getDisplayName() {
			return "Submit to Chuck Norris' will";
		}
	}
}
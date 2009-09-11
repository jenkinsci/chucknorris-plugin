package hudson.plugins.chucknorris;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Project;
import hudson.tasks.Recorder;

import java.io.IOException;
import java.util.logging.Logger;

import org.kohsuke.stapler.DataBoundConstructor;

public class CordellWalkerRecorder extends Recorder {

	private static final Logger LOGGER = Logger
			.getLogger(CordellWalkerRecorder.class.getName());
	private FactGenerator factGenerator;

	@DataBoundConstructor
	public CordellWalkerRecorder() {
		this(new FactGenerator());
	}

	public CordellWalkerRecorder(FactGenerator factGenerator) {
		this.factGenerator = factGenerator;
		LOGGER.info("Chuck Norris is activated");
	}

	public Action getProjectAction(Project project) {
		Action action = null;
		if (project.getLastBuild() != null) {
			Style style = Style.get(project.getLastBuild().getResult());
			String fact = factGenerator.random();
			action = new RoundhouseAction(style, fact);
		}
		return action;
	}

	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {
		Style style = Style.get(build.getResult());
		String fact = factGenerator.random();
		build.getActions().add(new RoundhouseAction(style, fact));
		return true;
	}
}

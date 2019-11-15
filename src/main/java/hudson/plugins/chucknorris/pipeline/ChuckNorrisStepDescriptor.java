package hudson.plugins.chucknorris.pipeline;

import hudson.Extension;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;


@Extension
public class ChuckNorrisStepDescriptor extends StepDescriptor {

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
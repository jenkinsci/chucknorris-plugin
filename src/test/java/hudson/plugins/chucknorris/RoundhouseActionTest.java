package hudson.plugins.chucknorris;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;

import java.util.Arrays;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import hudson.model.Job;
import hudson.model.Result;
import hudson.model.Run;
import junit.framework.TestCase;

public class RoundhouseActionTest extends TestCase {

	private RoundhouseAction action;

	private Run<?, ?> run;
	private RoundhouseAction lastBuildAction;

	@SuppressWarnings("rawtypes")
	public void setUp() {
		action = new RoundhouseAction(Style.BAD_ASS,
				"Chuck Norris can divide by zero.");

		run = mock(Run.class);
		given(run.getResult()).willReturn(Result.SUCCESS);

		lastBuildAction = new RoundhouseAction(Style.ALERT,
				"Chuck Norris went out of an infinite loop.");
		final Job job = mock(Job.class);
		Run<?, ?> lastRun = mock(Run.class);

		given(run.getParent()).willAnswer(new Answer<Job>() {
			@Override
			public Job answer(InvocationOnMock invocation) throws Throwable {
				return job;
			}
		});
		given(job.getLastCompletedBuild()).willReturn(lastRun);
		given(lastRun.getActions(eq(RoundhouseAction.class))).willReturn(Arrays.asList(lastBuildAction));
	}

	public void testAccessors() {
		assertEquals(Style.BAD_ASS, action.getStyle());
		assertEquals("Chuck Norris can divide by zero.", action
				.getFact());
		assertEquals("Chuck Norris", action.getDisplayName());
		assertNull(action.getIconFileName());
		assertEquals("chucknorris", action.getUrlName());
	}

	public void testGetProjectActions() {
		assertNotNull(action.getProjectActions());
		assertEquals(1, action.getProjectActions().size());
		assertSame(action, action.getProjectActions().iterator().next());
	}

	public void testGetStyleFromRunResult() {
		action.onAttached(run);

		assertEquals(Style.THUMB_UP, action.getStyle());
	}

	public void testGetProjectActionsFromLastProjectBuild() {
		action.onAttached(run);

		assertNotNull(action.getProjectActions());
		assertEquals(1, action.getProjectActions().size());
		assertSame(lastBuildAction, action.getProjectActions().iterator().next());
	}
}

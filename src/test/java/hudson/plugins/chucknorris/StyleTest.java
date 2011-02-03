package hudson.plugins.chucknorris;

import hudson.model.Result;
import junit.framework.TestCase;

public class StyleTest extends TestCase {

	public void testGetWithFailureResultGivesBadAssStyle() {
		assertEquals(Style.BAD_ASS, Style.get(Result.FAILURE));
	}

	public void testGetWithSuccessResultGivesSuitupStyle() {
		assertEquals(Style.THUMB_UP, Style.get(Result.SUCCESS));
	}

	public void testGetWithAbortedResultGivesAlertStyle() {
		assertEquals(Style.ALERT, Style.get(Result.ABORTED));
	}

	public void testGetWithNotBuiltResultGivesAlertStyle() {
		assertEquals(Style.ALERT, Style.get(Result.NOT_BUILT));
	}

	public void testGetWithUnstableResultGivesAlertStyle() {
		assertEquals(Style.ALERT, Style.get(Result.UNSTABLE));
	}
}

package hudson.plugins.chucknorris;

import hudson.model.Result;

public enum Style {

	THUMB_UP, ALERT, BAD_ASS;

	public static Style get(Result result) {
		Style style;
		if (Result.FAILURE.equals(result)) {
			style = BAD_ASS;
		} else if (Result.SUCCESS.equals(result)) {
			style = THUMB_UP;
		} else {
			style = ALERT;
		}
		return style;
	}
}

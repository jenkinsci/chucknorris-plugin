package hudson.plugins.chucknorris;

import hudson.model.Action;

public class RoundhouseAction implements Action {

	private Style style;
	private String fact;

	public RoundhouseAction(Style style, String fact) {
		super();
		this.style = style;
		this.fact = fact;
	}

	public String getDisplayName() {
		return "Chuck Norris";
	}

	public String getIconFileName() {
		return null;
	}

	public String getUrlName() {
		return "chucknorris";
	}

	public Style getStyle() {
		return style;
	}

	public String getFact() {
		return fact;
	}
}

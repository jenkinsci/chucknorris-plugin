/**
 * Copyright (c) 2009 Cliffano Subagio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.plugins.chucknorris;

import hudson.model.Action;

/**
 * {@link RoundhouseAction} keeps the style and fact associated with the action.
 * For more info, please watch <a
 * href="http://www.youtube.com/watch?v=Vb7lnpk3tRY"
 * >http://www.youtube.com/watch?v=Vb7lnpk3tRY</a>
 * @author cliffano
 */
public final class RoundhouseAction implements Action {

    /**
     * The style - for backward compatibility to version 0.2.
     */
    private Style style;
    
    /**
     * The style.
     */
    private Style mStyle;

    /**
     * The fact - for backward compatibility to version 0.2.
     */
    private String fact;
    
    /**
     * The fact.
     */
    private String mFact;

    /**
     * Constructs a RoundhouseAction with specified style and fact.
     * @param style
     *            the style
     * @param fact
     *            the fact
     */
    public RoundhouseAction(final Style style, final String fact) {
        super();
        this.mStyle = style;
        this.mFact = fact;
    }

    /**
     * Gets the action display name.
     * @return the display name
     */
    public String getDisplayName() {
        return "Chuck Norris";
    }

    /**
     * This action doesn't provide any icon file.
     * @return null
     */
    public String getIconFileName() {
        return null;
    }

    /**
     * Gets the URL name for this action.
     * @return the URL name
     */
    public String getUrlName() {
        return "chucknorris";
    }

    /**
     * Gets the Chuck Norris style.
     * @return the style
     */
    public Style getStyle() {
        Style theStyle;
        if (mStyle != null) {
            theStyle = mStyle;
        } else {
            theStyle = style;
        }
        return theStyle;
    }

    /**
     * Gets the Chuck Norris fact.
     * @return the fact
     */
    public String getFact() {
        String theFact;
        if (mFact != null) {
            theFact = mFact;
        } else {
            theFact = fact;
        }
        return theFact;
    }
}

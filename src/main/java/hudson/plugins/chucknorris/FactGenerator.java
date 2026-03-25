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

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * {@link FactGenerator} provides Chuck Norris facts.
 * @author cliffano
 */
public class FactGenerator {

    private static final String BUNDLE_NAME = FactGenerator.class.getName();

    private static final Random RANDOM = new Random();

    private static final int FACT_COUNT;

    static {
        int count;
        try {
            ResourceBundle bundle =
                    ResourceBundle.getBundle(BUNDLE_NAME, Locale.ENGLISH, FactGenerator.class.getClassLoader());
            count = Integer.parseInt(bundle.getString("fact.count"));
        } catch (MissingResourceException | NumberFormatException e) {
            count = 1;
        }
        FACT_COUNT = count;
    }

    /**
     * Retrieves a random fact index.
     * @return a random index into the facts list
     */
    public int randomIndex() {
        return RANDOM.nextInt(FACT_COUNT);
    }

    /**
     * Retrieves a fact by index for the given locale.
     * @param index the fact index
     * @param locale the locale to use for lookup
     * @return the localized fact string
     */
    public static String getFact(int index, Locale locale) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale, FactGenerator.class.getClassLoader());
            return bundle.getString("fact." + index);
        } catch (MissingResourceException e) {
            return "Chuck Norris can divide by zero.";
        }
    }
}

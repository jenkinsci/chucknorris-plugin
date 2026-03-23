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

    private static final String BUNDLE_NAME = "hudson.plugins.chucknorris.facts";

    private static final String[] FACTS;

    static {
        FACTS = loadFacts();
    }

    /**
     * Random instance.
     */
    private static final Random RANDOM = new Random();

    private static String[] loadFacts() {
        try {
            ResourceBundle bundle =
                    ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault(), FactGenerator.class.getClassLoader());
            int count = Integer.parseInt(bundle.getString("fact.count"));
            String[] facts = new String[count];
            for (int i = 0; i < count; i++) {
                facts[i] = bundle.getString("fact." + i);
            }
            return facts;
        } catch (MissingResourceException | NumberFormatException e) {
            return new String[] {"Chuck Norris can divide by zero."};
        }
    }

    /**
     * Retrieves a random fact.
     * @return a random fact
     */
    public String random() {
        return FACTS[RANDOM.nextInt(FACTS.length)];
    }
}

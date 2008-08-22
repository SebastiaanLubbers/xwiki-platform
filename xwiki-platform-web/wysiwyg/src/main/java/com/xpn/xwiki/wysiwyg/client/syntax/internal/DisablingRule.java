/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.xpn.xwiki.wysiwyg.client.syntax.internal;

import com.xpn.xwiki.wysiwyg.client.syntax.ValidationRule;
import com.xpn.xwiki.wysiwyg.client.ui.XRichTextArea;

/**
 * Utility rule for disabling some of the editor's features.
 */
public class DisablingRule implements ValidationRule
{
    private String[] features;

    public DisablingRule(String[] features)
    {
        this.features = copy(features);
    }

    private String[] copy(String[] source)
    {
        // In the future, move this to an utility class or use a library method.
        String[] copy = new String[source.length];
        for (int i = 0; i < copy.length; i++) {
            copy[i] = source[i];
        }
        return copy;
    }

    /**
     * {@inheritDoc}
     * 
     * @see ValidationRule#areValid(XRichTextArea)
     */
    public boolean areValid(XRichTextArea textArea)
    {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see ValidationRule#getFeatures()
     */
    public String[] getFeatures()
    {
        return copy(features);
    }
}

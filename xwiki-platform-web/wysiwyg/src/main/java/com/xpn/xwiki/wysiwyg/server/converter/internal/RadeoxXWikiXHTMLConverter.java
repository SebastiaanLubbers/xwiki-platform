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
package com.xpn.xwiki.wysiwyg.server.converter.internal;

import com.xpn.xwiki.wysiwyg.server.converter.XHTMLConverter;
import com.xpn.xwiki.wysiwyg.server.converter.XHTMLConverterException;

public class RadeoxXWikiXHTMLConverter implements XHTMLConverter
{
    /**
     * {@inheritDoc}
     * 
     * @see XHTMLConverter#fromXHTML(String)
     */
    public String fromXHTML(String xhtml) throws XHTMLConverterException
    {
        return Html2XWikiConverter.convertHtml2XWiki(xhtml);
    }

    /**
     * {@inheritDoc}
     * 
     * @see XHTMLConverter#toXHTML(String)
     */
    public String toXHTML(String source) throws XHTMLConverterException
    {
        // XWikiContext context = getXWikiContext();
        // XWikiRadeoxRenderer renderEngine =
        // (XWikiRadeoxRenderer) context.getWiki().getRenderingEngine().getRenderer("wiki");
        // String xhtml = renderEngine.render(source, null, null, context);
        // return xhtml.replaceAll("\r", "");
        return "";
    }
}

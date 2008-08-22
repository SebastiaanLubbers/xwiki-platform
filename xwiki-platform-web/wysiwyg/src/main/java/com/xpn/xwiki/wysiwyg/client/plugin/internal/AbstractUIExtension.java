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
package com.xpn.xwiki.wysiwyg.client.plugin.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.ui.UIObject;
import com.xpn.xwiki.wysiwyg.client.plugin.UIExtension;

/**
 * Abstract implementation of the {@link UIExtension} interface. This could serve as a base class for all kind of user
 * interface extensions. It offers the possibility of adding and removing user interface features.
 */
public abstract class AbstractUIExtension implements UIExtension
{
    private final Map<String, UIObject> uiObjects;

    private final String role;

    public AbstractUIExtension(String role)
    {
        this.role = role;
        uiObjects = new HashMap<String, UIObject>();
    }

    /**
     * {@inheritDoc}
     * 
     * @see UIExtension#getFeatures()
     */
    public String[] getFeatures()
    {
        Set<String> features = uiObjects.keySet();
        return features.toArray(new String[features.size()]);
    }

    /**
     * {@inheritDoc}
     * 
     * @see UIExtension#getRole()
     */
    public String getRole()
    {
        return role;
    }

    /**
     * {@inheritDoc}
     * 
     * @see UIExtension#getUIObject(String)
     */
    public UIObject getUIObject(String feature)
    {
        return uiObjects.get(feature);
    }

    public UIObject addFeature(String feature, UIObject uiObject)
    {
        return uiObjects.put(feature, uiObject);
    }

    public UIObject removeFeature(String feature)
    {
        return uiObjects.remove(feature);
    }

    public void clearFeatures()
    {
        uiObjects.clear();
    }
}

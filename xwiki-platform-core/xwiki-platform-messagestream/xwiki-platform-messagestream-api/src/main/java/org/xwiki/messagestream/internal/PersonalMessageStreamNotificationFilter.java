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
package org.xwiki.messagestream.internal;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.eventstream.Event;
import org.xwiki.messagestream.PersonalMessageDescriptor;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.EntityReferenceSerializer;
import org.xwiki.notifications.NotificationFormat;
import org.xwiki.notifications.filters.NotificationFilterPreference;
import org.xwiki.notifications.filters.internal.user.EventUserFilterPreferencesGetter;

/**
 * Filter that make sure a message from the message stream is visible by the current user.
 *
 * @version $Id$
 * @since 10.5RC1
 * @since 9.11.6
 */
@Component
@Singleton
@Named("PersonalMessageStreamNotificationFilter")
public class PersonalMessageStreamNotificationFilter extends AbstractMessageStreamNotificationFilter
{
    @Inject
    private EntityReferenceSerializer<String> serializer;

    @Inject
    private EventUserFilterPreferencesGetter preferencesGetter;

    @Override
    public FilterPolicy filterEvent(Event event, DocumentReference user,
            Collection<NotificationFilterPreference> filterPreferences, NotificationFormat format)
    {
        // Don't handle events that are not personal messages!
        if (!getEventType().equals(event.getType())) {
            return FilterPolicy.NO_EFFECT;
        }

        String sender = this.serializer.serialize(event.getUser());
        return this.preferencesGetter.isUserFollowed(sender, filterPreferences, format) ? FilterPolicy.KEEP
                : FilterPolicy.FILTER;
    }

    @Override
    public String getName()
    {
        return "Personal Message Stream Notification Filter";
    }

    @Override
    String getEventType()
    {
        return PersonalMessageDescriptor.EVENT_TYPE;
    }
}

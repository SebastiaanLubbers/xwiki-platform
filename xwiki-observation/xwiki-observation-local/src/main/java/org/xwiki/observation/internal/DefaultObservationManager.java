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
 *
 */
package org.xwiki.observation.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.Requirement;
import org.xwiki.component.logging.AbstractLogEnabled;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.observation.EventListener;
import org.xwiki.observation.ObservationManager;
import org.xwiki.observation.event.AllEvent;
import org.xwiki.observation.event.Event;

/**
 * Default implementation of the {@link ObservationManager}.
 * 
 * @version $Id$
 */
@Component
public class DefaultObservationManager extends AbstractLogEnabled implements ObservationManager, Initializable
{
    /**
     * Registered listeners indexed on Event classes so that it's fast to find all the listeners
     * registered for a given event, so that {@link #notify} calls execute fast and in a fixed amount a time.
     * 
     * @todo Should we allow event inheritance?
     */
    private Map<Class< ? extends Event>, Map<String, RegisteredListener>> listenersByEvent = 
        new ConcurrentHashMap<Class< ? extends Event>, Map<String, RegisteredListener>>();

    /**
     * Registered listeners index by listener name. It makes it fast to perform operations on already
     * registered listeners.
     */
    private Map<String, EventListener> listenersByName = new ConcurrentHashMap<String, EventListener>();

    /**
     * Used to find all components implementing {@link EventListener} to register them automatically.
     * Note that 
     */
    @Requirement
    private ComponentManager componentManager;

    /**
     * Helper class to store the list of events of a given type associated with a given listener.
     * We need this for performance reasons and also in order to be able to add events after a
     * listener has been registered.
     */
    private static class RegisteredListener
    {
        /**
         * Events of a given type associated with a given listener.
         */
        private List<Event> events;

        /**
         * Listener associated with the events.
         */
        private EventListener listener;

        /**
         * @param listener the listener associated with the events.
         * @param event the first event to associate with the passed listener. More events are added by calling
         *       {@link #addEvent(Event)}
         */
        RegisteredListener(EventListener listener, Event event)
        {
            List<Event> list = new ArrayList<Event>();
            list.add(event);
            this.events = list;
            
            this.listener = listener;
        }

        /**
         * @param event the event to add
         */
        void addEvent(Event event)
        {
            this.events.add(event);
        }
        
        /**
         * @param event the event to remove
         */
        void removeEvent(Event event)
        {
            this.events.remove(event);
        }
    }
    
    /**
     * Register all components implementing the {@link EventListener} interface.
     * 
     * {@inheritDoc}
     * 
     * @see Initializable#initialize()
     */
    public void initialize() throws InitializationException
    {
        try {
            for (EventListener listener : this.componentManager.lookupList(EventListener.class)) {
                addListener(listener);
            }
        } catch (ComponentLookupException e) {
            throw new InitializationException("Failed to lookup Event Listeners", e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see ObservationManager#addListener(EventListener)
     */
    public void addListener(EventListener eventListener)
    {
        // If the passed event listener name is already registered, log a warning
        if (this.listenersByName.containsKey(eventListener.getName())) {
            getLogger().warn(
                "An Event Listener was already registered for the name [" + eventListener.getName() + "]."
                + " It (" + this.listenersByName.get(eventListener.getName())
                + ") has been overwritten with the new Listener (" + eventListener + ")."
                + " In the future consider removing a Listener first if you really want to register it again.");
        }
        
        // Register the listener by name. If already registered, override it.
        this.listenersByName.put(eventListener.getName(), eventListener);
        
        // For each event defined for this listener, add it to the Event Map.
        for (Event event : eventListener.getEvents()) {
            // Check if this is a new Event type not already registered
            Map<String, RegisteredListener> eventListeners = this.listenersByEvent.get(event.getClass());
            if (eventListeners == null) {
                // No listener registered for this event yet. Create a map to store listeners for this event.
                eventListeners = new HashMap<String, RegisteredListener>();
                this.listenersByEvent.put(event.getClass(), eventListeners);
            }
            eventListeners.put(eventListener.getName(), new RegisteredListener(eventListener, event));
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see ObservationManager#removeListener(String)
     */
    public void removeListener(String listenerName)
    {
        this.listenersByName.remove(listenerName);
        for (Map.Entry<Class< ? extends Event>, Map<String, RegisteredListener>> entry 
            : this.listenersByEvent.entrySet())
        {
            entry.getValue().remove(listenerName);
            if (entry.getValue().isEmpty()) {
                this.listenersByEvent.remove(entry.getKey());
            }
        }
    }

    /**
     * {@inheritDoc}
     * @see ObservationManager#addEvent(String, Event)
     */
    public void addEvent(String listenerName, Event event)
    {
        Map<String, RegisteredListener> listeners = this.listenersByEvent.get(event.getClass());
        RegisteredListener listener = listeners.get(listenerName);
        if (listener != null) {
            listener.addEvent(event);
        }
    }

    /**
     * {@inheritDoc}
     * @see ObservationManager#removeEvent(String, Event)
     */
    public void removeEvent(String listenerName, Event event)
    {
        Map<String, RegisteredListener> listeners = this.listenersByEvent.get(event.getClass());
        RegisteredListener listener = listeners.get(listenerName);
        if (listener != null) {
            listener.removeEvent(event);
        }
    }

    /**
     * {@inheritDoc}
     * @see ObservationManager#getListener(String)
     */
    public EventListener getListener(String listenerName)
    {
        return this.listenersByName.get(listenerName);
    }

    /**
     * {@inheritDoc}
     * 
     * @see ObservationManager#notify(org.xwiki.observation.event.Event, Object, Object)
     */
    public void notify(Event event, Object source, Object data)
    {
        // Find all listeners for this event
        Map<String, RegisteredListener> regListeners = this.listenersByEvent.get(event.getClass());
        if (regListeners != null) {
            notify(regListeners.values(), event, source, data);
        }

        // Find listener listening all events
        Map<String, RegisteredListener> allEventRegListeners = this.listenersByEvent.get(AllEvent.class);
        if (allEventRegListeners != null) {
            notify(allEventRegListeners.values(), event, source, data);
        }
    }

    /**
     * Call the provided listeners matching the passed Event. 
     * The definition of <em>source</em> and <em>data</em> is purely up to the communicating classes.
     * 
     * @param listeners the listeners to notify
     * @param event the event to pass to the registered listeners
     * @param source the source of the event (or <code>null</code>)
     * @param data the additional data related to the event (or <code>null</code>)
     */
    private void notify(Collection<RegisteredListener> listeners, Event event, Object source, Object data)
    {
        for (RegisteredListener listener : listeners) {
            // Verify that one of the events matches and send the first matching event
            for (Event listenerEvent : listener.events) {
                if (listenerEvent.matches(event)) {
                    try {
                        listener.listener.onEvent(event, source, data);
                    } catch (Exception e) {
                        // protect from bad listeners
                        getLogger().error("Fail to send event [" + event + "] to listener [" + listener.listener + "]",
                            e);
                    }

                    // Only send the first matching event since the listener should only be called once per event.
                    break;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see ObservationManager#notify(org.xwiki.observation.event.Event, Object)
     */
    public void notify(Event event, Object source)
    {
        notify(event, source, null);
    }
}

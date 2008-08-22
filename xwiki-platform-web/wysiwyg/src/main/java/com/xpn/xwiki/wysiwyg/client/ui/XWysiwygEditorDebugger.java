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
package com.xpn.xwiki.wysiwyg.client.ui;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.xpn.xwiki.wysiwyg.client.Constants;
import com.xpn.xwiki.wysiwyg.client.WysiwygService;
import com.xpn.xwiki.wysiwyg.client.timer.Timer;
import com.xpn.xwiki.wysiwyg.client.timer.TimerListener;

public class XWysiwygEditorDebugger extends Composite implements TimerListener
{
    private XWysiwygEditor editor;

    private TextArea wikiTextArea;

    private TextArea dirtyXHTMLTextArea;

    private TextArea cleanXHTMLTextArea;

    private TextArea eventsTextArea;

    private Timer timer;

    public XWysiwygEditorDebugger(XWysiwygEditor editor)
    {
        this.editor = editor;

        FlowPanel panel = new FlowPanel();
        panel.setWidth("100%");
        panel.add(editor.getUI());

        dirtyXHTMLTextArea = new TextArea();
        dirtyXHTMLTextArea.setWidth("400px");
        dirtyXHTMLTextArea.setHeight("220px");
        panel.add(dirtyXHTMLTextArea);

        cleanXHTMLTextArea = new TextArea();
        cleanXHTMLTextArea.setWidth("400px");
        cleanXHTMLTextArea.setHeight("220px");
        panel.add(cleanXHTMLTextArea);

        wikiTextArea = new TextArea();
        wikiTextArea.setWidth("400px");
        wikiTextArea.setHeight("220px");
        panel.add(wikiTextArea);

        eventsTextArea = new TextArea();
        eventsTextArea.setWidth("400px");
        eventsTextArea.setHeight("220px");
        panel.add(eventsTextArea);

        // get the transformed HTML Content
        dirtyXHTMLTextArea.setText(editor.getUI().getTextArea().getHTML());

        initWidget(panel);

        timer = new Timer();
        timer.addTimerListener(this);
        timer.scheduleRepeating(Constants.DEFAULT_SYNC_DELAY + 1000);
    }

    public void refreshData()
    {
        dirtyXHTMLTextArea.setText(editor.getUI().getTextArea().getHTML());

        WysiwygService.Singleton.getInstance().cleanXHTML(editor.getUI().getTextArea().getHTML(),
            new AsyncCallback<String>()
            {
                public void onFailure(Throwable caught)
                {
                    cleanXHTMLTextArea.setText(caught.toString());
                }

                public void onSuccess(String result)
                {
                    cleanXHTMLTextArea.setText(result);
                }
            });

        WysiwygService.Singleton.getInstance().fromXHTML(editor.getUI().getTextArea().getHTML(), editor.getSyntax(),
            new AsyncCallback<String>()
            {
                public void onFailure(Throwable caught)
                {
                    wikiTextArea.setText(caught.toString());
                }

                public void onSuccess(String result)
                {
                    wikiTextArea.setText(result);
                }
            });

        WysiwygService.Singleton.getInstance().fromXHTML(editor.getUI().getTextArea().getHTML(), "events",
            new AsyncCallback<String>()
            {
                public void onFailure(Throwable caught)
                {
                    eventsTextArea.setText(caught.toString());
                }

                public void onSuccess(String result)
                {
                    eventsTextArea.setText(result);
                }
            });
    }

    /**
     * {@inheritDoc}
     * 
     * @see TimerListener#onElapsed(Timer)
     */
    public void onElapsed(Timer sender)
    {
        if (sender == timer) {
            refreshData();
        }
    }
}

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
package com.xpn.xwiki.wysiwyg.client.ui.widget;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class CompositeDialog extends Composite implements SourcesPopupEvents,
    com.google.gwt.user.client.ui.PopupListener
{
    private final XDialogBox dialog;

    private final PopupListenerCollection popupListeners;

    public CompositeDialog(boolean autoHide, boolean modal)
    {
        dialog = new XDialogBox(autoHide, modal);
        dialog.addPopupListener(this);

        popupListeners = new PopupListenerCollection();
    }

    /**
     * {@inheritDoc}
     */
    protected void initWidget(Widget widget)
    {
        super.initWidget(widget);
        dialog.setWidget(this);
    }

    protected XDialogBox getDialog()
    {
        return dialog;
    }

    public void center()
    {
        // PopupPanel#center calls PopupPanel#hide if the dialog was not showing. We call PopupPanel#show before in
        // order to avoid this behavior.
        dialog.setVisible(false);
        dialog.show();
        dialog.center();
        dialog.setVisible(true);
    }

    public void hide()
    {
        dialog.hide();
    }

    /**
     * {@inheritDoc}
     * 
     * @see SourcesPopupEvents#addPopupListener(PopupListener)
     */
    public void addPopupListener(PopupListener listener)
    {
        popupListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     * 
     * @see SourcesPopupEvents#removePopupListener(PopupListener)
     */
    public void removePopupListener(PopupListener listener)
    {
        popupListeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.google.gwt.user.client.ui.PopupListener#onPopupClosed(PopupPanel, boolean)
     */
    public void onPopupClosed(PopupPanel sender, boolean autoClosed)
    {
        if (sender == dialog) {
            popupListeners.firePopupClosed(this, autoClosed);
        }
    }
}

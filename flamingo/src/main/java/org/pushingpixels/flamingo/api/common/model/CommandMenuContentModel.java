/*
 * Copyright (c) 2005-2020 Radiance Kirill Grouchnikov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of the copyright holder nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.pushingpixels.flamingo.api.common.model;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandMenuContentModel implements ContentModel {
    private CommandPanelContentModel panelContentModel;
    private List<CommandGroup> commandGroups;
    private Command highlightedCommand;

    /**
     * Stores the listeners on this model.
     */
    private EventListenerList listenerList = new EventListenerList();

    private CommandGroup.CommandGroupListener commandGroupListener;

    public CommandMenuContentModel(CommandGroup commands) {
        this(null, Collections.singletonList(commands));
    }

    public CommandMenuContentModel(CommandGroup... commandGroups) {
        this(null, Arrays.asList(commandGroups));
    }

    public CommandMenuContentModel(List<CommandGroup> commands) {
        this(null, commands);
    }

    public CommandMenuContentModel(CommandPanelContentModel panelContentModel,
            List<CommandGroup> commands) {
        this.commandGroups = new ArrayList<>(commands);
        this.commandGroupListener = new CommandGroup.CommandGroupListener() {
            @Override
            public void onCommandAdded(Command command) {
                fireStateChanged();
            }

            @Override
            public void onCommandRemoved(Command command) {
                fireStateChanged();
            }

            @Override
            public void onAllCommandsRemoved() {
                fireStateChanged();
            }
        };
        for (CommandGroup commandGroupModel : this.commandGroups) {
            commandGroupModel.addCommandGroupListener(this.commandGroupListener);
        }
        this.panelContentModel = panelContentModel;
        if (this.panelContentModel != null) {
            this.panelContentModel.addChangeListener((ChangeEvent changeEvent) -> fireStateChanged());
        }
    }

    public CommandPanelContentModel getPanelContentModel() {
        return this.panelContentModel;
    }

    public void addCommandGroup(CommandGroup commandGroupModel) {
        this.commandGroups.add(commandGroupModel);
        commandGroupModel.addCommandGroupListener(this.commandGroupListener);
        this.fireStateChanged();
    }

    public void removeCommandGroup(CommandGroup commandGroupModel) {
        this.commandGroups.remove(commandGroupModel);
        commandGroupModel.removeCommandGroupListener(this.commandGroupListener);
        this.fireStateChanged();
    }

    public void removeAllCommandGroups() {
        for (CommandGroup commandGroupModel : this.commandGroups) {
            commandGroupModel.removeCommandGroupListener(this.commandGroupListener);
        }
        this.commandGroups.clear();
        this.fireStateChanged();
    }

    public Command getHighlightedCommand() {
        return this.highlightedCommand;
    }

    public void setHighlightedCommand(Command highlightedCommand) {
        this.highlightedCommand = highlightedCommand;
        this.fireStateChanged();
    }

    public List<CommandGroup> getCommandGroups() {
        return Collections.unmodifiableList(this.commandGroups);
    }

    /**
     * Adds the specified change listener to track changes to this model.
     *
     * @param l Change listener to add.
     * @see #removeChangeListener(ChangeListener)
     */
    public void addChangeListener(ChangeListener l) {
        this.listenerList.add(ChangeListener.class, l);
    }

    /**
     * Removes the specified change listener from tracking changes to this model.
     *
     * @param l Change listener to remove.
     * @see #addChangeListener(ChangeListener)
     */
    public void removeChangeListener(ChangeListener l) {
        this.listenerList.remove(ChangeListener.class, l);
    }

    /**
     * Notifies all registered listeners that the state of this model has changed.
     */
    private void fireStateChanged() {
        // Guaranteed to return a non-null array
        Object[] listeners = this.listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        ChangeEvent event = new ChangeEvent(this);
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                ((ChangeListener) listeners[i + 1]).stateChanged(event);
            }
        }
    }
}

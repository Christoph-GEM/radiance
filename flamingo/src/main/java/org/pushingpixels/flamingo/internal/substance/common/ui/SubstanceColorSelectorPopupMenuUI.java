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
package org.pushingpixels.flamingo.internal.substance.common.ui;

import org.pushingpixels.flamingo.api.common.popup.AbstractPopupMenu;
import org.pushingpixels.flamingo.api.common.popup.JColorSelectorPopupMenu;
import org.pushingpixels.flamingo.internal.ui.common.popup.BasicColorSelectorPopupMenuUI;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.colorscheme.SubstanceColorScheme;
import org.pushingpixels.substance.internal.painter.BackgroundPaintingUtils;
import org.pushingpixels.substance.internal.painter.DecorationPainterUtils;
import org.pushingpixels.substance.internal.utils.SubstanceColorSchemeUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceSizeUtils;
import org.pushingpixels.substance.internal.utils.WidgetUtilities;
import org.pushingpixels.substance.internal.utils.menu.SubstanceMenuBackgroundDelegate;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

/**
 * UI for {@link JColorSelectorPopupMenu} components in <b>Substance</b> look and
 * feel.
 * 
 * @author Kirill Grouchnikov
 */
public class SubstanceColorSelectorPopupMenuUI extends BasicColorSelectorPopupMenuUI {
    public static ComponentUI createUI(JComponent c) {
        SubstanceCoreUtilities.testComponentCreationThreadingViolation(c);
        return new SubstanceColorSelectorPopupMenuUI();
    }

    private SubstanceColorSelectorPopupMenuUI() {
    }

    private DecorationPainterUtils.PopupInvokerLink popupInvokerLink;

    @Override
    public void installUI(JComponent c) {
        this.popupInvokerLink = ((AbstractPopupMenu) c)::getInvoker;
        super.installUI(c);
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        this.popupMenu.putClientProperty(DecorationPainterUtils.POPUP_INVOKER_LINK,
                this.popupInvokerLink);
    }

    @Override
    protected void uninstallDefaults() {
        this.popupMenu.putClientProperty(DecorationPainterUtils.POPUP_INVOKER_LINK, null);
        super.uninstallDefaults();
    }

    @Override
    protected JPanel createMenuPanel() {
        JPanel result = new SubstanceMenuPanel();
        result.putClientProperty(DecorationPainterUtils.POPUP_INVOKER_LINK, this.popupInvokerLink);
        return result;
    }

    protected static class SubstanceMenuPanel extends MenuPanel {
        @Override
        protected void paintIconGutterBackground(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            float fillAlpha = SubstanceCoreUtilities.getMenuGutterFillAlpha();
            if (fillAlpha > 0.0f) {
                SubstanceColorScheme scheme = SubstanceColorSchemeUtilities.getColorScheme(this,
                        ComponentState.ENABLED);
                g2d.setComposite(WidgetUtilities.getAlphaComposite(this, fillAlpha, g));
                g2d.setColor(scheme.getAccentedBackgroundFillColor());

                int sepX = this.getSeparatorX();
                if (this.getComponentOrientation().isLeftToRight()) {
                    g2d.fillRect(0, 0, sepX, this.getHeight());
                } else {
                    g2d.fillRect(sepX + 2, 0, this.getWidth() - sepX, this.getHeight());
                }
            }
            g2d.dispose();
        }
    }

    @Override
    public void update(Graphics g, JComponent c) {
        BackgroundPaintingUtils.update(g, c, false);
    }

    @Override
    public int getColorSelectorCellSize() {
        return SubstanceSizeUtils.getComponentFontSize(popupPanel);
    }

    @Override
    public int getColorSelectorCellGap() {
        return (int) SubstanceSizeUtils.getAdjustedSize(
                SubstanceSizeUtils.getComponentFontSize(popupPanel), 4, 1, 0.25f);
    }
}

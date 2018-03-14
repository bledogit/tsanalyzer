/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tsanalyzer.ui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import sun.swing.DefaultLookup;

public class TsStreamTreeRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        //super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        String stringValue = tree.convertValueToText(value, sel,
                expanded, leaf, row, hasFocus);

        this.hasFocus = hasFocus;
        setText(stringValue);

        Color fg = null;

        JTree.DropLocation dropLocation = tree.getDropLocation();
        if (dropLocation != null
                && dropLocation.getChildIndex() == -1
                && tree.getRowForPath(dropLocation.getPath()) == row) {
                fg = getTextSelectionColor();

        } else if (sel) {
            fg = getTextSelectionColor();
        } else {
            fg = getTextNonSelectionColor();
        }

        setForeground(fg);

        Icon icon;
        if ((value instanceof TsStreamNode) && (value != null)) {
            icon = ((TsStreamNode) value).getIcon();
        } else if (leaf) {
            icon = getLeafIcon();
        } else if (expanded) {
            icon = getOpenIcon();
        } else {
            icon = getClosedIcon();
        }

        if (!tree.isEnabled()) {
            setEnabled(false);
            LookAndFeel laf = UIManager.getLookAndFeel();
            Icon disabledIcon = laf.getDisabledIcon(tree, icon);
            if (disabledIcon != null) {
                icon = disabledIcon;
            }
            setDisabledIcon(icon);
        } else {
            setEnabled(true);
            setIcon(icon);
        }
        setComponentOrientation(tree.getComponentOrientation());

        selected = sel;

        
        return this;

        /*
        String stringValue = tree.convertValueToText(value, sel,
        expanded, leaf, row, hasFocus);
        
        this.hasFocus = hasFocus;
        setText(stringValue);
        if (sel) {
        setForeground(getTextSelectionColor());
        } else {
        setForeground(getTextNonSelectionColor());
        }
        
        if (!tree.isEnabled()) {
        setEnabled(false);
        } else {
        setEnabled(true);
        }
        setComponentOrientation(tree.getComponentOrientation());
        selected = sel;
         */

        //return this;

    }
}

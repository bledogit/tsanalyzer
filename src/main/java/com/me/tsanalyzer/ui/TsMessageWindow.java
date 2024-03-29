/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.me.tsanalyzer.ui;

import com.me.tsanalyzer.video.util.TsLogger;
import java.awt.Color;
import java.awt.Font;
import java.util.logging.Level;
import javax.swing.text.Document;

/**
 *
 * @author mortjo01
 */
public class TsMessageWindow extends javax.swing.JFrame 
    implements tsanalyzer.video.util.MessageProcessor
{

        //<editor-fold defaultstate="collapsed" desc="Updater">
    /**
     * Thread to update the Message Window
     * to avoid performance issues.
     */
    private class Updater extends Thread {

        private final StringBuilder buffer = new StringBuilder();
        private final Object signal = new Object();
        private boolean loop = true;
        private long interval = 250;

        public long getInterval() {
            return interval;
        }

        public void setInterval(long interval) {
            this.interval = interval;
        }

        public void add(String msg) {
            synchronized (signal) {
                synchronized (buffer) {
                    buffer.append(msg);
                }
                signal.notify();
            }
        }

        private String getString() {
            String msg;
            synchronized (buffer) {
                msg = buffer.toString();
                buffer.delete(0, buffer.length());
            }
            return msg;
        }

        @Override
        public void run() {
            long then = System.currentTimeMillis();
            try {
                while (loop) {
                    synchronized (signal) {
                        signal.wait();
                        long now = System.currentTimeMillis();
                        if (now - then > 1000) {
                            append(Color.BLACK, getString());
                            then = System.currentTimeMillis();
                        }
                    }
                }
            } catch (Exception e) {
                append(Color.RED, "Exception: " + e.getMessage());
            }
        }
    }
    //</editor-fold>
    
    private Updater updater = new Updater();

    @Override
    public void setVisible(boolean b) {
        if (!updater.isAlive()) {
            updater.start();
        }

        super.setVisible(b);
        if (TsLogger.getLogLevel() == Level.ALL) {
            jComboDebugLevel.setSelectedIndex(0);
        } else if (TsLogger.getLogLevel() == Level.FINEST) {
            jComboDebugLevel.setSelectedIndex(1);
        } else if (TsLogger.getLogLevel() == Level.FINER) {
            jComboDebugLevel.setSelectedIndex(2);
        } else if (TsLogger.getLogLevel() == Level.FINE) {
            jComboDebugLevel.setSelectedIndex(3);
        } else if (TsLogger.getLogLevel() == Level.INFO) {
            jComboDebugLevel.setSelectedIndex(4);
        } else if (TsLogger.getLogLevel() == Level.WARNING) {
            jComboDebugLevel.setSelectedIndex(5);
        } else if (TsLogger.getLogLevel() == Level.SEVERE) {
            jComboDebugLevel.setSelectedIndex(6);
        } else if (TsLogger.getLogLevel() == Level.OFF) {
            jComboDebugLevel.setSelectedIndex(7);
        }

    }
    
    /**
     * Creates new form TsMessageWindow
     */
    public TsMessageWindow() {
        initComponents();
        Font font = new Font(Font.MONOSPACED, Font.TRUETYPE_FONT, 12);
        jTextDebug.setFont(font);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextDebug = new javax.swing.JTextPane();
        jComboDebugLevel = new javax.swing.JComboBox();
        jButtonClear = new javax.swing.JButton();

        jScrollPane1.setViewportView(jTextDebug);

        jComboDebugLevel.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "Finest", "Finer", "Fine", "Info", "Warning", "Error", "None" }));
        jComboDebugLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onDebugLevelChange(evt);
            }
        });

        jButtonClear.setText("Clear");
        jButtonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onClear(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane1)
                    .add(layout.createSequentialGroup()
                        .add(jComboDebugLevel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 205, Short.MAX_VALUE)
                        .add(jButtonClear)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jComboDebugLevel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButtonClear))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void onDebugLevelChange(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onDebugLevelChange
        switch (jComboDebugLevel.getSelectedIndex()) {
            case 0:
                TsLogger.setLogLevel(Level.ALL);
                break;
            case 1:
                TsLogger.setLogLevel(Level.FINEST);
                break;
            case 2:
                TsLogger.setLogLevel(Level.FINER);
                break;
            case 3:
                TsLogger.setLogLevel(Level.FINE);
                break;
            case 4:
                TsLogger.setLogLevel(Level.INFO);
                break;
            case 5:
                TsLogger.setLogLevel(Level.WARNING);
                break;
            case 6:
                TsLogger.setLogLevel(Level.SEVERE);
                break;
            case 7:
                TsLogger.setLogLevel(Level.OFF);
                break;
        }
    }//GEN-LAST:event_onDebugLevelChange

    private void onClear(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onClear
        this.jTextDebug.setText(null);
    }//GEN-LAST:event_onClear

    public void append(Color color, String text) {
        try {
            Document doc = jTextDebug.getDocument();

            // Move the insertion point to the end
            jTextDebug.setCaretPosition(doc.getLength());
            jTextDebug.setForeground(color);
            // Insert the text
            jTextDebug.replaceSelection(text);
            jTextDebug.validate();
            // Convert the new end location
            // to view co-ordinates
            //Rectangle r = jTextDebug.modelToView(doc.getLength());

            // Finally, scroll so that the new text is visible
            //if (r != null) {
            //    jTextDebug.scrollRectToVisible(r);
            //}
        } catch (Exception e) {
            System.out.println("Failed to append text: " + e);
        }
    }

    public void println(Level level, String msg) {
        print(level, msg + "\n");
    }

    public void print(Level level, String msg) {
        Color color = Color.BLACK;

        if (level == Level.WARNING) {
            color = Color.YELLOW;
        } else if (level == Level.SEVERE) {
            color = Color.RED;
        }

        updater.add(msg);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClear;
    private javax.swing.JComboBox jComboDebugLevel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextDebug;
    // End of variables declaration//GEN-END:variables
}

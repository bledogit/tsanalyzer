/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.me.tsanalyzer.ui;

import com.me.tsanalyzer.video.transport.Descriptor;
import com.me.tsanalyzer.video.transport.Observer.PacketObserver;
import com.me.tsanalyzer.video.transport.Observer.ParseProgress;
import com.me.tsanalyzer.video.transport.Observer.PesObserver;
import com.me.tsanalyzer.video.transport.Observer.ProgressObserver;
import com.me.tsanalyzer.video.transport.Observer.PsiObserver;
import com.me.tsanalyzer.video.transport.Packet;
import com.me.tsanalyzer.video.transport.Pat;
import com.me.tsanalyzer.video.transport.Pes;
import com.me.tsanalyzer.video.transport.Pmt;
import com.me.tsanalyzer.video.transport.PsiTable;
import com.me.tsanalyzer.video.transport.Stream;
import com.me.tsanalyzer.video.transport.TsStreamAnalyzer;
import com.me.tsanalyzer.video.util.TsLogger;
import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author mortjo01
 */
public class TsAnalyzer extends javax.swing.JFrame implements PesObserver, PacketObserver, PsiObserver, ProgressObserver {

    //private final Timer messageTimer = new Timer();
    //private final Timer busyIconTimer;
    private JTextArea packetInfo = new JTextArea();
    private JTextArea pesInfo = new JTextArea();
    private JTextArea elemInfo = new JTextArea();
    private JTextArea psiInfo = new JTextArea();
    private TsStreamAnalyzer ts = new TsStreamAnalyzer();
    private String lastFilePath = null;
    private TsMessageWindow messageWindow = new TsMessageWindow();
    private Font fontStats = new Font("Book Antiqua", Font.PLAIN, 10);

    /**
     * Creates new form TsAnalyzer
     */
    public TsAnalyzer() {
        initComponents();


        TsLogger.setLogLevel(Level.INFO);

        Font font = new Font(Font.MONOSPACED, Font.TRUETYPE_FONT, 12);

        setStoppedStatus();

        packetInfo.setFont(font);
        pesInfo.setFont(font);
        elemInfo.setFont(font);
        psiInfo.setFont(font);

        jTabbedPane.add("Packet Info", new JScrollPane(packetInfo));
        jTabbedPane.add("Pes Info", new JScrollPane(pesInfo));
        jTabbedPane.add("Elementary Info", new JScrollPane(elemInfo));
        jTabbedPane.add("Psi Info", new JScrollPane(psiInfo));


        int messageTimeout = 5000; //TODO: Fix
        /*
         messageTimer = new Timer(messageTimeout, new ActionListener() {
         public void actionPerformed(ActionEvent e) {
         statusMessageLabel.setText("");
         }
         });
         messageTimer.setRepeats(false);
         int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
         for (int i = 0; i < busyIcons.length; i++) {
         busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
         }
         busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
         public void actionPerformed(ActionEvent e) {
         busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
         statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
         }
         });
         idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
         statusAnimationLabel.setIcon(idleIcon);

         // connecting action tasks to status bar via TaskMonitor
         TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
         taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
         public void propertyChange(java.beans.PropertyChangeEvent evt) {
         String propertyName = evt.getPropertyName();
         if ("started".equals(propertyName)) {
         if (!busyIconTimer.isRunning()) {
         statusAnimationLabel.setIcon(busyIcons[0]);
         busyIconIndex = 0;
         busyIconTimer.start();
         }
         } else if ("done".equals(propertyName)) {
         busyIconTimer.stop();
         statusAnimationLabel.setIcon(idleIcon);
         } else if ("message".equals(propertyName)) {
         String text = (String) (evt.getNewValue());
         statusMessageLabel.setText((text == null) ? "" : text);
         messageTimer.restart();
         } else if ("progress".equals(propertyName)) {
         int value = (Integer) (evt.getNewValue());
         }
         }
         });
         */
    }

    private void setResetStatus() {
        try {
            ts.resetAnalysis();
        } catch (Exception ex) {
            TsLogger.log(ex);
        }
        TsStreamNode root = new TsStreamNode(TsStreamNode.TRANSPORT, "Streams");
        jTreeTransport.setCellRenderer(new TsStreamTreeRenderer());
        jTreeTransport.setModel(new javax.swing.tree.DefaultTreeModel(root));
        setStoppedStatus();
    }

    private void setStoppedStatus() {
        jButtonPause.setEnabled(false);
        jButtonStop.setEnabled(false);
        jButtonStepForward.setEnabled(false);
        jButtonStop.setEnabled(false);
        jButtonFastForward.setEnabled(true);
        jButtonPlay.setEnabled(true);
    }

    private void setRunningStatus() {
        jButtonPause.setEnabled(true);
        jButtonStop.setEnabled(true);
        jButtonStepForward.setEnabled(true);
        jButtonStop.setEnabled(true);
        jButtonFastForward.setEnabled(true);
        jButtonPlay.setEnabled(false);
    }

    int getSafeInt(String value) {
        int val = -1;
        try {
            val = Integer.parseInt(value);
        } catch (Exception e) {
        }
        return val;
    }

    private void setStopTrigger() {
        Component c = jTabbedPane.getSelectedComponent();
        String name = jTabbedPane.getTitleAt(jTabbedPane.getSelectedIndex());

        ts.setPid(getSafeInt(jTextPid.getText()));

        ts.setPacketObserver(null);
        ts.setPesObserver(null);
        ts.setPsiObserver(null);

        if (name.contentEquals("Packet Info")) {
            ts.setPacketObserver(this);
        } else if (name.contentEquals("Pes Info")) {
            ts.setPesObserver(this);
        } else if (name.contentEquals("Psi Info")) {
            ts.setPsiObserver(this);
        } else if (name.contentEquals("Elementary Info")) {
        }
    }

    public void populatePrograms(Pat pat) {
        TsStreamNode root = new TsStreamNode(TsStreamNode.TRANSPORT, "Streams");
        for (Pmt p : (Collection<Pmt>) pat.getPrograms().values()) {
            TsStreamNode program = new TsStreamNode(TsStreamNode.STREAM, p);
            root.add(program);

            if (p.getDescriptors() != null) {
                TsStreamNode programDesc = new TsStreamNode(TsStreamNode.INFO, "Descriptors ");
                program.add(programDesc);

                for (Descriptor d : (Collection<Descriptor>) p.getDescriptors().values()) {
                    TsStreamNode desc = new TsStreamNode(TsStreamNode.INFO, d);
                    programDesc.add(desc);
                }
            }


            if (p.getStreams() != null) {
                for (Stream s : (Collection<Stream>) p.getStreams().values()) {
                    String type = TsStreamNode.OTHER;
                    if (s.isScrambled()) {
                        type = TsStreamNode.LOCK;
                    } else if (s.isAudio()) {
                        type = TsStreamNode.AUDIO;
                    } else if (s.isVideo()) {
                        type = TsStreamNode.VIDEO;
                    }

                    TsStreamNode stream = new TsStreamNode(type, s);
                    program.add(stream);

                    if (s.getDescriptors() != null) {
                        for (Descriptor d : (Collection<Descriptor>) s.getDescriptors().values()) {
                            TsStreamNode desc = new TsStreamNode(TsStreamNode.INFO, d);
                            stream.add(desc);
                        }
                    }
                }
            }
        }
        jTreeTransport.setCellRenderer(new TsStreamTreeRenderer());
        jTreeTransport.setModel(new javax.swing.tree.DefaultTreeModel(root));
    }

    //<editor-fold defaultstate="collapsed" desc="Stream Observers">
    public void process(Packet packet) {
        packetInfo.setText(packet.toString());
        ts.pauseAnalysis();
        setStoppedStatus();
    }

    public void process(PsiTable psi) {
        ts.pauseAnalysis();
        setStoppedStatus();
        if (psi != null) {
            if (psi.getTableId() == 0) {
                this.populatePrograms((Pat) psi);
                psiInfo.setText(psi.toString());
            } else if (psi.getTableId() == 2) {
                psiInfo.setText(psi.toString());
            }
        }
    }

    public void process(Pes pes) {
        pesInfo.setText(pes.toString());
        ts.pauseAnalysis();
        setStoppedStatus();
    }

    public void process(ParseProgress progress) {
        this.jProgressBar.setValue(progress.progress);
        if (progress.progress == 100) {
            if (ts.getPat()!= null) populatePrograms(ts.getPat());
            if (ts.getPes()!= null) pesInfo.setText(ts.getPes().toString());
            if (ts.getPacket()!= null) packetInfo.setText(ts.getPacket().toString());
            if (ts.getPat()!= null) psiInfo.setText(ts.getPat().toString());
            setStoppedStatus();
        }

    }
    //</editor-fold>

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextFileName = new javax.swing.JTextPane();
        jLabel1 = new javax.swing.JLabel();
        jProgressBar = new javax.swing.JProgressBar();
        jToolBar1 = new javax.swing.JToolBar();
        jButtonStop = new javax.swing.JButton();
        jButtonPlay = new javax.swing.JButton();
        jButtonPause = new javax.swing.JButton();
        jButtonStepForward = new javax.swing.JButton();
        jButtonFastForward = new javax.swing.JButton();
        jButtonRefresh = new javax.swing.JButton();
        jButtonSearch = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanelLeft = new javax.swing.JPanel();
        jCheckPid = new javax.swing.JCheckBox();
        jTextPid = new javax.swing.JTextField();
        jScrollPaneTs = new javax.swing.JScrollPane();
        jTreeTransport = new javax.swing.JTree();
        jTabbedPane = new javax.swing.JTabbedPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuFileOpen = new javax.swing.JMenuItem();
        jMenuStreamOpen = new javax.swing.JMenuItem();
        jMenuExit = new javax.swing.JMenuItem();
        jMenuVideo = new javax.swing.JMenu();
        jMenuMessages = new javax.swing.JMenuItem();
        jMenuVideoStatsStreams = new javax.swing.JMenuItem();
        jMenuVideoStatsProgram = new javax.swing.JMenuItem();
        jMenuSettings = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        jMenuAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextFileName.setEditable(false);
        jScrollPane1.setViewportView(jTextFileName);

        jLabel1.setText("File:");

        jToolBar1.setBackground(java.awt.SystemColor.control);
        jToolBar1.setRollover(true);

        jButtonStop.setBackground(java.awt.SystemColor.window);
        jButtonStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tsanalyzer/ui/resources/toolbar/Stop24.gif"))); // NOI18N
        jButtonStop.setFocusable(false);
        jButtonStop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonStop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onStop(evt);
                onPlay(evt);
                onPause(evt);
                onStepForward(evt);
                onFastForward(evt);
                onRefresh(evt);
                onSearch(evt);
            }
        });
        jToolBar1.add(jButtonStop);

        jButtonPlay.setBackground(java.awt.SystemColor.window);
        jButtonPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tsanalyzer/ui/resources/toolbar/Play24.gif"))); // NOI18N
        jButtonPlay.setFocusable(false);
        jButtonPlay.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonPlay.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onPlay(evt);
            }
        });
        jToolBar1.add(jButtonPlay);

        jButtonPause.setBackground(java.awt.SystemColor.window);
        jButtonPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tsanalyzer/ui/resources/toolbar/Pause24.gif"))); // NOI18N
        jButtonPause.setFocusable(false);
        jButtonPause.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonPause.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onPause(evt);
            }
        });
        jToolBar1.add(jButtonPause);

        jButtonStepForward.setBackground(java.awt.SystemColor.window);
        jButtonStepForward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tsanalyzer/ui/resources/toolbar/StepForward24.gif"))); // NOI18N
        jButtonStepForward.setFocusable(false);
        jButtonStepForward.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonStepForward.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonStepForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onStepForward(evt);
            }
        });
        jToolBar1.add(jButtonStepForward);

        jButtonFastForward.setBackground(java.awt.SystemColor.window);
        jButtonFastForward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tsanalyzer/ui/resources/toolbar/FastForward24.gif"))); // NOI18N
        jButtonFastForward.setFocusable(false);
        jButtonFastForward.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonFastForward.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonFastForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onFastForward(evt);
            }
        });
        jToolBar1.add(jButtonFastForward);

        jButtonRefresh.setBackground(java.awt.SystemColor.window);
        jButtonRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tsanalyzer/ui/resources/toolbar/Refresh24.gif"))); // NOI18N
        jButtonRefresh.setFocusable(false);
        jButtonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onRefresh(evt);
            }
        });
        jToolBar1.add(jButtonRefresh);

        jButtonSearch.setBackground(java.awt.SystemColor.window);
        jButtonSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tsanalyzer/ui/resources/toolbar/FindAgain24.gif"))); // NOI18N
        jButtonSearch.setFocusable(false);
        jButtonSearch.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onSearch(evt);
            }
        });
        jToolBar1.add(jButtonSearch);

        jSplitPane1.setDividerLocation(200);

        jCheckPid.setText("PID");
        jCheckPid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckPidActionPerformed(evt);
            }
        });

        jTextPid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onPidChange(evt);
            }
        });

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        jTreeTransport.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jTreeTransport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                onTreeClicked(evt);
            }
        });
        jScrollPaneTs.setViewportView(jTreeTransport);

        org.jdesktop.layout.GroupLayout jPanelLeftLayout = new org.jdesktop.layout.GroupLayout(jPanelLeft);
        jPanelLeft.setLayout(jPanelLeftLayout);
        jPanelLeftLayout.setHorizontalGroup(
            jPanelLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelLeftLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanelLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPaneTs)
                    .add(jPanelLeftLayout.createSequentialGroup()
                        .add(jCheckPid)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jTextPid, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE))))
        );
        jPanelLeftLayout.setVerticalGroup(
            jPanelLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanelLeftLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanelLeftLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jCheckPid)
                    .add(jTextPid, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPaneTs, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(jPanelLeft);
        jSplitPane1.setRightComponent(jTabbedPane);

        jMenuFile.setText("File");

        jMenuFileOpen.setText("File Open");
        jMenuFileOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OnFileOpen(evt);
            }
        });
        jMenuFile.add(jMenuFileOpen);

        jMenuStreamOpen.setText("Stream Open");
        jMenuStreamOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuStreamOpenActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuStreamOpen);

        jMenuExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.SHIFT_MASK));
        jMenuExit.setText("Exit");
        jMenuFile.add(jMenuExit);

        jMenuBar1.add(jMenuFile);

        jMenuVideo.setText("Video");

        jMenuMessages.setText("Analyzer Messages");
        jMenuMessages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onMessageWindow(evt);
            }
        });
        jMenuVideo.add(jMenuMessages);

        jMenuVideoStatsStreams.setText("Streams");
        jMenuVideoStatsStreams.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onStreamStatistics(evt);
            }
        });
        jMenuVideo.add(jMenuVideoStatsStreams);

        jMenuVideoStatsProgram.setText("Programs");
        jMenuVideoStatsProgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onProgramStatistics(evt);
            }
        });
        jMenuVideo.add(jMenuVideoStatsProgram);

        jMenuBar1.add(jMenuVideo);

        jMenuSettings.setText("Settings");
        jMenuBar1.add(jMenuSettings);

        jMenu4.setText("Help");

        jMenuAbout.setText("About...");
        jMenuAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onMenuAbout(evt);
            }
        });
        jMenu4.add(jMenuAbout);

        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 718, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jSplitPane1)
                    .add(jProgressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 66, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSplitPane1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jProgressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    //<editor-fold defaultstate="collapsed" desc="UI Event Handlers">
    private void OnFileOpen(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OnFileOpen
        if (true) {
            JFileChooser chooser = new javax.swing.JFileChooser();
            if (lastFilePath != null) {
                File dir = new File(lastFilePath);
                chooser.setCurrentDirectory(dir);
            }

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                String filename = f.getPath();
                lastFilePath = filename.substring(0, filename.lastIndexOf(File.separator));
                ts = new TsStreamAnalyzer();
                ts.setFileName(filename);
                ts.setProgressObserver(this);
                setStopTrigger();
                jTextFileName.setText(filename);
                TsLogger.log(filename);
                setResetStatus();
            }
        } else {
            String filename = "C:\\Users\\mortjo01\\Desktop\\Sample Videos\\t13-1.ts";
            filename = "/mnt/farmer/sampler/s1001-1-0.ts";

            ts = new TsStreamAnalyzer();
            ts.setFileName(filename);
            ts.setProgressObserver(this);
            setStopTrigger();
            jTextFileName.setText(filename);
            TsLogger.log(filename);
        }
    }//GEN-LAST:event_OnFileOpen

    private void onStop(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onStop
        ts.stopAnalysis();
    }//GEN-LAST:event_onStop

    private void onPlay(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onPlay
        if (ts.isStopped()) {
            ts = new TsStreamAnalyzer();
            ts.setFileName(jTextFileName.getText());
            ts.setProgressObserver(this);
        }
        ts.startAnalysis();
        setStopTrigger();
        setRunningStatus();
    }//GEN-LAST:event_onPlay

    private void onPause(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onPause
        ts.pauseAnalysis();
        if (ts.getPacket() != null) {
            packetInfo.setText(ts.getPacket().toString());
        }
        if (ts.getPes() != null) {
            pesInfo.setText(ts.getPes().toString());
        }
        if (ts.getPmt() != null) {
            psiInfo.setText(ts.getPmt().toString());
        }
    }//GEN-LAST:event_onPause

    private void onStepForward(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onStepForward
        setStopTrigger();
        ts.startAnalysis();
        setRunningStatus();
    }//GEN-LAST:event_onStepForward

    private void onFastForward(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onFastForward
        onPlay(evt);
        ts.setPacketObserver(null);
        ts.setPesObserver(null);
        ts.setPsiObserver(null);
        setRunningStatus();
    }//GEN-LAST:event_onFastForward

    private void onRefresh(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onRefresh
        setResetStatus();
    }//GEN-LAST:event_onRefresh

    private void onSearch(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onSearch
        // TODO add your handling code here:
    }//GEN-LAST:event_onSearch

    private void onMessageWindow(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onMessageWindow
        TsLogger.setProcessor(messageWindow);
        messageWindow.setVisible(true);
    }//GEN-LAST:event_onMessageWindow

    private void onStreamStatistics(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onStreamStatistics
        DefaultPieDataset ds = new DefaultPieDataset();

        Map<Integer, Stream> map = ts.getStreams();
        for (Stream s : map.values()) {
            ds.setValue(s.getPid() + ":" + s.getStreamType(), s.getCount());
        }



        JFreeChart chart = ChartFactory.createPieChart(null,
                ds,
                false,
                true,
                true);
        PiePlot p = (PiePlot) chart.getPlot();
        p.setForegroundAlpha(0.5f);

        ChartFrame frame = new ChartFrame("Packet Distribution for each Stream", chart);
        frame.setVisible(true);
        frame.setSize(this.getSize());
    }//GEN-LAST:event_onStreamStatistics

    private void onProgramStatistics(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onProgramStatistics
        DefaultPieDataset ds = new DefaultPieDataset();

        Map<Integer, Pmt> programs = ts.getPat().getPrograms();
        for (Pmt pmt : programs.values()) {
            int count = 0;
            String name = "Program: " + pmt.getProgramNumber() + "\n";
            Map<Integer, Stream> map = pmt.getStreams();
            for (int pid : map.keySet()) {
                Stream s = (Stream) ts.getStreams().get(pid);
                if (s != null) {
                    count += s.getCount();
                    name += s.getPid() + ":" + s.getStreamType() + "\n";
                }
            }
            ds.setValue(name, count);
        }


        JFreeChart chart = ChartFactory.createPieChart(null,
                ds,
                false,
                true,
                true);
        PiePlot p = (PiePlot) chart.getPlot();
        p.setForegroundAlpha(0.5f);
        p.setLabelFont(fontStats);

        ChartFrame frame = new ChartFrame("Packet Distribution for each Program", chart);
        frame.setVisible(true);
        frame.setSize(this.getSize());
    }//GEN-LAST:event_onProgramStatistics

    private void onTreeClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_onTreeClicked
        int x = evt.getX();
        int y = evt.getY();
        JTree tree = (JTree) evt.getSource();
        TreePath path = tree.getPathForLocation(x, y);

        if (path == null) {
            return;
        }

        Object obj = path.getLastPathComponent();
        if ((obj instanceof TsStreamNode) && (obj != null)) {
            TsStreamNode node = (TsStreamNode) obj;
            elemInfo.setText(node.getObj().toString());
            jTabbedPane.setSelectedIndex(3);
        }
    }//GEN-LAST:event_onTreeClicked

    private void onPidChange(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onPidChange
        if (jCheckPid.isSelected()) {
            ts.setPid(getSafeInt(jTextPid.getText()));

        } else {
            ts.setPid(-1);
        }
    }//GEN-LAST:event_onPidChange

    private void jCheckPidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckPidActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckPidActionPerformed

    private void onMenuAbout(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onMenuAbout
        // TODO add your handling code here:
        TsAnalyzerAbout about = new TsAnalyzerAbout();
        
        about.setVisible(true);
    }//GEN-LAST:event_onMenuAbout

    private void jMenuStreamOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuStreamOpenActionPerformed
        // TODO add your handling code here:
            
            TsUrlSelector selector = new TsUrlSelector(this, true);
            if (selector.showDialog() == TsUrlSelector.APPROVE_OPTION) {
                URL url = selector.getSelection();
                ts = new TsStreamAnalyzer();
                ts.setUrl(url);
                ts.setProgressObserver(this);
                setStopTrigger();
                jTextFileName.setText(url.toString());
                setStoppedStatus();
                TsLogger.log(url.toString());
            }
            JOptionPane.showMessageDialog(this, null, "selection = " , JOptionPane.WARNING_MESSAGE);
    }//GEN-LAST:event_jMenuStreamOpenActionPerformed

    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Main">
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Test");
        
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TsAnalyzer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TsAnalyzer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TsAnalyzer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TsAnalyzer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TsAnalyzer().setVisible(true);
            }
        });
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Generated Variable Declarations - do not modify">                          
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonFastForward;
    private javax.swing.JButton jButtonPause;
    private javax.swing.JButton jButtonPlay;
    private javax.swing.JButton jButtonRefresh;
    private javax.swing.JButton jButtonSearch;
    private javax.swing.JButton jButtonStepForward;
    private javax.swing.JButton jButtonStop;
    private javax.swing.JCheckBox jCheckPid;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuItem jMenuAbout;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuExit;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenuItem jMenuFileOpen;
    private javax.swing.JMenuItem jMenuMessages;
    private javax.swing.JMenu jMenuSettings;
    private javax.swing.JMenuItem jMenuStreamOpen;
    private javax.swing.JMenu jMenuVideo;
    private javax.swing.JMenuItem jMenuVideoStatsProgram;
    private javax.swing.JMenuItem jMenuVideoStatsStreams;
    private javax.swing.JPanel jPanelLeft;
    private javax.swing.JProgressBar jProgressBar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPaneTs;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JTextPane jTextFileName;
    private javax.swing.JTextField jTextPid;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTree jTreeTransport;
    // End of variables declaration//GEN-END:variables
    //</editor-fold>

}

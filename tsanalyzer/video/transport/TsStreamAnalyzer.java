/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tsnanalyzer.video.transport;

import tsanalyzer.video.transport.Observer.ProgressObserver;
import tsanalyzer.video.transport.Observer.PacketObserver;
import tsanalyzer.video.transport.Observer.ParseProgress;
import tsanalyzer.video.transport.Observer.PsiObserver;
import tsanalyzer.video.transport.Observer.PesObserver;
import tsanalyzer.video.transport.Observer.PcrObserver;
import tsanalyzer.video.util.TsLogger;
import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.logging.Level;
import tsanalyzer.ui.TsSource;

/**
 *
 * @author Jose Mortensen
 */
public class TsStreamAnalyzer extends Thread {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    private String fileName = null;
    private URL url = null;

    private enum RunningStatus {

        INIT, START, RUN, PAUSEDREQ, PAUSED, STOPREQ, STOPPED
    };
    private RunningStatus runningStatus = RunningStatus.INIT;
    private Pcr pcr = new Pcr();
    // Observers
    private PesObserver pesObserver = null;
    private PacketObserver packetObserver = null;
    private PcrObserver pcrObserver = null;
    private PsiObserver psiObserver = null;
    private ProgressObserver progressObserver = null;
    // streams. keeping track of streams seen on the transport
    // stream.
    private HashMap streams = new HashMap();
    // Last elements parsed.
    private Pat pat = null;
    private int pid = -1;
    private Pmt pmt;
    private Packet packet;
    private Pes pes;
    // keeping some statistics of the parsing.
    private ParseProgress progress = new ParseProgress();
    private int lastProgress = 0;
    private TsSource source;
    private boolean payloadStart = false;
    private final Object loopLock = new Object();

    //</editor-fold>

    public TsStreamAnalyzer() {
    }

    //<editor-fold defaultstate="collapsed" desc="Setters and Getters">
    /**
     * Limit analysis to specific pid
     * @param pid Stream pid
     */
    public void setPid(int pid) {
        this.pid = pid;
    }

    /**
     * Limit analysis to specific pid
     * @return set pid
     */
    public synchronized int getPid() {
        return pid;
    }

    public Packet getPacket() {
        return packet;
    }

    public Pes getPes() {
        return pes;
    }

    public Pmt getPmt() {
        return pmt;
    }

    public Pat getPat() {
        return pat;
    }

    public HashMap getStreams() {
        return streams;
    }

    /**
     * Set the value of fileName
     *
     * @param fileName new value of fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;

    }

    public void setUrl(URL url) {
        this.url = url;
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Data Observers">
    public void setPcrObserver(PcrObserver observer) {
        pcrObserver = observer;
    }

    public void setPesObserver(PesObserver observer) {
        pesObserver = observer;
    }

    public void setPacketObserver(PacketObserver observer) {
        packetObserver = observer;
    }

    public void setPsiObserver(PsiObserver observer) {
        psiObserver = observer;
    }

    public void setProgressObserver(ProgressObserver progressObserver) {
        this.progressObserver = progressObserver;
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Control Functions">
    public void startAnalysis() {
        synchronized (loopLock) {
            if (runningStatus == RunningStatus.INIT) {
                this.start();
            } else if (runningStatus == RunningStatus.PAUSED) {
                runningStatus = RunningStatus.START;
            }
        }
    }

    public void pauseAnalysis() {
        runningStatus = RunningStatus.PAUSEDREQ;
    }

    public void stopAnalysis() {
        runningStatus = RunningStatus.STOPREQ;
    }

    public boolean isStopped() {
        return (runningStatus == RunningStatus.STOPPED);
    }

    public void resetAnalysis() throws Exception {
        try {
            synchronized (loopLock) {
                streams = new HashMap();
                pat = null;
                pid = -1;
                pmt = null;
                packet = null;
                pes = null;
                progress = new ParseProgress();
                lastProgress = 0;
                payloadStart = false;

                createInputStream();
                
                if (progressObserver != null) {
                    progressObserver.process(progress);
                }
            }
        } catch (Exception e) {
            TsLogger.log(e);
            throw e;
        }
    }
    //</editor-fold>

    private TsSource createInputStream() throws Exception {
        TsSource s = null;
        if (fileName != null) {
            File file = new File(fileName);
            s = new TsSource(file);
        } else if (url != null) {
            URLConnection conn = url.openConnection();
            s = new TsSource(conn.getInputStream());
        }
        
        return s;
    }

    private void pauseLoop() {
        try {
            runningStatus = RunningStatus.PAUSED;
            TsLogger.log("Parsing paused");

            while (runningStatus == RunningStatus.PAUSED) {
                Thread.sleep(100);
            }
            TsLogger.log("Parsing pause released");
        } catch (Exception e) {
            TsLogger.log(e);
        }

    }

    @Override
    public synchronized void run() {
        try {
            TsLogger.log("Parsing Start");
            runningStatus = RunningStatus.RUN;

            source = createInputStream();

            byte[] buffer = new byte[188];
            payloadStart = false;
            progress.reset();

            while ((source.read(buffer) == 188) && (runningStatus != RunningStatus.STOPPED)) {

                if ((runningStatus == RunningStatus.STOPPED)
                        || (runningStatus == RunningStatus.STOPREQ)) {
                    break;
                }

                if (runningStatus == RunningStatus.PAUSEDREQ) {
                    pauseLoop();
                }

                synchronized (loopLock) {

                    progress.bytes += 188;
                    progress.progress = (int) (progress.bytes * 100 / source.length());
                    progress.packets++;

                    if ((progress.progress != lastProgress) & (progressObserver != null)) {
                        progressObserver.process(progress);
                    }
                    lastProgress = progress.progress;

                    packet = new Packet(buffer);
                    payloadStart = packet.payloadStart;

                    // inform packet contains a PCR value
                    if (pcrObserver != null) {
                        pcr.pcr = packet.af.pcr;
                        if (pcr.pcr != -1) {
                            pcrObserver.Process(pcr);
                        }
                    }


                    if ((pid == -1) || (pid == packet.pid)) {

                        TsLogger.log(Level.FINEST, "Packet " + progress.packets
                                + " pid= " + packet.pid
                                + " af.l=" + ((packet.af == null) ? -1 : packet.af.field_length)
                                + " ps=" + packet.payloadStart
                                + " d.p=" + packet.dataPtr
                                + " d.l=" + (188 - packet.dataPtr));

                        Stream stream = null;
                        // pid already found
                        if (streams.containsKey(packet.pid)) {
                            stream = (Stream) streams.get(packet.pid);
                        } else {
                            stream = new Stream(packet.pid);
                            streams.put(packet.pid, stream);
                        }

                        stream.count++;

                        if (packetObserver != null) {
                            packet.number = progress.packets;
                            packetObserver.process(packet);
                        }

                        if (packet.pid == 0x1fff) {
                            continue;
                        }

                        if ((((stream.continuityCounter + 1 ) & 0xf) != packet.continuityCounter) &&
                                (stream.continuityCounter != -1)) {
                            TsLogger.log("Discontinuity on PID " + packet.pid);
                        }

                        stream.continuityCounter = packet.continuityCounter;

                        if (packet.scramblingControl != 0) {
                            stream.scrambled = true;
                            continue;
                        }

                        PacketBuffer packetBuffer = stream.packetBuffer;

                        if (payloadStart && (!packetBuffer.empty())) {
                            // Previous pes/si complete
                            if (stream.pid == 0) {
                                // Decode PAT
                                if (pat == null) {
                                    pat = new Pat(packetBuffer.getBuffer());
                                    if (psiObserver != null) {
                                        psiObserver.process(pat);
                                    }
                                }
                            } else if ((pat != null) && pat.isProgramTable(stream.pid)) {
                                // Decode PMT
                                pmt = pat.getProgram(stream.pid);
                                pmt.setAllStreams(streams);
                                pmt.process(packetBuffer.getBuffer());
                                if (psiObserver != null) {
                                    psiObserver.process(pat);
                                    psiObserver.process(pmt);
                                }
                                TsLogger.log(Level.FINER, "Pmt " + pmt.program_no + " " + pmt.section_length + " " + packetBuffer.getPackLength());

                            } else if ((pat != null) && pat.isNetworkPID(stream.pid)) {
                                Nit nit = pat.nit;
                                nit.process(packetBuffer.getBuffer());
                                if (psiObserver != null) {
                                    psiObserver.process(nit);
                                }

                            } else {
                                // Decode PES
                                pes = new Pes(packetBuffer);

                                if (pes.packet_start_code_prefix != 1)
                                TsLogger.log(Level.FINE,
                                        "Pes " + Integer.toHexString(pes.packet_start_code_prefix)
                                        + " id = " + Integer.toHexString(pes.stream_id)
                                        + " pid = " + Integer.toHexString(stream.pid)
                                        + " scr = " + Integer.toHexString(packet.scramblingControl)
                                        + " payload = " + packetBuffer.hasPayloadStart()
                                        + " len = " + (pes.PES_packet_length + 6));

                                if (pesObserver != null) {
                                    pesObserver.process(pes);
                                }

                                //if (packetBuffer.hasPayloadStart()) {
                                //    if (stream.pid == 256)
                                //        pes.writeElementary(fos256);
                                //    if (stream.pid == 257) {
                                //        pes.writeElementary(fos257);
                                //        fos257.flush();
                                //    }
                                //}
                            }

                            packetBuffer.clear();
                        }


                        packetBuffer.addBytes(buffer, packet.dataPtr);
                        if (packet.payloadStart) {
                            packetBuffer.setPayloadStart(true);
                        }

                    } // pid processing
                } //lock
            } // while



        } catch (Exception e) {
            TsLogger.log(e);
        }

        try {
            if ((packetObserver != null)&&(packet!=null)) {
                packetObserver.process(packet);
            }

            if ((pesObserver != null)&&(pes!=null)) {
                pesObserver.process(pes);
            }

            if ((psiObserver != null)&&(pat!=null)) {
                psiObserver.process(pat);
            }

            progress.progress = 100;
            if ((progressObserver != null)) {
                progressObserver.process(progress);
            }
        } catch (Exception e) {
            TsLogger.log(e);
        }

        runningStatus = RunningStatus.STOPPED;
        TsLogger.log("Parsing stopped");

    }
}

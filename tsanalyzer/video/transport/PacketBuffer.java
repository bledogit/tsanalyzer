/*
 * Transport Stream Analyzer 
 * Copyright 2011 Jose Mortensen
 * 
 * This file is part of tsAnalyzer.
 * 
 * tsAnalyzer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * tsAnalyzer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package tsnanalyzer.video.transport;

import tsanalyzer.video.util.TsLogger;

/**
 * Contains pes / stream packet data. Class to hold data extracted from 
 * transport stream packets.
 * 
 * @author Jose Mortensen <josemortensen@gmail.com>
 */
public class PacketBuffer {

    /** Maximum packet size */
    private int maxSize = 2*64*1024;
    
    private byte[] buffer = new byte[maxSize];
    private int packLength = 0;
    private boolean scrambled = false;
    private boolean payloadStart = false;
    
    protected  int pid;
    
    public PacketBuffer() {
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        buffer = new byte[maxSize];
    }

    /**
     * Add bytes to current packet.
     * @param data  Data buffer
     * @param ptr   Offset where the data is to be found
     */
    public void addBytes(byte[] data, int ptr) {
        int len = data.length - ptr;
        if (len + packLength < maxSize) {
            System.arraycopy(data, ptr, buffer, packLength, len);
            packLength += len;
        } else {
            TsLogger.log("Packet too big. Packet Dropped pid=" + pid);
            clear();
        }
    }

    /**
     * Indicates if the payload start indicator was found for this packet.
     * @return Payload Start
     */
    public boolean hasPayloadStart() {
        return payloadStart;
    }

    /**
     * Sets the payload start indicator.  This flag should be set if the
     * packet buffer contains the start of whatever contained data is has.
     * @param payloadStart Payload Start Flag
     */
    public void setPayloadStart(boolean payloadStart) {
        this.payloadStart = payloadStart;
    }

    /**
     * Indicates that the packet's payload is encrypted
     */
    public boolean isScrambled() {
        return scrambled;
    }

    /**
     * Sets the encrypted flag on this packet.
     * @param scrambled 
     */
    public void setScrambled(boolean scrambled) {
        this.scrambled = scrambled;
    }

    /**
     * Gets packet buffer.
     * @return Packet Buffer
     */
    public byte[] getBuffer() {
        byte[] ret = new byte[packLength];
        System.arraycopy(buffer, 0, ret, 0, packLength);
        return ret;
    }

    /**
     * Sets external packet buffer
     * @param buffer  Buffer
     */
    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
        packLength = buffer.length;
    }

    /**
     * Gets current packet length.
     * @return Packet length
     */
    public int getPackLength() {
        return packLength;
    }

    /**
     * Indicates packet is empty
     */
    public boolean empty() {
        return (packLength == 0);
    }

    /**
     * Erases packet buffer
     */
    public void clear() {
        packLength = 0;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

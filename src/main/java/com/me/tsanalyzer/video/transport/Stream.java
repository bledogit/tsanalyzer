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

package com.me.tsanalyzer.video.transport;

import java.util.Collection;
import java.util.HashMap;

/**
 * Transport Stream. This class represents the data structures of a 
 * stream contained in the on a transport stream. 
 * 
 * @author Jose Mortensen <josemortensen@gmail.com>
 */
public class Stream {

    static final int TYPE_PAT = 0xff00;
    static final int TYPE_CAT = 0xff01;
    static final int TYPE_TSDT = 0xff02;
    static final int TYPE_IPMP = 0xff03;
    static final int TYPE_PMT = 0xff04;
    static final int TYPE_NIT = 0xff05;
    static final int TYPE_TVCT = 0xff06;
    static final int TYPE_PAD = 0xffff;
    
    protected  PacketBuffer packetBuffer = new PacketBuffer();
    protected  int pid;
    protected  long count;
    protected  int stream_type = -1;
    protected  boolean scrambled = false;
    protected  HashMap descriptors = new HashMap();
    protected  int continuityCounter = -1;

    public long getCount() {
        return count;
    }

    public int getPid() {
        return pid;
    }

    public boolean isScrambled() {
        return scrambled;
    }

    public int getStream_type() {
        return stream_type;
    }
    
    
    
    Stream(int pid) {
        this.pid = pid;
        packetBuffer.pid = pid;
        if (pid == 0x1fff) stream_type = TYPE_PAD;
        else if (pid == 0) stream_type = TYPE_PAT;
        else if (pid == 1) stream_type = TYPE_CAT;
        else if (pid == 2) stream_type = TYPE_TSDT;
        else if (pid == 3) stream_type = TYPE_IPMP;
    }

    public void setDescriptors(HashMap descriptors) {
        this.descriptors = descriptors;
    }

    public HashMap getDescriptors() {
        return descriptors;
    }

    public String getStreamType() {

        if (pid == 0x1fff) {
            return "Padding";
        }

        switch (stream_type) {
            case -1:
                return "Unknown";
            case 0x00:
                return "Reserved";
            case 0x01:
                return "ISO/IEC 11172 Video";
            case 0x02:
                return "ISO/IEC 13818-2 Video";
            case 0x03:
                return "ISO/IEC 11172 Audio";
            case 0x04:
                return "ISO/IEC 13818-3 Audio";
            case 0x05:
                return "ISO/IEC 13818-1 Private Section";
            case 0x06:
                return "ISO/IEC 13818-1 Private PES data packets";
            case 0x07:
                return "ISO/IEC 13522 MHEG";
            case 0x08:
                return "ISO/IEC 13818-1 Annex A DSM CC";
            case 0x09:
                return "H222.1";
            case 0x0A:
                return "ISO/IEC 13818-6 type A";
            case 0x0B:
                return "ISO/IEC 13818-6 type B";
            case 0x0C:
                return "ISO/IEC 13818-6 type C";
            case 0x0D:
                return "ISO/IEC 13818-6 type D";
            case 0x0E:
                return "ISO/IEC 13818-1 auxillary";
            case 0x0F:
                return "ISO/IEC 13818-7 Audio with ADTS";
            case 0x10:
                return "ISO/IEC 14496-2 Visual";
            case 0x11:
                return "ISO/IEC 14496-3 Audio with the LATM";
            case 0x12:
                return "ISO/IEC 14496-1 SL-packetized stream or FlexMux stream carried in PES packets";
            case 0x13:
                return "ISO/IEC 14496-1 SL-packetized stream or FlexMux stream carried in ISO/IEC 14496_sections";
            case 0x14:
                return "ISO/IEC 13818-6 Synchronized Download Protocol";
            case 0x15:
                return "Metadata carried in PES packets";
            case 0x16:
                return "Metadata carried in metadata_sections";
            case 0x17:
                return "Metadata carried in ISO/IEC 13818-6 Data Carousel";
            case 0x18:
                return "Metadata carried in ISO/IEC 13818-6 Object Carousel";
            case 0x19:
                return "Metadata carried in ISO/IEC 13818-6 Synchronized Download Protocol";
            case 0x1A:
                return "IPMP stream (defined in ISO/IEC 13818-11, MPEG-2 IPMP)";
            case 0x1B:
                return "AVC video ITU-T Rec. H.264 | ISO/IEC 14496-10";
            case 0x80:
                return "User Private (0x80)";
            case 0x81:
                if (descriptors.containsKey(0xa))
                    return "Dolby AC-3 Audio";
                return "User Private (0x81)";
            case 0x86:
                return "SCTE35 (0x86)";
            case TYPE_PAT:
                return "Program Association Table";
            case TYPE_CAT:
                return "Conditional Access Table";
            case TYPE_TSDT:
                return "TS Description Table";
            case TYPE_IPMP:
                return "IPMP Control Information Table";
            case TYPE_PMT:
                return "Program Map Table";
            case TYPE_NIT:
                return "Network Information Table";
            case TYPE_TVCT:
                return "TVCT or CVCT (ATSC)";
            case TYPE_PAD:
                return "Padding";
            default:
                if (stream_type < 0x80) {
                    return "ISO/IEC 13818-1 reserved";
                } else {
                    return "Unknown ( 0x" + Integer.toHexString(stream_type) + ")";
                }
        }
    }

    public boolean isAudio() {
        switch (stream_type) {
            case 0x03: // return "ISO/IEC 11172 Audio";
            case 0x04: // return "ISO/IEC 13818-3 Audio";
            case 0x0F: // return "ISO/IEC 13818-7 Audio with ADTS";
            case 0x11: // return "ISO/IEC 14496-3 Audio with the LATM";
            case 0x81: // return "User Private (0x81)";
                return true;
            default:
                return false;
        }
    }

    public boolean isVideo() {
        switch (stream_type) {
            case 0x01: // "ISO/IEC 11172 Video";
            case 0x02: // "ISO/IEC 13818-2 Video";
            case 0x07: // "ISO/IEC 13522 MPEG";
            case 0x09: // "H222.1";
            case 0x10: // "ISO/IEC 14496-2 Visual";
            case 0x1B: // "AVC video ITU-T Rec. H.264 | ISO/IEC 14496-10";
            case 0x80: // "User Private (0x80)";
                return true;
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getStreamType()).append("\n");
        sb.append("  +-- Stream Pid: ").append(pid).append("\n");
        sb.append("  +-- Stream type: ").append(stream_type).append("\n");
        sb.append("  +-- Descriptors: ").append("\n");
        for(Descriptor d: (Collection<Descriptor>) descriptors.values()) {
            sb.append("    +-- ").append(d.getDescriptorInfo()).append("\n");
        }
        
        return sb.toString();
    }
}

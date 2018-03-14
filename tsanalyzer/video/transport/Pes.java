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

import tsanalyzer.video.util.VideoException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Pes Packet. Contains data associated to a PES packet.
 * @author Jose Mortensen <josemortensen@gmail.com>
 */
public class Pes {

    //<editor-fold defaultstate="collapsed" desc="ISO PES fields">
    int packet_start_code_prefix;   // 24 bslbf
    int stream_id;                  // 8 uimsbf
    int PES_packet_length;          // 16 uimsbf
    int PES_scrambling_control;     // 2 bslbf
    boolean PES_priority;               // 1 bslbf
    boolean data_alignment_indicator;   // 1 bslbf
    boolean copyright;                  // 1 bslbf
    boolean original_or_copy;           // 1 bslbf
    boolean PTS_flag;                   // 1 bslbf
    boolean DTS_flag;                   // 1 bslbf
    boolean ESCR_flag;                  // 1 bslbf
    boolean ES_rate_flag;               // 1 bslbf
    boolean DSM_trick_mode_flag;        // 1 bslbf
    boolean additional_copy_info_flag;  // 1 bslbf
    boolean PES_CRC_flag;               // 1 bslbf
    boolean PES_extension_flag;         // 1 bslbf
    int PES_header_data_length;     // 8 uimsbf
    long pts = -1;
    long dts = -1;
    // aux
    boolean hasPayloadStart = false;
    
    //////////////////////////////////////////////////////
    // PES Extension
    boolean PES_private_data_flag;      // 1 bslbf
    boolean pack_header_field_flag;     // 1 bslbf
    boolean program_packet_sequence_counter_flag; // 1 bslbf
    boolean P_STD_buffer_flag;          // 1 bslbf
    // reserved
    boolean PES_extension_flag_2;       // 1 bslbf
    byte[] PES_private_data = new byte[16]; // 128 bslbf
    int pack_field_length;                  // 8 bslbf
    int program_packet_sequence_counter;    // 7 bslbf
    boolean MPEG1_MPEG2_identifier;         // 1 bslbf
    int original_stuff_length;              // 6 bslbf
    boolean P_STD_buffer_scale;             // 1 bslbf
    int P_STD_buffer_size;                  // 13 bslbf        
    
    int PES_extension_field_length;         // 7 uimsbf
    boolean stream_id_extension_flag;
            
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Stream IDs">
    static final int PROGRAM_STREAM_MAP = 0xbc;
    static final int PRIVATE_STREAM_1 = 0xbd;
    static final int PADDING_STREAM = 0xbe;
    static final int PRIVATE_STREAM_2 = 0xbf;
    static final int AUDIO_STREAM_S = 0xc0;
    static final int AUDIO_STREAM_E = 0xdf;
    static final int VIDEO_STREAM_S = 0xe0;
    static final int VIDEO_STREAM_E = 0xef;
    static final int ECM_STREAM = 0xf0;
    static final int EMM_STREAM = 0xf1;
    static final int DSMCC_STREAM = 0xf2;
    static final int ISO_13522_STREAM = 0xf3;
    static final int H222_1A_STREAM = 0xf4;
    static final int H222_1B_STREAM = 0xf5;
    static final int H222_1C_STREAM = 0xf6;
    static final int H222_1D_STREAM = 0xf7;
    static final int H222_1E_STREAM = 0xf8;
    static final int ANC_STREAM = 0xf9;
    static final int ISO_14496_SL_STREAM = 0xfa;
    static final int ISO_14496_FM_STREAM = 0xfb;
    static final int METADATA_STREAM = 0xfc;
    static final int EXTENDED_STREAM_ID = 0xfd;
    static final int RESERVED_STREAM = 0xfe;
    static final int PROGRAM_STREAM_DIR = 0xff;
    //</editor-fold>

    byte[] data;
    int dataOffset = 0;
    int dataLength = 0;

    /**
     * Constructor
     * @param buffer Buffer with pes data
     * @throws VideoException 
     */
    public Pes(PacketBuffer buffer) throws VideoException {

        data = buffer.getBuffer();
        hasPayloadStart = buffer.hasPayloadStart();
        int ptr = 0;

        packet_start_code_prefix = data[ptr++] & 0xff;
        packet_start_code_prefix <<= 8;
        packet_start_code_prefix += data[ptr++] & 0xff;
        packet_start_code_prefix <<= 8;
        packet_start_code_prefix += data[ptr++] & 0xff;
        stream_id = data[ptr++] & 0xff;

        if (packet_start_code_prefix == 1
            && stream_id != PADDING_STREAM
            && stream_id != PRIVATE_STREAM_2
            && stream_id != ECM_STREAM
            && stream_id != EMM_STREAM
            && stream_id != PROGRAM_STREAM_DIR
            && stream_id != DSMCC_STREAM
            && stream_id != H222_1E_STREAM)
        
        {

            PES_packet_length = data[ptr++] & 0xff;
            PES_packet_length <<= 8;
            PES_packet_length += data[ptr++] & 0xff;

            dataLength = buffer.getPackLength();

            int flags = data[ptr++] & 0xff;
            flags <<= 8;
            flags += data[ptr++] & 0xff;

            PES_scrambling_control = (flags >> 12) & 3;     // 2 bslbf
            PES_priority = (flags & 0x800) != 0;
            data_alignment_indicator = (flags & 0x400) != 0;
            copyright = (flags & 0x200) != 0;
            original_or_copy = (flags & 0x100) != 0;
            PTS_flag = (flags & 0x80) != 0;
            DTS_flag = (flags & 0x40) != 0;

            ESCR_flag = (flags & 0x20) != 0;
            ES_rate_flag = (flags & 0x10) != 0;
            DSM_trick_mode_flag = (flags & 0x8) != 0;
            additional_copy_info_flag = (flags & 0x4) != 0;
            PES_CRC_flag = (flags & 0x2) != 0;
            PES_extension_flag = (flags & 0x1) != 0;

            PES_header_data_length = data[ptr++] & 0xff;

            if (PTS_flag) {
                pts = data[ptr++] & 0xf; // bits 32..30
                pts &= 0xfffffffffffffffeL;
                pts <<= 7;
                pts += data[ptr++]; // bits 29..22
                pts <<= 8;
                pts += data[ptr++]; // bits 21..15
                pts &= 0xfffffffffffffffeL;
                pts <<= 7;
                pts += data[ptr++]; // bits 14..7
                pts <<= 8;
                pts += data[ptr++]; // buts 6..0
                pts >>= 1;
            }

            if (DTS_flag) {
                dts = data[ptr++] & 0xf; // bits 32..30
                dts &= 0xfffffffffffffffeL;
                dts <<= 7;
                dts += data[ptr++]; // bits 29..22
                dts <<= 8;
                dts += data[ptr++]; // bits 21..15
                dts &= 0xfffffffffffffffeL;
                dts <<= 7;
                dts += data[ptr++]; // bits 14..7
                dts <<= 8;
                dts += data[ptr++]; // buts 6..0
                dts >>= 1;
            }


            if (ESCR_flag) {
                ptr += 6;
            }

            if (ES_rate_flag) {
                ptr += 3;
            }

            if (DSM_trick_mode_flag) {
                throw new VideoException("Trick mode flag not implemented");
            }

            if (copyright) {
                ptr += 1;
            }

            if (PES_CRC_flag) {
                ptr += 2;
            }

            if (this.PES_extension_flag) {
                PES_private_data_flag = ((data[ptr] & 0x80) != 0);
                pack_header_field_flag = ((data[ptr] & 0x40) != 0);
                program_packet_sequence_counter_flag = ((data[ptr] & 0x20) != 0);
                P_STD_buffer_flag = ((data[ptr] & 0x10) != 0);
                PES_extension_flag_2 = ((data[ptr++] & 0x1) != 0);

                if (PES_private_data_flag) {
                    System.arraycopy(data, ptr, this.PES_private_data, 0, 16);
                    ptr += 16;
                }
                
                if (pack_header_field_flag) {
                    pack_field_length = data[ptr++] & 0xff;
                }
                
                if (program_packet_sequence_counter_flag) {
                    program_packet_sequence_counter = data[ptr++] & 0x7f;
                    MPEG1_MPEG2_identifier = (data[ptr] & 0x40) != 0;
                    original_stuff_length = data[ptr++] & 0x3f;
                }
                
                if (P_STD_buffer_flag) {
                    P_STD_buffer_scale = (data[ptr] & 0x20) != 0;             // 1 bslbf
                    P_STD_buffer_size = data[ptr++] & 0x1f;
                    P_STD_buffer_size += data[ptr++] & 0xff;                 // 13 bslbf        
                }
                
                if (PES_extension_flag_2) {
                    throw new VideoException("PES Extension 2 not implemented");
                }
                
                throw new VideoException("PES Extension not tested");

            }

            dataOffset = ptr;
        }
    }

    /**
     * Gets a string with the information relative to the stream id.
     * @param id Stream Id
     * @return information
     */
    public String getStreamIdInfo(int id) {
        String info = "Unknown";
        if (id == PROGRAM_STREAM_MAP) {
            info = "program_stream_map";
        } else if (id == PRIVATE_STREAM_1) {
            info = "private_stream_1";
        } else if (id == PADDING_STREAM) {
            info = "padding_stream";
        } else if (id == PRIVATE_STREAM_2) {
            info = "private_stream_2";
        } else if ((id >= AUDIO_STREAM_S) && (id <= AUDIO_STREAM_E)) {
            info = "ISO/IEC 13818-3 or ISO/IEC 11172-3 or ISO/IEC 13818-7 or ISO/IEC 14496-3 audio stream";
        } else if ((id >= VIDEO_STREAM_S) && (id <= VIDEO_STREAM_E)) {
            info = "ITU-T Rec. H.262 | ISO/IEC 13818-2, ISO/IEC 11172-2, ISO/IEC 14496-2 or ITU-T Rec. H.264 | ISO/IEC 14496-10 video stream";
        } else if (id == ECM_STREAM) {
            info = "ECM_stream";
        } else if (id == EMM_STREAM) {
            info = "EMM_stream";
        } else if (id == DSMCC_STREAM) {
            info = "ITU-T Rec. H.222.0 | ISO/IEC 13818-1 Annex A or ISO/IEC 13818-6_DSMCC_stream";
        } else if (id == ISO_13522_STREAM) {
            info = "ISO/IEC_13522_stream";
        } else if (id == H222_1A_STREAM) {
            info = "ITU-T Rec. H.222.1 type A";
        } else if (id == H222_1B_STREAM) {
            info = "ITU-T Rec. H.222.1 type B";
        } else if (id == H222_1C_STREAM) {
            info = "ITU-T Rec. H.222.1 type C";
        } else if (id == H222_1D_STREAM) {
            info = "ITU-T Rec. H.222.1 type D";
        } else if (id == H222_1E_STREAM) {
            info = "ITU-T Rec. H.222.1 type E";
        } else if (id == ANC_STREAM) {
            info = "ancillary_stream";
        } else if (id == ISO_14496_SL_STREAM) {
            info = "ISO/IEC 14496-1_SL-packetized_stream";
        } else if (id == ISO_14496_FM_STREAM) {
            info = "ISO/IEC 14496-1_FlexMux_stream";
        } else if (id == METADATA_STREAM) {
            info = "metadata stream";
        } else if (id == EXTENDED_STREAM_ID) {
            info = "extended_stream_id";
        } else if (id == RESERVED_STREAM) {
            info = "reserved data stream";
        } else if (id == PROGRAM_STREAM_DIR) {
            info = "program_stream_directory";
        }
        return info;
    }

    public void writeElementary(FileOutputStream fos) throws IOException {
        fos.write(data, dataOffset, dataLength - dataOffset);
        boolean go = false;
        if (go) {
            fos.write(data, dataOffset, dataLength - dataOffset);
        }
    }

    /**
     * Gets PES payload
     * @return payload bytes
     */
    public byte[] getPayload() {
        byte[] pay = new byte[dataLength - dataOffset];
        System.arraycopy(data, dataOffset, pay, 0, pay.length);
        return pay;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Pes Packet:");
        sb.append(hasPayloadStart).append(" \n");
        sb.append(" +- packet_start_code_prefix : ").append(Integer.toHexString(packet_start_code_prefix)).append("\n");
        sb.append(" +- stream_id: ").append(Integer.toHexString(stream_id)).append("\n");
        sb.append(" +- PES_packet_length: ").append(PES_packet_length).append("\n");
        sb.append(" +- PES_scrambling_control: ").append(PES_scrambling_control).append("\n");
        sb.append(" +- PES_priority: ").append(PES_priority).append("\n");
        sb.append(" +- data_alignment_indicator: ").append(data_alignment_indicator).append("\n");
        sb.append(" +- copyright: ").append(copyright).append("\n");
        sb.append(" +- original_or_copy: ").append(original_or_copy).append("\n");
        sb.append(" +- PTS_flag: ").append(PTS_flag).append("\n");
        if (PTS_flag) {
            sb.append("    +- PTS = ").append(pts).append(" (").append(pts / 90000.0).append("s )\n");
        }
        sb.append(" +- DTS_flag: ").append(DTS_flag).append("\n");
        if (DTS_flag) {
            sb.append("    +- DTS = ").append(dts).append(" (").append(dts / 90000.0).append("s )\n");
        }
        sb.append(" +- ESCR_flag: ").append(ESCR_flag).append("\n");
        sb.append(" +- ES_rate_flag: ").append(ES_rate_flag).append("\n");
        sb.append(" +- DSM_trick_mode_flag: ").append(DSM_trick_mode_flag).append("\n");
        sb.append(" +- additional_copy_info_flag: ").append(additional_copy_info_flag).append("\n");
        sb.append(" +- PES_CRC_flag: ").append(PES_CRC_flag).append("\n");
        sb.append(" +- PES_extension_flag: ").append(PES_extension_flag).append("\n");

        if (PES_extension_flag) {
            sb.append("    +- PES_private_data_flag: ").append(PES_private_data_flag).append("\n");
            sb.append("    +- pack_header_field_flag: ").append(pack_header_field_flag).append("\n");
            sb.append("    +- program_packet_sequence_counter_flag: ").append(program_packet_sequence_counter_flag).append("\n");
            sb.append("    +- P_STD_buffer_flag: ").append(P_STD_buffer_flag).append("\n");
            sb.append("    +- PES_extension_flag_2: ").append(PES_extension_flag_2).append("\n");
        }

        sb.append(" +- PES_header_data_length: ").append(PES_header_data_length).append("\n");

        return sb.toString();
    }
}

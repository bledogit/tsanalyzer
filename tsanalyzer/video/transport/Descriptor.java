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

import tsanalyzer.video.transport.descriptors.LanguageDescriptor;
import tsanalyzer.video.util.TableBuilder;

/**
 * Generic descriptor base class
 * See  ISO/IEC standard 13818-1 or ITU-T Rec. H.222.0
 * 
 * @author Jose Mortensen <josemortensen@gmail.com>
 */
public class Descriptor {

    protected int descriptor_tag;
    protected int descriptor_length;
    protected byte[] data;

    /** Sets descriptor data
     * 
     * @param buffer  Buffer where the descriptor is located
     * @param pos     Position where the descriptor starts within buffer
     * @param len     Descriptor length
     */
    public void setData(byte[] buffer, int pos, int len) {
        data = new byte[len];

        System.arraycopy(buffer, pos, data, 0, len);
    }

    /**
     * Gets descriptor data
     * @return Descriptor data
     */
    public byte[] getData() {
        return data;
    }

    /** 
     * Gets descriptor length, as found on
     * the descriptor header.
     * @return descriptor_length field
     */
    public int getDescriptorLength() {
        return descriptor_length;
    }

    /**
     * Gets descriptor tag as found on the 
     * descriptor header
     * @return descriptor_tag field
     */
    public int getDescriptorTag() {
        return descriptor_tag;
    }

    /**
     * Gets a text description of the descriptor
     * based on the descriptor type parsed from the
     * stream.
     * Covers descriptors documented on the following standards:
     * 13818-1, dvb, scte57 and scte65
     * 
     * @return Descriptor type description
     */
    public String getDescriptorInfo() {
        switch (descriptor_tag) {
            case 0:
                return "Reserved";
            case 1:
                return "Reserved";
            case 2:
                return "video_stream_descriptor";
            case 3:
                return "audio_stream_descriptor";
            case 4:
                return "hierarchy_descriptor";
            case 5:
                return "registration_descriptor";
            case 6:
                return "data_stream_alignment_descriptor";
            case 7:
                return "target_background_grid_descriptor";
            case 8:
                return "video_window_descriptor";
            case 9:
                return "CA_descriptor";
            case 10:
                return "ISO_639_language_descriptor";
            case 11:
                return "system_clock_descriptor";
            case 12:
                return "multiplex_buffer_utilization_descriptor";
            case 13:
                return "copyright_descriptor";
            case 14:
                return "maximum_bitrate_descriptor";
            case 15:
                return "private_data_indicator_descriptor";
            case 16:
                return "smoothing_buffer_descriptor";
            case 17:
                return "STD_descriptor";
            case 18:
                return "IBP_descriptor";
            case 27:
                return "MPEG-4_video_descriptor";
            case 28:
                return "MPEG-4_audio_descriptor";
            case 29:
                return "IOD_descriptor";
            case 30:
                return "SL_descriptor";
            case 31:
                return "FMC_descriptor";
            case 32:
                return "external_ES_ID_descriptor";
            case 33:
                return "MuxCode_descriptor";
            case 34:
                return "FmxBufferSize_descriptor";
            case 35:
                return "multiplexbuffer_descriptor";
            case 36:
                return "content_labeling_descriptor";
            case 37:
                return "metadata_pointer_descriptor";
            case 38:
                return "metadata_descriptor";
            case 39:
                return "metadata_STD_descriptor";
            case 40:
                return "AVC video descriptor";
            case 41:
                return "IPMP_descriptor";
            case 42:
                return "AVC timing and HRD descriptor";
            case 43:
                return "MPEG-2_AAC_audio_descriptor";
            case 44:
                return "FlexMuxTiming Descriptor";
            // 44 = 0x2c

            //
            // DVB
            //
            case 0x40:
                return "Network Name (DVB)";
            case 0x41:
                return "Service List (DVB)";
            case 0x42:
                return "Stuffing (DVB)";
            case 0x43:
                return "Satellite Delivery (DVB)";
            case 0x44:
                return "Cable Delivery (DVB)";
            case 0x45:
                return "VBI Data (DVB)";
            case 0x46:
                return "VBI Teletext (DVB)";
            case 0x47:
                return "Bouquet Name (DVB)";
            case 0x48:
                return "Service (DVB)";
            case 0x49:
                return "Country Availability (DVB)";
            case 0x4a:
                return "Linkage (DVB)";
            case 0x4b:
                return "NVOD Reference (DVB)";
            case 0x4c:
                return "Time Shifted Service (DVB)";
            case 0x4d:
                return "Short Event (DVB)";
            case 0x4e:
                return "Extended Event (DVB)";
            case 0x4f:
                return "Time Shifted Event (DVB)";
            case 0x50:
                return "Component (DVB)";
            case 0x51:
                return "Mosaic (DVB)";
            case 0x52:
                return "Stream Indentifier (DVB)";
            case 0x53:
                return "Conditional Access (DVB)";
            case 0x54:
                return "Content (DVB)";
            case 0x55:
                return "Parental Rating (DVB)";
            case 0x56:
                return "Teletext (DVB)";
            case 0x57:
                return "Telephone (DVB)";
            case 0x58:
                return "Local Time Offset  (DVB)";
            case 0x59:
                return "Subtitling (DVB)";
            case 0x5a:
                return "Terrestrial Delivery(DVB)";
            case 0x5b:
                return "Multi Lingual Network Name(DVB)";
            case 0x5c:
                return "Multi Lingual Bouquet Name(DVB)";
            case 0x5d:
                return "Multi Lingual Service Name(DVB)";
            case 0x5e:
                return "Multi Lingual Component Name(DVB)";
            case 0x5f:
                return "Private Data Specifier(DVB)";
            case 0x60:
                return "Service Move(DVB)";
            case 0x61:
                return "Short Smoothing Buffer(DVB)";
            case 0x62:
                return "Frequency List(DVB)";
            case 0x63:
                return "Partial Transport Stream(DVB)";
            case 0x64:
                return "Data Broadcast (DVB)";
            case 0x65:
                return "CA Systen   (DVB)";
            case 0x66:
                return "Data Broadcast ID  (DVB)";
            case 0x67:
                return "Transport Stream    (DVB)";
            case 0x68:
                return "DSNG (DVB)";
            case 0x69:
                return "PDC (DVB)";
            case 0x6a:
                return "AC-3 Audio(DVB)";
            case 0x6b:
                return "Ancilliary Data(DVB)";
            case 0x6c:
                return "Cell List(DVB)";
            case 0x6d:
                return "Cell Frequency Link(DVB)";
            case 0x6e:
                return "Announcement Support(DVB)";
            case 0x73:
                return "DTS Audio(DVB)";
            //
            // non 13818-1, specified in SCTE 65
            //
            case 0x80:
                return "stuffing descriptor (SCTE65)";
            case 0x81:
                return "AC-3 Descriptor (SCTE65)";
            case 0x86:
                return "Caption service (SCTE65)";
            case 0x87:
                return "Content advisory (SCTE65)";
            case 0x93:
                return "Revision detection (SCTE65)";
            case 0x94:
                return "Two part channel number (SCTE65)";
            case 0x95:
                return "Channel Properties (SCTE65)";
            case 0x96:
                return "Daylight Savings Time (SCTE65)";
            case 0xA0:
                return "Extended Channel Name (SCTE65)";
            case 0xA2:
                return "Time Shifted Service (SCTE65)";
            case 0xA3:
                return "Component Name (SCTE65)";
            //
            // non 13818-1, specified in SCTE 57
            //
            case 0x82:
                return "Framerate (SCTE57)";
            case 0x83:
                return "Extended video (SCTE57) / Logical Channel (DVB)";
            case 0x84:
                return "Component name (SCTE57)";
            case 0x90:
                return "Frequency spec(SCTE57)";
            case 0x91:
                return "Modulation params(SCTE57)";
            case 0x92:
                return "Transport stream ID(SCTE57)";



            default:
                if ((descriptor_tag >= 19) && (descriptor_tag <= 26)) // 0x13 - 0x1A
                {
                    return "Defined in ISO/IEC 13818-6";
                } else if ((descriptor_tag >= 45) && (descriptor_tag <= 63)) {
                    return "ITU-T Rec H.222.0 | ISO/IEC 13818-1 Reserved";
                } else {
                    return "User Private";
                }

        }
    }

    /**
     * Descriptor factory. Constructs descriptor for the specified descriptor tag.
     * @param tag Descriptor tag
     * @return Descriptor
     */
    static Descriptor CreateDescriptor(int tag) {
        if (tag == 0xa) {
            return new LanguageDescriptor();
        } else {
            return new Descriptor(); // default or unknown descriptor
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getDescriptorInfo()).append(":\n");
        sb.append("  +-- Tag: ").append(Integer.toHexString(descriptor_tag)).append("\n");
        sb.append("  +-- Length: ").append(Integer.toHexString(descriptor_length)).append("\n");
        sb.append("  +-- Data: \n");

        sb.append(TableBuilder.BuildTable(data, 16, "      "));
        
        return sb.toString();
    }
}

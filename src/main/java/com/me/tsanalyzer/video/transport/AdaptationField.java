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

import com.me.tsanalyzer.video.util.TsLogger;

/**
 * Data structure to parse and represent the adaptation field contained 
 * within a transport stream packet. 
 * See  ISO/IEC standard 13818-1 or ITU-T Rec. H.222.0
 * 
 * @author Jose Mortensen <josemortensen@gmail.com>
 */
public class AdaptationField {

    protected boolean discontinuity_indicator;
    protected boolean random_access_indicator;
    protected boolean elementary_stream_priority_indicator;
    protected boolean PCR_flag;
    protected boolean OPCR_flag;
    protected boolean splicing_point_flag;
    protected boolean transport_private_data_flag;
    protected boolean adaptation_field_extension_flag;
    protected long pcr = -1;
    protected int field_length = 0;
    protected int ptr;

    public AdaptationField(byte[] buffer, int aptr) {
        ptr = aptr;
        int af_length = buffer[ptr++] & 0xff;
        field_length = af_length;

        if (af_length > 0) {
            int afptr = ptr;
            int flags = buffer[afptr++] & 0xff;
            af_length--;
            discontinuity_indicator = (flags & 0x80) != 0;
            random_access_indicator = (flags & 0x40) != 0;
            elementary_stream_priority_indicator = (flags & 0x20) != 0;
            PCR_flag = (flags & 0x10) != 0;
            OPCR_flag = (flags & 0x8) != 0;
            splicing_point_flag = (flags & 0x4) != 0;
            transport_private_data_flag = (flags & 0x2) != 0;
            adaptation_field_extension_flag = (flags & 0x1) != 0;

            if (PCR_flag) {
                pcr = buffer[afptr++] & 0xff;  // 8
                pcr <<= 8;
                pcr += buffer[afptr++] & 0xff; // 16
                pcr <<= 8;
                pcr += buffer[afptr++] & 0xff; // 24
                pcr <<= 8;
                pcr += buffer[afptr++] & 0xff; // 32
                pcr <<= 1;
                pcr += ((buffer[afptr] & 0xff) >> 7); // 33 bit
                pcr *= 300;
                
                int pcr_ext = (buffer[afptr++] & 0x1); // ext 1 bit
                pcr_ext <<= 8;
                pcr_ext += buffer[afptr++] & 0xff; // ext 1-9 bits

                pcr += pcr_ext;
                
                af_length -= 6;

            }
        }

        ptr += field_length;
    }

    @Override
    public String toString() {
        String s = "Adaptation Field:\n";
        s += " +- length: " + field_length + "\n";
        s += " +- discontinuity_indicator: " + discontinuity_indicator + "\n";
        s += " +- random_access_indicator: " + random_access_indicator + "\n";
        s += " +- elementary_stream_priority_indicator: " + elementary_stream_priority_indicator + "\n";
        s += " +- PCR_flag: " + PCR_flag + "\n";
        if (PCR_flag) s += " +- PCR : " + pcr + " ("+ pcr/90000.0/300.0 + " s)\n";
        s += " +- OPCR_flag: " + OPCR_flag + "\n";
        s += " +- splicing_point_flag: " + splicing_point_flag + "\n";
        s += " +- transport_private_data_flag: " + transport_private_data_flag + "\n";
        s += " +- adaptation_field_extension_flag: " + adaptation_field_extension_flag + "\n";

        return s;
    }
}

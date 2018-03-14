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
package tsanalyzer.video.transport.Observer;

/**
 * Transport stream parser progress.  Used to deliver information
 * about the status of the parser.
 * 
 * @author Jose Mortensen <josemortensen@gmail.com>
 */
public class ParseProgress {

    /** Parsing progress (percent) */
    public int progress;
    /** Number of packets parsed */
    public int packets;
    /** Number of bytes parsed */
    public long bytes;
    /** Last PCR received */
    public long pcr;

    
    public long getBytes() {
        return bytes;
    }

    public int getPackets() {
        return packets;
    }

    public long getPcr() {
        return pcr;
    }

    public int getProgress() {
        return progress;
    }

    public void setPcr(long pcr) {
        this.pcr = pcr;
    }

    public void reset() {
        progress = 0;
        packets = 0;
        bytes = 0;
    }
}

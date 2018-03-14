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

import tsnanalyzer.video.transport.Pcr;

/**
 * Transport Stream PCR observer.  Used to deliver the last PCR parsed from
 * the stream.
 * 
 * @author Jose Mortensen <josemortensen@gmail.com>
 */
public interface PcrObserver {
    /** Callback to inform about the PCR parsed from the stream */
    void Process(Pcr pcr);    
}

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
 * Transport Stream parsing progress observer. This class is used as
 * an interface to inform about the status of the parser.
 * 
 * @author Jose Mortensen <josemortensen@gmail.com>
 */
public interface ProgressObserver {
    /** Callback with information about the parser progress status */
     void process(ParseProgress progress);
}

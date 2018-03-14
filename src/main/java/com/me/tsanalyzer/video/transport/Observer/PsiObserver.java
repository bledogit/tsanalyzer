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

package com.me.tsanalyzer.video.transport.Observer;

import com.me.tsanalyzer.video.transport.PsiTable;

/**
 * Transport Stream PSI table observer. Interface for the parser to inform
 * about the last psi table parsed from the transport stream. The parser will 
 * deliver information about tables like the PAT, PMT, NIT, CAT, etc.
 * 
 * @author Jose Mortensen <josemortensen@gmail.com>
 */
public interface PsiObserver {
    /** Callback with information about the last table */
    void process(PsiTable psi);
}

/*******************************************************************************
 * Copyright (c) 2015 Luca Laurino.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Mozilla Public License, v. 2.0
 * which accompanies this distribution, and is available at
 * http://mozilla.org/MPL/2.0/
 ******************************************************************************/

package it.unisa.di.weblab.localcontext;

/**
 * @author Luca Laurino
 * @version $Id$
 */

import org.jgrapht.graph.DefaultEdge;

public class LabelEdge extends DefaultEdge {
	private String v1;
    private String v2;
    private String label;

    public LabelEdge(String v1, String v2, String label) {
        this.v1 = v1;
        this.v2 = v2;
        this.label = label;
    }

    public String getV1() {
        return v1;
    }

    public String getV2() {
        return v2;
    }

    public String toString() {
        return label;
    }
}



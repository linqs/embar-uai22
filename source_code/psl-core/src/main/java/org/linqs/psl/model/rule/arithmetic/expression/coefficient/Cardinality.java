/*
 * This file is part of the PSL software.
 * Copyright 2011-2015 University of Maryland
 * Copyright 2013-2020 The Regents of the University of California
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.linqs.psl.model.rule.arithmetic.expression.coefficient;

import org.linqs.psl.model.rule.arithmetic.expression.SummationVariable;

import java.util.Map;

/**
 * The number of substitutions made for a {@link SummationVariable} in a grounding.
 */
public class Cardinality extends Coefficient {
    protected final SummationVariable v;

    public Cardinality(SummationVariable v) {
        this.v = v;
    }

    public SummationVariable getSummationVariable() {
        return v;
    }

    @Override
    public float getValue(Map<SummationVariable, Integer> subs) {
        return subs.get(v).intValue();
    }

    @Override
    public String toString() {
        return "|" + v.getVariable().toString() + "|";
    }

    @Override
    public Coefficient simplify() {
        return this;
    }

    @Override
    public int hashCode() {
        return v.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }

        return this.v.equals(((Cardinality)other).v);
    }
}
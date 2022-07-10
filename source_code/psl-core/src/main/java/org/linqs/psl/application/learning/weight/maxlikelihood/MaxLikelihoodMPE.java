/*
 * This file is part of the PSL software.
 * Copyright 2011-2015 University of Maryland
 * Copyright 2013-2019 The Regents of the University of California
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
package org.linqs.psl.application.learning.weight.maxlikelihood;

import org.linqs.psl.application.learning.weight.TrainingMap;
import org.linqs.psl.application.learning.weight.VotedPerceptron;
import org.linqs.psl.database.Database;
import org.linqs.psl.database.atom.PersistedAtomManager;
import org.linqs.psl.grounding.GroundRuleStore;
import org.linqs.psl.model.Model;
import org.linqs.psl.model.rule.Rule;
import org.linqs.psl.model.rule.WeightedRule;
import org.linqs.psl.reasoner.Reasoner;
import org.linqs.psl.reasoner.term.TermGenerator;
import org.linqs.psl.reasoner.term.TermStore;

import java.util.List;

/**
 * Learns weights by optimizing the log likelihood of the data using
 * the voted perceptron algorithm.
 *
 * The expected total incompatibility is estimated with the total incompatibility
 * in the MPE state.
 *
 * The default implementations in VotedPerceptron are sufficient.
 */
public class MaxLikelihoodMPE extends VotedPerceptron {
    public MaxLikelihoodMPE(Model model, Database rvDB, Database observedDB) {
        this(model.getRules(), rvDB, observedDB);
    }

    public MaxLikelihoodMPE(List<Rule> rules, Database rvDB, Database observedDB) {
        super(rules, rvDB, observedDB, false);
    }

    public MaxLikelihoodMPE(List<Rule> rules, List<WeightedRule> mutableRules,
                            Database rvDB, Database observedDB,
                            Reasoner reasoner, GroundRuleStore groundRuleStore,
                            TermStore termStore, TermGenerator termGenerator,
                            PersistedAtomManager atomManager, TrainingMap trainingMap){
        super(rules, mutableRules, rvDB, observedDB, reasoner, groundRuleStore, termStore,
                termGenerator, atomManager, trainingMap);
    }
}

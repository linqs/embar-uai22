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
import org.linqs.psl.config.Config;
import org.linqs.psl.database.Database;
import org.linqs.psl.database.atom.PersistedAtomManager;
import org.linqs.psl.grounding.GroundRuleStore;
import org.linqs.psl.model.Model;
import org.linqs.psl.model.atom.RandomVariableAtom;
import org.linqs.psl.model.atom.GroundAtom;
import org.linqs.psl.model.rule.GroundRule;
import org.linqs.psl.model.rule.Rule;
import org.linqs.psl.model.rule.WeightedGroundRule;
import org.linqs.psl.model.rule.WeightedRule;
import org.linqs.psl.application.learning.weight.VotedPerceptron;
import org.linqs.psl.reasoner.Reasoner;
import org.linqs.psl.reasoner.term.TermGenerator;
import org.linqs.psl.reasoner.term.TermStore;
import org.linqs.psl.util.Parallel;
import org.linqs.psl.util.RandUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Learns weights by optimizing the piecewise-pseudo-log-likelihood of the data using
 * the voted perceptron algorithm.
 */
public class MaxPiecewisePseudoLikelihood extends VotedPerceptron {
    /**
     * Prefix of property keys used by this class.
     */
    public static final String CONFIG_PREFIX = "maxpiecewisepseudolikelihood";

    /**
     * Key for positive integer property.
     * MaxPiecewisePseudoLikelihood will sample this many values to approximate the expectations.
     */
    public static final String NUM_SAMPLES_KEY = CONFIG_PREFIX + ".numsamples";
    public static final int NUM_SAMPLES_DEFAULT = 100;

    private final int maxNumSamples;
    private int numSamples;
    private List<Map<RandomVariableAtom, List<WeightedGroundRule>>> ruleRandomVariableMap;

    // We need an RNG for each thread.
    private Random[] rands;

    public MaxPiecewisePseudoLikelihood(Model model, Database rvDB, Database observedDB) {
        this(model.getRules(), rvDB, observedDB);
    }
    public MaxPiecewisePseudoLikelihood(List<Rule> rules, List<WeightedRule> mutableRules,
                            Database rvDB, Database observedDB,
                            Reasoner reasoner, GroundRuleStore groundRuleStore,
                            TermStore termStore, TermGenerator termGenerator,
                            PersistedAtomManager atomManager, TrainingMap trainingMap){
        super(rules, mutableRules, rvDB, observedDB, reasoner, groundRuleStore, termStore,
                termGenerator, atomManager, trainingMap);

        maxNumSamples = Config.getInt(NUM_SAMPLES_KEY, NUM_SAMPLES_DEFAULT);
        numSamples = maxNumSamples;
        if (numSamples <= 0) {
            throw new IllegalArgumentException("Number of samples must be positive.");
        }

        rands = new Random[Parallel.getNumThreads()];
        for (int i = 0; i < Parallel.getNumThreads(); i++) {
            rands[i] = new Random(RandUtils.nextLong());
        }

//        ruleRandomVariableMap = null;

        averageSteps = false;
    }

    public MaxPiecewisePseudoLikelihood(List<Rule> rules, Database rvDB, Database observedDB) {
        super(rules, rvDB, observedDB, false);

        maxNumSamples = Config.getInt(NUM_SAMPLES_KEY, NUM_SAMPLES_DEFAULT);
        numSamples = maxNumSamples;
        if (numSamples <= 0) {
            throw new IllegalArgumentException("Number of samples must be positive.");
        }

        rands = new Random[Parallel.getNumThreads()];
        for (int i = 0; i < Parallel.getNumThreads(); i++) {
            rands[i] = new Random(RandUtils.nextLong());
        }

//        ruleRandomVariableMap = null;

        averageSteps = false;
    }

    @Override
    protected void postInitGroundModel() {
        populateRandomVariableMap();
    }

    /**
     * Create a dictonary for each unground rule.
     * The dictonary maps random variable atoms to the ground rules it participates in.
     * */
    private void populateRandomVariableMap() {
        ruleRandomVariableMap = new ArrayList<Map<RandomVariableAtom, List<WeightedGroundRule>>>();

        for (Rule rule : mutableRules) {
            Map<RandomVariableAtom, List<WeightedGroundRule>> groundRuleMap = new HashMap<RandomVariableAtom, List<WeightedGroundRule>>();
            for (GroundRule groundRule : groundRuleStore.getGroundRules(rule)) {
                for (GroundAtom atom : groundRule.getAtoms()) {
                    if (!(atom instanceof RandomVariableAtom)) {
                        continue;
                    }

                    RandomVariableAtom rva = (RandomVariableAtom)atom;
                    if (!groundRuleMap.containsKey(rva)) {
                        groundRuleMap.put(rva, new ArrayList<WeightedGroundRule>());
                    }

                    groundRuleMap.get(atom).add((WeightedGroundRule)groundRule);
                }
            }

            ruleRandomVariableMap.add(groundRuleMap);
        }
    }

    /**
     * Compute the expected incompatibility using the piecewisepseudolikelihood.
     * Use Monte Carlo integration to approximate epectations.
     */
    @Override
    protected void computeExpectedIncompatibility() {
        setLabeledRandomVariables();

        Parallel.count(mutableRules.size(), new Parallel.Worker<Integer>() {
            @Override
            public void work(int ruleIndex, Integer ignore) {
                WeightedRule rule = mutableRules.get(ruleIndex);
                Map<RandomVariableAtom, List<WeightedGroundRule>> groundRuleMap = ruleRandomVariableMap.get(ruleIndex);

                double accumulateIncompatibility = 0.0;
                double weight = rule.getWeight();

                for (RandomVariableAtom atom : groundRuleMap.keySet()) {
                    List<WeightedGroundRule> groundRules = groundRuleMap.get(atom);

                    double numerator = 0.0;
                    double denominator = 1e-6;

                    for (int sampleIndex = 0; sampleIndex < numSamples; sampleIndex++) {
                        float sample = rands[id].nextFloat();

                        double energy = 0;
                        for (int i = 0; i < groundRules.size(); i++) {
                            energy += groundRules.get(i).getIncompatibility(atom, sample);
                        }

                        numerator += Math.exp(-1.0 * weight * energy) * energy;
                        denominator += Math.exp(-1.0 * weight * energy);
                    }

                    accumulateIncompatibility += numerator / denominator;
                }

                expectedIncompatibility[ruleIndex] = accumulateIncompatibility;
            }
        });
    }

    @Override
    public double computeLoss() {
        setLabeledRandomVariables();

        final double[] losses = new double[mutableRules.size()];
        Parallel.count(mutableRules.size(), new Parallel.Worker<Integer>() {
            public void work(int ruleIndex, Integer ignore) {
                Map<RandomVariableAtom, List<WeightedGroundRule>> groundRuleMap = ruleRandomVariableMap.get(ruleIndex);
                WeightedRule rule = mutableRules.get(ruleIndex);
                double weight = rule.getWeight();

                for (RandomVariableAtom atom : groundRuleMap.keySet()) {
                    List<WeightedGroundRule> groundRules = groundRuleMap.get(atom);

                    double expInc = 0;
                    for (int sampleIndex = 0; sampleIndex < numSamples; sampleIndex++) {
                        float sample = rands[id].nextFloat();

                        double energy = 0;
                        for (int i = 0; i < groundRules.size(); i++) {
                            energy -= groundRules.get(i).getIncompatibility(atom, sample);
                        }
                        expInc += Math.exp(weight * energy);
                    }

                    double obsInc = 0;
                    for (int i = 0; i < groundRules.size(); i++) {
                        obsInc += (-1.0 * weight * groundRules.get(i).getIncompatibility());
                    }

                    expInc = -1.0 * Math.log(expInc / numSamples);
                    losses[ruleIndex] += (obsInc + expInc);
                }

                losses[ruleIndex] += (-0.5 * l2Regularization * Math.pow(weight, 2.0));
            }
        });

        double loss = 0;
        for (double ruleLoss : losses) {
            loss += ruleLoss;
        }

        return loss;
    }

    @Override
    protected void computeObservedIncompatibility() {
        setLabeledRandomVariables();

        // Compute the observed incompatibilities and numbers of groundings.
        for (int ruleIndex = 0; ruleIndex < mutableRules.size(); ruleIndex++) {
            WeightedRule rule = mutableRules.get(ruleIndex);
            Map<RandomVariableAtom, List<WeightedGroundRule>> groundRuleMap = ruleRandomVariableMap.get(ruleIndex);

            double weight = ((WeightedRule) rule).getWeight();
            double obsInc = 0;

            for (RandomVariableAtom atom : groundRuleMap.keySet()) {
                for (WeightedGroundRule groundRule : groundRuleMap.get(atom)) {
                    obsInc += groundRule.getIncompatibility();
                }
            }

            observedIncompatibility[ruleIndex] = obsInc;
        }
    }

    @Override
    public void setBudget(double budget) {
        super.setBudget(budget);

        numSamples = (int)Math.ceil(budget * maxNumSamples);
    }
}

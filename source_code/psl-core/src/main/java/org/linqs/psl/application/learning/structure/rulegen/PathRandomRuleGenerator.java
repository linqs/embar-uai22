package org.linqs.psl.application.learning.structure.rulegen;

import org.linqs.psl.application.learning.structure.template.PathRuleTemplate;
import org.linqs.psl.model.predicate.StandardPredicate;
import org.linqs.psl.model.rule.Rule;
import org.linqs.psl.util.RandUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PathRandomRuleGenerator extends PathRuleTemplate implements DRLRuleGenerator,RandomRuleGenerator{
    private static final Logger log = LoggerFactory.getLogger(PathRandomRuleGenerator.class);

    private List<StandardPredicate> localCopyPredicates;
    private List<StandardPredicate> localCopyOpenPredicates;
    private List<StandardPredicate> localCopyClosedPredicates;

    public PathRandomRuleGenerator(Set<StandardPredicate> closedPredicates, Set<StandardPredicate> openPredicates){
        this(closedPredicates, openPredicates, new HashMap<StandardPredicate, StandardPredicate>());
    }

    public PathRandomRuleGenerator(Set<StandardPredicate> closedPredicates, Set<StandardPredicate> openPredicates,
                                   Map<StandardPredicate, StandardPredicate> open2BlockPred) {
        super(closedPredicates, openPredicates, open2BlockPred);
        this.localCopyPredicates = new ArrayList<>(this.predicates);
        this.localCopyOpenPredicates = new ArrayList<>();
        this.localCopyClosedPredicates = new ArrayList<>();
        for (StandardPredicate p : this.closedPredicates) {
            this.localCopyClosedPredicates.add(p);
        }
        for (StandardPredicate p : this.openPredicates) {
            this.localCopyOpenPredicates.add(p);
        }
        for (StandardPredicate p : open2BlockPred.values()){
            this.localCopyClosedPredicates.remove(p);
            this.localCopyPredicates.remove(p);
        }
    }

    @Override
    public Rule generateRule(int ruleLen) {
        if (ruleLen < 3) {
            throw new RuntimeException("Rule length must be greater than 2.");
        }
        List<StandardPredicate> predicates = new ArrayList<>();
        List<Boolean> isNegated = new ArrayList<>();

        StandardPredicate headPredicate = this.localCopyOpenPredicates.get(RandUtils.nextInt(this.localCopyOpenPredicates.size()));
        List<StandardPredicate> validPredicates = new ArrayList<>();
        for (StandardPredicate p: this.localCopyPredicates) {
            if (p.getDomains()[0].equals(headPredicate.getDomains()[0])) {
                validPredicates.add(p);
            }
        }

        StandardPredicate nextPredicate = validPredicates.get(RandUtils.nextInt(validPredicates.size()));
        predicates.add(nextPredicate);
        isNegated.add(RandUtils.nextBoolean());
        String prevDomain = nextPredicate.getDomains()[1];

        for (int i = 1; i < ruleLen - 1; i++) {
            if (i == ruleLen-2) {
                List<StandardPredicate> finalValidPredicates = new ArrayList<>();
                for (StandardPredicate p: this.localCopyPredicates) {
                    if (p.getDomains()[0].equals(prevDomain) && p.getDomains()[1].equals(headPredicate.getDomains()[1])) {
                        finalValidPredicates.add(p);
                    }
                }
                if (finalValidPredicates.size() == 0) {
                    throw new RuntimeException("Could not generate the path rule.");
                }
                nextPredicate = finalValidPredicates.get(RandUtils.nextInt(finalValidPredicates.size()));
                predicates.add(nextPredicate);
                isNegated.add(RandUtils.nextBoolean());
//                isNegated.add(false);

                break;
            }
            validPredicates = new ArrayList<>();
            for (StandardPredicate p: this.localCopyPredicates) {
                if (p.getDomains()[0].equals(prevDomain)) {
                    validPredicates.add(p);
                }
            }
            if (validPredicates.size() == 0) {
                log.debug("Failed to generate a rule");
                return null;
            }
            nextPredicate = validPredicates.get(RandUtils.nextInt(validPredicates.size()));
            predicates.add(nextPredicate);
            isNegated.add(RandUtils.nextBoolean());
//            isNegated.add(false);
            prevDomain = nextPredicate.getDomains()[1];
        }
        predicates.add(headPredicate);
        isNegated.add(RandUtils.nextBoolean());
//        isNegated.add(false);
        return getRule(predicates, isNegated, true, 0);
    }

    @Override
    public Rule generateRule(StandardPredicate headPredicate, List<StandardPredicate> bodyPredicates,
                             List<Boolean> isNegated) {
        if (bodyPredicates == null || headPredicate == null || bodyPredicates.size() < 2) {
            throw new RuntimeException("Rule length must be greater than 2.");
        }
        List<StandardPredicate> predicates = new ArrayList<>();

        predicates.addAll(bodyPredicates);
        predicates.add(headPredicate);
        return getRule(predicates, isNegated, true, 0);
    }

    @Override
    public boolean isValid(StandardPredicate targetPredicate, List<StandardPredicate> rulePredicates, StandardPredicate action, int maxRuleLength) {
        int currentRuleLength = rulePredicates.size();
        boolean isValidFlag = false;
        //If it is the first predicate after target predicate
        if (action.getDomains().length == 2) {
            if (currentRuleLength == 1) {
                if (targetPredicate.getDomains()[0].equals(action.getDomains()[0])) {
                    isValidFlag = true;
                }
            } else  {
                if (rulePredicates.get(currentRuleLength - 1).getDomains()[1].equals(action.getDomains()[0])) {
                    if (rulePredicates.size() == maxRuleLength - 2) {
                        if (action.getDomains()[1].equals(targetPredicate.getDomains()[1])) {
                            isValidFlag = true;
                        }
                    }
                    else {
                        isValidFlag = true;
                    }
                }
            }
        }
        return isValidFlag;
    }
}


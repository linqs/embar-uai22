package org.linqs.psl.application.learning.structure.rulegen;

import org.linqs.psl.application.learning.structure.template.LocalRuleTemplate;
import org.linqs.psl.model.predicate.StandardPredicate;
import org.linqs.psl.model.rule.Rule;
import org.linqs.psl.util.RandUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class LocalRandomRuleGenerator extends LocalRuleTemplate implements DRLRuleGenerator,RandomRuleGenerator{
    private static final Logger log = LoggerFactory.getLogger(LocalRandomRuleGenerator.class);

    private List<StandardPredicate> localCopyPredicates;
    private List<StandardPredicate> localCopyOpenPredicates;
    private List<StandardPredicate> localCopyClosedPredicates;
    private Map<Integer, List<StandardPredicate>> arityToPredicates;


    public LocalRandomRuleGenerator(Set<StandardPredicate> closedPredicates, Set<StandardPredicate> openPredicates){
        this(closedPredicates, openPredicates, new HashMap<StandardPredicate, StandardPredicate>());
    }

    public LocalRandomRuleGenerator(Set<StandardPredicate> closedPredicates, Set<StandardPredicate> openPredicates,
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
        this.arityToPredicates = new HashMap<>();
        for (StandardPredicate p: this.localCopyClosedPredicates){
            List<StandardPredicate> preds = this.arityToPredicates.get(p.getArity());
            if (preds == null){
                preds = new ArrayList<>();
            }
            preds.add(p);
            this.arityToPredicates.put(p.getArity(), preds);
        }
    }

    @Override
    public Rule generateRule(int maxRuleLen) {
        if (maxRuleLen < 2) {
            throw new RuntimeException("Rule lenght must be greater than 2.");
        }
        int newRuleLen = RandUtils.nextInt(maxRuleLen-2) + 2;
        List<StandardPredicate> predicates = new ArrayList<>();
        List<Boolean> isNegated = new ArrayList<>();
        StandardPredicate headPredicate = this.localCopyOpenPredicates.get(RandUtils.nextInt(this.localCopyOpenPredicates.size()));
        List<StandardPredicate> possiblePredicates = new ArrayList<>();
        for (StandardPredicate p : this.localCopyClosedPredicates) {
            if (isValidDomain(headPredicate, p)){
                possiblePredicates.add(p);
            }
        }
        if (possiblePredicates.size() == 0) {
            log.debug("Failed to generate a rule");
            return null;
        }
        for (int i = 0; i < newRuleLen - 1; i++) {
            StandardPredicate p = possiblePredicates.get(RandUtils.nextInt(possiblePredicates.size()));
            predicates.add(p);
            isNegated.add(RandUtils.nextBoolean());
//            isNegated.add(false);
            possiblePredicates.remove(p);
            if (possiblePredicates.size() == 0) {
                break;
            }
        }
        predicates.add(headPredicate);
        isNegated.add(RandUtils.nextBoolean());
//        isNegated.add(false);
        return getRule(predicates, isNegated, true, 0);
    }


    @Override
    public Rule generateRule(StandardPredicate headPredicate, List<StandardPredicate> bodyPredicates,
                             List<Boolean> isNegated) {
        if (bodyPredicates == null || headPredicate == null || bodyPredicates.size() < 1) {
            throw new RuntimeException("Rule length must be greater than 2.");
        }
        List<StandardPredicate> predicates = new ArrayList<>();

        predicates.addAll(bodyPredicates);
        predicates.add(headPredicate);
        return getRule(predicates, isNegated, true, 0);
    }

    @Override
    public boolean isValid(StandardPredicate targetPredicate, List<StandardPredicate> rulePredicates, StandardPredicate action, int maxRuleLength) {


        String[] chosenDomains = targetPredicate.getDomains();
        if (Arrays.equals(action.getDomains(), chosenDomains)) {
            return true;
        }
        else {
            return false;
        }

    }
}

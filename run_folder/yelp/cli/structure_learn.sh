for fold_num in {0..4};do 
  for explain_ratio in 0 0.2 0.4 0.6 0.8 1.0;do
    java -jar ../../../source_code/psl-cli-2.2.0-SNAPSHOT.jar --strlearn org.linqs.psl.application.learning.structure.RandomStructureLearner --data yelp-learn-${fold_num}.data --model yelp.psl -D log4j.threshold=trace --eval org.linqs.psl.evaluation.statistics.ContinuousEvaluator -D stucturelearning.evaluator=org.linqs.psl.evaluation.statistics.ContinuousEvaluator -D stucturelearning.checkpoint=checkpoint_model_learned_${fold_num}_${explain_ratio} -D rsl.numrules=15 -D rsl.block=rated:rating,user:avg_user_rating,item:avg_item_rating,rated:sgd_rating,rated:bpmf_rating,rated:item_pearson_rating -D rsl.iter=100 -D rsl.predexp=predicate_explanation.txt -D rsl.expruleratio=${explain_ratio} -D ruletemplate.nonegate=true --postgres psl --output inferred-predicates-${fold_num}-${explain_ratio} --int-ids > new_learn_output_${fold_num}_${explain_ratio}
    mv yelp-learned.psl yelp-learned-${fold_num}-${explain_ratio}.psl
  done
done

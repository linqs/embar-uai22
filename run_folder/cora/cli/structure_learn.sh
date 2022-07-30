for i in 1 2 3 4 5;do
	java -jar psl-cli-2.2.0-SNAPSHOT.jar --strlearn org.linqs.psl.application.learning.structure.RandomStructureLearner --data cora-learn-${i}.data --model cora.psl -D log4j.threshold=trace --eval org.linqs.psl.evaluation.statistics.RankingEvaluator -D categoricalevaluator.defaultpredicate=samebib -D stucturelearning.evaluator=org.linqs.psl.evaluation.statistics.RankingEvaluator -D stucturelearning.checkpoint=checkpoint_model_learned-${i} -D rsl.numrules=50 -D rsl.iter=15 -D rsl.rulelen=4 -D ruletemplate.nonegate=true -D rsl.expruleratio=0 --postgres psl --output inferred-predicates-${i} > learn_output_${i};
	mv cora-learned.psl cora-learned-esms-${i}.psl;
done

for i in 1 2 3 4 5;
do
	java -jar psl-cli-2.2.0-SNAPSHOT.jar --infer --data cora-eval-${i}.data --model  cora-learned-${i}.psl -D log4j.threshold=trace --eval org.linqs.psl.evaluation.statistics.RankingEvaluator  -D admmreasoner.explain=false  --postgres ram --output inferred-predicates-eval-${i} > eval-${i}.output;
done

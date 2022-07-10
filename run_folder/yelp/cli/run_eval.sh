for fold_num in {0..4};do
  for explain_ratio in 0 0.2 0.4 0.6 0.8 1.0;do
    export folder=checkpoint_model_learned_${fold_num}_${explain_ratio}
    for m in $(ls ${folder});do 
      export model=${folder}/${m}
      export pmodel=$(echo ${model}|sed "s/\.psl//g")
      echo "Running : ${pmodel}"
      java -jar ../../../source_code/psl-cli-2.2.0-SNAPSHOT.jar --infer --data yelp-eval-${1}.data --model ${model} -D log4j.threshold=trace --eval org.linqs.psl.evaluation.statistics.ContinuousEvaluator -D admmreasoner.explain=true -D admmreasoner.explanationfile=${pmodel}_eval.explain --int-ids --postgres ram --output inferred-predicates-${pmodel}-eval > ${pmodel}_eval.output
    done
  done
done


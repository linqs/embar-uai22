# embar-uai22
Contains code, data and scripts to run and reproduce results for the UAI 2022 paper : Learning Explainable Templated Graphical Models

To build the jar, go the source_code directory and run 
```mvn clean install -DskipTests```

Then copy the generated psl-cli-2.2.0-SNAPSHOT.jar to the source_code directory by running
``` cp ./psl-cli/target/psl-cli-2.2.0-SNAPSHOT.jar .```

To run learning for each dataset
```
cd run_folder/{dataset_name}/data
./fetch_data.sh
cd ../cli
./structure_learn.sh
./run_eval.sh
```
dataset_name = [yelp, lastfm, cora]

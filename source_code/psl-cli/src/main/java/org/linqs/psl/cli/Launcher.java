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
package org.linqs.psl.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.linqs.psl.application.inference.InferenceApplication;
import org.linqs.psl.application.learning.structure.AbstractStructureLearningApplication;
import org.linqs.psl.application.learning.weight.WeightLearningApplication;
import org.linqs.psl.database.DataStore;
import org.linqs.psl.database.Database;
import org.linqs.psl.database.Partition;
import org.linqs.psl.database.rdbms.RDBMSDataStore;
import org.linqs.psl.database.rdbms.driver.DatabaseDriver;
import org.linqs.psl.database.rdbms.driver.H2DatabaseDriver;
import org.linqs.psl.database.rdbms.driver.H2DatabaseDriver.Type;
import org.linqs.psl.database.rdbms.driver.PostgreSQLDriver;
import org.linqs.psl.evaluation.statistics.Evaluator;
import org.linqs.psl.grounding.GroundRuleStore;
import org.linqs.psl.model.Model;
import org.linqs.psl.model.atom.GroundAtom;
import org.linqs.psl.model.predicate.StandardPredicate;
import org.linqs.psl.model.rule.GroundRule;
import org.linqs.psl.model.rule.Rule;
import org.linqs.psl.model.rule.UnweightedGroundRule;
import org.linqs.psl.model.rule.WeightedGroundRule;
import org.linqs.psl.model.term.Constant;
import org.linqs.psl.parser.CommandLineLoader;
import org.linqs.psl.parser.ModelLoader;
import org.linqs.psl.util.Reflection;
import org.linqs.psl.util.StringUtils;
import org.linqs.psl.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Launches PSL from the command line.
 * Supports inference and supervised parameter learning.
 */
public class Launcher {
    public static final String MODEL_FILE_EXTENSION = ".psl";

    // Reserved partition names.
    public static final String PARTITION_NAME_OBSERVATIONS = "observations";
    public static final String PARTITION_NAME_TARGET = "targets";
    public static final String PARTITION_NAME_LABELS = "truth";

    private static final Logger log = LoggerFactory.getLogger(Launcher.class);
    private CommandLine parsedOptions;

    private Launcher(CommandLine givenOptions) {
        this.parsedOptions = givenOptions;
    }

    /**
     * Set up the DataStore.
     */
    private DataStore initDataStore() {
        String dbPath = CommandLineLoader.DEFAULT_H2_DB_PATH;
        boolean useH2 = true;

        if (parsedOptions.hasOption(CommandLineLoader.OPTION_DB_H2_PATH)) {
            dbPath = parsedOptions.getOptionValue(CommandLineLoader.OPTION_DB_H2_PATH);
        } else if (parsedOptions.hasOption(CommandLineLoader.OPTION_DB_POSTGRESQL_NAME)) {
            dbPath = parsedOptions.getOptionValue(CommandLineLoader.OPTION_DB_POSTGRESQL_NAME, CommandLineLoader.DEFAULT_POSTGRES_DB_NAME);
            useH2 = false;
        }

        DatabaseDriver driver = null;
        if (useH2) {
            driver = new H2DatabaseDriver(Type.Disk, dbPath, true);
        } else {
            driver = new PostgreSQLDriver(dbPath, true);
        }

        return new RDBMSDataStore(driver);
    }

    private Set<StandardPredicate> loadData(DataStore dataStore) {
        log.info("Loading data");

        Set<StandardPredicate> closedPredicates;
        try {
            String path = parsedOptions.getOptionValue(CommandLineLoader.OPTION_DATA);
            closedPredicates = DataLoader.load(dataStore, path, parsedOptions.hasOption(CommandLineLoader.OPTION_INT_IDS));
        } catch (ConfigurationException | FileNotFoundException ex) {
            throw new RuntimeException("Failed to load data.", ex);
        }

        log.info("Data loading complete");

        return closedPredicates;
    }

    /**
     * Possible output the ground rules.
     * 
     * @param path where to output the ground rules. Use stdout if null.
     */
    private void outputGroundRules(GroundRuleStore groundRuleStore, String path, boolean includeSatisfaction) {
        PrintStream stream = System.out;
        boolean closeStream = false;

        if (path != null) {
            try {
                stream = new PrintStream(path);
                closeStream = true;
            } catch (IOException ex) {
                log.error(String.format("Unable to open file (%s) for ground rules, using stdout instead.", path), ex);
            }
        }

        // Write a header.
        String header = StringUtils.join("\t", "Weight", "Squared?", "Rule");
        if (includeSatisfaction) {
            header = StringUtils.join("\t", header, "Satisfaction");
        }
        stream.println(header);

        for (GroundRule groundRule : groundRuleStore.getGroundRules()) {
            String row = "";
            double satisfaction = 0.0;

            if (groundRule instanceof WeightedGroundRule) {
                WeightedGroundRule weightedGroundRule = (WeightedGroundRule)groundRule;
                row = StringUtils.join("\t",
                        "" + weightedGroundRule.getWeight(), "" + weightedGroundRule.isSquared(), groundRule.baseToString());
                satisfaction = 1.0 - weightedGroundRule.getIncompatibility();
            } else {
                UnweightedGroundRule unweightedGroundRule = (UnweightedGroundRule)groundRule;
                row = StringUtils.join("\t", ".", "" + false, groundRule.baseToString());
                satisfaction = 1.0 - unweightedGroundRule.getInfeasibility();
            }

            if (includeSatisfaction) {
                row = StringUtils.join("\t", row, "" + satisfaction);
            }

            stream.println(row);
        }

        if (closeStream) {
            stream.close();
        }
    }

    /**
     * Run inference.
     * The caller is responsible for closing the database.
     */
    private Database runInference(Model model, DataStore dataStore, Set<StandardPredicate> closedPredicates, String inferenceName) {
        log.info("Starting inference with class: {}", inferenceName);

        // Create database.
        Partition targetPartition = dataStore.getPartition(PARTITION_NAME_TARGET);
        Partition observationsPartition = dataStore.getPartition(PARTITION_NAME_OBSERVATIONS);
        Database database = dataStore.getDatabase(targetPartition, closedPredicates, observationsPartition);

        InferenceApplication inferenceApplication =
                InferenceApplication.getInferenceApplication(inferenceName, model, database);

        if (parsedOptions.hasOption(CommandLineLoader.OPTION_OUTPUT_GROUND_RULES_LONG)) {
            String path = parsedOptions.getOptionValue(CommandLineLoader.OPTION_OUTPUT_GROUND_RULES_LONG);
            outputGroundRules(inferenceApplication.getGroundRuleStore(), path, false);
        }

        boolean commitAtoms = !parsedOptions.hasOption(CommandLineLoader.OPTION_SKIP_ATOM_COMMIT_LONG);

        inferenceApplication.inference(commitAtoms);

        if (parsedOptions.hasOption(CommandLineLoader.OPTION_OUTPUT_SATISFACTION_LONG)) {
            String path = parsedOptions.getOptionValue(CommandLineLoader.OPTION_OUTPUT_SATISFACTION_LONG);
            outputGroundRules(inferenceApplication.getGroundRuleStore(), path, true);
        }

        log.info("Inference Complete");

        // Output the results.
        outputResults(database, dataStore, closedPredicates);

        return database;
    }

    private void outputResults(Database database, DataStore dataStore, Set<StandardPredicate> closedPredicates) {
        // Set of open predicates
        Set<StandardPredicate> openPredicates = dataStore.getRegisteredPredicates();
        openPredicates.removeAll(closedPredicates);

        // If we are just writing to the console, use a more human-readable format.
        if (!parsedOptions.hasOption(CommandLineLoader.OPTION_OUTPUT_DIR)) {
            for (StandardPredicate openPredicate : openPredicates) {
                for (GroundAtom atom : database.getAllGroundRandomVariableAtoms(openPredicate)) {
                    System.out.println(atom.toString() + " = " + atom.getValue());
                }
            }

            return;
        }

        // If we have an output directory, then write a different file for each predicate.
        String outputDirectoryPath = parsedOptions.getOptionValue(CommandLineLoader.OPTION_OUTPUT_DIR);
        File outputDirectory = new File(outputDirectoryPath);

        // mkdir -p
        outputDirectory.mkdirs();

        for (StandardPredicate openPredicate : openPredicates) {
            try {
                FileWriter predFileWriter = new FileWriter(new File(outputDirectory, openPredicate.getName() + ".txt"));
                StringBuilder row = new StringBuilder();

                for (GroundAtom atom : database.getAllGroundRandomVariableAtoms(openPredicate)) {
                    row.setLength(0);

                    for (Constant term : atom.getArguments()) {
                        row.append(term.rawToString());
                        row.append("\t");
                    }
                    row.append(Double.toString(atom.getValue()));
                    row.append("\n");

                    predFileWriter.write(row.toString());
                }

                predFileWriter.close();
            } catch (IOException ex) {
                log.error("Exception writing predicate {}", openPredicate);
            }
        }
    }

    private void learnWeights(Model model, DataStore dataStore, Set<StandardPredicate> closedPredicates, String wlaName) {
        log.info("Starting weight learning with learner: " + wlaName);

        Partition targetPartition = dataStore.getPartition(PARTITION_NAME_TARGET);
        Partition observationsPartition = dataStore.getPartition(PARTITION_NAME_OBSERVATIONS);
        Partition truthPartition = dataStore.getPartition(PARTITION_NAME_LABELS);

        Database randomVariableDatabase = dataStore.getDatabase(targetPartition, closedPredicates, observationsPartition);
        Database observedTruthDatabase = dataStore.getDatabase(truthPartition, dataStore.getRegisteredPredicates());

        WeightLearningApplication learner = WeightLearningApplication.getWLA(wlaName, model.getRules(),
                randomVariableDatabase, observedTruthDatabase);
        learner.learn();

        if (parsedOptions.hasOption(CommandLineLoader.OPTION_OUTPUT_GROUND_RULES_LONG)) {
            String path = parsedOptions.getOptionValue(CommandLineLoader.OPTION_OUTPUT_GROUND_RULES_LONG);
            outputGroundRules(learner.getGroundRuleStore(), path, false);
        }

        learner.close();

        if (parsedOptions.hasOption(CommandLineLoader.OPTION_OUTPUT_SATISFACTION_LONG)) {
            String path = parsedOptions.getOptionValue(CommandLineLoader.OPTION_OUTPUT_SATISFACTION_LONG);
            outputGroundRules(learner.getGroundRuleStore(), path, true);
        }

        randomVariableDatabase.close();
        observedTruthDatabase.close();

        log.info("Weight learning complete");

        String modelFilename = parsedOptions.getOptionValue(CommandLineLoader.OPTION_MODEL);

        String learnedFilename;
        int prefixPos = modelFilename.lastIndexOf(MODEL_FILE_EXTENSION);
        if (prefixPos == -1) {
            learnedFilename = modelFilename + MODEL_FILE_EXTENSION;
        } else {
            learnedFilename = modelFilename.substring(0, prefixPos) + "-learned" + MODEL_FILE_EXTENSION;
        }
        log.info("Writing learned model to {}", learnedFilename);

        String outModel = model.asString();

        // Remove excess parens.
        outModel = outModel.replaceAll("\\( | \\)", "");

        try (FileWriter learnedFileWriter = new FileWriter(new File(learnedFilename))) {
            learnedFileWriter.write(outModel);
        } catch (IOException ex) {
            log.error("Failed to write learned model:\n" + outModel);
            throw new RuntimeException("Failed to write learned model to: " + learnedFilename, ex);
        }
    }



    private void learnRules(List<Rule> rules, DataStore dataStore, Set<StandardPredicate> closedPredicates, String slaName) {
        log.info("Starting rules learning with learner: " + slaName);

        Partition targetPartition = dataStore.getPartition(PARTITION_NAME_TARGET);
        Partition observationsPartition = dataStore.getPartition(PARTITION_NAME_OBSERVATIONS);
        Partition truthPartition = dataStore.getPartition(PARTITION_NAME_LABELS);

        Database randomVariableDatabase = dataStore.getDatabase(targetPartition, closedPredicates, observationsPartition);
        Database observedTruthDatabase = dataStore.getDatabase(truthPartition, dataStore.getRegisteredPredicates());

        Set<StandardPredicate> openPredicates = dataStore.getRegisteredPredicates();
        openPredicates.removeAll(closedPredicates);
        AbstractStructureLearningApplication learner = AbstractStructureLearningApplication.getSLA(slaName, rules,
                randomVariableDatabase, observedTruthDatabase, closedPredicates, openPredicates);
        log.info("Started rules learning with learner: " + slaName);
        learner.learn();

        if (parsedOptions.hasOption(CommandLineLoader.OPTION_OUTPUT_GROUND_RULES_LONG)) {
            String path = parsedOptions.getOptionValue(CommandLineLoader.OPTION_OUTPUT_GROUND_RULES_LONG);
            outputGroundRules(learner.getGroundRuleStore(), path, false);
        }

        Model model = learner.getBestModel();

        learner.close();

        if (parsedOptions.hasOption(CommandLineLoader.OPTION_OUTPUT_SATISFACTION_LONG)) {
            String path = parsedOptions.getOptionValue(CommandLineLoader.OPTION_OUTPUT_SATISFACTION_LONG);
            outputGroundRules(learner.getGroundRuleStore(), path, true);
        }

        randomVariableDatabase.close();
        observedTruthDatabase.close();

        log.info("Rules learning complete");

        String modelFilename = parsedOptions.getOptionValue(CommandLineLoader.OPTION_MODEL);

        String learnedFilename;
        int prefixPos = modelFilename.lastIndexOf(MODEL_FILE_EXTENSION);
        if (prefixPos == -1) {
            learnedFilename = modelFilename + MODEL_FILE_EXTENSION;
        } else {
            learnedFilename = modelFilename.substring(0, prefixPos) + "-learned" + MODEL_FILE_EXTENSION;
        }
        log.info("Writing learned model to {}", learnedFilename);

        String outModel = model.asString();

        // Remove excess parens.
        outModel = outModel.replaceAll("\\( | \\)", "");

        try (FileWriter learnedFileWriter = new FileWriter(new File(learnedFilename))) {
            learnedFileWriter.write(outModel);
        } catch (IOException ex) {
            log.error("Failed to write learned model:\n" + outModel);
            throw new RuntimeException("Failed to write learned model to: " + learnedFilename, ex);
        }
    }


    /**
     * Run eval.
     * 
     * @param predictionDatabase can be passed in to speed up evaluation. If null, one will be created and closed internally.
     */
    private void evaluation(DataStore dataStore, Database predictionDatabase, Set<StandardPredicate> closedPredicates, String evalClassName) {
        log.info("Starting evaluation with class: {}.", evalClassName);

        // Set of open predicates
        Set<StandardPredicate> openPredicates = dataStore.getRegisteredPredicates();
        openPredicates.removeAll(closedPredicates);

        // Create database.
        Partition targetPartition = dataStore.getPartition(PARTITION_NAME_TARGET);
        Partition observationsPartition = dataStore.getPartition(PARTITION_NAME_OBSERVATIONS);
        Partition truthPartition = dataStore.getPartition(PARTITION_NAME_LABELS);

        boolean closePredictionDB = false;
        if (predictionDatabase == null) {
            closePredictionDB = true;
            predictionDatabase = dataStore.getDatabase(targetPartition, closedPredicates, observationsPartition);
        }

        Database truthDatabase = dataStore.getDatabase(truthPartition, dataStore.getRegisteredPredicates());

        Evaluator evaluator = (Evaluator)Reflection.newObject(evalClassName);

        for (StandardPredicate targetPredicate : openPredicates) {
            // Before we run evaluation, ensure that the truth database actaully has instances of the target predicate.
            if (truthDatabase.countAllGroundAtoms(targetPredicate) == 0) {
                log.info("Skipping evaluation for {} since there are no ground truth atoms", targetPredicate);
                continue;
            }

            evaluator.compute(predictionDatabase, truthDatabase, targetPredicate, !closePredictionDB);
            log.info("Evaluation results for {} -- {}", targetPredicate.getName(), evaluator.getAllStats());
        }

        if (closePredictionDB) {
            predictionDatabase.close();
        }
        truthDatabase.close();
    }

    private Model loadModel(DataStore dataStore) {
        log.info("Loading model from {}", parsedOptions.getOptionValue(CommandLineLoader.OPTION_MODEL));

        Model model = null;

        try (FileReader reader = new FileReader(new File(parsedOptions.getOptionValue(CommandLineLoader.OPTION_MODEL)))) {
            model = ModelLoader.load(dataStore, reader);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load model from file: " + parsedOptions.getOptionValue(CommandLineLoader.OPTION_MODEL), ex);
        }

        log.debug("Model:");
        for (Rule rule : model.getRules()) {
            log.debug("   " + rule);
        }

        log.info("Model loading complete");

        return model;
    }

    private void run() {
        log.info("Running PSL CLI Version {}", Version.getFull());
        DataStore dataStore = initDataStore();

        // Load data
        Set<StandardPredicate> closedPredicates = loadData(dataStore);

        // Load model
        Model model = null;
        List<Rule> rules = new ArrayList<>();
        if (parsedOptions.hasOption(CommandLineLoader.OPERATION_STR_LEARN)) {
            try {
                model = loadModel(dataStore);
                rules = model.getRules();
            } catch (RuntimeException e) {
                log.debug("Initialize with an empty model.");
            }
        } else {
            model = loadModel(dataStore);
        }

        // Inference
        Database evalDB = null;
        if (parsedOptions.hasOption(CommandLineLoader.OPERATION_INFER)) {
            evalDB = runInference(model, dataStore, closedPredicates, parsedOptions.getOptionValue(CommandLineLoader.OPERATION_INFER, CommandLineLoader.DEFAULT_IA));
        } else if (parsedOptions.hasOption(CommandLineLoader.OPERATION_LEARN)) {
            learnWeights(model, dataStore, closedPredicates, parsedOptions.getOptionValue(CommandLineLoader.OPERATION_LEARN, CommandLineLoader.DEFAULT_WLA));
        } else if (parsedOptions.hasOption(CommandLineLoader.OPERATION_STR_LEARN)) {
            learnRules(rules, dataStore, closedPredicates, parsedOptions.getOptionValue(CommandLineLoader.OPERATION_STR_LEARN, CommandLineLoader.DEFAULT_SLA));
        } else {
            throw new IllegalArgumentException("No valid operation provided.");
        }

        // Evaluation
        if (parsedOptions.hasOption(CommandLineLoader.OPTION_EVAL)) {
            for (String evaluator : parsedOptions.getOptionValues(CommandLineLoader.OPTION_EVAL)) {
                evaluation(dataStore, evalDB, closedPredicates, evaluator);
            }

            log.info("Evaluation complete.");
        }

        if (evalDB != null) {
            evalDB.close();
        }

        dataStore.close();
    }

    private static boolean isCommandLineValid(CommandLine givenOptions) {
        // Return early in case of help or version option.
        if (givenOptions.hasOption(CommandLineLoader.OPTION_HELP) ||
                givenOptions.hasOption(CommandLineLoader.OPTION_VERSION)) {
            return false;
        }

        // Data and model are required.
        // (We don't enforce them earlier so we can have successful runs with help and version.)
        HelpFormatter helpFormatter = new HelpFormatter();
        if (!givenOptions.hasOption(CommandLineLoader.OPTION_DATA)) {
            System.out.println(String.format("Missing required option: --%s/-%s.", CommandLineLoader.OPTION_DATA_LONG, CommandLineLoader.OPTION_DATA));
            helpFormatter.printHelp("psl", CommandLineLoader.getOptions(), true);
            return false;
        }
        if (!givenOptions.hasOption(CommandLineLoader.OPTION_MODEL)) {
            System.out.println(String.format("Missing required option: --%s/-%s.", CommandLineLoader.OPTION_MODEL_LONG, CommandLineLoader.OPTION_MODEL));
            helpFormatter.printHelp("psl", CommandLineLoader.getOptions(), true);
            return false;
        }

        if (!givenOptions.hasOption(CommandLineLoader.OPERATION_INFER) &&
                (!givenOptions.hasOption(CommandLineLoader.OPERATION_LEARN)) &&
                (!givenOptions.hasOption(CommandLineLoader.OPERATION_STR_LEARN))) {
            System.out.println(String.format("Missing required option: --%s/-%s.", CommandLineLoader.OPERATION_INFER_LONG, CommandLineLoader.OPERATION_INFER));
            helpFormatter.printHelp("psl", CommandLineLoader.getOptions(), true);
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        main(args, false);
    }

    public static void main(String[] args, boolean rethrow) {
        try {
            CommandLineLoader commandLineLoader = new CommandLineLoader(args);
            CommandLine givenOptions = commandLineLoader.getParsedOptions();
            // Return for command line parse errors or PSL errors.
            if (( givenOptions == null) || (!(isCommandLineValid(givenOptions)))) {
                return;
            }
            Launcher pslLauncher = new Launcher(givenOptions);
            pslLauncher.run();
        } catch (Exception ex) {
            if (rethrow) {
                throw new RuntimeException("Failed to run CLI: " + ex.getMessage(), ex);
            } else {
                System.err.println("Unexpected exception!");
                ex.printStackTrace(System.err);
                System.exit(1);
            }
        }
    }
}

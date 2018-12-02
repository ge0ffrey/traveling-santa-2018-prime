/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplannerdelirium.tsp2018prime.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplannerdelirium.tsp2018prime.domain.TspSolution;
import org.optaplannerdelirium.tsp2018prime.persistence.TspExporter;
import org.optaplannerdelirium.tsp2018prime.persistence.TspFileIO;
import org.optaplannerdelirium.tsp2018prime.persistence.TspImporter;
import org.optaplannerdelirium.tsp2018prime.swingui.TspPanel;

public class Tsp2018PrimeApp extends CommonApp<TspSolution> {

    public static final String SOLVER_CONFIG
            = "org/optaplannerdelirium/tsp2018prime/solver/tsp2018PrimeSolverConfig.xml";

    public static final String DATA_DIR_NAME = "tsp2018prime";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new Tsp2018PrimeApp().init();
    }

    public Tsp2018PrimeApp() {
        super("Traveling salesman 2018 Prime",
                "Kaggle compo",
                SOLVER_CONFIG, DATA_DIR_NAME,
                null);
    }

    @Override
    protected TspPanel createSolutionPanel() {
        return new TspPanel();
    }

    @Override
    public TspFileIO createSolutionFileIO() {
        return new TspFileIO();
    }

    @Override
    protected AbstractSolutionImporter[] createSolutionImporters() {
        return new AbstractSolutionImporter[]{
                new TspImporter()
        };
    }

    @Override
    protected AbstractSolutionExporter createSolutionExporter() {
        return new TspExporter();
    }

}

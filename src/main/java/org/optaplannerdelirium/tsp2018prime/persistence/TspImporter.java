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

package org.optaplannerdelirium.tsp2018prime.persistence;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.optaplanner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.optaplannerdelirium.tsp2018prime.domain.Domicile;
import org.optaplannerdelirium.tsp2018prime.domain.Location;
import org.optaplannerdelirium.tsp2018prime.domain.TspSolution;
import org.optaplannerdelirium.tsp2018prime.domain.Visit;

public class TspImporter extends AbstractTxtSolutionImporter<TspSolution> {

    public static final String INPUT_FILE_SUFFIX = "csv";

    @Override
    public String getInputFileSuffix() {
        return INPUT_FILE_SUFFIX;
    }

    @Override
    public TxtInputBuilder<TspSolution> createTxtInputBuilder() {
        return new TspInputBuilder();
    }

    public static class TspInputBuilder extends TxtInputBuilder<TspSolution> {

        private TspSolution tspSolution;



        @Override
        public TspSolution readSolution() throws IOException {
            tspSolution = new TspSolution();
            tspSolution.setId(0L);
            readConstantLine("CityId,X,Y");

            Domicile domicile = null;
            ArrayList<Visit> visitList = new ArrayList<>(200_000);
            String line = bufferedReader.readLine();
            List<Long> primeIdList = new ArrayList<>(10_000);
            while (line != null) {
                String[] tokens = line.split(",");
                if (tokens.length != 3) {
                    throw new IllegalStateException("Invalid line (" + line + ").");
                }
                Long id = Long.parseLong(tokens[0]);
                Location location = new Location(id, Double.parseDouble(tokens[2]), Double.parseDouble(tokens[1]));
                if (domicile == null) {
                    domicile = new Domicile(id, location);
                } else {
                    boolean prime = isPrime(primeIdList, id);
                    visitList.add(new Visit(id, location, prime));
                }

                line = bufferedReader.readLine();
            }
            tspSolution.setDomicile(domicile);
            tspSolution.setVisitList(visitList);


            BigInteger possibleSolutionSize = factorial(tspSolution.getVisitList().size());
            logger.info("TspSolution {} has {} locations with a search space of {}.",
                    getInputId(),
                    tspSolution.getVisitList().size() + 1,
                    getFlooredPossibleSolutionSize(possibleSolutionSize));
            return tspSolution;
        }

        private boolean isPrime(List<Long> primeIdList, Long id) {
            if (id < 2L) {
                return false;
            } else {
                for (Long primeId : primeIdList) {
                    if (id % primeId == 0) {
                        return false;
                    }
                }
                primeIdList.add(id);
                return true;
            }
        }
    }

}

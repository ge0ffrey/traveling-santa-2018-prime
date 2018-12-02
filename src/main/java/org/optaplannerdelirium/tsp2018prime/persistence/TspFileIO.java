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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
import org.optaplannerdelirium.tsp2018prime.domain.Domicile;
import org.optaplannerdelirium.tsp2018prime.domain.Location;
import org.optaplannerdelirium.tsp2018prime.domain.Standstill;
import org.optaplannerdelirium.tsp2018prime.domain.TspSolution;
import org.optaplannerdelirium.tsp2018prime.domain.Visit;

public class TspFileIO implements SolutionFileIO<TspSolution> {

    @Override
    public String getInputFileExtension() {
        return "txt";
    }

    @Override
    public String getOutputFileExtension() {
        return "txt";
    }

    @Override
    public TspSolution read(File inputSolutionFile) {
        TspSolution solution = new TspSolution();
        solution.setId(0L);
        try (BufferedReader reader = new BufferedReader(new FileReader(inputSolutionFile))) {
            String line = reader.readLine();
            if (!line.equals("id,latitude,longitude[,prime[,index]]")) {
                throw new IllegalStateException("Invalide line (" + line + ").");
            }
            line = reader.readLine();
            if (!line.equals("Domicile:")) {
                throw new IllegalStateException("Invalid line (" + line + ").");
            }
            line = reader.readLine();
            String[] domicileTokens = line.split(",");
            if (domicileTokens.length != 3) {
                throw new IllegalStateException("Invalid line (" + line + ").");
            }
            long domicileId = Long.parseLong(domicileTokens[0]);
            Domicile domicile = new Domicile(domicileId,
                    new Location(domicileId, Double.parseDouble(domicileTokens[1]), Double.parseDouble(domicileTokens[2])));
            solution.setDomicile(domicile);
            ArrayList<Visit> visitList = new ArrayList<>(200_000);
            line = reader.readLine();
            if (!line.equals("Assigned:")) {
                throw new IllegalStateException("Invalid line (" + line + ").");
            }
            line = reader.readLine();
            Standstill previous = domicile;
            while (!line.equals("")) {
                String[] tokens = line.split(",");
                if (tokens.length != 5) {
                    throw new IllegalStateException("Invalid line (" + line + ").");
                }
                long id = Long.parseLong(tokens[0]);
                Visit visit = new Visit(id,
                        new Location(id, Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2])), Boolean.parseBoolean(tokens[3]));
                visit.setIndex(Integer.parseInt(tokens[4]));
                visit.setPreviousStandstill(domicile);
                domicile.setNextVisit(visit);
                visitList.add(visit);
                previous = visit;
                line = reader.readLine();
            }
            line = reader.readLine();
            if (!line.equals("Unassigned:")) {
                throw new IllegalStateException("Invalid line (" + line + ").");
            }
            line = reader.readLine();
            while (line != null) {
                String[] tokens = line.split(",");
                if (tokens.length != 4) {
                    throw new IllegalStateException("Invalid line (" + line + ").");
                }
                long id = Long.parseLong(tokens[0]);
                Visit visit = new Visit(id,
                        new Location(id, Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2])), Boolean.parseBoolean(tokens[3]));
                visitList.add(visit);
                line = reader.readLine();
            }
            solution.setVisitList(visitList);
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Failed reading inputSolutionFile ("
                    + inputSolutionFile + ").", e);
        }
        return solution;
    }

    @Override
    public void write(TspSolution solution, File outputSolutionFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputSolutionFile))) {
            writer.write("id,latitude,longitude[,prime[,index]]\n");
            writer.write("Domicile:\n");
            Domicile domicile = solution.getDomicile();
            writer.write(domicile.getId() + "," + domicile.getLocation().getLatitude() + "," + domicile.getLocation().getLongitude() + "\n");
            writer.write("Assigned:\n");
            Visit assignedVisit = domicile.getNextVisit();
            while (assignedVisit != null) {
                writer.write(assignedVisit.getId() + "," + assignedVisit.getLocation().getLatitude() + "," + assignedVisit.getLocation().getLongitude()
                        + "," + assignedVisit.isPrime() + "," + assignedVisit.getIndex() + "\n");
                assignedVisit = assignedVisit.getNextVisit();
            }
            writer.write("\nUnassigned:\n");
            for (Visit visit : solution.getVisitList()) {
                if (visit.getPreviousStandstill() == null) {
                    writer.write(visit.getId() + "," + visit.getLocation().getLatitude() + "," + visit.getLocation().getLongitude()
                            + "," + visit.isPrime() + "\n");
                }
            }

        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Failed writing outputSolutionFile ("
                    + outputSolutionFile + ") for solution (" + solution + ").", e);
        }
    }

}

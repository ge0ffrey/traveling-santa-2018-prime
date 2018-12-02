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

package org.optaplannerdelirium.tsp2018prime.domain.solver;

import java.util.Objects;

import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplannerdelirium.tsp2018prime.domain.Standstill;
import org.optaplannerdelirium.tsp2018prime.domain.Visit;

public class IndexUpdatingVariableListener implements VariableListener<Visit> {

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, Visit visit) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, Visit visit) {
        updateIndex(scoreDirector, visit);
    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, Visit visit) {
        // Do nothing
    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, Visit visit) {
        updateIndex(scoreDirector, visit);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, Visit visit) {
        // Do nothing
    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, Visit visit) {
        // Do nothing
    }

    protected void updateIndex(ScoreDirector scoreDirector, Visit sourceVisit) {
        Standstill previousStandstill = sourceVisit.getPreviousStandstill();
        Integer index = previousStandstill == null ? null
                : previousStandstill.getIndex() + 1;
        Visit shadowVisit = sourceVisit;
        while (shadowVisit != null && !Objects.equals(shadowVisit.getIndex(), index)) {
            scoreDirector.beforeVariableChanged(shadowVisit, "index");
            shadowVisit.setIndex(index);
            scoreDirector.afterVariableChanged(shadowVisit, "index");
            shadowVisit = shadowVisit.getNextVisit();
            index = index == null ? null : index + 1;
        }
    }

}

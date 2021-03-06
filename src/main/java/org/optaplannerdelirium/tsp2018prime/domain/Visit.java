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

package org.optaplannerdelirium.tsp2018prime.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplannerdelirium.tsp2018prime.domain.solver.DomicileAngleVisitDifficultyWeightFactory;
import org.optaplannerdelirium.tsp2018prime.domain.solver.DomicileDistanceStandstillStrengthWeightFactory;
import org.optaplannerdelirium.tsp2018prime.domain.solver.IndexUpdatingVariableListener;

@PlanningEntity(difficultyWeightFactoryClass = DomicileAngleVisitDifficultyWeightFactory.class)
public class Visit extends AbstractPersistable implements Standstill {

    private Location location;
    private boolean prime;

    // Planning variables: changes during planning, between score calculations.
    private Standstill previousStandstill;
    private Visit nextVisit;
    private Integer index;

    public Visit() {
    }

    public Visit(Long id, Location location, boolean prime) {
        super(id);
        this.location = location;
        this.prime = prime;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isPrime() {
        return prime;
    }

    public void setPrime(boolean prime) {
        this.prime = prime;
    }

    @PlanningVariable(valueRangeProviderRefs = {"domicileRange", "visitRange"},
            graphType = PlanningVariableGraphType.CHAINED,
            strengthWeightFactoryClass = DomicileDistanceStandstillStrengthWeightFactory.class)
    public Standstill getPreviousStandstill() {
        return previousStandstill;
    }

    public void setPreviousStandstill(Standstill previousStandstill) {
        this.previousStandstill = previousStandstill;
    }

    @Override
    public Visit getNextVisit() {
        return nextVisit;
    }

    public void setNextVisit(Visit nextVisit) {
        this.nextVisit = nextVisit;
    }

    @Override
    @CustomShadowVariable(variableListenerClass = IndexUpdatingVariableListener.class,
            sources = {@PlanningVariableReference(variableName = "previousStandstill")})
    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    /**
     * @return a positive number, the distance multiplied by 1000 to avoid floating point arithmetic rounding errors
     */
    public long getDistanceFromPreviousStandstill() {
        if (previousStandstill == null) {
            return 0L;
        }
        return getDistanceFrom(previousStandstill);
    }

    /**
     * @param standstill never null
     * @return a positive number, the distance multiplied by 1000 to avoid floating point arithmetic rounding errors
     */
    public long getDistanceFrom(Standstill standstill) {
        return standstill.getLocation().getDistanceTo(location);
    }

    /**
     * @param standstill never null
     * @return a positive number, the distance multiplied by 1000 to avoid floating point arithmetic rounding errors
     */
    @Override
    public long getDistanceTo(Standstill standstill) {
        return location.getDistanceTo(standstill.getLocation());
    }

}

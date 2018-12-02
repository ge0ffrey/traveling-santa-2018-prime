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

import org.optaplanner.examples.common.domain.AbstractPersistable;

public class Domicile extends AbstractPersistable implements Standstill {

    private Location location;

    private Visit nextVisit;

    public Domicile() {
    }

    public Domicile(Long id, Location location) {
        super(id);
        this.location = location;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public Visit getNextVisit() {
        return nextVisit;
    }

    @Override
    public void setNextVisit(Visit nextVisit) {
        this.nextVisit = nextVisit;
    }

    @Override
    public Integer getIndex() {
        return -1;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    /**
     * @param standstill never null
     * @return a positive number, the distance multiplied by 1000 to avoid floating point arithmetic rounding errors
     */
    @Override
    public long getDistanceTo(Standstill standstill) {
        return location.getDistanceTo(standstill.getLocation());
    }

}

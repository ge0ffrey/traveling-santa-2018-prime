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

package org.optaplannerdelirium.tsp2018prime.swingui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JPanel;

import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.examples.common.swingui.latitudelongitude.LatitudeLongitudeTranslator;
import org.optaplanner.swing.impl.TangoColorFactory;
import org.optaplannerdelirium.tsp2018prime.domain.Location;
import org.optaplannerdelirium.tsp2018prime.domain.TspSolution;
import org.optaplannerdelirium.tsp2018prime.domain.Visit;

public class TspWorldPanel extends JPanel {

    private static final int TEXT_SIZE = 12;
    private static final int LOCATION_NAME_TEXT_SIZE = 8;
    private static final NumberFormat NUMBER_FORMAT = new DecimalFormat("#,##0.00");

    private final TspPanel tspPanel;

    private BufferedImage canvas = null;
    private LatitudeLongitudeTranslator translator = null;

    public TspWorldPanel(TspPanel tspPanel) {
        this.tspPanel = tspPanel;
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // TODO Not thread-safe during solving
                TspSolution tspSolution = TspWorldPanel.this.tspPanel.getSolution();
                if (tspSolution != null) {
                    resetPanel(tspSolution);
                }
            }
        });
    }

    public void resetPanel(TspSolution tspSolution) {
        translator = new LatitudeLongitudeTranslator();
        Location domicileLocation = tspSolution.getDomicile().getLocation();
        translator.addCoordinates(domicileLocation.getLatitude(), domicileLocation.getLongitude());
        for (Visit visit : tspSolution.getVisitList()) {
            Location location = visit.getLocation();
            translator.addCoordinates(location.getLatitude(), location.getLongitude());
        }

        Dimension size = getSize();
        double width = size.getWidth();
        double height = size.getHeight();
        translator.prepareFor(width, height);

        Graphics2D g = createCanvas(width, height);
        g.setFont(g.getFont().deriveFont((float) LOCATION_NAME_TEXT_SIZE));
        List<Visit> visitList = tspSolution.getVisitList();
        for (Visit visit : visitList) {
            if (visit.isPrime()) {
                g.setColor(TangoColorFactory.ORANGE_2);
            } else {
                g.setColor(TangoColorFactory.PLUM_2);
            }
            Location location = visit.getLocation();
            int x = translator.translateLongitudeToX(location.getLongitude());
            int y = translator.translateLatitudeToY(location.getLatitude());
            g.fillRect(x - 1, y - 1, 3, 3);
        }
        g.setColor(TangoColorFactory.ALUMINIUM_4);
        int domicileX = translator.translateLongitudeToX(domicileLocation.getLongitude());
        int domicileY = translator.translateLatitudeToY(domicileLocation.getLatitude());
        g.fillRect(domicileX - 2, domicileY - 2, 5, 5);
        Set<Visit> needsBackToDomicileLineSet = new HashSet<>(visitList);
        for (Visit trailingVisit : visitList) {
            if (trailingVisit.getPreviousStandstill() instanceof Visit) {
                needsBackToDomicileLineSet.remove(trailingVisit.getPreviousStandstill());
            }
        }
        g.setColor(TangoColorFactory.CHOCOLATE_1);
        for (Visit visit : visitList) {
            if (visit.getPreviousStandstill() != null) {
                Location previousLocation = visit.getPreviousStandstill().getLocation();
                Location location = visit.getLocation();
                translator.drawRoute(g, previousLocation.getLongitude(), previousLocation.getLatitude(),
                        location.getLongitude(), location.getLatitude(),
                        true, false);
                // Back to domicile line
                if (needsBackToDomicileLineSet.contains(visit)) {
                    translator.drawRoute(g, location.getLongitude(), location.getLatitude(),
                            domicileLocation.getLongitude(), domicileLocation.getLatitude(),
                            true, true);
                }
            }
        }
        // Legend
        g.setColor(TangoColorFactory.ALUMINIUM_4);
        g.fillRect(5, (int) height - 15 - TEXT_SIZE, 5, 5);
        g.drawString("Domicile", 15, (int) height - 10 - TEXT_SIZE);
        g.setColor(TangoColorFactory.PLUM_2);
        g.fillRect(6, (int) height - 9, 3, 3);
        g.drawString("Visit", 15, (int) height - 5);
        g.setColor(TangoColorFactory.ALUMINIUM_5);
        String locationsSizeString = (tspSolution.getVisitList().size() + 1) + " locations";
        g.drawString(locationsSizeString,
                ((int) width - g.getFontMetrics().stringWidth(locationsSizeString)) / 2, (int) height - 5);
        // Show soft score
        g.setColor(TangoColorFactory.ORANGE_3);
        SimpleLongScore score = tspSolution.getScore();
        if (score != null) {
            String distanceString = Long.toString(score.getScore());
            g.setFont(g.getFont().deriveFont(Font.BOLD, (float) TEXT_SIZE * 2));
            g.drawString(distanceString,
                    (int) width - g.getFontMetrics().stringWidth(distanceString) - 10, (int) height - 15 - 2 * TEXT_SIZE);
        }
        repaint();
    }

    public void updatePanel(TspSolution tspSolution) {
        resetPanel(tspSolution);
    }

    private Graphics2D createCanvas(double width, double height) {
        int canvasWidth = (int) Math.ceil(width) + 1;
        int canvasHeight = (int) Math.ceil(height) + 1;
        canvas = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = canvas.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, canvasWidth, canvasHeight);
        return g;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (canvas != null) {
            g.drawImage(canvas, 0, 0, this);
        }
    }

}

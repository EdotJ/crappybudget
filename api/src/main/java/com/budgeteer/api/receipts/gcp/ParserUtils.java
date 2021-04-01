package com.budgeteer.api.receipts.gcp;

import com.budgeteer.api.core.Pair;
import com.budgeteer.api.receipts.gcp.model.response.BoundingPoly;
import com.budgeteer.api.receipts.gcp.model.response.TextAnnotation;
import com.budgeteer.api.receipts.gcp.model.response.Vertex;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public class ParserUtils {

    protected Orientation getOrientation(TextAnnotation textAnnotation) {
        BoundingPoly boundingPoly = textAnnotation.getBoundingPoly();
        List<Vertex> vertices = boundingPoly.getVertices();
        Pair<Double, Double> centerCoords = getCenterVertex(vertices);

        return determineOrientation(centerCoords, vertices.get(0));
    }

    private Pair<Double, Double> getCenterVertex(List<Vertex> vertices) {
        double centerX = 0;
        double centerY = 0;
        for (Vertex v : vertices) {
            centerX += v.getX();
            centerY += v.getY();
        }
        centerX = centerX / 4;
        centerY = centerY / 4;
        return new Pair<>(centerX, centerY);
    }

    private Orientation determineOrientation(Pair<Double, Double> centerCoords, Vertex vertex) {
        if (vertex.getX() < centerCoords.getFirst()) {
            if (vertex.getY() < centerCoords.getSecond()) {
                return Orientation.UP;
            } else {
                return Orientation.LEFT;
            }
        } else {
            if (vertex.getY() < centerCoords.getSecond()) {
                return Orientation.RIGHT;
            } else {
                return Orientation.DOWN;
            }
        }
    }
}

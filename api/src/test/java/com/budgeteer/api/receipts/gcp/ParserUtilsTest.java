package com.budgeteer.api.receipts.gcp;

import com.budgeteer.api.receipts.gcp.model.response.BoundingPoly;
import com.budgeteer.api.receipts.gcp.model.response.TextAnnotation;
import com.budgeteer.api.receipts.gcp.model.response.Vertex;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
@Tag("Unit")
public class ParserUtilsTest {

    @Inject
    ParserUtils parserUtils;

    @Test
    void testOrientationShouldReturnRightward() {
        TextAnnotation textAnnotation = new TextAnnotation();
        BoundingPoly boundingPoly = new BoundingPoly();
        boundingPoly.getVertices().add(new Vertex(1010, 621));
        boundingPoly.getVertices().add(new Vertex(1017, 792));
        boundingPoly.getVertices().add(new Vertex(946, 795));
        boundingPoly.getVertices().add(new Vertex(939, 624));
        textAnnotation.setBoundingPoly(boundingPoly);
        Orientation orientation = parserUtils.getOrientation(textAnnotation);
        assertEquals(Orientation.RIGHT, orientation);
    }

    @Test
    void testOrientationShouldReturnUpward() {
        TextAnnotation textAnnotation = new TextAnnotation();
        BoundingPoly boundingPoly = new BoundingPoly();
        boundingPoly.getVertices().add(new Vertex(148, 2673));
        boundingPoly.getVertices().add(new Vertex(326, 2673));
        boundingPoly.getVertices().add(new Vertex(326, 2752));
        boundingPoly.getVertices().add(new Vertex(148, 2752));
        textAnnotation.setBoundingPoly(boundingPoly);
        Orientation orientation = parserUtils.getOrientation(textAnnotation);
        assertEquals(Orientation.UP, orientation);
    }

    @Test
    void testOrientationShouldReturnDownward() {
        TextAnnotation textAnnotation = new TextAnnotation();
        BoundingPoly boundingPoly = new BoundingPoly();
        boundingPoly.getVertices().add(new Vertex(2401, 1008));
        boundingPoly.getVertices().add(new Vertex(2227, 1011));
        boundingPoly.getVertices().add(new Vertex(2226, 945));
        boundingPoly.getVertices().add(new Vertex(2400, 942));
        textAnnotation.setBoundingPoly(boundingPoly);
        Orientation orientation = parserUtils.getOrientation(textAnnotation);
        assertEquals(Orientation.DOWN, orientation);
    }

    @Test
    void testOrientationShouldReturnLeftward() {
        TextAnnotation textAnnotation = new TextAnnotation();
        BoundingPoly boundingPoly = new BoundingPoly();
        boundingPoly.getVertices().add(new Vertex(2840, 2134));
        boundingPoly.getVertices().add(new Vertex(2839, 1957));
        boundingPoly.getVertices().add(new Vertex(2897, 1957));
        boundingPoly.getVertices().add(new Vertex(2898, 2134));
        textAnnotation.setBoundingPoly(boundingPoly);
        Orientation orientation = parserUtils.getOrientation(textAnnotation);
        assertEquals(Orientation.LEFT, orientation);
    }
}

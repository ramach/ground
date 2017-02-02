package edu.berkeley.ground.util;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class JGraphTUtilsTest {
  private DirectedGraph<String, DefaultEdge> graph;

  @Before
  public void setup() {
    this.graph = JGraphTUtils.createGraph();
  }

  @Test
  public void testAddVertex() {
    String testId = "testId";

    JGraphTUtils.addVertex(this.graph, testId);

    assertEquals(1, this.graph.vertexSet().size());
    assertTrue(this.graph.containsVertex(testId));
  }

  @Test
  public void testAddEdge() {
    String fromId = "testFrom";
    String toId = "testTo";
    JGraphTUtils.addVertex(this.graph, fromId);
    JGraphTUtils.addVertex(this.graph, toId);

    JGraphTUtils.addEdge(this.graph, fromId, toId);

    assertEquals(1, this.graph.edgeSet().size());
    DefaultEdge edge = this.graph.getEdge(fromId, toId);

    assertNotNull(edge);
  }

  @Test
  public void testDFS() {
    String idOne = "idOne";
    String idTwo = "idTwo";
    String idThree = "idThree";
    JGraphTUtils.addVertex(this.graph, idOne);
    JGraphTUtils.addVertex(this.graph, idTwo);
    JGraphTUtils.addVertex(this.graph, idThree);

    JGraphTUtils.addEdge(this.graph, idOne, idTwo);
    JGraphTUtils.addEdge(this.graph, idTwo, idThree);

    List<String> reachable = JGraphTUtils.runDFS(this.graph, idOne);

    assertEquals(3, reachable.size());
    assert (reachable).contains(idTwo);
    assert (reachable).contains(idThree);
  }
}

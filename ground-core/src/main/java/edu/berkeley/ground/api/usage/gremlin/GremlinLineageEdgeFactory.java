package edu.berkeley.ground.api.usage.gremlin;

import edu.berkeley.ground.api.usage.LineageEdge;
import edu.berkeley.ground.api.usage.LineageEdgeFactory;
import edu.berkeley.ground.api.versions.GroundType;
import edu.berkeley.ground.api.versions.gremlin.GremlinItemFactory;
import edu.berkeley.ground.db.DBClient.GroundDBConnection;
import edu.berkeley.ground.db.DbDataContainer;
import edu.berkeley.ground.db.GremlinClient;
import edu.berkeley.ground.db.GremlinClient.GremlinConnection;
import edu.berkeley.ground.exceptions.GroundException;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GremlinLineageEdgeFactory extends LineageEdgeFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(GremlinLineageEdgeFactory.class);
    private GremlinClient dbClient;

    private GremlinItemFactory itemFactory;

    public GremlinLineageEdgeFactory(GremlinItemFactory itemFactory, GremlinClient dbClient) {
        this.dbClient = dbClient;
        this.itemFactory = itemFactory;
    }

    public LineageEdge create(String name) throws GroundException {
        GremlinConnection connection = this.dbClient.getConnection();

        try {
            String uniqueId = "LineageEdges." + name;

            this.itemFactory.insertIntoDatabase(connection, uniqueId);

            List<DbDataContainer> insertions = new ArrayList<>();
            insertions.add(new DbDataContainer("name", GroundType.STRING, name));
            insertions.add(new DbDataContainer("id", GroundType.STRING, uniqueId));

            connection.addVertex("LineageEdges", insertions);

            connection.commit();
            LOGGER.info("Created lineage edge " + name + ".");

            return LineageEdgeFactory.construct(uniqueId, name);
        } catch (GroundException e) {
            connection.abort();

            throw e;
        }
    }

    public LineageEdge retrieveFromDatabase(String name) throws GroundException {
        GremlinConnection connection = this.dbClient.getConnection();

        try {
            List<DbDataContainer> predicates = new ArrayList<>();
            predicates.add(new DbDataContainer("name", GroundType.STRING, name));
            Vertex vertex = connection.getVertex(predicates);
            String id = vertex.property("id").value().toString();

            connection.commit();
            LOGGER.info("Retrieved lineage edge " + name + ".");

            return LineageEdgeFactory.construct(id, name);
        } catch (GroundException e) {
            connection.abort();

            throw e;
        }
    }

    public void update(GroundDBConnection connection, String itemId, String childId, Optional<String> parent) throws GroundException {
        this.itemFactory.update(connection, itemId, childId, parent);
    }
}
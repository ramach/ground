/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.berkeley.ground.api.models.neo4j;

import edu.berkeley.ground.api.models.Graph;
import edu.berkeley.ground.api.models.GraphFactory;
import edu.berkeley.ground.api.models.Tag;
import edu.berkeley.ground.api.versions.GroundType;
import edu.berkeley.ground.api.versions.neo4j.Neo4jItemFactory;
import edu.berkeley.ground.db.DbDataContainer;
import edu.berkeley.ground.db.Neo4jClient;
import edu.berkeley.ground.exceptions.EmptyResultException;
import edu.berkeley.ground.exceptions.GroundDBException;
import edu.berkeley.ground.exceptions.GroundException;
import edu.berkeley.ground.util.IdGenerator;

import org.neo4j.driver.internal.value.StringValue;
import org.neo4j.driver.v1.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Neo4jGraphFactory extends GraphFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jGraphFactory.class);

  private final Neo4jClient dbClient;
  private final Neo4jItemFactory itemFactory;

  private final IdGenerator idGenerator;

  public Neo4jGraphFactory(Neo4jClient dbClient, Neo4jItemFactory itemFactory, IdGenerator idGenerator) {
    this.dbClient = dbClient;
    this.itemFactory = itemFactory;
    this.idGenerator = idGenerator;
  }

  public Graph create(String name, Map<String, Tag> tags) throws GroundException {
    try {
      long uniqueId = this.idGenerator.generateItemId();

      List<DbDataContainer> insertions = new ArrayList<>();
      insertions.add(new DbDataContainer("name", GroundType.STRING, name));
      insertions.add(new DbDataContainer("id", GroundType.LONG, uniqueId));

      this.dbClient.addVertex("Graph", insertions);
      this.itemFactory.insertIntoDatabase(uniqueId, tags);

      this.dbClient.commit();
      LOGGER.info("Created graph " + name + ".");

      return GraphFactory.construct(uniqueId, name, tags);
    } catch (GroundDBException e) {
      this.dbClient.abort();

      throw e;
    }
  }

  public Graph retrieveFromDatabase(String name) throws GroundException {
    try {
      List<DbDataContainer> predicates = new ArrayList<>();
      predicates.add(new DbDataContainer("name", GroundType.STRING, name));

      Record record;
      try {
        record = this.dbClient.getVertex(predicates);
      } catch (EmptyResultException e) {
        throw new GroundDBException("No Graph found with name " + name + ".");
      }

      long id = record.get("v").asNode().get("id").asLong();
      Map<String, Tag> tags = this.itemFactory.retrieveFromDatabase(id).getTags();

      this.dbClient.commit();
      LOGGER.info("Retrieved graph " + name + ".");

      return GraphFactory.construct(id, name, tags);
    } catch (GroundDBException e) {
      this.dbClient.abort();

      throw e;
    }
  }

  public void update(long itemId, long childId, List<Long> parentIds) throws GroundException {
    this.itemFactory.update(itemId, childId, parentIds);
  }
}

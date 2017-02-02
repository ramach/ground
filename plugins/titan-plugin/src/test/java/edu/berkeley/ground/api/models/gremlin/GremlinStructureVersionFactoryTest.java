package edu.berkeley.ground.api.models.gremlin;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.berkeley.ground.api.GremlinTest;
import edu.berkeley.ground.api.models.StructureVersion;
import edu.berkeley.ground.api.versions.GroundType;
import edu.berkeley.ground.exceptions.GroundException;

import static org.junit.Assert.*;

public class GremlinStructureVersionFactoryTest extends GremlinTest {

  public GremlinStructureVersionFactoryTest() throws GroundException {
    super();
  }

  @Test
  public void testStructureVersionCreation() throws GroundException {
    String structureName = "testStructure";
    String structureId = super.factories.getStructureFactory().create(structureName).getId();

    Map<String, GroundType> structureVersionAttributes = new HashMap<>();
    structureVersionAttributes.put("intfield", GroundType.INTEGER);
    structureVersionAttributes.put("boolfield", GroundType.BOOLEAN);
    structureVersionAttributes.put("strfield", GroundType.STRING);

    String structureVersionId = super.factories.getStructureVersionFactory().create(
        structureId, structureVersionAttributes, new ArrayList<>()).getId();

    StructureVersion retrieved = super.factories.getStructureVersionFactory()
        .retrieveFromDatabase(structureVersionId);

    assertEquals(structureId, retrieved.getStructureId());
    Map<String, GroundType> retrievedAttributes = retrieved.getAttributes();

    for (String key : structureVersionAttributes.keySet()) {
      assert (retrievedAttributes).containsKey(key);
      assertEquals(structureVersionAttributes.get(key), retrievedAttributes.get(key));
    }
  }
}

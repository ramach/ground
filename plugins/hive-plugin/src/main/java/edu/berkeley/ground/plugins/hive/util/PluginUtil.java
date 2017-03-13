package edu.berkeley.ground.plugins.hive.util;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import edu.berkeley.ground.api.models.Structure;
import edu.berkeley.ground.api.models.StructureVersion;
import edu.berkeley.ground.api.versions.GroundType;
import edu.berkeley.ground.exceptions.GroundException;
import edu.berkeley.ground.plugins.hive.GroundReadWrite;

public class PluginUtil {

  private PluginUtil() {
  }

  public static <T> T fromJSON(String json, Class<T> clazz) {
    Gson gson = new Gson();
    return gson.fromJson(json.replace("\\", ""), clazz);
  }

  public static String toJSON(Object object) {
    Gson gson = new Gson();
    return gson.toJson(object);
  }

  public static StructureVersion getStructureVersion(GroundReadWrite groundReadWrite,
          String entityType, String state) throws GroundException {
      Map<String, GroundType> structureVersionAttribs = new HashMap<>();
      //structureVersionAttribs.put(state, GroundType.STRING);
      return groundReadWrite.getStructureVersion(entityType, structureVersionAttribs);
  }
}

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

package edu.berkeley.ground.api.models;

import edu.berkeley.ground.db.DBClient.GroundDBConnection;
import edu.berkeley.ground.exceptions.GroundException;

import java.util.List;

public abstract class StructureFactory {
  public abstract Structure create(String name) throws GroundException;

  public abstract Structure retrieveFromDatabase(String name) throws GroundException;

  public abstract void update(GroundDBConnection connection, String itemId, String childId, List<String> parentIds) throws GroundException;

  public abstract List<String> getLeaves(String name) throws GroundException;

  protected static Structure construct(String id, String name) {
    return new Structure(id, name);
  }
}

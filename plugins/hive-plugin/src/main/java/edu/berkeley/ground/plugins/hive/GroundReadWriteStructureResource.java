package edu.berkeley.ground.plugins.hive;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.stream.JsonReader;

import edu.berkeley.ground.api.models.Structure;
import edu.berkeley.ground.api.models.StructureVersion;
import edu.berkeley.ground.api.models.Tag;
import edu.berkeley.ground.api.versions.GroundType;
import edu.berkeley.ground.exceptions.GroundException;
import edu.berkeley.ground.plugins.hive.util.PluginUtil;

public class GroundReadWriteStructureResource {

    static final private Logger logger =
            LoggerFactory.getLogger(GroundReadWriteStructureResource.class.getName());

    // create node for input Tag
    Structure createStructure(String name) throws GroundException {
        try {
            String encodedUri = PluginUtil.groundServerAddress + "structures/" + URLEncoder.encode(name, "UTF-8");
            PostMethod post = new PostMethod(encodedUri);
            ObjectMapper mapper = new ObjectMapper();
            Tag tag = new Tag(-1L, name, "active", GroundType.STRING);
            Map<String, Tag> tags = new HashMap<>();
            tags.put(name, tag);
            String jsonRecord = mapper.writeValueAsString(tags);
            StringRequestEntity requestEntity = PluginUtil.createRequestEntity(jsonRecord);
            post.setRequestEntity(requestEntity);
            String response = PluginUtil.execute(post);
            return constructStructure(response);
        } catch (IOException ioe) {
            throw new GroundException(ioe);
        }
    }

    // method to create StructureVersion given the nodeId and the tags
    StructureVersion createStructureVersion(long id, long structureId, Map<String, GroundType> attributes)
            throws GroundException {
        StructureVersion structureVersion = new StructureVersion(id, structureId, attributes);
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(structureVersion);
            String uri = PluginUtil.groundServerAddress + "structures/versions";
            PostMethod post = new PostMethod(uri);
            post.setRequestEntity(PluginUtil.createRequestEntity(jsonString));
            String response = PluginUtil.execute(post);
            return constructStructureVersion(response);
        } catch (IOException e) {
            throw new GroundException(e);
        }
    }

    public StructureVersion getStructureVersion(String entityType, String state)
            throws GroundException {
        Map<String, GroundType> structureVersionAttribs = new HashMap<>();
        // structureVersionAttribs.put(state, GroundType.STRING);
        return getStructureVersion(entityType, structureVersionAttribs);
    }

    Structure getStructure(String name) throws GroundException {
        GetMethod get = new GetMethod(PluginUtil.groundServerAddress + "structures/" + name);
        try {
            String response = PluginUtil.execute(get);
            if (response != null) {
                return constructStructure(response);
            }
            return createStructure(name);
        } catch (IOException e) {
            throw new GroundException(e);
        }
    }

    // this is the only public API needed for creating and accessing
    // StructureVersion
    public StructureVersion getStructureVersion(String name, Map<String, GroundType> attribs) throws GroundException {
        List<Long> versions = (List<Long>) PluginUtil.getLatestVersions(name, "structures");
        if (versions != null && !versions.isEmpty()) {
            logger.info("getting versions: {}, {}", versions.size(), versions.get(0));
            return new StructureVersion(versions.get(0), getStructure(name).getId(), attribs);
        } else {
            return createStructureVersion(1L, getStructure(name).getId(), attribs);
        }
    }

    private Structure constructStructure(String response) throws IOException {
        JsonReader reader = new JsonReader(new StringReader(response));
        return PluginUtil.fromJson(reader, Structure.class);
    }

    private StructureVersion constructStructureVersion(String response) throws IOException {
        JsonReader reader = new JsonReader(new StringReader(response));
        return PluginUtil.fromJson(reader, StructureVersion.class);
    }

}

package com.joe.doc.model;

import lombok.Builder;
import lombok.Data;
import org.apache.tika.metadata.Metadata;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @description  parse result
 * @author  JoeBlackZ
 * @date  2020/1/5 12:01
 */
@Data
@Builder
public class TikaModel {

    private String content;

    private Metadata metadata;

    /**
     * put all metadata to map
     *
     * @return metadata in map
     */
    public Map<String, Object> getMetadataAsMap() {
        if (this.metadata == null || this.metadata.size() == 0) {
            return Collections.emptyMap();
        }
        Map<String, Object> map = new HashMap<>(this.metadata.size());
        for (String name : this.metadata.names()) {
            map.put(name, this.metadata.get(name));
        }
        return map;
    }
}

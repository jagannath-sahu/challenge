package com.dws.challenge.other.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Iterator;

import org.springframework.stereotype.Service;

@Service
public class IsomorphicJsonObjectService {

    public boolean compareJsonObjectStructure(JsonNode node1, JsonNode node2) {
        if (node1.getNodeType() != node2.getNodeType()) {
            return false;
        }

        if (node1.isObject()) {
            if (node1.size() != node2.size()) {
                return false;
            }

            Iterator<String> fieldNames1 = node1.fieldNames();
            while (fieldNames1.hasNext()) {
                String fieldName = fieldNames1.next();

                if (!node2.has(fieldName)) {
                    return false;
                }

                if (!compareJsonObjectStructure(node1.get(fieldName), node2.get(fieldName))) {
                    return false;
                }
            }
        } else if (node1.isArray()) {
            if (node1.size() != node2.size()) {
                return false;
            }

            for (int i = 0; i < node1.size(); i++) {
                if (!compareJsonObjectStructure(node1.get(i), node2.get(i))) {
                    return false;
                }
            }
        }

        return true;
    }
}

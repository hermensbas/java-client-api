package com.marklogic.client.impl;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteOperation.OperationType;
import com.marklogic.client.impl.PlanBuilderSubImpl.PlanDocDescriptorImpl;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.StringHandle;

public class PlanDocDescriptorImplTest extends Assert {

    @Test
    public void minimalWriteOp() {
        ObjectNode plan = export(new DocumentWriteOperationImpl(
                "/test.json", new StringHandle("{\"hello\":1}").withFormat(Format.JSON)));
        assertEquals("/test.json", plan.get("uri").asText());
        assertEquals(1, plan.get("doc").get("hello").asInt());
        assertEquals("Expecting only 'uri' and 'doc'", 2, plan.size());
    }

    @Test
    public void completeWriteOp() {
        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        metadata.getCollections().addAll("c1", "c2");
        metadata.setQuality(2);
        metadata.getMetadataValues().add("key1", "value1");
        metadata.getMetadataValues().add("key2", "value2");
        metadata.getPermissions().add("rest-reader", Capability.READ, Capability.EXECUTE);
        metadata.getPermissions().add("rest-writer", Capability.UPDATE);
        metadata.getProperties().put("prop1", "value1");

        ObjectNode plan = export(new DocumentWriteOperationImpl(OperationType.DOCUMENT_WRITE,
                "/test2.json", metadata, new StringHandle("{}").withFormat(Format.JSON), "someTemporalCollection"));

        assertEquals("/test2.json", plan.get("uri").asText());
        assertEquals("someTemporalCollection", plan.get("temporalCollection").asText());
        assertEquals(2, plan.get("quality").asInt());
        assertEquals("c1", plan.get("collections").get(0).asText());
        assertEquals("c2", plan.get("collections").get(1).asText());
        // TODO Test permissions for real, not these hardcoded ones
        // Should really be 3 permissions
        assertEquals(2, plan.get("permissions").size());
        assertEquals("value1", plan.get("metadata").get("key1").asText());
        assertEquals("value2", plan.get("metadata").get("key2").asText());

        assertEquals("Expecting uri, temporalCollection, doc, quality, collections, permissions, and metadata; "
                + "document properties are not yet supported", 7, plan.size());
    }

    @Test
    public void nonJsonContent() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> export(
                new DocumentWriteOperationImpl("/test.xml", new StringHandle("<test/>").withFormat(Format.XML))));
        assertEquals(
                "Only JSON content can be used with fromDocDescriptors; non-JSON content found for document with URI: /test.xml",
                ex.getMessage());
    }

    private ObjectNode export(DocumentWriteOperation writeOp) {
        String template = new PlanDocDescriptorImpl(writeOp).exportAst(new StringBuilder()).toString();
        try {
            return (ObjectNode) new ObjectMapper().readTree(template);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

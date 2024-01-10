package io.github.patternatlas.api.unit;


import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperTest {

    @Test
    public void test() throws Exception {
        var objectMapper = new ObjectMapper();
        var propertiesString = """
            {
                "configHook": "$messageEndpointUri$"
            }
            """;
        var value = objectMapper.readTree(propertiesString).toString();
        assertNotNull(value);
    }
}

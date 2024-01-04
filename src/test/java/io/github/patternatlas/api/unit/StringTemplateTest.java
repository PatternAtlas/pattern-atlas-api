package io.github.patternatlas.api.unit;


import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.stringtemplate.v4.ST;

import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
public class StringTemplateTest {

    @Test
    public void giveStringThenReplacePlaceholder() {
        String template = "Hello, $name$!";
        String rendered = template.replace("$name$", "World");
        log.debug(rendered);
        assertTrue(rendered, rendered.contains("World"));
    }

    @Test
    public void givenStringTemplateThenReplacePlaceholder() {
        ST template = new ST("Hello, $name$!", '$', '$');
        template.add("name", "World");
        String rendered = template.render();
        log.debug(rendered);
        assertTrue(rendered, rendered.contains("World"));
    }

}

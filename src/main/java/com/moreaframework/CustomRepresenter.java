package com.moreaframework;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomRepresenter extends Representer {
    public CustomRepresenter() {
        super();
        this.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        this.setPropertyUtils(new PropertyUtils() {
            @Override
            public Set<Property> getProperties(Class<?> type) {
                Set<Property> properties = super.getProperties(type);
                return properties.stream()
                        .sorted(Comparator.comparing(Property::getName))
                        .collect(Collectors.toSet());
            }
        });
    }

    @Override
    protected Node representScalar(Tag tag, String value, DumperOptions.ScalarStyle style) {
        if (style == DumperOptions.ScalarStyle.PLAIN) {
            return super.representScalar(tag, value, DumperOptions.ScalarStyle.PLAIN);
        } else {
            return super.representScalar(tag, value, style);
        }
    }
}

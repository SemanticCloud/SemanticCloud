package org.semanticcloud.semanticEngine.entity.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.semanticcloud.semanticEngine.entity.OntoProperty;

@AllArgsConstructor
@Getter
public class OwlDatatypeProperty implements OntoProperty{
    private String namespace;
    private String localName;
    private String datatype;

    public String getUri(){
        return String.format("%s%s", namespace, localName);
    }
}

package org.semanticcloud.providers.joyent.triton.domain;

import lombok.Data;

import java.util.UUID;

@Data
public class Image {
    private UUID id;
    private String name;
    private String os;
    private String version;
}

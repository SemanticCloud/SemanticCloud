package org.semanticcloud.providers.joyent.triton.domain;

import lombok.Data;

import java.util.UUID;
@Data
public class Package {
    private UUID id;
    private String name;
    private long memory;
    private long disk;
    private long swap;
    private int vcpus;
    private long lwps;
    private String version;
    private String group;
    private String description;
}

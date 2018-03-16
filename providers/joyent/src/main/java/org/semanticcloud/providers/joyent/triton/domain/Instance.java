package org.semanticcloud.providers.joyent.triton.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.net.InetAddress;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
public class Instance {
    private UUID id;
    private String name;
    private String brand;
    private long memory;
    private long disk;
    private String state;
    private String image;
    private Set<InetAddress> ips;
    private Map<String, String> metadata;
    private Map<String, String> tags;
//    private Instant created;
//    private Instant updated;
    private Set<UUID> networks;
    private InetAddress primaryIp;
    private Boolean docker;
    private boolean firewall_enabled;
    private UUID compute_node;
    @JsonProperty(value = "package")
    private String packageId;
    private Set<String> dns_names;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public long getMemory() {
        return memory;
    }

    public void setMemory(long memory) {
        this.memory = memory;
    }

    public long getDisk() {
        return disk;
    }

    public void setDisk(long disk) {
        this.disk = disk;
    }


}

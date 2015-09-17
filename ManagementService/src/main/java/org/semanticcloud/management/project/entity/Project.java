package org.semanticcloud.management.project.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Project {
    @Id
    private Long id;
    private String name;
    private String description;
}

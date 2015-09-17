package org.semanticcloud.management.project.boundary;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("project")
public class ProjectResource {
    @RequestMapping("{projectId}")
    public String getProject(@PathVariable("projectId") int projectId){
        return projectId+"";
    }
}

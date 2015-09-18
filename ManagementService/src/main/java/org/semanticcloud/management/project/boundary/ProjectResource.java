package org.semanticcloud.management.project.boundary;

import java.util.List;

import org.semanticcloud.management.project.controll.ProjectRepository;
import org.semanticcloud.management.project.entity.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("project")
public class ProjectResource {
    @Autowired
    private ProjectRepository projectRepository;

    @RequestMapping(method = RequestMethod.GET)
    public List<Project> findProjects() {
        return projectRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    @RequestMapping(value = "{projectId}", method = RequestMethod.GET)
    public Project findProject(@PathVariable("projectId") Long projectId) {
        return projectRepository.findOne(projectId);
    }

    @RequestMapping(value = "{projectId}", method = RequestMethod.PUT)
    public Project updateProject(Project project) {
        return projectRepository.save(project);
    }

    @RequestMapping(value = "{projectId}", method = RequestMethod.DELETE)
    public void deleteProject(@PathVariable("projectId") Long projectId) {
        projectRepository.delete(projectId);
    }
}

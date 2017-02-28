package main.java.com.management.project.models;

/**
 * Created by Aleksey on 28.02.2017.
 */

/**
 * Class project
 */
public class Project {

    /**
     * @param ID. Is the first int field of projects class. Contain id of project.
     */
    private int id;

    /**
     * @param projects. Is the second string field of projects class. Contain name of project.
     */
    private String project;

    /**
     * @param costs. Is the third string field of project class. Contain costs of the project.
     */
    private String costs;

    /**
     * public constructor Project with all fields.
     *
     * @param id
     * @param project
     * @param costs
     */
    public Project(int id, String project, String costs) {
        this.id = id;
        this.project = project;
        this.costs = costs;
    }

    /**
     * Getters and setters methods by all fields of Project
     **/
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getCosts() {
        return costs;
    }

    public void setCosts(String costs) {
        this.costs = costs;
    }

    /**
     * @override method toString (implementation of object Project)
     */
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", project='" + project + '\'' +
                ", costs='" + costs + '\'' +
                '}';
    }

    /**
     * equals and hashCode methods with check null
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Project projects = (Project) o;

        if (id != projects.id) return false;
        if (!project.equals(projects.project)) return false;
        return costs.equals(projects.costs);
    }

    public int hashCode() {
        int result = id;
        result = 31 * result + project.hashCode();
        result = 31 * result + costs.hashCode();
        return result;
    }
}
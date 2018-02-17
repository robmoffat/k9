package com.kite9.k9server.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
public class Document extends AbstractLongIdEntity {

	@Column(length=50,nullable=false)
    private String title = "New Diagram";

    @Column(length=200,nullable=true)
    private String description;
    
    public Document() {
    }
	
    public Document(String title, String description, Project project) {
		super();
		this.title = title;
		this.description = description;
		this.project = project;
	}

    @ManyToOne(targetEntity=Revision.class, optional=true, fetch=FetchType.LAZY)
	private Revision currentRevision = null;
    
    private Date dateCreated = new Date();
    private Date lastUpdated;
	
    @ManyToOne(targetEntity=Project.class, optional=false)
    private Project project;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Revision getCurrentRevision() {
		return currentRevision;
	}

	public void setCurrentRevision(Revision currentRevision) {
		this.currentRevision = currentRevision;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Date getDateCreated() {
		return dateCreated;
	}
}

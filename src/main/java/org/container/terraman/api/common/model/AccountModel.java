package org.container.terraman.api.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cp_cloud_accounts")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AccountModel {
    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "project")
    private String project;

    @Column(name = "region")
    private String region;

    @Column(name = "site")
    private String site;

    @Column(name = "created", nullable = false)
    private String created;

    @Column(name = "last_modified", nullable = false)
    private String lastModified;

    public AccountModel(int id, String name, String provider, String project, String region, String site, String created, String lastModified) {
        this.id = id;
        this.name = name;
        this.provider = provider;
        this.project = project;
        this.region = region;
        this.site = site;
        this.created = created;
        this.lastModified = lastModified;
    }

    public AccountModel() {

    }
}

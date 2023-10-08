package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Transient;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.GenerationType;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = { "internalId", "updateDate", "author", "authorId"})
@Entity(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer internalId;

    @JsonProperty("id")
    @Column(name = "external_id")
    private Integer externalId;

    @JsonProperty("userId")
    @Transient
    private Integer authorId;

    @ManyToOne
    @JoinColumn(name="author_id")
    private Author author;

    @Column(name = "update_date")
    private Timestamp updateDate;
    private String title;
    private String body;
}
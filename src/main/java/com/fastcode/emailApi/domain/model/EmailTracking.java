package com.fastcode.emailApi.domain.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "email_tracking")
@Getter @Setter
public class EmailTracking {

 	@Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Basic
    @Column(name = "to_", nullable = false, length = 256)    
    private String to;

    @Basic
    @Column(name = "subject", nullable = true, length = 256)
    private String subject;

    @Basic
    @Column(columnDefinition = "TEXT",name = "body", nullable = true)
    private String body;

    @Basic
    @Column(name = "sent_date", nullable = true, length = 256)
    private Date sentDate;

   	@Basic
    @Column(name = "is_click", nullable = true, length = 256)
    private Boolean isClick;

   	@Basic
    @Column(name = "is_open", nullable = true, length = 256)
    private Boolean isOpen;

}


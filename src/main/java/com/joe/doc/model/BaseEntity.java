package com.joe.doc.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;

import java.io.Serializable;
import java.util.Date;

/**
 * entity base info
 *
 * @author JoezBlackZ
 */
@Data
public abstract class BaseEntity implements Serializable {

    @Id
    @Indexed
    private String id;

}

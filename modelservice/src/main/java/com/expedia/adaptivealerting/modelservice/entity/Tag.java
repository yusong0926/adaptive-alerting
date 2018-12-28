package com.expedia.adaptivealerting.modelservice.entity;

import com.expedia.adaptivealerting.modelservice.util.JpaConverterJson;
import lombok.Data;

import javax.persistence.*;
import java.util.Map;
import java.util.Set;

/**
 * Created by tbahl on 12/19/18.
 */



@Data
@Entity
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ukey")
    private String key;

    @Column(name = "uvalue")
    private String value;


}


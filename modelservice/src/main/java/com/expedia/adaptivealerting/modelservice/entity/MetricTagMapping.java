package com.expedia.adaptivealerting.modelservice.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by tbahl on 12/20/18.
 */

@Table(name = "metric_tag_mapper")
@Entity
@Data
public class MetricTagMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "metric_id")
    private Metric metric;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;
}

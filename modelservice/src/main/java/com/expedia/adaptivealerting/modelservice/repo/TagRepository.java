package com.expedia.adaptivealerting.modelservice.repo;

import com.expedia.adaptivealerting.modelservice.entity.Metric;
import com.expedia.adaptivealerting.modelservice.entity.Tag;
import com.expedia.adaptivealerting.modelservice.entity.projection.InlineType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * @author tbahl
 */
@RepositoryRestResource(excerptProjection = InlineType.class)
public interface TagRepository extends PagingAndSortingRepository<Tag, Long> {
    /**
     * Finds a tag by tag key
     * @param key Tag key.
     * @return tag identified by the tag key.
     */
    Tag findByKey(@Param("key") String key);

    //@Query(nativeQuery = true, value = "SELECT m.* from metric m WHERE m.tag=:tag;")

//    @Query(nativeQuery = true, value = "INSERT IGNORE INTO tag (id, ukey, uvalue) VALUES ((null),t_key,t_value)")
//    Tag save(Tag tag);


    /**
//     * Find a tag by tag value
//     * @param tagValue Tag tagValue.
//     * @return tag identified by the unique tag value.
//     */
//    List<Tag> findbyTagvalue(@Param("tagValue") String tagValue);

}

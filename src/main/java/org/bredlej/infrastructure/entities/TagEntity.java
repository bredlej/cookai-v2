package org.bredlej.infrastructure.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import org.bredlej.domain.entities.Tag;

@Entity(name = "tags")
@Data
public class TagEntity {
    @Id
    private String id;
    private String name;

    public Tag toDomain(){
        Tag result = new Tag();
        result.setId(id);
        result.setName(name);
        return result;
    }

    public static TagEntity fromDomain(Tag tag){
        TagEntity result = new TagEntity();
        result.setId(tag.getId());
        result.setName(tag.getName());
        return result;
    }
}

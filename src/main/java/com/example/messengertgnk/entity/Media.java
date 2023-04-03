package com.example.messengertgnk.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "media", schema = "jpa")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Media {
    public Media(String originalFileName, Long size, String mediaType, byte[] bytes) {
        this.originalFileName = originalFileName;
        this.size = size;
        this.mediaType = mediaType;
        this.bytes = bytes;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "original_file_name")
    private String originalFileName;


    @Column(name = "size")
    private Long size;


    @Column(name = "media_type")
    private String mediaType;

    @Lob
    @JsonIgnore
    private byte[] bytes;

}

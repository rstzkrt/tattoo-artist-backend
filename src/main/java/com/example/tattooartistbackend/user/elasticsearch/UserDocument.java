package com.example.tattooartistbackend.user.elasticsearch;

import com.example.tattooartistbackend.generated.models.UserDocumentDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "user")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class UserDocument {

    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Text)
    private String fullName;

    @Field(type = FieldType.Keyword)
    private String country;

    @Field(type = FieldType.Keyword)
    private String city;

    @Field(type = FieldType.Keyword)
    private String avatarUrl;

    @Field(type = FieldType.Boolean)
    private boolean hasTattooArtistAcc;

    @Field(type = FieldType.Double)
    private Double averageRating;

    public static UserDocumentDto toDto(UserDocument userDocument){
        UserDocumentDto userDocumentDto= new UserDocumentDto();
        userDocumentDto.setAvatarUrl(userDocument.getAvatarUrl());
        userDocumentDto.setFullName(userDocument.getFullName());
        userDocumentDto.setId(userDocument.getId());
        return userDocumentDto;
    }
}

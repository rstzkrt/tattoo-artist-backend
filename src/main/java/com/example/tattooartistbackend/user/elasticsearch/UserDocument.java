package com.example.tattooartistbackend.user.elasticsearch;

import com.example.tattooartistbackend.generated.models.Gender;
import com.example.tattooartistbackend.generated.models.Language;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private UUID id;

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

    private List<String> languages;

    @Field(type = FieldType.Keyword)
    private Gender gender;

    public static UserDocumentDto toDto(UserDocument userDocument){
        UserDocumentDto userDocumentDto= new UserDocumentDto();
        userDocumentDto.setAvatarUrl(userDocument.getAvatarUrl());
        userDocumentDto.setFullName(userDocument.getFullName());
        userDocumentDto.setId(userDocument.getId());
        userDocumentDto.setLanguages(userDocument.getLanguages()==null ? new ArrayList<>(): userDocument.getLanguages().stream().map(Language::valueOf).toList());
        return userDocumentDto;
    }
}

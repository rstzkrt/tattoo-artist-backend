package com.example.tattooartistbackend.tattooWork.elasticsearch;

import com.example.tattooartistbackend.generated.models.Currency;
import com.example.tattooartistbackend.generated.models.MadeByInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;

@Document(indexName = "tattoo")
@Getter
@Setter
@RequiredArgsConstructor
public class TattooWorkDocument {

    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Keyword)
    private Currency currency;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Object)
    private MadeByInfo madeByInfo;

    @Field(type = FieldType.Double)
    private BigDecimal price;


    //tattoo type
    //title

}

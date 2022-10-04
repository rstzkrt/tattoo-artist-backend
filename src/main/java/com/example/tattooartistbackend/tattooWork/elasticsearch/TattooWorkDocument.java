package com.example.tattooartistbackend.tattooWork.elasticsearch;

import com.example.tattooartistbackend.generated.models.Currency;
import com.example.tattooartistbackend.generated.models.MadeByInfo;
import com.example.tattooartistbackend.generated.models.TattooStyle;
import com.example.tattooartistbackend.tattooWork.TattooWork;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.util.UUID;

@Document(indexName = "tattoo")
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class TattooWorkDocument {

    @Id
    @Field(type = FieldType.Keyword)
    private UUID id;

    @Field(type = FieldType.Keyword)
    private Currency currency;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Object)
    private MadeByInfo madeByInfo;

    @Field(type = FieldType.Double)
    private BigDecimal price;

    @Field(type = FieldType.Keyword)
    private TattooStyle tattooStyle;

    public static TattooWorkDocument fromTattooWork(TattooWork tattooWork){
        return TattooWorkDocument.builder()
                .id(tattooWork.getId())
                .currency(tattooWork.getCurrency())
                .description(tattooWork.getDescription())
                .madeByInfo(tattooWork.getMadeBy().toMadeByInfoDto())
                .price(tattooWork.getPrice())
                .build();
    }
}

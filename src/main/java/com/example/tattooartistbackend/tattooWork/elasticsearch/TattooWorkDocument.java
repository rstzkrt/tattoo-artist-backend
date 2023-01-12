package com.example.tattooartistbackend.tattooWork.elasticsearch;

import com.example.tattooartistbackend.generated.models.Currency;
import com.example.tattooartistbackend.generated.models.MadeByInfo;
import com.example.tattooartistbackend.generated.models.TattooStyle;
import com.example.tattooartistbackend.tattooWork.TattooWork;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.math.BigDecimal;
import java.util.UUID;

@Document(indexName = "tattoo")
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class TattooWorkDocument {

    @Id
    @Field(type = FieldType.Keyword)
    private UUID id;

    @MultiField(mainField = @Field(type = FieldType.Text),
            otherFields = { @InnerField(suffix = "keyword", type = FieldType.Keyword) })
    private Currency currency;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Double)
    private BigDecimal price;

    @MultiField(mainField = @Field(type = FieldType.Text),
            otherFields = { @InnerField(suffix = "keyword", type = FieldType.Keyword) })
    private TattooStyle tattooStyle;

    public static TattooWorkDocument fromTattooWork(TattooWork tattooWork){
        return TattooWorkDocument.builder()
                .id(tattooWork.getId())
                .currency(tattooWork.getCurrency())
                .description(tattooWork.getDescription())
                .price(tattooWork.getPrice())
                .tattooStyle(tattooWork.getTattooStyle())
                .build();
    }
}

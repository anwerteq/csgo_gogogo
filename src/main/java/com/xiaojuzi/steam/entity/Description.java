package com.xiaojuzi.steam.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "cz75_item_descriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Description {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 物品的描述内容。
     */
    @Column(name = "description")
    private String description;

    /**
     * 描述的类型，如“exterior_wear”（外观磨损）、"stattrak_score"（StatTrak 分数）等。
     */
    @Column(name = "description_type")
    private String descriptionType;

    /**
     * 物品的描述所属 CZ75 物品。
     */
    @ManyToOne
    @JoinColumn(name = "cz75_item_id")
    private CZ75Item cz75Item;

}

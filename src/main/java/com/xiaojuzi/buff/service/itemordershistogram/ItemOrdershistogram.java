package com.xiaojuzi.buff.service.itemordershistogram;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Auto-generated: 2023-10-09 21:6:53
 *
 * @author www.ecjson.com
 * @website http://www.ecjson.com/json2java/
 */
@Data
public class ItemOrdershistogram {

    private int success;
    @JsonProperty("sell_order_table")
    private String sellOrderTable;
    @JsonProperty("sell_order_summary")
    private String sellOrderSummary;
    @JsonProperty("buy_order_table")
    private String buyOrderTable;
    @JsonProperty("buy_order_summary")
    private String buyOrderSummary;
    @JsonProperty("highest_buy_order")
    private String highestBuyOrder;
    @JsonProperty("lowest_sell_order")
    private String lowestSellOrder;
    @JsonProperty("buy_order_graph")
    private List<List<String>> buyOrderGraph;
    @JsonProperty("sell_order_graph")
    private List<List<String>> sellOrderGraph;
    @JsonProperty("graph_max_y")
    private int graphMaxY;
    @JsonProperty("graph_min_x")
    private double graphMinX;
    @JsonProperty("graph_max_x")
    private double graphMaxX;
    @JsonProperty("price_prefix")
    private String pricePrefix;
    @JsonProperty("price_suffix")
    private String priceSuffix;
}

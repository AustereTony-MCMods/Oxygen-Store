package austeretony.oxygen_store.common.store.goods;

import com.google.gson.JsonElement;

public enum EnumGoodsType {

    ITEM {

        @Override
        public Goods fromJson(JsonElement jsonObject) {
            return GoodsItem.fromJson(jsonObject);
        }
    },    
    COMMAND {

        @Override
        public Goods fromJson(JsonElement jsonObject) {
            return GoodsCommand.fromJson(jsonObject);
        }
    },   
    SCRIPT {

        @Override
        public Goods fromJson(JsonElement jsonObject) {
            return GoodsScript.fromJson(jsonObject);
        }
    };

    public abstract Goods fromJson(JsonElement jsonObject);
}

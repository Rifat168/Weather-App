
package com.tanvir.training.weatherappbatch2.current;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Clouds {

    @SerializedName("all")
    @Expose
    private long all;

    public long getAll() {
        return all;
    }

    public void setAll(long all) {
        this.all = all;
    }

}

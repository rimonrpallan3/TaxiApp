package metrotaxi.project.in.metrotaxi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Kripa on 02-10-2017.
 */

public class AutoChargeModel {
    public int getMinimumCharge() {
        return minimumCharge;
    }

    public void setMinimumCharge(int minimumCharge) {
        this.minimumCharge = minimumCharge;
    }

    public int getAfterMinimum() {
        return afterMinimum;
    }

    public void setAfterMinimum(int afterMinimum) {
        this.afterMinimum = afterMinimum;
    }

    @SerializedName("minimum")
    private int minimumCharge;
    @SerializedName("per_km")
    private int afterMinimum;
}

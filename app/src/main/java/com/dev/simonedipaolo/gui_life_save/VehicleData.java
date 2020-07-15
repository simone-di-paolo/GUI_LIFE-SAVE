package com.dev.simonedipaolo.gui_life_save;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class VehicleData {

    private int speed, solarValue, batteryPower, braking, nm, consumo, oggiWh, frenataRigenerativa;

    public VehicleData(int speed, int solarValue, int batteryPower, int braking,
            int nm, int consumo, int oggiWh, int frenataRigenerativa) {
        this.speed = speed;
        this.solarValue = solarValue;
        this.batteryPower = batteryPower;
        this.braking = braking;
        this.nm = nm;
        this.consumo = consumo;
        this.oggiWh = oggiWh;
        this.frenataRigenerativa = frenataRigenerativa;
    }

    public VehicleData() {

    }

    public int getSolarValue() {
        return solarValue;
    }

    public void setSolarValue(int solarValue) {
        this.solarValue = solarValue;
    }

    public int getBatteryPower() {
        return batteryPower;
    }

    public void setBatteryPower(int batteryPower) {
        this.batteryPower = batteryPower;
    }

    public int getBraking() {
        return braking;
    }

    public void setBraking(int braking) {
        this.braking = braking;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getNm() {
        return nm;
    }

    public void setNm(int nm) {
        this.nm = nm;
    }

    public int getConsumo() {
        return consumo;
    }

    public void setConsumo(int consumo) {
        this.consumo = consumo;
    }

    public int getOggiWh() {
        return oggiWh;
    }

    public void setOggiWh(int oggiWh) {
        this.oggiWh = oggiWh;
    }

    public int getFrenataRigenerativa() {
        return frenataRigenerativa;
    }

    public void setFrenataRigenerativa(int frenataRigenerativa) {
        this.frenataRigenerativa = frenataRigenerativa;
    }

    // to string / equals

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}

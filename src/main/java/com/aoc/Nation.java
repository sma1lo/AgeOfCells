package com.aoc;

import java.math.BigInteger;

public class Nation {
    String name;
    NationType nationType;
    BigInteger power;
    Color color;
    SituationState state;

    public Nation(String name, NationType nationType, BigInteger power) {
        this.name = name;
        this.nationType = nationType;
        this.power = power;
        this.state = SituationState.PEACE;

        if (this.nationType == NationType.ROME) {
            this.color = Color.RED;
        } else if (this.nationType == NationType.BAVARIA) {
            this.color = Color.CYAN;
        } else if (this.nationType == NationType.ENGLAND) {
            this.color = Color.GREEN;
        }else if (this.nationType == NationType.FRANCE) {
            this.color = Color.PINK;
        }else if (this.nationType == NationType.SCOTLAND) {
            this.color = Color.YELLOW;
        }else if (this.nationType == NationType.AUSTRIA) {
            this.color = Color.PURPLE;
        }else if (this.nationType == NationType.RUSSIA) {
            this.color = Color.BROWN;
        } else {
            this.color = Color.WHITE;
        }
    }

    public String getName() {
        return name;
    }

    public NationType getNationType() {
        return nationType;
    }

    public BigInteger getPower() {
        return power;
    }

    public Color getColor() {
        return color;
    }

    public SituationState getState() {
        return state;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNationType(NationType nationType) {
        this.nationType = nationType;
    }

    public void setPower(BigInteger power) {
        this.power = power;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setState(SituationState state) {
        this.state = state;
    }
}

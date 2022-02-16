package com.example.guideme;


import android.app.Application;

public class Globale extends Application {

    private boolean lancerDetection = false;

    public boolean getLancerDetection() {
        return lancerDetection;
    }

    public void setLancerDetection(boolean someVariable) {
        this.lancerDetection = someVariable;
    }
}

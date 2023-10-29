package com.itbd.examineradmin.DataMoldes;

public class DashButtonModel {
    String btnTxt;
    int btnIcon;

    public DashButtonModel() {
    }

    public DashButtonModel(String btnTxt, int btnIcon) {
        this.btnTxt = btnTxt;
        this.btnIcon = btnIcon;
    }

    public String getBtnTxt() {
        return btnTxt;
    }

    public void setBtnTxt(String btnTxt) {
        this.btnTxt = btnTxt;
    }

    public int getBtnIcon() {
        return btnIcon;
    }

    public void setBtnIcon(int btnIcon) {
        this.btnIcon = btnIcon;
    }
}

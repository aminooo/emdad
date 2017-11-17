package com.upsym.emdad.earthquake.login;

/**
 * Created by Karo on 11/16/2017.
 */

public interface LoginModel {
    void stepOne(String mobile, String emdad);

    void stepTwo(String code);
}

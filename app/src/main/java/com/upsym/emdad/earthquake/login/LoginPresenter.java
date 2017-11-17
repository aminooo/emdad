package com.upsym.emdad.earthquake.login;

/**
 * Created by Karo on 11/16/2017.
 */

public interface LoginPresenter {

    void stepOne(Long aLong, String emdad);

    void stepTwo(String code);
}

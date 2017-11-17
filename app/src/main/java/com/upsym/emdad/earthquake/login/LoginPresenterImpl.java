package com.upsym.emdad.earthquake.login;

import com.upsym.emdad.earthquake.ServerUtil;
import com.upsym.emdad.earthquake.database.DatabaseUtil;

/**
 * Created by Karo on 11/16/2017.
 */

public class LoginPresenterImpl implements LoginPresenter {
    LoginView view;
    LoginModel model;

    public LoginPresenterImpl(LoginActivity loginActivity) {
        view = loginActivity;
        model = new LoginModelImpl(DatabaseUtil.getDatabase(loginActivity), new ServerUtil(loginActivity));
    }

    @Override
    public void stepOne(Long aLong, String emdad) {
        view.showProgress(true);
        model.stepOne("0" + aLong, emdad);
    }

    @Override
    public void stepTwo(String code) {
        view.showProgress(true);
        model.stepTwo(code);
    }
}

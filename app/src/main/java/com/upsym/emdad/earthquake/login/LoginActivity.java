package com.upsym.emdad.earthquake.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.upsym.emdad.earthquake.R;
import com.upsym.emdad.earthquake.login.events.StepOneErrorEvent;
import com.upsym.emdad.earthquake.login.events.StepOneOkEvent;
import com.upsym.emdad.earthquake.login.events.StepTwoErrorEvent;
import com.upsym.emdad.earthquake.login.events.StepTwoOkEvent;
import com.upsym.emdad.earthquake.main.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements LoginView{

    @BindView(R.id.phone)
    MaterialEditText phoneView;

    @BindView(R.id.code)
    MaterialEditText codeView;

    @BindView(R.id.progressbar)
    ProgressBar progressBar;

    @BindView(R.id.emdad)
    MaterialEditText emdadView;

    @BindView(R.id.button)
    Button button;

    LoginPresenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this );
        presenter = new LoginPresenterImpl(this);
//        startActivity(new Intent(this, MainActivity.class));

    }


    @OnClick(R.id.button)
    public void onDoneClick(){
        if(codeView.getVisibility() == View.GONE) {
            if(phoneView.getText().toString().length() < 5){
                makeToast("لطفا شماره تماس را صحیح وارد کنید");
                return;
            }
            presenter.stepOne(Long.valueOf(phoneView.getText().toString()), emdadView.getText().toString());

        } else {
            if(codeView.length() == 4 ){
                presenter.stepTwo(codeView.getText().toString());
            } else {
                makeToast("لطفا کد را وارد کنید");
            }
        }
    }

    private void makeToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }



    @Override
    public void showProgress(boolean b) {
        if(b){
            progressBar.setVisibility(View.VISIBLE);
            button.setEnabled(false);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            button.setEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onStepOneError(StepOneErrorEvent event) {
        showProgress(false);
        showTast("خطا در ارتباط با سرور");
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onStepOneOK(StepOneOkEvent event) {
        showProgress(false);
        codeView.setVisibility(View.VISIBLE);
        phoneView.setEnabled(false);
        emdadView.setEnabled(false);
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onStepTwoError(StepTwoErrorEvent event) {
        showProgress(false);
        showTast("خطا در ورود کد");
        EventBus.getDefault().removeStickyEvent(event);
    }

    private void showTast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onStepTwoOK(StepTwoOkEvent event) {
        showProgress(false);
        startActivity(new Intent(this, MainActivity.class));
        EventBus.getDefault().removeStickyEvent(event);
        finish();
    }
}

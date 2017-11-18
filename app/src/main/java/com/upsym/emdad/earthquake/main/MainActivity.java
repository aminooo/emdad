package com.upsym.emdad.earthquake.main;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.orhanobut.logger.Logger;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.upsym.emdad.earthquake.CustomClusterRenderer;
import com.upsym.emdad.earthquake.PermissionUtils;
import com.upsym.emdad.earthquake.R;
import com.upsym.emdad.earthquake.database.EventPoint;
import com.upsym.emdad.earthquake.database.ValidPackage;
import com.upsym.emdad.earthquake.main.data.Item;
import com.upsym.emdad.earthquake.main.events.UpdatedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        MainView {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int LOCATION_TURN_ON                 = 2;

    private CustomClusterManager<Item> mClusterManager;

    @BindView(R.id.center_img)
    AppCompatImageView centerImage;

    private boolean mPermissionDenied = false;

    private GoogleMap mMap;

    MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        presenter = new MainPresenterImpl(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(34.3277 , 47.0778), 10));

        mClusterManager = new CustomClusterManager<Item>(this, mMap);
        mClusterManager.setRenderer(new CustomClusterRenderer(this, mMap, mClusterManager));
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<Item>() {
            @Override
            public boolean onClusterClick(Cluster<Item> cluster) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Item marker : cluster.getItems()) {
                    builder.include(marker.getPosition());
                }
                LatLngBounds bounds = builder.build();
                int padding = (int)convertDpToPixel(16f);
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.moveCamera(cu);
                return true;
            }

            public float convertDpToPixel(float dp){
                Resources resources = getResources();
                DisplayMetrics metrics = resources.getDisplayMetrics();
                return dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
            }

        });

        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                centerImage.clearAnimation();
                centerImage.animate().scaleX(1.2f).start();
                centerImage.animate().scaleY(1.2f).start();
            }
        });

        presenter.onMapReady();
        presenter.getMarkerData().observeForever(new Observer<List<EventPoint>>() {
            @Override
            public void onChanged(@Nullable List<EventPoint> eventPoints) {
                if(eventPoints != null){
                    EventBus.getDefault().postSticky(new UpdatedEvent(eventPoints));
                }
            }
        });
    }


    @OnClick(R.id.center_img)
    public void onCenterImageClick(){
        presenter.onCenterClick();
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        displayLocationSettingsRequest();
        return false;
    }


    private LatLng getCenter(){
        return mMap.getCameraPosition().target;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {

        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }



    public void displayLocationSettingsRequest() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Logger.i("All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Logger.i("Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            status.startResolutionForResult(MainActivity.this , LOCATION_TURN_ON);
                        } catch (IntentSender.SendIntentException e) {
                            Logger.i("PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Logger.i("Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 17){
            if(resultCode == RESULT_OK){
//                presenter.locationActivityOk();
            }
        }
    }


    AlertDialog dialog;
    @Override
    public void openNewEventDialog() {
        if(dialog != null && dialog.isShowing()){
            return;
        }

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_event, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        final MaterialEditText title = view.findViewById(R.id.title);
        final MaterialEditText address = view.findViewById(R.id.address);
        final MaterialEditText count = view.findViewById(R.id.count);

        LinearLayout linearLayout = view.findViewById(R.id.main_linear);
        final RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        radioGroup.setGravity(Gravity.RIGHT);
        for(ValidPackage pack : presenter.getValidPackages()){
            RadioButton radioButton = new RadioButton(this);
            radioButton.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            radioButton.setText(pack.getTitle() + " " + pack.getContentPerPackage());
            radioGroup.addView(radioButton);
        }
        linearLayout.addView(radioGroup);
        radioGroup.check(radioGroup.getChildAt(0).getId());
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("".equals(address.getText().toString()) || "".equals(title.getText().toString())){
                    makeToast("لطفا همه فیلدهارا پر کنید");
                    return;
                }
                if("".equals(count.getText().toString())){
                    makeToast("لطفا همه فیلدهارا پر کنید");
                    return;
                }
                presenter.saveNewEvent(getCenter(), title.getText().toString(), address.getText().toString(), (Integer.valueOf(count.getText().toString()) == 0) ? 1 : Integer.valueOf(count.getText().toString()),radioGroup.indexOfChild(radioGroup.findViewById(radioGroup.getCheckedRadioButtonId())));
                dialog.dismiss();
            }
        });


        builder.setView(view);
        dialog = builder.show();

    }

    @Override
    public void reloadMap(List<EventPoint> markerData) {
        mClusterManager.clearItems();
        for(EventPoint point : markerData){
            Item item = new Item(point.getLat(), point.getLng(), "", String.valueOf(point.getId()), point.getStatus());
            mClusterManager.addItem(
                    item
            );
        }
        mClusterManager.cluster();
    }

    private void makeToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onUpdateStickers(UpdatedEvent event){
        reloadMap(event.getEventPoints());
        EventBus.getDefault().removeStickyEvent(event);
    }

    class CustomClusterManager<T extends ClusterItem> extends ClusterManager<T> {

        public CustomClusterManager(Context context, GoogleMap map) {
            super(context, map);
            setAnimation(false);
        }

        public CustomClusterManager(Context context, GoogleMap map, MarkerManager markerManager) {
            super(context, map, markerManager);
        }

        @Override
        public void onCameraIdle() {
            super.onCameraIdle();
            centerImage.clearAnimation();
            centerImage.animate().scaleX(1f).start();
            centerImage.animate().scaleY(1f).start();
        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            if(marker != null && marker.getSnippet() != null){
                openMarkerInfoDialog(marker);
            }
            return super.onMarkerClick(marker);
        }
    }

    private void openMarkerInfoDialog(Marker marker) {
        if(dialog != null && dialog.isShowing()){
            return;
        }

        EventPoint eventPoint = presenter.getEventPoint(Long.valueOf(marker.getSnippet()));

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_info_window, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        final TextView title = view.findViewById(R.id.title);
        final TextView details = view.findViewById(R.id.details);


        if(eventPoint.getTitle() != null) title.setText(eventPoint.getTitle());
        else title.setText("بدون عنوان");
        String status = "نامشخص";
        if ("waiting".equals(eventPoint.getStatus())){
            status = "در حال انتظار";
        } else if("on_the_way".equals(eventPoint.getStatus())){
            status = "در مرحله پردازش و ارسال";
        } else if("delivered".equals(eventPoint.getStatus())){
            status = "تحویل داده شده";
        }

        details.setText("تعداد: " + eventPoint.getCount() + "\n" + "آدرس و توضیحات: " + eventPoint.getAddress()
                        + "\n" + "وضعیت: " + status);
        view.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        builder.setView(view);
        dialog = builder.show();

    }

    @OnClick(R.id.refresh)
    public void onRefreshClick(){
        findViewById(R.id.refresh).animate().rotationBy(360f).setDuration(700).start();
        presenter.onRefreshClick();
    }


    boolean isMap = true;
    @OnClick(R.id.change_layer)
    public void onChangeLayer(){
        if(isMap){
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        isMap = !isMap;
    }

}

package com.example.sharetrip.home.fragments;

import static org.maplibre.android.MapLibre.getApplicationContext;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.sharetrip.R;
import com.example.sharetrip.home.HomeActivity;

import org.maplibre.android.MapLibre;
import org.maplibre.android.camera.CameraPosition;
import org.maplibre.android.camera.CameraUpdateFactory;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.location.LocationComponentActivationOptions;
import org.maplibre.android.location.modes.CameraMode;
import org.maplibre.android.location.modes.RenderMode;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.maps.Style;
import org.maplibre.android.plugins.annotation.SymbolManager;
import org.maplibre.android.plugins.annotation.SymbolOptions;

import java.io.IOException;
import java.io.InputStream;

public class MapFragment extends Fragment {

    private MapView mapView;
    private MapLibreMap mapLibreMap;

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            centerMapOnUserLocation();
                        } else {
                            Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MapLibre.getInstance(requireContext().getApplicationContext());

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        String styleJson = loadStyleFromAssets();

        mapView.getMapAsync(mapboxMap -> {
            mapboxMap.setStyle(new Style.Builder().fromJson(styleJson), style -> {
                // Adicionar marcador (só após carregar o estilo!)
                SymbolManager symbolManager = new SymbolManager(mapView, mapboxMap, style);
                symbolManager.create(new SymbolOptions()
                        .withLatLng(new LatLng(38.7223, -9.1393)) // Lisboa
                        .withIconImage("marker-15")); // Precisas de garantir que o estilo contém este ícone

                ImageButton centerButton = view.findViewById(R.id.centerLocationButton);
                centerButton.setOnClickListener(v ->
                        askForLocationWithPermissions());
                        mapboxMap.setCameraPosition(new CameraPosition.Builder()
                                .target(new LatLng(38.522928, -8.891219))
                                .zoom(15.0)
                                .build());
                        mapboxMap.addOnMapClickListener(point -> {
                        symbolManager.create(new SymbolOptions()
                                .withLatLng(point)
                                .withIconImage("marker"));
                        return true;
                });
            });
        });
    }

    private void askForLocationWithPermissions() {
        String finePermission =
                android.Manifest.permission.ACCESS_FINE_LOCATION;
        String coarsePermission =
                android.Manifest.permission.ACCESS_COARSE_LOCATION;
        if (ContextCompat.checkSelfPermission(getContext(), finePermission) ==
                PackageManager.PERMISSION_GRANTED) {
            // Fine location permission already granted
            centerMapOnUserLocation();
        } else if (ContextCompat.checkSelfPermission(getContext(),
                coarsePermission) == PackageManager.PERMISSION_GRANTED) {
            // Coarse location permission granted, inform user about approximate location
            new AlertDialog.Builder(getContext())
                    .setMessage("Your location will be approximate. To enable precise location, go to app settings and grant Fine Location permission.")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, id) ->
                            centerMapOnUserLocation())
                    .setNegativeButton("Go to Settings", (dialog,
                                                                  id) -> openAppSettings())
                    .create()
                    .show();
        } else if
        (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), finePermission)
                        ||
                        ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                coarsePermission)) {
            // Permission was denied earlier, show rationale dialog
            new AlertDialog.Builder(getContext())
                    .setMessage("We need your location to center the map. Please grant location permission.")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, id) ->
                            permissionLauncher.launch(finePermission))
                    .create()
                    .show();
        } else {
            // Neither permission has been granted, request fine location
            permissionLauncher.launch(finePermission);
        }
    }

    private void openAppSettings() {
        Intent intent = new
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @SuppressLint("MissingPermission")
    private void enableLocationComponent() {
        LocationComponentActivationOptions build =
                LocationComponentActivationOptions.builder(requireContext(), mapLibreMap.getStyle()).build();

        mapLibreMap.getLocationComponent().activateLocationComponent(build);

        mapLibreMap.getLocationComponent().setLocationComponentEnabled(true);

        mapLibreMap.getLocationComponent().setCameraMode(CameraMode.TRACKING);

        mapLibreMap.getLocationComponent().setRenderMode(RenderMode.COMPASS);
    }

    private void centerMapOnUserLocation() {
        if
        (!mapLibreMap.getLocationComponent().isLocationComponentActivated()) {
            enableLocationComponent();
        }
        Location location =
                mapLibreMap.getLocationComponent().getLastKnownLocation();
        if (location != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(),
                            location.getLongitude())) // Set target to user's location
                    .zoom(18.0) // Optional: Adjust zoom level
                    .build();
            mapLibreMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    // Method to load the style JSON from assets folder
    private String loadStyleFromAssets() {
        String styleJson = "";
        try {
            InputStream inputStream = requireContext().getAssets().open("style.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            styleJson = new String(buffer);
        } catch (IOException e) {
            Log.e("MapLibre", "Error loading style file", e);
        }
        return styleJson;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }
}
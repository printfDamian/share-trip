package com.example.sharetrip.home.fragments;

import static android.content.Context.MODE_PRIVATE;
import static org.maplibre.android.style.expressions.Expression.eq;
import static org.maplibre.android.style.expressions.Expression.get;
import static org.maplibre.android.style.expressions.Expression.literal;
import static org.maplibre.android.style.layers.PropertyFactory.circleColor;
import static org.maplibre.android.style.layers.PropertyFactory.circleRadius;
import static org.maplibre.android.style.layers.PropertyFactory.circleStrokeColor;
import static org.maplibre.android.style.layers.PropertyFactory.circleStrokeWidth;
import static org.maplibre.android.style.layers.PropertyFactory.iconAllowOverlap;
import static org.maplibre.android.style.layers.PropertyFactory.iconImage;
import static org.maplibre.android.style.layers.PropertyFactory.iconSize;
import static org.maplibre.android.style.layers.PropertyFactory.textAllowOverlap;
import static org.maplibre.android.style.layers.PropertyFactory.textColor;
import static org.maplibre.android.style.layers.PropertyFactory.textField;
import static org.maplibre.android.style.layers.PropertyFactory.textIgnorePlacement;
import static org.maplibre.android.style.layers.PropertyFactory.textSize;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.sharetrip.R;
import com.example.sharetrip.api.ApiClient;
import com.example.sharetrip.api.markers.MarksResponse;
import com.example.sharetrip.auth.LoginActivity;
import com.example.sharetrip.home.ViewPoiActivity;
import com.google.gson.Gson;

import org.maplibre.android.MapLibre;
import org.maplibre.android.camera.CameraPosition;
import org.maplibre.android.camera.CameraUpdateFactory;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.geometry.LatLngBounds;
import org.maplibre.android.location.LocationComponentActivationOptions;
import org.maplibre.android.location.modes.CameraMode;
import org.maplibre.android.location.modes.RenderMode;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.maps.Style;
import org.maplibre.android.style.layers.SymbolLayer;
import org.maplibre.android.style.sources.GeoJsonSource;
import org.maplibre.geojson.FeatureCollection;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment {

    private MapView mapView;
    private MapLibreMap map;
    private SharedPreferences prefs;
    private boolean isLoadingMarkers = false;

    private final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    centerMapOnUserLocation();
                } else {
                    Toast.makeText(getContext(), getContext().getString(R.string.perm_denied), Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        prefs = requireActivity().getSharedPreferences(LoginActivity.PREFS, MODE_PRIVATE);
        MapLibre.getInstance(requireContext().getApplicationContext());

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String styleJson = loadStyleFromAssets();

        mapView.getMapAsync(Map -> {
            map = Map;
            map.setStyle(new Style.Builder().fromJson(styleJson), style -> {
                map.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(40.0, -8.0)) // Center of Europe
                        .zoom(4.0)
                        .build());

                ImageButton centerButton = view.findViewById(R.id.centerLocationButton);
                centerButton.setOnClickListener(v -> askForLocationWithPermissions());

                Bitmap iconBitmap = getBitmapFromVectorDrawable(R.drawable.baseline_place_24);

                if (iconBitmap != null) {
                    style.addImage("marker", iconBitmap);
                    Log.d("MapFragment", "Added marker image to style");
                } else {
                    Log.e("MapFragment", "Failed to load marker bitmap");
                }

                map.addOnMapClickListener(point -> {
                    return handleMapClick(point);
                });

                map.addOnCameraIdleListener(() -> {
                    loadMarkersForCurrentView();
                });

                loadMarkersForCurrentView();
            });
        });
    }

    private boolean handleMapClick(LatLng point) {
        if (map == null || map.getStyle() == null) {
            return false;
        }

        android.graphics.PointF screenPoint = map.getProjection().toScreenLocation(point);

        List<org.maplibre.geojson.Feature> features = map.queryRenderedFeatures(screenPoint, "api-marker-layer");

        if (!features.isEmpty()) {
            org.maplibre.geojson.Feature feature = features.get(0);

            String poiName = feature.getStringProperty("title");
            String poiDescription = feature.getStringProperty("description");
            String poiType = feature.getStringProperty("type");
            String poiAddress = feature.getStringProperty("address");
            String tripTitle = feature.getStringProperty("trip_title");

            Intent intent = new Intent(getContext(), ViewPoiActivity.class);
            intent.putExtra("ViewPoi_name", poiName);
            intent.putExtra("ViewPoi_description", poiDescription);
            intent.putExtra("ViewPoi_type", poiType);
            intent.putExtra("ViewPoi_address", poiAddress);
            intent.putExtra("ViewPoi_tripTitle", tripTitle);

            startActivity(intent);

            return true;
        }
        return false;
    }

    private Bitmap getBitmapFromVectorDrawable(int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(requireContext(), drawableId);
        if (drawable == null) {
            Log.e("MapFragment", "Drawable não encontrado");
            return null;
        }
        if (drawable instanceof VectorDrawableCompat || drawable instanceof VectorDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return ((BitmapDrawable) drawable).getBitmap();
        }
    }

    private void askForLocationWithPermissions() {
        String finePermission = android.Manifest.permission.ACCESS_FINE_LOCATION;
        String coarsePermission = android.Manifest.permission.ACCESS_COARSE_LOCATION;

        if (ContextCompat.checkSelfPermission(getContext(), finePermission) == PackageManager.PERMISSION_GRANTED) {
            centerMapOnUserLocation();
        } else if (ContextCompat.checkSelfPermission(getContext(),
                coarsePermission) == PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(getContext())
                    .setMessage(getContext().getString(R.string.approximate_location))
                    .setCancelable(false)
                    .setPositiveButton(getContext().getString(R.string.btn_ok), (dialog, id) -> centerMapOnUserLocation())
                    .setNegativeButton(getContext().getString(R.string.btn_settings), (dialog, id) -> openAppSettings())
                    .create()
                    .show();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), finePermission) ||
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), coarsePermission)) {
            new AlertDialog.Builder(getContext())
                    .setMessage(getContext().getString(R.string.ask_perm))
                    .setCancelable(false)
                    .setPositiveButton(getContext().getString(R.string.btn_ok), (dialog, id) -> permissionLauncher.launch(finePermission))
                    .create()
                    .show();
        } else {
            permissionLauncher.launch(finePermission);
        }
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @SuppressLint("MissingPermission")
    private void enableLocationComponent() {
        LocationComponentActivationOptions options = LocationComponentActivationOptions
                .builder(requireContext(), map.getStyle()).build();
        map.getLocationComponent().activateLocationComponent(options);
        map.getLocationComponent().setLocationComponentEnabled(true);
        map.getLocationComponent().setCameraMode(CameraMode.TRACKING);
        map.getLocationComponent().setRenderMode(RenderMode.COMPASS);
    }

    private void centerMapOnUserLocation() {
        if (!map.getLocationComponent().isLocationComponentActivated()) {
            enableLocationComponent();
        }
        Location location = map.getLocationComponent().getLastKnownLocation();
        if (location != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(18.0)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private String loadStyleFromAssets() {
        try {
            InputStream inputStream = requireContext().getAssets().open("style.json");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            return new String(buffer);
        } catch (IOException e) {
            Log.e("MapLibre", "Error loading style file", e);
            return "";
        }
    }

    private void loadMarkersForCurrentView() {
        if (map == null || isLoadingMarkers)
            return;

        isLoadingMarkers = true;

        LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;

        double north = bounds.getNorthEast().getLatitude();
        double south = bounds.getSouthWest().getLatitude();
        double east = bounds.getNorthEast().getLongitude();
        double west = bounds.getSouthWest().getLongitude();

        Log.d("MapFragment",
                String.format("Loading markers for bounds: N=%.6f, S=%.6f, E=%.6f, W=%.6f", north, south, east, west));

        ApiClient.getApiService(getContext()).getMarkersInBounds(north, south, east, west, 1000)
                .enqueue(new Callback<MarksResponse>() {
                    @Override
                    public void onResponse(Call<MarksResponse> call, Response<MarksResponse> response) {
                        isLoadingMarkers = false;

                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            try {
                                Object data = response.body().getData();
                                FeatureCollection featureCollection;

                                if (data instanceof String) {
                                    String geoJsonString = data.toString();
                                    if (geoJsonString.startsWith("\"") && geoJsonString.endsWith("\"")) {
                                        geoJsonString = geoJsonString.substring(1, geoJsonString.length() - 1);
                                        geoJsonString = geoJsonString.replace("\\\"", "\"");
                                    }
                                    featureCollection = FeatureCollection.fromJson(geoJsonString);
                                } else {
                                    Gson gson = new Gson();
                                    String jsonString = gson.toJson(data);
                                    featureCollection = FeatureCollection.fromJson(jsonString);
                                }

                                updateMapMarkers(featureCollection);

                            } catch (Exception e) {
                                Log.e("MapFragment", "Error parsing GeoJSON", e);
                                Toast.makeText(requireActivity(), getContext().getString(R.string.err_marks_proc), Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } else {
                            Log.e("MapFragment", "API response not successful: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<MarksResponse> call, Throwable t) {
                        isLoadingMarkers = false; // Reset flag on failure
                        Log.e("MapFragment", "API call failed", t);
                    }
                });
    }

    private void updateMapMarkers(FeatureCollection featureCollection) {
        if (map == null || map.getStyle() == null)
            return;

        Style style = map.getStyle();

        // Remove existing layer first, then source
        if (style.getLayer("api-marker-layer") != null) {
            style.removeLayer("api-marker-layer");
        }
        if (style.getSource("api-markers") != null) {
            style.removeSource("api-markers");
        }

        // Add new markers
        GeoJsonSource markersSource = new GeoJsonSource("api-markers", featureCollection);
        style.addSource(markersSource);

        SymbolLayer markerLayer = new SymbolLayer("api-marker-layer", "api-markers");
        markerLayer.setProperties(
                iconImage("marker"),
                iconSize(1.0f),
                iconAllowOverlap(true));
        style.addLayer(markerLayer);

        Log.d("MapFragment", "Updated map with " + featureCollection.features().size() + " markers");
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

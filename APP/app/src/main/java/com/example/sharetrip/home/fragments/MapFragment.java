package com.example.sharetrip.home.fragments;

import static android.content.Context.MODE_PRIVATE;
import static org.maplibre.android.MapLibre.getApplicationContext;
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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
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
import com.example.sharetrip.api.post.PostsResponse;
import com.example.sharetrip.auth.SignInActivity;
import com.example.sharetrip.home.HomeActivity;
import com.example.sharetrip.home.adapter.PostAdapter;
import com.example.sharetrip.models.Post;
import com.example.sharetrip.utils.Utils;
import com.google.gson.Gson;

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
import org.maplibre.android.style.layers.CircleLayer;
import org.maplibre.android.style.layers.SymbolLayer;
import org.maplibre.android.style.sources.GeoJsonOptions;
import org.maplibre.android.style.sources.GeoJsonSource;
import org.maplibre.geojson.FeatureCollection;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment {

    private MapView mapView;
    private MapLibreMap map;
    private GeoJsonSource clusterSource;
    private SharedPreferences prefs;

    private final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    centerMapOnUserLocation();
                } else {
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        prefs = requireActivity().getSharedPreferences("prefs", MODE_PRIVATE);
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
                // Start with a wider view to see more markers
                map.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(40.0, -8.0)) // Center of Europe
                        .zoom(4.0) // Very zoomed out to see the full range
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

                // Remove test markers - only load API markers
                loadApiMarkers();
            });
        });
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
                    .setMessage(
                            "Your location will be approximate. To enable precise location, go to app settings and grant Fine Location permission.")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, id) -> centerMapOnUserLocation())
                    .setNegativeButton("Go to Settings", (dialog, id) -> openAppSettings())
                    .create()
                    .show();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), finePermission) ||
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), coarsePermission)) {
            new AlertDialog.Builder(getContext())
                    .setMessage("We need your location to center the map. Please grant location permission.")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, id) -> permissionLauncher.launch(finePermission))
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

    private void loadApiMarkers() {
        Log.d("MapFragment", "Starting to load API markers");
        ApiClient.getApiService(getContext()).getAllMarkers().enqueue(new Callback<MarksResponse>() {
            @Override
            public void onResponse(Call<MarksResponse> call, Response<MarksResponse> response) {
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

                        Log.d("MapFragment", "Loaded " + featureCollection.features().size() + " markers");

                        // Log coordinates to verify they're different
                        if (featureCollection.features().size() > 0) {
                            for (int i = 0; i < Math.min(10, featureCollection.features().size()); i++) {
                                var feature = featureCollection.features().get(i);
                                if (feature.geometry() instanceof org.maplibre.geojson.Point) {
                                    org.maplibre.geojson.Point point = (org.maplibre.geojson.Point) feature.geometry();
                                    Log.d("MapFragment", "Marker " + i + ": [" + point.longitude() + ", "
                                            + point.latitude() + "] - " + feature.properties().get("title"));
                                }
                            }
                        }

                        if (map != null && map.getStyle() != null) {
                            Style style = map.getStyle();

                            // Remove any existing sources and layers
                            if (style.getSource("api-markers") != null) {
                                style.removeSource("api-markers");
                            }
                            if (style.getLayer("api-marker-layer") != null) {
                                style.removeLayer("api-marker-layer");
                            }

                            // Add simple source without clustering
                            GeoJsonSource markersSource = new GeoJsonSource("api-markers", featureCollection);
                            style.addSource(markersSource);

                            // Add simple marker layer
                            SymbolLayer markerLayer = new SymbolLayer("api-marker-layer", "api-markers");
                            markerLayer.setProperties(
                                    iconImage("marker"),
                                    iconSize(1.0f),
                                    iconAllowOverlap(true));
                            style.addLayer(markerLayer);

                            Log.d("MapFragment",
                                    "Added " + featureCollection.features().size() + " API markers to map");
                        }
                    } catch (Exception e) {
                        Log.e("MapFragment", "Error parsing GeoJSON", e);
                        Utils.showToast(getContext(), "Erro ao processar marcadores", getLayoutInflater());
                    }
                } else {
                    Log.e("MapFragment", "API response not successful: " + response.code());
                    Utils.showToast(getContext(), "Erro ao carregar marcadores", getLayoutInflater());
                }
            }

            @Override
            public void onFailure(Call<MarksResponse> call, Throwable t) {
                Log.e("MapFragment", "API call failed", t);
                Utils.showToast(getContext(), "Erro: " + t.getMessage(), getLayoutInflater());
            }
        });
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

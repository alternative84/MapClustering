package es.ondroid.mapclustering;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidmapsextensions.ClusteringSettings;
import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends ActionBarActivity implements MyMapFragment.MapCallback {

    private static final double[] CLUSTER_SIZES = new double[]{180, 160, 144, 120, 96};
    private MyMapFragment mMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        mMapFragment = (MyMapFragment) getSupportFragmentManager().findFragmentByTag(MyMapFragment.TAG);
        if (mMapFragment == null) {
            mMapFragment = new MyMapFragment();
            mMapFragment.setMapCallback(this);

            // mMapFragment.getSupportMapFragment().getMapAsync(this);
            ft.add(R.id.fragment_container, mMapFragment, MyMapFragment.TAG);
        } else {
            mMapFragment.setMapCallback(this);
        }

        ft.commit();
    }

    // --------------------------------------------------------------------------------------------
    //                                   MAP SETUP
    // --------------------------------------------------------------------------------------------

    @Override
    public void onMapReady(com.androidmapsextensions.GoogleMap googleMap) {

        if (googleMap != null) {

            setupMap();

            addMarkers();

            updateClustering(2, true);
        }
    }

    private void setupMap() {
        mMapFragment.getExtendedMap().getUiSettings().setMapToolbarEnabled(false);

        mMapFragment.getExtendedMap().setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            private TextView tv;

            {
                tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.BLACK);
            }

            private Collator collator = Collator.getInstance();
            private Comparator<Marker> comparator = new Comparator<Marker>() {
                public int compare(Marker lhs, Marker rhs) {
                    String leftTitle = lhs.getTitle();
                    String rightTitle = rhs.getTitle();
                    if (leftTitle == null && rightTitle == null) {
                        return 0;
                    }
                    if (leftTitle == null) {
                        return 1;
                    }
                    if (rightTitle == null) {
                        return -1;
                    }
                    return collator.compare(leftTitle, rightTitle);
                }
            };

            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                float zoomActual = mMapFragment.getExtendedMap().getCameraPosition().zoom;
                float zoomMax = mMapFragment.getExtendedMap().getMaxZoomLevel();

                // Toast.makeText(getApplicationContext(), "actual: " + zoomActual + ", max: " + zoomMax, Toast.LENGTH_LONG).show();

                if ((zoomActual >= zoomMax - 5) && marker.isCluster()) {
                    Toast.makeText(getApplicationContext(), "Eventos en mismo sitio", Toast.LENGTH_LONG).show();
                } else if (marker.isCluster()) {
                    List<Marker> markers = marker.getMarkers();
                    int i = 0;
                    String text = "";
                    while (i < 3 && markers.size() > 0) {
                        Marker m = Collections.min(markers, comparator);
                        String title = m.getTitle();
                        if (title == null) {
                            break;
                        }
                        text += title + "\n";
                        markers.remove(m);
                        i++;
                    }
                    if (text.length() == 0) {
                        text = "Markers with mutable data";
                    } else if (markers.size() > 0) {
                        text += "y " + markers.size() + " más...";
                    } else {
                        text = text.substring(0, text.length() - 1);
                    }
                    tv.setText(text);
                    return tv;
                } else {
//                    if (marker.getData() instanceof MutableData) {
//                        MutableData mutableData = marker.getData();
//                        tv.setText("Value: " + mutableData.value);
//                        return tv;
//                    }
                }

                return null;
            }
        });

        mMapFragment.getExtendedMap().setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (marker.isCluster()) {
                    List<Marker> markers = marker.getMarkers();
                    LatLngBounds.Builder builder = LatLngBounds.builder();
                    for (Marker m : markers) {
                        builder.include(m.getPosition());
                    }
                    LatLngBounds bounds = builder.build();

                    float zoomActual = mMapFragment.getExtendedMap().getCameraPosition().zoom;
                    float zoomMax = mMapFragment.getExtendedMap().getMaxZoomLevel();


                    mMapFragment.getExtendedMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, getResources().getDimensionPixelSize(R.dimen.padding)));
                }
            }
        });
    }

    private void addMarkers() {
        // Markers prueba
        ArrayList<LatLng> arrayPosiciones = new ArrayList<>();
        arrayPosiciones.add(new LatLng(37.183921, -6.962879));
        arrayPosiciones.add(new LatLng(37.256346, -6.95969));
        arrayPosiciones.add(new LatLng(37.261372, -6.957967));
        arrayPosiciones.add(new LatLng(37.265543, -6.952087));
        arrayPosiciones.add(new LatLng(37.255885, -6.95171));
        arrayPosiciones.add(new LatLng(37.265123, -6.95161));
        arrayPosiciones.add(new LatLng(37.169897, -6.951505));
        arrayPosiciones.add(new LatLng(37.253725, -6.95094));
        arrayPosiciones.add(new LatLng(37.173103, -6.950683));
        arrayPosiciones.add(new LatLng(37.255635, -6.948534));

        ArrayList<MarkerOptions> markers = new ArrayList<>();

        for (int i = 0; i < arrayPosiciones.size(); i++) {
            MarkerOptions mo = new MarkerOptions();
            mo.position(arrayPosiciones.get(i));
            mo.title("Evento " + i);
            mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marcador_verde));
            markers.add(mo);
        }

        for (int i = 0; i < markers.size(); i++) {
            mMapFragment.getExtendedMap().addMarker(markers.get(i));
        }

        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(37.265123, -6.95161), 11);
        mMapFragment.getExtendedMap().moveCamera(cu);
    }

    void updateClustering(int clusterSizeIndex, boolean enabled) {
        if (mMapFragment.getExtendedMap() == null) {
            return;
        }
        ClusteringSettings clusteringSettings = new ClusteringSettings();
        clusteringSettings.addMarkersDynamically(true);

        if (enabled) {
            clusteringSettings.clusterOptionsProvider(new MyClusterOptionsProvider(getResources()));

            // double clusterSize = CLUSTER_SIZES[clusterSizeIndex];
            double clusterSize = 70;
            clusteringSettings.clusterSize(clusterSize);
        } else {
            clusteringSettings.enabled(false);
        }
        mMapFragment.getExtendedMap().setClustering(clusteringSettings);
    }

    // --------------------------------------------------------------------------------------------
    //                                   OPTIONS MENU
    // --------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

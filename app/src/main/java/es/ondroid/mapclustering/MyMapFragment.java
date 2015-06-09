package es.ondroid.mapclustering;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.SupportMapFragment;

public class MyMapFragment extends SupportMapFragment {
    public static final String TAG = "mapFragment";
    private MapCallback callback;
    private GoogleMap mMap;

    public void setMapCallback(MapCallback callback) {
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (callback != null) {
            mMap = getExtendedMap();
            callback.onMapReady(mMap);
        }

        // Fix para dispositivos 2.3.x (si da problemas, hacerlo cada vez que hay que mostrar/ocultar el mapa
        setMapTransparent((ViewGroup) getView());
    }

    // Janky "fix" to prevent artefacts when embedding GoogleMaps in a sliding view.
    // https://github.com/jfeinstein10/SlidingMenu/issues/168
    // set background to transparent
    public void setMapTransparent(ViewGroup group) {
        int childCount = group.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = group.getChildAt(i);
            if (child instanceof ViewGroup) {
                setMapTransparent((ViewGroup) child);
            } else if (child instanceof SurfaceView) {
                child.setBackgroundColor(0x00000000);
            }
        }
    }

    public static interface MapCallback {
        public void onMapReady(GoogleMap map);
    }
}

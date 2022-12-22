package com.bbAndy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.GeoPoint;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.neighboursearch.KDTree;


public class SukiStoreMap {

    static ArrayList<Attribute> storeGeopoints;
    static Instances instances;
    static Attribute latitude;
    static Attribute longitude;
    static KDTree kdTree;
    static Instances nearest;
    static Boolean isFirebaseInitialize = false;
    static FirebaseOptions options;
    static FileInputStream serviceAccount;

    public static void initializeMap() throws Exception {

        File file1, file2, file3, file4;
        file1 = new File("./src/main/resources/serviceAccountKey.json"); 
        file2 = new File("serviceAccountKey.json");
        file3 = new File("D:\\home\\site\\wwwroot\\serviceAccountKey.json");
        file4 = new File("../serviceAccountKey.json");
        

        if (file1.exists()){
            serviceAccount = new FileInputStream(file1);
        }

        if (file2.exists()){
            serviceAccount = new FileInputStream(file2);
        }

        if (file3.exists()){
            serviceAccount = new FileInputStream(file3);
        }

        if (file4.exists()){
            serviceAccount = new FileInputStream(file4);
        }

        options = new FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .setDatabaseUrl("https://pabili-app-e5c54-default-rtdb.asia-southeast1.firebasedatabase.app")
        .build();

    

        if (isFirebaseInitialize == false) {
            
            // Initialize the default app
            FirebaseApp.initializeApp(options);
            isFirebaseInitialize = true;
        }
        
        // Use the shorthand notation to retrieve the default app's services
        Firestore db = FirestoreClient.getFirestore();

        // Create a reference to the stores collection
        CollectionReference stores = db.collection("stores");
        
        // retrieve  query results asynchronously using query.get()
        ApiFuture<QuerySnapshot> querySnapshot = stores.get();

        initializeInstances();
        
        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            GeoPoint gPoint = document.getGeoPoint("geopoint");
            registerGeopoints(gPoint.getLatitude(), gPoint.getLongitude());
        }
    }

    public static void initializeInstances() throws Exception{
        storeGeopoints = new ArrayList<>();
        latitude = new Attribute("latitude");
        storeGeopoints.add(latitude);
        longitude = new Attribute("longitude");
        storeGeopoints.add(longitude);
        
        instances = new Instances("test", storeGeopoints, 50);
    }

    public static void registerGeopoints(double inputLatitude, double inputLongitude){
        Instance i = new DenseInstance(2);
        i.setValue(latitude, inputLatitude);
        i.setValue(longitude, inputLongitude);
        instances.add(i);
    }

    public static String findNearestStore(double inputLatitude, double inputLongitude , double rangeMeter) {
        
        ArrayList<SukiStore> arrStores = new ArrayList<>();

        String jsonNearestStores = "";

        Instance i = new DenseInstance(2);
        i.setValue(latitude, inputLatitude) ;
        i.setValue(longitude, inputLongitude);
        instances.add(0, i);
        kdTree = new KDTree();

        try {
            kdTree.setInstances(instances);
            nearest = kdTree.kNearestNeighbours(instances.get(0), 5);
            for(Instance near: nearest){

                double lat1 = inputLatitude;
                double lon1 = inputLongitude;
                double lat2 = near.value(latitude);
                double lon2 = near.value(longitude);
                
                double computedDistance = Location.computeDistance(lat1, lon1, lat2, lon2);
                
                if (computedDistance < rangeMeter){
                    arrStores.add(new SukiStore(findStoreName(lat2, lon2), lat2, lon2,  computedDistance));
                }

                instances.remove(0);

                jsonNearestStores = new Gson().toJson(arrStores);
            }   

        } catch (Exception e) {
            e.printStackTrace();
        }
                
        return jsonNearestStores;
    }

    private static String findStoreName(double lat2, double lon2) throws InterruptedException, ExecutionException {
        // Use the shorthand notation to retrieve the default app's services
        Firestore db = FirestoreClient.getFirestore();

        // Create a reference to the stores collection
        CollectionReference stores = db.collection("stores");
        
        // target gPoint
        GeoPoint targetGpoint = new GeoPoint(lat2, lon2); 

        // Create a query against the collection.
        Query query = stores.whereEqualTo("geopoint", targetGpoint);

        // retrieve  query results asynchronously using query.get()
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        
        ArrayList<String> storeNames = new ArrayList<>(); 

        for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
            storeNames.add(document.getString("username"));
        }

        return new Gson().toJson(storeNames);
    }
}

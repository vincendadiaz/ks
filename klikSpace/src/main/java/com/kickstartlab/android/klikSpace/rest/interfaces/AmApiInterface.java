package com.kickstartlab.android.klikSpace.rest.interfaces;

import com.kickstartlab.android.klikSpace.rest.models.Asset;
import com.kickstartlab.android.klikSpace.rest.models.AssetImages;
import com.kickstartlab.android.klikSpace.rest.models.City;
import com.kickstartlab.android.klikSpace.rest.models.DeviceType;
import com.kickstartlab.android.klikSpace.rest.models.Location;
import com.kickstartlab.android.klikSpace.rest.models.MemberData;
import com.kickstartlab.android.klikSpace.rest.models.ResultObject;
import com.kickstartlab.android.klikSpace.rest.models.ScanLog;
import com.kickstartlab.android.klikSpace.rest.models.Venue;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by awidarto on 10/14/14.
 */
public interface AmApiInterface {

    @FormUrlEncoded
    @POST("/")
    public void doLogin(@Field("user") String user, @Field("pwd") String pwd, Callback<MemberData> response);

    @GET("/auth/logout/{key}")
    public void logout(
            @Path("key") String key,
            Callback callback
    );

    //tambahin utk klikSpace
    @GET("/venue")
    public void getVenue(@Query("key") String key, @Query("location") String location ,Callback<List<Venue>> response);

    @GET("/venuetype")
    public void geVenueType(@Query("key") String key, Callback<List<DeviceType>> response);

    @POST("/venue")
    public void sendVenue(@Query("key") String key,  @Body Venue venue, Callback<ResultObject> result );

    @PUT("/venue/{id}")
    public void updateVenue(@Query("key") String key,  @Path("id") String id ,@Body Venue venue, Callback<ResultObject> result );


    @POST("/")
    public void getCity(@Query("key") String key ,Callback<List<City>> response);

    @POST("/city")
    public void sendLocation(@Query("key") String key,  @Body City city, Callback<ResultObject> result );

    @PUT("/city/{id}")
    public void updateLocation(@Query("key") String key,  @Path("id") String id ,@Body City city, Callback<ResultObject> result );

    @PUT("/sync/racks")
    public void updateLocationBatch( @Query("key") String key, @Query("batch") Integer batch, @Body List<City> cities, Callback<ResultObject> result );

    @GET("/location")
    public void getLocation(@Query("key") String key, @Query("city") String city ,Callback<List<Location>> response);

    //

    @GET("/location")
    public void getLocation(@Query("key") String key, Callback<List<City>> response);

    @GET(("/rack"))
    public void getRack(@Query("key") String key, @Query("location") String location ,Callback<List<Location>> response);

    @POST("/location")
    public void sendRack(@Query("key") String key,  @Body Location location, Callback<ResultObject> result );

    @PUT("/location/{id}")
    public void updateRack(@Query("key") String key,  @Path("id") String id ,@Body Location location, Callback<ResultObject> result );

    @PUT("/sync/locations")
    public void updateRackBatch( @Query("key") String key, @Query("batch") Integer batch, @Body List<Location> locations, Callback<ResultObject> result );

    @GET("/asset")
    public void getAsset(@Query("key") String key, @Query("rack") String rack ,Callback<List<Asset>> response);

    @GET("/assettype")
    public void getAssetType(@Query("key") String key, Callback<List<DeviceType>> response);

    @POST("/asset")
    public void sendAsset(@Query("key") String key,  @Body Asset asset, Callback<ResultObject> result );

    @PUT("/asset/{id}")
    public void updateAsset(@Query("key") String key,  @Path("id") String id ,@Body Asset asset, Callback<ResultObject> result );

    @PUT("/sync/klikSpace")
    public void updateAssetBatch( @Query("key") String key, @Query("batch") Integer batch, @Body List<Asset> klikSpace, Callback<ResultObject> result );

    @POST("/sync/scanlog")
    public void sendScanlogBatch( @Query("key") String key, @Query("batch") Integer batch, @Body List<ScanLog> logs, Callback<List<ResultObject>> result );

    @GET("/img")
    public void getImage( @Query("key") String key,  @Query("id") String id, @Query("cls") String cls ,Callback<List<AssetImages>> response);

    @Multipart
    @POST("/upload")
    public void uploadImage(
        @Query("key") String key,
        @Query("ns") String ns,
        @Query("parid") String parent_id,
        @Query("parclass") String parent_class,
        @Query("fid") String file_id,
        @Query("img") String img_id,
        @Part("imagefile") TypedFile file,
        Callback<ResultObject> result
    );
}

package com.kickstartlab.android.klikSpace.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.kickstartlab.android.klikSpace.events.VenueEvent;
import com.kickstartlab.android.klikSpace.events.DeviceTypeEvent;
import com.kickstartlab.android.klikSpace.events.ImageEvent;
import com.kickstartlab.android.klikSpace.events.CityEvent;
import com.kickstartlab.android.klikSpace.events.LogEvent;
import com.kickstartlab.android.klikSpace.events.MerchantEvent;
import com.kickstartlab.android.klikSpace.events.OrderEvent;
import com.kickstartlab.android.klikSpace.events.LocationEvent;
import com.kickstartlab.android.klikSpace.events.ScannerEvent;
import com.kickstartlab.android.klikSpace.fragments.VenueDetailFragment;
import com.kickstartlab.android.klikSpace.fragments.OrderListFragment;
import com.kickstartlab.android.klikSpace.fragments.LocationDetailFragment;
import com.kickstartlab.android.klikSpace.fragments.ScannerFragment;
import com.kickstartlab.android.klikSpace.fragments.SettingsFragment;
import com.kickstartlab.android.klikSpace.rest.interfaces.JwhApiInterface;
import com.kickstartlab.android.klikSpace.rest.models.AssetImages;
import com.kickstartlab.android.klikSpace.rest.models.City;
import com.kickstartlab.android.klikSpace.rest.models.DeviceType;
import com.kickstartlab.android.klikSpace.rest.models.Location;
import com.kickstartlab.android.klikSpace.rest.models.Merchant;
import com.kickstartlab.android.klikSpace.rest.models.OrderItem;
import com.kickstartlab.android.klikSpace.R;
import com.kickstartlab.android.klikSpace.fragments.VenueListFragment;
import com.kickstartlab.android.klikSpace.fragments.CityListFragment;
import com.kickstartlab.android.klikSpace.fragments.LocationListFragment;
import com.kickstartlab.android.klikSpace.rest.interfaces.AmApiInterface;
import com.kickstartlab.android.klikSpace.rest.models.Asset;
import com.kickstartlab.android.klikSpace.rest.models.ResultObject;
import com.kickstartlab.android.klikSpace.rest.models.ScanLog;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;


public class MainActivity extends ActionBarActivity implements
        FragmentManager.OnBackStackChangedListener {

    private static final String SCREEN_LOCATION = "location";
    private static final String SCREEN_SCAN = "scan";

    private String current_mode;
    private String current_location;
    private String current_rack;
    private String current_asset;

    Toolbar mToolbar;

    SmoothProgressBar mProgressBar;

    TextView mProgressIndicator;

    SharedPreferences spref;
    String akey;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mProgressBar = (SmoothProgressBar) findViewById(R.id.loadProgressBar);
        mProgressIndicator = (TextView) findViewById(R.id.progressMessage);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        mProgressBar.setVisibility(View.GONE);
        mProgressIndicator.setVisibility(View.GONE);

        EventBus.getDefault().register(this);
        getSupportFragmentManager().addOnBackStackChangedListener(this);

        spref = PreferenceManager.getDefaultSharedPreferences(this);

        akey = spref.getString("auth","");

        uid = spref.getString("uid","");

        if("".equalsIgnoreCase(akey)){
            showLogin();
        }else{
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new CityListFragment(),"location_fragment")
                        .setBreadCrumbTitle(getResources().getString(R.string.app_name))
                                //.addToBackStack("merchant_fragment")
                        .commit();
            }

        }

        refreshAssetType();
    }


    @Override
    public void onBackStackChanged() {
        String title;
        FragmentManager fm = getSupportFragmentManager();
        int bc = fm.getBackStackEntryCount();
        Log.i("BC",String.valueOf(bc));
        if(bc > 0){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            title = fm.getBackStackEntryAt(bc - 1).getBreadCrumbTitle().toString();

            getSupportActionBar().setTitle(title);
        }else{
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            FragmentManager fm = getSupportFragmentManager();

            fm.beginTransaction()
                    .replace(R.id.container, SettingsFragment.newInstance("Do","Setting"),"setting_fragment")
                    .setBreadCrumbTitle(getString(R.string.settings_title))
                    .addToBackStack("setting_fragment")
                    .commit();
            getSupportActionBar().setTitle(getString(R.string.settings_title));

            //Intent i = new Intent(this, SettingsActivity.class );
            //startActivity(i);
        }

        if(id == R.id.action_scan_rack){
            current_mode = "location";
            doScan(current_rack, current_mode);
        }

        if(id == R.id.action_scan_location){
            current_mode = "location";
            doScan(current_location, current_mode);
        }

        if(id == R.id.action_scan_asset){
            current_mode = "asset";
            doScan(current_rack, current_mode);
        }

        if(id == R.id.action_refresh){
            //refreshOrders(String.valueOf(current_merchant));
        }

        if(id == R.id.action_scan){
            //doScan(String.valueOf(current_merchant));
        }

        if(id == R.id.action_merchant){

            refreshMerchants();
        }


        if(id == R.id.action_refresh_location){
            refreshLocations();
        }

        if(id == R.id.action_refresh_rack){
            refreshRack(current_location);
        }

        if(id == R.id.action_edit_rack){
            EventBus.getDefault().post(new LocationEvent("editRack"));
        }

        if(id == R.id.action_refresh_rack_detail){
            downsyncImages("all","all");
            //Asset a = Select.from(Asset.class).where( Condition.prop("ext_id").eq(current_asset) ).first();
            EventBus.getDefault().post(new LocationEvent("refreshDetailView"));
        }

        if(id == R.id.action_refresh_asset){
            refreshAsset(current_rack);
        }

        if(id == R.id.action_refresh_asset_detail){
            downsyncImages("all","all");
            //Asset a = Select.from(Asset.class).where( Condition.prop("ext_id").eq(current_asset) ).first();
            EventBus.getDefault().post(new VenueEvent("refreshDetailView"));
        }

        if(id == R.id.action_edit_asset){
            EventBus.getDefault().post(new VenueEvent("editAsset"));
        }

        if(id == R.id.action_rack_detail){
            Location r = Select.from(Location.class).where(Condition.prop("ext_id").eq(current_rack)).first();
            EventBus.getDefault().post(new LocationEvent("selectDetail", r ));
        }

        if(id == R.id.action_authorize){
            Intent i = new Intent(this, AuthorizeActivity.class);
            startActivity(i);
        }

        if(id == R.id.action_logout){
            doSignOut();
        }

        if(id == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /* EVENTS */

    public void onEvent(ScannerEvent se){
        if(se.getCommand() == "scan"){
            String mode = se.getMode();

            if(mode.equalsIgnoreCase("location")){
                doScan(current_rack, mode);
            }

            if(mode.equalsIgnoreCase("location")){
                doScan(current_location, mode);
            }

            if(mode.equalsIgnoreCase("asset")){
                doScan(current_rack, mode);
            }
        }
    }

    public void onEvent(CityEvent le){
        if(le.getAction() == "select"){
            FragmentManager fm = getSupportFragmentManager();

            fm.beginTransaction()
                    .replace(R.id.container, LocationListFragment.newInstance(le.getCity().getExtId(), le.getCity().getName()),"rack_fragment")
                    .setBreadCrumbTitle(le.getCity().getName())
                    .addToBackStack("rack_fragment")
                    .commit();
            getSupportActionBar().setTitle(le.getCity().getName());
            Toast.makeText(this, le.getCity().getName(),Toast.LENGTH_SHORT ).show();

            current_location = le.getCity().getExtId();
        }

        if(le.getAction() == "refreshImage"){
            downsyncImages(le.getCity().getExtId(), "location");
        }
    }

    public void onEvent(LocationEvent re){
        if(re.getAction() == "select") {
            FragmentManager fm = getSupportFragmentManager();

            fm.beginTransaction()
                    .replace(R.id.container, VenueListFragment.newInstance(re.getLocation().getExtId(), re.getLocation().getSKU()), "asset_fragment")
                    .setBreadCrumbTitle(re.getLocation().getSKU())
                    .addToBackStack("asset_fragment")
                    .commit();
            getSupportActionBar().setTitle(re.getLocation().getSKU());
            Toast.makeText(this, re.getLocation().getSKU(), Toast.LENGTH_SHORT).show();

            current_rack = re.getLocation().getExtId();

        }else if(re.getAction() == "selectDetail"){

            FragmentManager fm = getSupportFragmentManager();
            current_asset = re.getLocation().getExtId();
            fm.beginTransaction()
                    .replace(R.id.container, LocationDetailFragment.newInstance(re.getLocation().getExtId(), re.getLocation().getSKU()), "rack_detail_fragment")
                    .setBreadCrumbTitle(re.getLocation().getSKU())
                    .addToBackStack("rack_detail_fragment")
                    .commit();
            getSupportActionBar().setTitle(re.getLocation().getSKU());

            Toast.makeText(this, re.getLocation().getSKU(), Toast.LENGTH_SHORT).show();

            current_location = re.getLocation().getCityId();

        }else if(re.getAction() == "refreshById"){
            refreshRack(re.getLocationId());
        }else if(re.getAction() == "syncRack"){
            Location location = re.getLocation();
            syncRack(location);
        }else if(re.getAction() == "upsyncBatch"){
            upsyncBatchRack();
        }else if(re.getAction() == "upsyncRack"){
            Location location = re.getLocation();
            upsyncRack(location);
        }else if(re.getAction() == "refreshImage"){
            downsyncImages(re.getLocation().getExtId(), "location");
        }else if(re.getAction() == "createRack"){
            City l = Select.from(City.class).where( Condition.prop("ext_id").eq(current_location) ).first();
            Intent i = new Intent(this, AddLocationActivity.class);
            i.putExtra("locationId",current_location);
            i.putExtra("locationName",l.getName());
            startActivity(i);
        }

    }

    public void onEvent(VenueEvent ae){
        if(ae.getAction() == "select"){
            FragmentManager fm = getSupportFragmentManager();
            current_asset = ae.getAsset().getExtId();
            fm.beginTransaction()
                    .replace(R.id.container, VenueDetailFragment.newInstance(ae.getAsset().getExtId(), ae.getAsset().getSKU()), "asset_detail_fragment")
                    .setBreadCrumbTitle(ae.getAsset().getSKU())
                    .addToBackStack("asset_detail_fragment")
                    .commit();
            getSupportActionBar().setTitle(ae.getAsset().getSKU());
            Toast.makeText(this, ae.getAsset().getSKU(), Toast.LENGTH_SHORT).show();

            current_rack = ae.getAsset().getRackId();
        }else if(ae.getAction() == "refreshById"){
            refreshAsset(ae.getRackId());
        }else if(ae.getAction() == "syncAsset"){
            Asset asset = ae.getAsset();
            syncAsset( asset );
        }else if(ae.getAction() == "upsyncBatch"){
            upsyncBatchAsset();
        }else if(ae.getAction() == "upsyncAsset"){
            Asset asset = ae.getAsset();
            upsyncAsset(asset);
        }else if(ae.getAction() == "refreshImage"){
            downsyncImages(ae.getAsset().getExtId(), "asset");
        }else if(ae.getAction() == "moveRack"){
            Asset asset = ae.getAsset();
            String rackId = ae.getRackId();
            asset.setRackId(rackId);
            asset.setLocalEdit(1);
            asset.save();
        }else if(ae.getAction() == "createAsset"){
            Location r = Select.from(Location.class).where( Condition.prop("ext_id").eq(current_rack) ).first();
            Intent i = new Intent(this, AddVenueActivity.class);
            i.putExtra("rackId",current_rack);
            i.putExtra("rackName",r.getSKU());
            startActivity(i);
        }

    }

    public void onEvent(LogEvent le){
        if( le.getCommand() == "syncLog") {
            syncScanlog();
        }

        if( le.getCommand() == "purgeLog"){
            ScanLog.deleteAll(ScanLog.class);
            Toast.makeText(MainActivity.this, "Logs Cleared", Toast.LENGTH_SHORT).show();
        }
    }

    public void onEvent(ImageEvent im){
        if( im.getAction() == "sync" ){
            downsyncImages( im.getEntityId() , im.getEntityType());
        }
        if( im.getAction() == "upsync" ){
            uploadImages(im.getEntityId(), im.getEntityType());
        }

        if( im.getAction() == "upload" ){
            uploadImage(im.getEntityId());
        }

    }

    public void onEvent(MerchantEvent me){
        if(me.getAction() == "select"){
            FragmentManager fm = getSupportFragmentManager();

            fm.beginTransaction()
                    .replace(R.id.container, OrderListFragment.newInstance(me.getMerchant().getExtId(), me.getMerchant().getMerchantname()),"order_fragment")
                    .setBreadCrumbTitle(me.getMerchant().getMerchantname())
                    .addToBackStack("order_fragment")
                    .commit();
            getSupportActionBar().setTitle(me.getMerchant().getMerchantname());
            Toast.makeText(this, me.getMerchant().getMerchantname(),Toast.LENGTH_SHORT ).show();

            //current_merchant = me.getMerchant().getExtId();
        }
    }

    public void onEvent(DeviceTypeEvent dt){
        if("sync".equalsIgnoreCase(dt.getAction())){
            refreshAssetType();
        }
    }

    public void onEvent(OrderEvent oe){
        if(oe.getAction() == "select"){
            OrderItem o = oe.getOrderItem();
            String message = new StringBuilder("ID : ")
                    .append(o.getDeliveryId())
                    .append(System.lineSeparator())
                    .append("Type : ").append(o.getDeliveryType())
                    .append(System.lineSeparator())
                    .append("No Kode Toko : ").append(o.getMerchantTransId())
                    .append(System.lineSeparator())
                    .append("Zone : ").append(o.getBuyerdeliveryzone())
                    .append(System.lineSeparator())
                    .append("Shipping Address : ")
                    .append(System.lineSeparator())
                    .append(o.getShippingAddress())
                    .append(System.lineSeparator())
                    .append(o.getBuyerdeliverycity())
                    .toString();
            String device = o.getDeviceName();
            String courier = o.getCourierName();
            String merchant = o.getMerchantName();
            String deliveryid = o.getDeliveryId();
            String invoice = o.getMerchantTransId();
            String buyer = o.getBuyerName();
            String recipient = o.getRecipientName();
            String deliverydate = o.getAssignmentDate();
            String deliverytype = o.getDeliveryType();
            String zone = o.getBuyerdeliveryzone();

            try{
                deliverydate = deliverydate.substring(0,10);
            }catch(NullPointerException e){
                deliverydate = "";
            }
            showMessageDialog(message, device,
                    buyer, recipient,deliverydate, deliverytype, courier, merchant, deliveryid, invoice, zone);
            Log.i("dialog", "open");
            //Toast.makeText(this, oe.getOrderItem().toString(),Toast.LENGTH_SHORT ).show();
        }else if(oe.getAction() == "refreshById"){
            refreshOrders(oe.getMerchantId());
        }else if(oe.getAction()== "status"){
            Log.i("event delivery id", oe.getMerchantId());
            updateStatus(oe.getMerchantId());
        }
    }

    public void showMessageDialog(String message,String device,
                                  String buyer, String recipient,String deliverydate,
                                  String deliverytype,
                                  String courier,String merchant,String deliveryid,String invoice, String zone ) {
        /*
        DialogFragment fragment = MessageDialogFragment.newInstance("Order Info", message, device,
                buyer, recipient,deliverydate,
                deliverytype, courier, merchant, deliveryid, invoice,zone, this);
        fragment.show( getSupportFragmentManager(), "scan_results");*/

         String mMessage = message;
         String mDevice = device;
         String mCourier = courier;
         String mMerchant = merchant;
         final String mDeliveryId = deliveryid;
         String mInvoice = invoice;
         String mBuyerName = buyer;
         String mRecipientName = recipient;
         String mDeliveryDate = deliverydate;
         String mDeliveryType = deliverytype;
         String mDeliveryZone = zone;

        LayoutInflater inflater = this.getLayoutInflater();
        View mview = inflater.inflate(R.layout.dialog_message_layout,null);

        TextView txtDevice = (TextView) mview.findViewById(R.id.deviceValue);
        TextView txtCourier = (TextView) mview.findViewById(R.id.courierValue);
        TextView txtDeliveryId = (TextView) mview.findViewById(R.id.deliveryIdValue);
        TextView txtInvoice = (TextView) mview.findViewById(R.id.invoiceValue);
        TextView txtMerchant = (TextView) mview.findViewById(R.id.merchantValue);
        TextView txtDeliveryDate = (TextView) mview.findViewById(R.id.dateValue);
        TextView txtRecipientName = (TextView) mview.findViewById(R.id.recipientValue);
        TextView txtBuyerName = (TextView) mview.findViewById(R.id.buyerValue);
        TextView txtDeliveryType = (TextView) mview.findViewById(R.id.typeValue);
        TextView txtZone = (TextView) mview.findViewById(R.id.zoneValue);

        mDeliveryType = ("Delivery Only".equalsIgnoreCase(mDeliveryType))?"DO":mDeliveryType;

        txtDevice.setText(mDevice);
        txtCourier.setText(mCourier);
        txtDeliveryId.setText(mDeliveryId);
        txtMerchant.setText(mMerchant);
        txtInvoice.setText(mInvoice);
        txtBuyerName.setText(mBuyerName);
        txtRecipientName.setText(mRecipientName);
        txtDeliveryDate.setText(mDeliveryDate);
        txtDeliveryType.setText(mDeliveryType);
        txtZone.setText(mDeliveryZone);


        if("COD".equalsIgnoreCase(mDeliveryType) || "CCOD".equalsIgnoreCase(mDeliveryType) ){
            txtDeliveryType.setBackgroundColor( getResources().getColor(R.color.red) );
            txtDeliveryType.setTextColor( getResources().getColor(R.color.white) );
        }else{
            txtDeliveryType.setBackgroundColor( getResources().getColor(R.color.green) );
            txtDeliveryType.setTextColor(getResources().getColor(R.color.white));
        }

        new MaterialDialog.Builder(this)
                .title(R.string.dialog_order_title)
                .positiveText(R.string.ok_button)
                .positiveColor(R.color.green)
                .negativeText(R.string.cancel_button)
                .negativeColor(R.color.red)
                .customView(mview, true)
                .callback(new MaterialDialog.ButtonCallback() {
                              @Override
                              public void onPositive(MaterialDialog dialog) {
                                  //super.onPositive(dialog);
                                  String deliveryId = mDeliveryId;
                                  Log.i("current delivery Id", deliveryId);
                                  updateStatus(deliveryId);
                                  EventBus.getDefault().post(new ScannerEvent("resume"));

                              }

                              @Override
                              public void onNegative(MaterialDialog dialog) {
                                  EventBus.getDefault().post(new ScannerEvent("resume"));
                                  super.onNegative(dialog);
                              }

                              @Override
                              public void onNeutral(MaterialDialog dialog) {
                                  super.onNeutral(dialog);
                              }
                          }

                )
                .show();
    }

    public void closeMessageDialog() {
        closeDialog("scan_results");
    }

    public void closeDialog(String dialogName) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        DialogFragment fragment = (DialogFragment) fragmentManager.findFragmentByTag(dialogName);
        if(fragment != null) {
            fragment.dismiss();
        }
        Log.i("dialog", "closed");
        EventBus.getDefault().post(new ScannerEvent("resume"));
    }

    /*
    @Override
    public void onDialogPositiveClick(MessageDialogFragment dialog, String mDeliveryId) {
        // Resume the camera
        String deliveryId = mDeliveryId;
        Log.i("current delivery Id", deliveryId);
        updateStatus(deliveryId);
        EventBus.getDefault().post(new ScannerEvent("resume"));
    }
    */




    public void refreshMerchants(){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.jwh_api_base_url))
                .build();
        JwhApiInterface jwhApiInterface = restAdapter.create(JwhApiInterface.class);

        setProgressVisibility(true);

        jwhApiInterface.getMerchants(new Callback<List<Merchant>>() {
            @Override
            public void success(List<Merchant> merchants, Response response) {
                Log.i("merchants retrieved", String.valueOf(merchants.size()) );
                for(int i = 0; i < merchants.size(); i++){

                    Select select = Select.from(Merchant.class).where(Condition.prop("ext_id").eq(merchants.get(i).getExtId()))
                            .limit("1");

                    if(select.count() > 0){
                        Merchant mcb = (Merchant) select.first();
                        Merchant mc = merchants.get(i);

                        mcb.setMcCity(mc.getMcCity());
                        mcb.setMcDistrict(mc.getMcDistrict());
                        mcb.setMcEmail(mc.getMcEmail());
                        mcb.setMcPhone(mc.getMcPhone());
                        mcb.setMcStreet(mc.getMcStreet());
                        mcb.setMerchantname(mc.getMerchantname());
                        mcb.setMcMobile(mc.getMcMobile());
                        mcb.setProvince(mc.getProvince());

                        mcb.save();
                        Log.i("merchant updated",mc.getMerchantname());
                    }else{
                        merchants.get(i).save();
                        Log.i("merchant saved", merchants.get(i).getMerchantname());
                    }
                }
                setProgressVisibility(false);

                EventBus.getDefault().post(new MerchantEvent("refresh"));

            }

            @Override
            public void failure(RetrofitError error) {
                setProgressVisibility(false);
                Log.i("merchant get failure", error.toString());
                EventBus.getDefault().post(new MerchantEvent("refresh"));
            }
        });

    }

    public void refreshOrders(String merchantId){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.jwh_api_base_url))
                .build();
        JwhApiInterface jwhApiInterface = restAdapter.create(JwhApiInterface.class);

        Log.i("order merchant",merchantId);

        setProgressVisibility(true);

        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        jwhApiInterface.getOrders("JY-DEV",merchantId, today , new Callback<List<OrderItem>>() {
            @Override
            public void success(List<OrderItem> orderItems, Response response) {
                try{
                    Log.i("orders retrieved", String.valueOf(orderItems.size()) );
                    for(int i = 0; i < orderItems.size(); i++){

                        Select select = Select.from(OrderItem.class).where(Condition.prop("delivery_id").eq(orderItems.get(i).getDeliveryId()))
                                .limit("1");

                        if(select.count() > 0){
                            OrderItem ocb = (OrderItem) select.first();
                            OrderItem oc = orderItems.get(i);

                            ocb.setDeviceId(oc.getDeviceId());
                            ocb.setDeviceName(oc.getDeviceName());
                            ocb.setCourierId(oc.getCourierId());
                            ocb.setCourierName(oc.getCourierName());
                            ocb.setBuyerdeliveryzone(oc.getBuyerdeliveryzone());
                            ocb.setStatus(oc.getStatus());
                            ocb.setPickupStatus(oc.getPickupStatus());

                            ocb.save();
                            Log.i("merchant updated",oc.toString());
                        }else{
                            orderItems.get(i).save();

                            Log.i("order saved", orderItems.get(i).toString());
                        }
                    }
                    setProgressVisibility(false);

                    EventBus.getDefault().post(new OrderEvent("refresh"));

                }catch(Exception e){
                    setProgressVisibility(false);
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.no_order_found),Toast.LENGTH_SHORT).show();
                    Log.i("Order Exception",e.toString());
                    EventBus.getDefault().post(new OrderEvent("refresh"));
                }

            }

            @Override
            public void failure(RetrofitError error) {
                setProgressVisibility(false);
                Log.i("order get failure", error.toString());
                EventBus.getDefault().post(new OrderEvent("refresh"));
            }
        });

    }

    public void refreshLocations(){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(getResources().getString(R.string.api_base_url_getcity))
                .build();
        AmApiInterface amApiInterface = restAdapter.create(AmApiInterface.class);

        setProgressVisibility(true);
        setProgressIndicator("");

        amApiInterface.getCity(akey, new Callback<List<City>>() {
            @Override
            public void success(List<City> cities, Response response) {
                Log.i("REFRESH LOCATION", "GET LOCATION SUCCESS");
                for (int i = 0; i < cities.size(); i++) {
                    Log.i("jumlah Kota,", "" + cities.size());
                    Toast.makeText(MainActivity.this, ""+cities.size(), Toast.LENGTH_SHORT).show();
                    Select select = Select.from(City.class);

                    if (select.count() > 0) {
                        City loc = (City) select.first();
                        City lin = cities.get(i);

                        loc.setName(lin.getName());
                        loc.setSlug(lin.getSlug());
                        loc.setAddress(lin.getAddress());
                        loc.setCategory(lin.getCategory());
                        loc.setDescription(lin.getDescription());
                        loc.setLatitude(lin.getLatitude());
                        loc.setLongitude(lin.getLongitude());
                        loc.setPhone(lin.getPhone());
                        loc.setTags(lin.getTags());
                        loc.setVenue(lin.getVenue());
                        loc.setDeleted(lin.getDeleted());

                        loc.setPictureThumbnailUrl(lin.getPictureThumbnailUrl());
                        loc.setPictureMediumUrl(lin.getPictureMediumUrl());
                        loc.setPictureLargeUrl(lin.getPictureLargeUrl());
                        loc.setPictureFullUrl(lin.getPictureFullUrl());

                        loc.save();
                    } else {
                        cities.get(i).save();
                    }
                }
                setProgressVisibility(false);
                unsetProgressIndicator();

                EventBus.getDefault().post(new CityEvent("refresh"));
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                setProgressVisibility(false);
                unsetProgressIndicator();
                EventBus.getDefault().post(new CityEvent("refresh"));
            }
        });
    }

    public void refreshRack(String locationId){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(getResources().getString(R.string.api_base_url))
                .build();
        AmApiInterface amApiInterface = restAdapter.create(AmApiInterface.class);

        setProgressVisibility(true);

        amApiInterface.getRack( akey, locationId ,new Callback<List<Location>>() {
            @Override
            public void success(List<Location> locations, Response response) {
                for (int i = 0; i < locations.size(); i++) {

                    Select select = Select.from(Location.class).where(Condition.prop("ext_id").eq(locations.get(i).getExtId()))
                            .limit("1");

                    if (select.count() > 0) {
                        Location rob = (Location) select.first();
                        Location rin = locations.get(i);

                        rob.setItemDescription(rin.getItemDescription());
                        rob.setSKU(rin.getSKU());
                        rob.setCityId(rin.getCityId());
                        rob.setTags(rin.getTags());
                        rob.setCityName(rin.getCityName());
                        rob.setStatus(rin.getStatus());
                        rob.setDeleted(rin.getDeleted());

                        rob.setPictureMediumUrl(rin.getPictureMediumUrl());
                        rob.setPictureThumbnailUrl(rin.getPictureThumbnailUrl());
                        rob.setPictureLargeUrl(rin.getPictureLargeUrl());
                        rob.setPictureFullUrl(rin.getPictureFullUrl());

                        if( rob.getLocalEdit() == null || rob.getLocalEdit() == 0){
                            rob.setLocalEdit(0);
                            rob.setUploaded(1);
                            rob.save();
                        }

                    } else {
                        Location newrack = locations.get(i);
                        newrack.setLocalEdit(0);
                        newrack.setUploaded(1);
                        newrack.save();
                    }

                }
                setProgressVisibility(false);
                EventBus.getDefault().post(new LocationEvent("refresh"));

                EventBus.getDefault().post(new ImageEvent("sync","all","all"));

            }

            @Override
            public void failure(RetrofitError error) {
                setProgressVisibility(false);
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT ).show();
                EventBus.getDefault().post(new LocationEvent("refresh"));
            }
        });
    }

    public void refreshAsset(String rackId){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(getResources().getString(R.string.api_base_url))
                .build();
        AmApiInterface amApiInterface = restAdapter.create(AmApiInterface.class);

        Log.i("REFRESH RACK ID ",rackId);

        setProgressVisibility(true);

        amApiInterface.getAsset( akey , rackId ,new Callback<List<Asset>>() {
            @Override
            public void success(List<Asset> klikSpace, Response response) {
                for (int i = 0; i < klikSpace.size(); i++) {

                    Select select = Select.from(Asset.class)
                            .where(Condition.prop("ext_id").eq(klikSpace.get(i).getExtId()))
                            .limit("1");

                    if (select.count() > 0) {
                        Asset aso = (Asset) select.first();
                        Asset asi = klikSpace.get(i);

                        aso.setIP(asi.getIP());
                        aso.setSKU(asi.getSKU());
                        aso.setHostName(asi.getHostName());
                        aso.setTags(asi.getTags());
                        aso.setItemDescription(asi.getItemDescription());
                        aso.setAssetType(asi.getAssetType());
                        aso.setStatus(asi.getStatus());
                        aso.setLocationId(asi.getLocationId());
                        aso.setOwner(asi.getOwner());
                        aso.setRackId(asi.getRackId());
                        aso.setDeleted(asi.getDeleted());

                        aso.setVirtualStatus(asi.getVirtualStatus());
                        aso.setLabelStatus(asi.getLabelStatus());
                        aso.setPowerStatus(asi.getPowerStatus());

                        aso.setPictureMediumUrl(asi.getPictureMediumUrl());
                        aso.setPictureThumbnailUrl(asi.getPictureThumbnailUrl());
                        aso.setPictureLargeUrl(asi.getPictureLargeUrl());
                        aso.setPictureFullUrl(asi.getPictureFullUrl());

                        if(aso.getLocalEdit() == 0){
                            aso.setLocalEdit(0);
                            aso.setUploaded(1);
                            aso.save();
                        }

                    } else {
                        Asset newasset = klikSpace.get(i);
                        newasset.setLocalEdit(0);
                        newasset.setUploaded(1);
                        newasset.save();

                    }

                }

                setProgressVisibility(false);
                EventBus.getDefault().post(new VenueEvent("refresh"));

                EventBus.getDefault().post(new ImageEvent("sync","all","all"));

            }

            @Override
            public void failure(RetrofitError error) {
                setProgressVisibility(false);
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT ).show();
                EventBus.getDefault().post(new VenueEvent("refresh"));
            }
        });
    }

    public void refreshAssetType(){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.api_base_url))
                .build();
        AmApiInterface amApiInterface = restAdapter.create(AmApiInterface.class);

        setProgressVisibility(true);

        amApiInterface.getAssetType(akey, new Callback<List<DeviceType>>() {
            @Override
            public void success(List<DeviceType> deviceTypes, Response response) {
                DeviceType.deleteAll(DeviceType.class);

                for (int i = 0; i < deviceTypes.size(); i++) {
                    DeviceType deviceType = deviceTypes.get(i);
                    deviceType.save();
                }

                setProgressVisibility(false);
                EventBus.getDefault().post(new DeviceTypeEvent("refresh"));

            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT ).show();
                setProgressVisibility(false);
            }
        });
    }

    public void downsyncImages(String entityId, String entityType ){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(getResources().getString(R.string.api_base_url))
                .build();
        AmApiInterface amApiInterface = restAdapter.create(AmApiInterface.class);

        setProgressVisibility(true);

        amApiInterface.getImage(akey, entityId, entityType, new Callback<List<AssetImages>>() {
            @Override
            public void success(List<AssetImages> assetImages, Response response) {
                Log.i("IMAGE DOWNSYNC SUCCESS", response.toString());

                for(int i=0;i < assetImages.size();i++ ){

                    Select select = Select.from(AssetImages.class)
                            .where(Condition.prop("file_id").eq(assetImages.get(i).getFileId()))
                            .limit("1");

                    if (select.count() > 0) {
                        AssetImages aso = (AssetImages) select.first();
                        AssetImages asi = assetImages.get(i);

                        Log.i("FID", aso.getFileId() +" "+ String.valueOf(aso.getDeleted()) +" "+ String.valueOf(asi.getDeleted()) );

                        aso.setName(asi.getName());
                        aso.setSize(asi.getSize());
                        aso.setNs(asi.getNs());
                        aso.setType(asi.getType());
                        aso.setParentClass(asi.getParentClass());
                        aso.setFileId(asi.getFileId());
                        aso.setUrl(asi.getUrl());
                        aso.setExtUrl(asi.getUrl());
                        aso.setPictureFullUrl(asi.getPictureFullUrl());
                        aso.setPictureLargeUrl(asi.getPictureLargeUrl());
                        aso.setPictureMediumUrl(asi.getPictureMediumUrl());
                        aso.setPictureThumbnailUrl(asi.getPictureThumbnailUrl());

                        aso.setTemp_dir(asi.getTemp_dir());
                        aso.setIsDefault(asi.getIsDefault());
                        aso.setIsImage(asi.getIsImage());
                        aso.setIsAudio(asi.getIsAudio());
                        aso.setIsVideo(asi.getIsVideo());
                        aso.setIsDoc(asi.getIsDoc());
                        aso.setIsPdf(asi.getIsPdf());

                        aso.setIsLocal(0);
                        aso.setUploaded(1);
                        aso.setDeleted(asi.getDeleted());
                        aso.save();

                        Asset as =  (Asset) Select.from(Asset.class).where(Condition.prop("ext_id").eq(asi.getParentId())).first();

                        if(as != null){
                            as.setPictureFullUrl(asi.getPictureFullUrl());
                            as.setPictureLargeUrl(asi.getPictureLargeUrl());
                            as.setPictureMediumUrl(asi.getPictureMediumUrl());
                            as.setPictureThumbnailUrl(asi.getPictureThumbnailUrl());
                            as.save();
                        }

                    }else{
                        AssetImages asi = assetImages.get(i);
                        asi.setIsLocal(0);
                        asi.setUploaded(1);
                        asi.save();

                        Asset as =  (Asset) Select.from(Asset.class).where(Condition.prop("ext_id").eq(asi.getParentId())).first();

                        if(as != null){
                            as.setPictureFullUrl(asi.getPictureFullUrl());
                            as.setPictureLargeUrl(asi.getPictureLargeUrl());
                            as.setPictureMediumUrl(asi.getPictureMediumUrl());
                            as.setPictureThumbnailUrl(asi.getPictureThumbnailUrl());
                            as.save();
                        }
                    }


                }

                setProgressVisibility(false);
                EventBus.getDefault().postSticky(new ImageEvent("refreshImageView"));


            }

            @Override
            public void failure(RetrofitError error) {

                setProgressVisibility(false);
                EventBus.getDefault().postSticky(new ImageEvent("refreshImageView"));
            }
        });

        EventBus.getDefault().postSticky(new ImageEvent("refreshImageView"));

    }

    public void uploadImage(final String file_id){

        Select select = Select.from(AssetImages.class).where(Condition.prop("file_id").eq(file_id),Condition.prop("uploaded").eq(0));

        if(select.count() > 0){

            String total = String.valueOf(select.count());

            AssetImages aim = (AssetImages) select.first();

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setLogLevel(RestAdapter.LogLevel.HEADERS)
                    .setEndpoint(getResources().getString(R.string.api_base_url))
                    .build();

            AmApiInterface amApiInterface = restAdapter.create(AmApiInterface.class);

            setProgressVisibility(true);
            setProgressVisibility(true);
            setProgressIndicator(getString(R.string.uploading_images) + " 1 of " + total );

            //final file_id = aim.getFileId();

            Log.i("FILE ID", file_id);

            TypedFile imagefile = new TypedFile("image/jpg", new File( aim.getUri() ) );

            amApiInterface.uploadImage(
                akey,
                aim.getNs(),
                aim.getParentId(),
                aim.getParentClass(),
                file_id,
                aim.getExtId(),
                imagefile, new Callback<ResultObject>() {
                    @Override
                    public void success(ResultObject resultObject, Response response) {
                        Log.i("MESSAGE", resultObject.getMessage());
                        Log.i("RESULT", resultObject.getStatus());
                        if ("OK".equalsIgnoreCase(resultObject.getStatus())) {
                            AssetImages im = Select.from(AssetImages.class).where(Condition.prop("file_id").eq(file_id)).first();

                            Log.i("IMAGE ASSET", im.getExtId() + " : " + resultObject.getMessage());

                            im.setUploaded(1);
                            im.save();
                        }
                        setProgressVisibility(false);
                        unsetProgressIndicator();
                        EventBus.getDefault().postSticky(new ImageEvent("refreshImageView"));
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.i("UPLOAD ERROR", error.toString());
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        setProgressVisibility(false);
                        unsetProgressIndicator();
                        EventBus.getDefault().postSticky(new ImageEvent("refreshImageView"));
                    }
                });

            EventBus.getDefault().postSticky(new ImageEvent("refreshImageView"));

        }else{
            Toast.makeText(MainActivity.this, "Image already uploaded", Toast.LENGTH_SHORT).show();
        }

    }

    public void uploadImages(String parent_id, String parent_class){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.HEADERS)
                .setEndpoint(getResources().getString(R.string.api_base_url))
                .build();

        AmApiInterface amApiInterface = restAdapter.create(AmApiInterface.class);

        setProgressVisibility(true);

        List<AssetImages> images;

        if("all".equalsIgnoreCase(parent_id)){
            images = Select.from(AssetImages.class).where(Condition.prop("uploaded").eq(0)).list();
        }else{
            images = Select.from(AssetImages.class).where(
                    Condition.prop("parent_class").eq(parent_class),
                    Condition.prop("parent_id").eq(parent_id),
                    Condition.prop("is_local").eq(1),
                    Condition.prop("uploaded").eq(0)).list();
        }

        Log.i("IMAGE COUNT UPLOAD", String.valueOf(images.size()) );

        String total = String.valueOf(images.size());

        for(int i = 0; i < images.size();i++){
            AssetImages aim = images.get(i);

            Uri furi = Uri.parse(aim.getUri());

            Log.i("UPLOADED IMAGE URI", aim.getUri());

            TypedFile imagefile = new TypedFile("image/jpg", new File( aim.getUri() ) );

            Log.i("UPLOADED IMAGE STRING", imagefile.toString());

            setProgressVisibility(true);
            setProgressIndicator(getString(R.string.uploading_images) + " " + i + " of " + total );

            final String file_id = aim.getFileId();

            Log.i("FILE ID", file_id);

            amApiInterface.uploadImage(akey, aim.getNs(),
                    aim.getParentId(),
                    aim.getParentClass(),
                    file_id,
                    aim.getExtId(),
                    imagefile, new Callback<ResultObject>() {
                @Override
                public void success(ResultObject resultObject, Response response) {
                    Log.i("MESSAGE", resultObject.getMessage());
                    Log.i("RESULT", resultObject.getStatus());
                    if("OK".equalsIgnoreCase(resultObject.getStatus())){
                        AssetImages im = Select.from(AssetImages.class).where(Condition.prop("file_id").eq(file_id)).first();

                        Log.i("IMAGE ASSET", im.getExtId() + " : " + resultObject.getMessage() );

                        im.setUploaded(1);
                        im.save();
                    }
                    setProgressVisibility(false);
                    unsetProgressIndicator();
                    EventBus.getDefault().postSticky(new ImageEvent("refreshImageView"));
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.i("UPLOAD ERROR", error.toString());
                    Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    setProgressVisibility(false);
                    unsetProgressIndicator();
                    EventBus.getDefault().postSticky(new ImageEvent("refreshImageView"));
                }
            });

            EventBus.getDefault().postSticky(new ImageEvent("refreshImageView"));

        }

        unsetProgressIndicator();

    }


    public void syncRack(Location location){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.api_base_url))
                .build();

        AmApiInterface amApiInterface = restAdapter.create(AmApiInterface.class);

        setProgressVisibility(true);

        amApiInterface.sendRack(akey, location, new Callback<ResultObject>() {
            @Override
            public void success(ResultObject resultObject, Response response) {
                Log.i("SYNC RESULT", resultObject.getStatus());

                if("OK".equalsIgnoreCase(resultObject.getStatus()) ){
                    if("".equalsIgnoreCase(resultObject.getMessage()) == false){
                        Location as = (Location) Select.from(Location.class).where( Condition.prop("ext_id").eq(resultObject.getMessage()) ).first();
                        as.setLocalEdit(0);
                        as.setUploaded(1);
                        as.save();
                    }
                }

                setProgressVisibility(false);

            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("SYNC ERR", error.toString() );
                setProgressVisibility(false);

            }
        });

    }

    public void upsyncRack(Location location){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setEndpoint(getResources().getString(R.string.api_base_url))
                .build();

        AmApiInterface amApiInterface = restAdapter.create(AmApiInterface.class);

        setProgressVisibility(true);

        amApiInterface.updateRack(akey, location.getExtId() , location, new Callback<ResultObject>() {
            @Override
            public void success(ResultObject resultObject, Response response) {

                if("OK".equalsIgnoreCase(resultObject.getStatus()) ){
                    if("".equalsIgnoreCase(resultObject.getMessage())){

                    }else{
                        Location as = (Location) Select.from(Location.class).where( Condition.prop("ext_id").eq(resultObject.getMessage()) ).first();
                        as.setLocalEdit(0);
                        as.setUploaded(1);
                        as.save();

                        //EventBus.getDefault().post(new ImageEvent("upsync",as.getExtId(), "asset"));
                        Log.i("UPSYNC SUCCESS", resultObject.getMessage());
                    }
                }

                Log.i("UPSYNC RESULT", resultObject.getStatus());
                setProgressVisibility(false);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("UPSYNC ERR", error.toString() );
                setProgressVisibility(false);
            }
        });

    }


    public void upsyncBatchRack(){

        List<Location> locations = Select.from(Location.class).where( Condition.prop("local_edit").eq(0)).list();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(getResources().getString(R.string.api_base_url))
                .build();

        AmApiInterface amApiInterface = restAdapter.create(AmApiInterface.class);

        setProgressVisibility(true);

        amApiInterface.updateRackBatch(akey, 1, locations, new Callback<ResultObject>() {
            @Override
            public void success(ResultObject resultObject, Response response) {
                Log.i("ASSET BATCH RESP", response.toString());
                setProgressVisibility(false);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("ASSET BATCH ERR", error.toString());
                setProgressVisibility(false);

            }
        } );

    }


    public void syncAsset(Asset asset){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.api_base_url))
                .build();

        AmApiInterface amApiInterface = restAdapter.create(AmApiInterface.class);

        setProgressVisibility(true);

        amApiInterface.sendAsset(akey, asset, new Callback<ResultObject>() {
            @Override
            public void success(ResultObject resultObject, Response response) {
                Log.i("SYNC RESULT", resultObject.getStatus());

                if("OK".equalsIgnoreCase(resultObject.getStatus()) ){
                    if("".equalsIgnoreCase(resultObject.getMessage()) == false){
                        Asset as = (Asset) Select.from(Asset.class).where( Condition.prop("ext_id").eq(resultObject.getMessage()) ).first();
                        as.setLocalEdit(0);
                        as.setUploaded(1);
                        as.save();
                    }
                }

                setProgressVisibility(false);

            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("SYNC ERR", error.toString() );
                setProgressVisibility(false);

            }
        });

    }

    public void upsyncAsset(Asset asset){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setEndpoint(getResources().getString(R.string.api_base_url))
                .build();

        AmApiInterface amApiInterface = restAdapter.create(AmApiInterface.class);

        setProgressVisibility(true);

        amApiInterface.updateAsset( akey, asset.getExtId() ,asset, new Callback<ResultObject>() {
            @Override
            public void success(ResultObject resultObject, Response response) {

                if("OK".equalsIgnoreCase(resultObject.getStatus()) ){
                    if("".equalsIgnoreCase(resultObject.getMessage())){

                    }else{
                        Asset as = (Asset) Select.from(Asset.class).where( Condition.prop("ext_id").eq(resultObject.getMessage()) ).first();
                        as.setLocalEdit(0);
                        as.setUploaded(1);
                        as.save();

                        //EventBus.getDefault().post(new ImageEvent("upsync",as.getExtId(), "asset"));
                        Log.i("UPSYNC SUCCESS", resultObject.getMessage());
                    }
                }

                Log.i("UPSYNC RESULT", resultObject.getStatus());
                setProgressVisibility(false);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("UPSYNC ERR", error.toString() );
                setProgressVisibility(false);
            }
        });

    }

    public void syncScanlog(){
        List<ScanLog> logs = Select.from(ScanLog.class).where( Condition.prop("uploaded").eq(0) ).list();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(getResources().getString(R.string.api_base_url))
                .build();

        AmApiInterface amApiInterface = restAdapter.create(AmApiInterface.class);

        setProgressVisibility(true);

        amApiInterface.sendScanlogBatch(akey, 1, logs, new Callback<List<ResultObject>>() {
            @Override
            public void success(List<ResultObject> resultObjects, Response response) {
                for(int i = 0; i < resultObjects.size();i++){
                    ResultObject rs = resultObjects.get(i);

                    Select s = Select.from(ScanLog.class).where(Condition.prop("log_id").eq(rs.getMessage()));

                    if(s.count() > 0){
                        ScanLog sc = (ScanLog) s.first();
                        if( "OK".equalsIgnoreCase(rs.getStatus()) ){
                            sc.setUploaded(1);
                            sc.save();
                        }
                    }

                }
                setProgressVisibility(false);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("ASSET BATCH ERR", error.toString());
                setProgressVisibility(false);
            }
        } );

    }

    public void upsyncBatchAsset(){

        List<Asset> klikSpace = Select.from(Asset.class).where( Condition.prop("local_edit").eq(0)).list();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(getResources().getString(R.string.api_base_url))
                .build();

        AmApiInterface amApiInterface = restAdapter.create(AmApiInterface.class);

        setProgressVisibility(true);

        amApiInterface.updateAssetBatch(akey, 1, klikSpace, new Callback<ResultObject>() {
            @Override
            public void success(ResultObject resultObject, Response response) {
                Log.i("ASSET BATCH RESP", response.toString());
                setProgressVisibility(false);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("ASSET BATCH ERR", error.toString());
                setProgressVisibility(false);

            }
        } );

    }

    public void updateStatus(String deliveryId){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.jwh_api_base_url))
                .build();
        JwhApiInterface jwhApiInterface = restAdapter.create(JwhApiInterface.class);

        Log.i("order delivery id",deliveryId);

        setProgressVisibility(true);

        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        jwhApiInterface.setStatus(getResources().getString(R.string.wh_status_accept_wh), deliveryId, new Callback<ResultObject>() {
                    @Override
                    public void success(ResultObject resultObject, Response response) {
                        Log.i("update status result", resultObject.getStatus());
                        setProgressVisibility(false);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.i("update status error",error.toString());
                        setProgressVisibility(false);
                    }
                }
            );

        Select select = Select.from(OrderItem.class).where(Condition.prop("delivery_id").eq(deliveryId))
                .limit("1");
        if(select.count() > 0){
            OrderItem oi = (OrderItem) select.first();
            oi.setWarehouseStatus(getResources().getString(R.string.wh_status_long_accept_wh));
            oi.save();
            EventBus.getDefault().post(new OrderEvent("refresh",oi.getMerchantId()));
        }
    }

    private void setProgressVisibility(Boolean v){
        if(v == true){
            mProgressBar.setVisibility(View.VISIBLE);
        }else{
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void setProgressIndicator(String message){
        String defaultmessage = getResources().getString(R.string.loading_default);
        if("".equalsIgnoreCase(message) ){
            mProgressIndicator.setText(defaultmessage);
            mProgressIndicator.setVisibility(View.VISIBLE);
        }else{
            mProgressIndicator.setText(message);
            mProgressIndicator.setVisibility(View.VISIBLE);
        }
    }

    private void unsetProgressIndicator(){
        String defaultmessage = getResources().getString(R.string.loading_default);
        if("".equalsIgnoreCase(defaultmessage) ){
            mProgressIndicator.setText(defaultmessage);
            mProgressIndicator.setVisibility(View.GONE);
        }else{
            mProgressIndicator.setVisibility(View.GONE);
        }
    }

    public static void extractDb(File sourceFile, File destFile) {

        FileChannel source = null;
        FileChannel destination = null;

        try {
            if (!destFile.exists()) {
                destFile.createNewFile();
            }

            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (source != null) {
                    source.close();
                }
                if (destination != null) {
                    destination.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void doScan(String id, String mode){

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, ScannerFragment.newInstance(id, mode), "scan_fragment")
                .addToBackStack("scan_fragment")
                .setBreadCrumbTitle(getResources().getString(R.string.action_scan))
                .commit();
        getSupportActionBar().setTitle(getResources().getString(R.string.action_scan));
    }


    private void showLogin(){
        Intent intent;
        intent = new Intent(this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        //finish();
    }

    private void doSignOut(){
        SharedPreferences.Editor editor = spref.edit();
        editor.putString("auth","");
        editor.putString("uid","");
        editor.commit();
        showLogin();
    }

}

package com.kickstartlab.android.klikSpace.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.kickstartlab.android.klikSpace.R;
import com.kickstartlab.android.klikSpace.events.VenueEvent;
import com.kickstartlab.android.klikSpace.events.ImageEvent;
import com.kickstartlab.android.klikSpace.rest.models.Asset;
import com.kickstartlab.android.klikSpace.rest.models.AssetImages;
import com.kickstartlab.android.klikSpace.rest.models.DeviceType;
import com.kickstartlab.android.klikSpace.ui.LabeledSwitchView;
import com.kickstartlab.android.klikSpace.ui.SquareImageView;
import com.kickstartlab.android.klikSpace.utils.DbDateUtil;
import com.kickstartlab.android.klikSpace.utils.RandomStringGenerator;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.greenrobot.event.EventBus;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import nl.changer.polypicker.ImagePickerActivity;

public class EditVenueActivity extends ActionBarActivity {

    Toolbar mToolbar;
    SmoothProgressBar mProgressBar;
    TextView mProgressIndicator;

    private static final int INTENT_REQUEST_GET_CODE = 4554;
    private int INTENT_REQUEST_GET_IMAGES = 1667;

    MaterialEditText sku,desc,ip,host,os,pic,pic_email,pic_phone,contract,owner;

    Spinner asset_type;

    LabeledSwitchView powerStatus, virtualStatus, labelStatus;

    String ext_id;

    Asset asset;

    LinearLayout image_container;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_venue);

        context = this;

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mProgressBar = (SmoothProgressBar) findViewById(R.id.loadProgressBar);
        mProgressIndicator = (TextView) findViewById(R.id.progressMessage);

        mProgressBar.setVisibility(View.GONE);
        mProgressIndicator.setVisibility(View.GONE);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        getSupportActionBar().setHomeButtonEnabled(true);

        Bundle extra = getIntent().getExtras();

        if(extra.getString("ext_id") == null){
            finish();
        }else{
            ext_id = extra.getString("ext_id");

            Select select = Select.from(Asset.class).where(Condition.prop("ext_id").eq(ext_id))
                    .limit("1");

            Log.i("EDIT ASSET COUNT", String.valueOf(select.count()));


            if (select.count() > 0) {
                asset = (Asset) select.first();
                Log.i("EDIT ASSET SKU", asset.getSKU() ) ;

                this.getSupportActionBar().setTitle("Edit " + asset.getSKU());

                this.getSupportActionBar().setHomeButtonEnabled(true);

                LinearLayout detail_container = (LinearLayout) findViewById(R.id.detail_container);

                image_container = (LinearLayout) findViewById(R.id.image_container);

                sku = makeEditText(this,"Serial Number / Asset Code");
                desc = makeEditText(this,"Description");
                ip = makeEditText(this, "IP Address");
                host = makeEditText(this, "Host");
                os = makeEditText(this, "OS");
                pic = makeEditText(this, "PIC");
                pic_email = makeEditText(this, "PIC Email");
                pic_phone = makeEditText(this, "PIC Phone");
                contract = makeEditText(this, "Contract Number");
                //asset_type = makeEditText(this,"Asset Type");
                owner = makeEditText(this,"Owner");
                //MaterialEditText type = makeEditText(this,"Type");

                sku.setText(asset.getSKU());
                desc.setText(asset.getItemDescription());
                ip.setText(asset.getIP());
                host.setText(asset.getHostName());
                os.setText(asset.getOS());
                pic.setText(asset.getPIC());
                pic_email.setText(asset.getPicEmail());
                pic_phone.setText(asset.getPicPhone());
                contract.setText(asset.getContractNumber());
                //asset_type.setText(asset.getAssetType());
                owner.setText(asset.getOwner());
                //type.setText(asset.getType());

                asset_type = new Spinner(this);


                List<DeviceType> types = Select.from(DeviceType.class)
                        .orderBy("type collate nocase").list();

                TextView asset_type_label = new TextView(this);
                asset_type_label.setTextColor(R.color.primary_dark);
                asset_type_label.setTextSize(11);
                asset_type_label.setText("Asset Type");

                ArrayAdapter<DeviceType> typeAdapter = new ArrayAdapter<DeviceType>(this,android.R.layout.simple_spinner_dropdown_item, types );

                asset_type.setAdapter(typeAdapter);

                if(!asset.getAssetType().equals(null)) {

                    int spinnerPosition = 0;

                    for(int p = 0; p < typeAdapter.getCount();p++){
                        if(typeAdapter.getItem(p).toString().equalsIgnoreCase(asset.getAssetType())){
                            spinnerPosition = p;
                        }
                    }

                    asset_type.setSelection(spinnerPosition);
                    spinnerPosition = 0;
                }

                String yes = getResources().getString(R.string.label_yes);
                String no = getResources().getString(R.string.label_no);

                powerStatus = makeSwitch(this, getResources().getString(R.string.power_status) , 0, yes, no );
                labelStatus = makeSwitch(this, getResources().getString(R.string.label_status), 0, yes, no);
                virtualStatus = makeSwitch(this, getResources().getString(R.string.virtual_status), 0, yes, no);

                if(asset.getPowerStatus() == null || asset.getPowerStatus() == 0){
                    powerStatus.setChecked(false);
                }else{
                    powerStatus.setChecked(true);
                }

                if(asset.getLabelStatus() == null || asset.getLabelStatus() == 0){
                    labelStatus.setChecked(false);
                }else{
                    labelStatus.setChecked(true);
                }

                if(asset.getVirtualStatus() == null || asset.getVirtualStatus() == 0){
                    virtualStatus.setChecked(false);
                }else{
                    virtualStatus.setChecked(true);
                }

                detail_container.addView(sku);
                detail_container.addView(ip);
                detail_container.addView(host);
                detail_container.addView(os);
                detail_container.addView(contract);
                detail_container.addView(owner);
                detail_container.addView(pic);
                detail_container.addView(pic_email);
                detail_container.addView(pic_phone);

                detail_container.addView(asset_type_label);
                detail_container.addView(asset_type);

                detail_container.addView(powerStatus);
                detail_container.addView(labelStatus);
                detail_container.addView(virtualStatus);

                //detail_container.addView(type);
                detail_container.addView(desc);

                FloatingActionButton fabSave = (FloatingActionButton) findViewById(R.id.fab_save_item);

                fabSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveAsset();
                        Log.i("ASSET EXT ID",asset.getExtId());
                        /*
                        String lastUpdate = DbDateUtil.getDateTime();

                        asset.setLastUpdate(lastUpdate);
                        asset.setSKU(sku.getText().toString());
                        asset.setIP(ip.getText().toString());
                        asset.setHostName(host.getText().toString());
                        asset.setOS(os.getText().toString());
                        asset.setContractNumber(contract.getText().toString());
                        asset.setOwner(owner.getText().toString());
                        asset.setPIC(pic.getText().toString());
                        asset.setPicEmail(pic_email.getText().toString());
                        asset.setPicPhone(pic_phone.getText().toString());
                        asset.setAssetType(asset_type.getSelectedItem().toString());
                        asset.setItemDescription(desc.getText().toString());

                        asset.setLocalEdit(1);
                        asset.setUploaded(0);

                        asset.save();
                        Log.i("asset",asset.getIP());
                        EventBus.getDefault().post(new VenueEvent("refreshDetail",asset));
                        finish();
                        */
                    }
                });

                FloatingActionButton fabScanSerial = (FloatingActionButton) findViewById(R.id.fab_add_serial);

                fabScanSerial.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ScannerActivity.class);
                        startActivityForResult(intent, INTENT_REQUEST_GET_CODE);
                    }
                });

                FloatingActionButton fabAddImage = (FloatingActionButton) findViewById(R.id.fab_add_image);
                fabAddImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ImagePickerActivity.class);
                        intent.putExtra(ImagePickerActivity.EXTRA_SELECTION_LIMIT, 3);  // allow only upto 3 images to be selected.
                        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
                    }
                });

                refreshImage();

            }
        }

    }

    public void saveAsset(){
        Log.i("ASSET EXT ID",asset.getExtId());

        String lastUpdate = DbDateUtil.getDateTime();

        asset.setLastUpdate(lastUpdate);
        asset.setSKU(sku.getText().toString());
        asset.setIP(ip.getText().toString());
        asset.setHostName(host.getText().toString());
        asset.setOS(os.getText().toString());
        asset.setContractNumber(contract.getText().toString());
        asset.setOwner(owner.getText().toString());
        asset.setPIC(pic.getText().toString());
        asset.setPicEmail(pic_email.getText().toString());
        asset.setPicPhone(pic_phone.getText().toString());
        asset.setAssetType(asset_type.getSelectedItem().toString());
        asset.setItemDescription(desc.getText().toString());

        asset.setPowerStatus( (powerStatus.isChecked())?1:0 );
        asset.setLabelStatus( (labelStatus.isChecked())?1:0 );
        asset.setVirtualStatus( (virtualStatus.isChecked())?1:0 );

        asset.setLocalEdit(1);
        asset.setUploaded(0);

        asset.save();
        Log.i("asset", asset.getIP());
//        EventBus.getDefault().post(new VenueEvent("upsyncAsset",asset));
//        EventBus.getDefault().postSticky(new VenueEvent("refreshDetailView", asset));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_asset, menu);
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

        if(id == R.id.action_save_asset){
            saveAsset();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode == Activity.RESULT_OK ){
            if(requestCode == INTENT_REQUEST_GET_CODE){
                Bundle bundle = data.getExtras();
                sku.setText( bundle.getString("resultText"));
            }

            if(requestCode == INTENT_REQUEST_GET_IMAGES){
                Parcelable[] parcelableUris = data.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

                if(parcelableUris == null) {
                    return;
                }

                Uri[] uris = new Uri[parcelableUris.length];
                System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);

                if(uris != null) {
                    for(Uri uri : uris){
                        Select select = Select.from(AssetImages.class)
                                .where(Condition.prop("uri").eq(uri.toString()))
                                .limit("1");

                        if(select.count() > 0){

                        }else{
                            AssetImages aim = new AssetImages();

                            String file_id = RandomStringGenerator.generateRandomString(15, RandomStringGenerator.Mode.HEX).toLowerCase();
                            String upload_id = RandomStringGenerator.generateRandomString(24, RandomStringGenerator.Mode.HEX).toLowerCase();

                            aim.setNs("assetpic");
                            aim.setParentClass("asset");
                            aim.setParentId(ext_id);
                            aim.setFileId(file_id);
                            aim.setExtId(upload_id);
                            aim.setUri(uri.toString());
                            aim.setIsLocal(1);
                            aim.setUploaded(0);
                            aim.setDeleted(0);
                            aim.save();

                        }

                    }

                    //EventBus.getDefault().post(new VenueEvent("refreshImage"));
                    refreshImage();

                }
            }

        }
    }

    public void onEvent(ImageEvent im){

        if( im.getAction() == "refreshImageView" ){
            refreshImage();
        }

    }

    public void refreshImage(){
        image_container.removeAllViews();

        Log.i("IMAGE","REFRESH");

        //Select select = Select.from(AssetImages.class).where(Condition.prop("ext_id").eq(ext_id));

        Select select = Select.from(AssetImages.class).where(Condition.prop("parent_id").eq(ext_id),
                Condition.prop("parent_class").eq("asset"),
                Condition.prop("deleted").eq(0));

        Log.i( "IMG", String.valueOf(select.count()) );

        if(select.count() > 0){

            List<AssetImages> aim = select.list();
            for(int im = 0; im < select.count();im++ ){
                AssetImages am = aim.get(im);
                SquareImageView defpic = makeImage(am);
                image_container.addView(defpic);

                if( am.getUrl() == null || "".equalsIgnoreCase(am.getUrl()) ){
                    Picasso.with(context)
                            .load("file://" + am.getUri())
                            .fit()
                            .centerCrop()
                            .into(defpic);

                }else{
                    if( "".equalsIgnoreCase( am.getUrl() ) == false && am.getUrl() != null ) {
                        Picasso.with(context)
                                .load(am.getUrl())
                                .fit()
                                .centerCrop()
                                .into(defpic);
                    }
                }
            }
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


    public MaterialEditText makeEditText(Context context,String label){
        MaterialEditText editText = new MaterialEditText(context);
        editText.setFloatingLabelText(label);
        editText.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
        return editText;
    }

    public LabeledSwitchView makeSwitch(Context context, String label, int checked, String positive, String negative){
        LabeledSwitchView sw = new LabeledSwitchView(context);
        sw.setLabel(label);
        sw.setPositive(positive);
        sw.setNegative(negative);
        sw.setChecked( (checked == 1)?true:false );
        return sw;
    }

    public SquareImageView makeImage(AssetImages am){

        SquareImageView defpic = new SquareImageView(context);
        defpic.setPadding(0,8,0,8);


        if(am.getUploaded() == 0){
            defpic.setAlpha(0.75f);

            final String file_id = am.getFileId();

            defpic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("IMAGE CLICK", file_id);
                    EventBus.getDefault().post(new ImageEvent("upload", file_id , "asset"));
                }
            });
        }

        return defpic;
    }

}

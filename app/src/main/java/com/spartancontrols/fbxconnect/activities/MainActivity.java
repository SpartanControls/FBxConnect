package com.spartancontrols.fbxconnect.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.automatak.dnp3.Channel;
import com.automatak.dnp3.ChannelRetry;
import com.automatak.dnp3.DNP3Manager;
import com.automatak.dnp3.Header;
import com.automatak.dnp3.LogMasks;
import com.automatak.dnp3.Master;
import com.automatak.dnp3.MasterStackConfig;
import com.automatak.dnp3.Outstation;
import com.automatak.dnp3.enums.QualifierCode;
import com.automatak.dnp3.impl.DNP3ManagerFactory;
import com.automatak.dnp3.mock.DefaultMasterApplication;
import com.automatak.dnp3.mock.PrintingChannelListener;
import com.automatak.dnp3.mock.PrintingLogHandler;
import com.automatak.dnp3.mock.PrintingSOEHandler;
import com.spartancontrols.fbxconnect.Globals;
import com.spartancontrols.fbxconnect.R;

import java.time.Duration;

public class MainActivity extends AppCompatActivity {

    static {System.loadLibrary("opendnp3java");}

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    private DNP3Manager manager;

    private Master master;

    private Outstation outstation;

    public MainActivity() {
        connectToMaster();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyStoragePermissions(this);

        System.out.println("HELLOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void goToEFMActivity(View view) {
        Intent intent = new Intent(this, EFMActivity.class);
        startActivity(intent);
    }

    public void goToGasActivity(View view) {
        Intent intent = new Intent(this, GasActivity.class);
        startActivity(intent);
    }

    public void goToPlateActivity(View view) {
        Intent intent = new Intent(this, PlateActivity.class);
        startActivity(intent);
    }

    /**
     * Connect the phone to the FB unit using DNP3
     */
    public void connectToMaster(){
        manager=null;
        try {
            manager = DNP3ManagerFactory.createManager(1, PrintingLogHandler.getInstance());
            System.out.println("DNP3 Starting");
            runMaster(manager);
            System.out.println("DNP3 Connection Made");
        }
        catch(Exception ex)
        {
            System.out.println("Exception: " + ex.getMessage());
        }
//        private Header(byte group, byte variation, QualifierCode qualifier, int count, int start, int stop)

//        byte group = 0x01;
//        byte variation = 3;
//        QualifierCode qualifier = QualifierCode.fromType(0x1);
//        int count = 0;
//        int start = 0;
//        int stop = 0;
//        short startIndex = 0;
//        short stopIndex = 10;
//
//        short x = 40;
//
//        ControlRelayOutputBlock crob = new ControlRelayOutputBlock(ControlCode.LATCH_ON, (short) 10, 1, 1000, CommandStatus.SUCCESS);
//        master.selectAndOperateCROB(crob, 0).thenAccept(
//                //asynchronously print the result of the command operation
//                (CommandTaskResult result) -> System.out.println("DNP3 CROB: "+result)
//        );
//
//        System.out.println("CROB: "+crob.status);
//        Header header = Header.Range16(group,variation,startIndex,stopIndex);
//        AnalogOutputInt16 aod = new AnalogOutputInt16(x, CommandStatus.SUCCESS);
//        master.selectAndOperateAOInt16(aod, 0).thenAccept(
//                (CommandTaskResult result) -> System.out.println("DNP3 Analog: "+result)
//        );
//        byte quality = 00000001;
//        AnalogInput input = new AnalogInput(1,quality,1);
//        master.scan(header);
//        System.out.println("HEADER SCANNED");
//
//
//
//        int i = 0;
//        while(i<10000){
//            OutstationChangeSet set = new OutstationChangeSet();
//            set.update(new Counter(i,(byte) 0x01, 0), 0);
//            outstation.apply(set);
//            i++;
//            System.out.print(i);
//        }
    }

    private void runMaster(DNP3Manager manager) throws Exception {
        // Create a tcp channel class that will connect to the loopback
        Channel channel = manager.addTCPClient(
                "client",
                LogMasks.NORMAL | LogMasks.APP_COMMS,
                ChannelRetry.getDefault(),
                "192.168.1.10",
                "0.0.0.0",
                20000,
                PrintingChannelListener.getInstance()
        );

        // You can modify the defaults to change the way the master behaves
        MasterStackConfig config = new MasterStackConfig();

        // Create a master instance, pass in a simple singleton to print received values to the console
        master = channel.addMaster("master", PrintingSOEHandler.getInstance(), DefaultMasterApplication.getInstance(), config);

        //Master master = channel.addMaster(master.getId(), soeHandler, DefaultMasterApplication.getInstance(), stackConfig);

        // do an integrity scan once per minute
        master.addPeriodicScan(Duration.ofSeconds(2), Header.getIntegrity());

        // Turn it on
        master.enable();

        master.scan(Header.getEventClasses());


        // Store the new connection in the global class
        // SO thy can be accessed from any other activity or class
        Globals g = (Globals)getApplication();
        g.setDNP3Manager(manager);
        g.setMaster(master);
    }

    public DNP3Manager getManager(){
        return manager;
    }

    public Master getMaster(){
        return master;
    }
}

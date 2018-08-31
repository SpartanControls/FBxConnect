package com.spartancontrols.fbxconnect;

import com.automatak.dnp3.*;
import com.automatak.dnp3.impl.DNP3ManagerFactory;
import com.automatak.dnp3.mock.DefaultMasterApplication;
import com.automatak.dnp3.mock.PrintingChannelListener;
import com.automatak.dnp3.mock.PrintingLogHandler;
import com.automatak.dnp3.mock.PrintingSOEHandler;

import java.time.Duration;

public class Connect {

    public Connect (){}

    /**
     * Call this method to create a DNP3Manager
     * Connects to the FB1100/FB12000
     * @return DNP3Manager
     */
    public DNP3Manager connectToMaster(){
        DNP3Manager manager=null;
        try {
            manager = DNP3ManagerFactory.createManager(1, PrintingLogHandler.getInstance());
            System.out.println("RUNNING THE DNP3");
            manager = run(manager);
            System.out.println("STOPPED THE DNP3");
        }
        catch(Exception ex)
        {
            System.out.println("Exception: " + ex.getMessage());
        }
        finally {
            //manager.shutdown();
            //System.out.println("SHUTDOWN THE DNP3");
        }
        return manager;
    }

    private static DNP3Manager run(DNP3Manager manager) throws Exception {
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
        Master master = channel.addMaster("master", PrintingSOEHandler.getInstance(), DefaultMasterApplication.getInstance(), config);

        // do an integrity scan once per minute
        master.addPeriodicScan(Duration.ofSeconds(2), Header.getIntegrity());

        // Turn it on
        master.enable();

        System.out.println("Before Scan");
        master.scan(Header.getEventClasses());
        System.out.println("After Scan");
        
        // Return the connected manager
        return manager;
    }
}

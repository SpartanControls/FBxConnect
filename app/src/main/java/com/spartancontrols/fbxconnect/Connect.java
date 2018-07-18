package com.spartancontrols.fbxconnect;

import com.automatak.dnp3.*;
import com.automatak.dnp3.enums.CommandStatus;
import com.automatak.dnp3.enums.ControlCode;
import com.automatak.dnp3.impl.DNP3ManagerFactory;
import com.automatak.dnp3.mock.DefaultMasterApplication;
import com.automatak.dnp3.mock.PrintingChannelListener;
import com.automatak.dnp3.mock.PrintingLogHandler;
import com.automatak.dnp3.mock.PrintingSOEHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;

/**
 * Example master than can be run against the example outstation
 */
public class Connect {

    public Connect () throws Exception {

        // create the root class with a thread pool size of 1
        DNP3Manager manager = DNP3ManagerFactory.createManager(1, PrintingLogHandler.getInstance());

        try {
            System.out.println("RUNNING THE DNP3");
            run(manager);
            System.out.println("STOPPED THE DNP3");
        }
        catch(Exception ex)
        {
            System.out.println("Exception: " + ex.getMessage());
        }
        finally {
            // This call is needed b/c the thread-pool will stop the application from exiting
            // and the finalizer isn't guaranteed to run b/c the GC might not be collected during main() exit
            manager.shutdown();
        }
    }

    static void run(DNP3Manager manager) throws Exception
    {
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

        master.enable();

        System.out.println("DNP3 IS SWAGGGGGGGGGGGG");

        // all this cruft just to read a line of text in Java. Oh the humanity.
        InputStreamReader converter = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(converter);
        String line = in.readLine();
        System.out.println("GENERAL KENOBI, YOU ARE A BOLD ONE"+line);
//
//        while (true) {
//            System.out.println("Enter something to issue a command or type <quit> to exit");
//            String line = in.readLine();
//            switch(line)
//            {
//                case("quit"):
//                    return;
//                case("crob"):
//                    ControlRelayOutputBlock crob = new ControlRelayOutputBlock(ControlCode.LATCH_ON, (short) 1, 100, 100, CommandStatus.SUCCESS);
//                    master.selectAndOperateCROB(crob, 0).thenAccept(
//                            //asynchronously print the result of the command operation
//                            (CommandTaskResult result) -> System.out.println(result)
//                    );
//                    break;
//                case("scan"):
//                    master.scan(Header.getEventClasses());
//                    break;
//                default:
//                    System.out.println("Unknown command: " + line);
//                    break;
//            }
//        }
    }

}

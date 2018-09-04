package com.spartancontrols.fbxconnect;

import android.app.Application;

import com.automatak.dnp3.DNP3Manager;
import com.automatak.dnp3.Master;

/**
 * Class used to store the Master and DNP3Manager
 * Other global variables can be added if needed
 */
public class Globals extends Application {

    private DNP3Manager manager;

    private Master master;

    /**
     * SETTERS
     * How to set from another class
     *      Globals g = (Globals)getApplication();
     *      g.setDNP3Manager(manager);
     *      g.setMaster(master);
     */

    public void setDNP3Manager(DNP3Manager manager){
        this.manager = manager;
    }

    public void setMaster(Master master){
        this.master = master;
    }

    /**
     * GETTERS
     * How to get any Global variable in another class
     *      Globals g = (Globals)getApplication();
     *      Master master = g.getMaster();
     *      DNP3Manager manager  = g.getDNP3Manager();
     */

    public DNP3Manager getDNP3Manager(){
        return manager;
    }

    public Master getMaster(){
        return master;
    }
}

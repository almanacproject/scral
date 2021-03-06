package it.ismb.pertlab.pwal.api.data.pusher;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.internal.Pwal;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DataPusher extends TimerTask
{

    protected Timer dataTimer;
    protected int seconds;
    protected Boolean isRunning;
    protected List<Device> pwalDevicesList;

    protected static final Logger log = LoggerFactory
            .getLogger(DataPusher.class);

    public DataPusher(int seconds, Pwal pwal)
    {
        this.isRunning = false;
        this.dataTimer = new Timer();
        this.seconds = seconds;
        this.startTimer(this.seconds);
        this.pwalDevicesList = new ArrayList<>();
    }

    protected Timer getTimer()
    {
        return this.dataTimer;
    }

    public synchronized void startTimer(int tickSeconds)
    {

            if (!this.isRunning)
            {
                log.info("Starting data pusher timer.");
                this.getTimer().scheduleAtFixedRate(this, this.seconds * 1000,
                        this.seconds * 1000);
                this.isRunning = true;
            }
            else
                log.warn("Cannot start data pusher timer. Timer is already running.");
       
    }

    public synchronized void stopTimer()
    {

            if (isRunning)
            {
                log.info("Stopping data pusher timer");
                this.getTimer().cancel();
                this.isRunning = false;
            }
            else
                log.warn("Cannot stop data pusher timer. Timer is already stopped");

    }
}

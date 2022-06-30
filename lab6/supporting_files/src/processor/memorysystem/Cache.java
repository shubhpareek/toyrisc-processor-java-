package processor.memorysystem;

import generic.*;
import processor.Clock;
import processor.Processor;
import configuration.Configuration;
import processor.pipeline.MemoryAccess;

public class Cache implements Element {
    Processor containingProcessor;
    //All necessary data related to cache
    int cacheSize, cacheLatency, noOfLines, noOfSets, cacheMissAddress, writeData;
    //The cache
    CacheLine[] realcachee;
    //Element for cache miss
    Element cacheMissElement;
    //Will be true if there is cache read and false if cache write
    Boolean isRead;
    
    //Function to handle the cache miss in cache read and write
    public void handleCacheMiss(int address, Element requestingElement){
        //Add event
        Simulator.getEventQueue().addEvent
        (
                new MemoryReadEvent
                (
                        Clock.getCurrentTime() + Configuration.mainMemoryLatency,
                        this,
                        containingProcessor.getMainMemory(),
                        address
                )
        );
        //Set the address of the cache miss
        cacheMissAddress = address;
        //Set the element of cache miss
        cacheMissElement = requestingElement;
    }
    
    //Function to handle the cache read operation
    public void cacheRead(int address, Element requestingElement) {
        //number of bits in the index is log_2_(number of lines)
        int indexxx = (int) (Math.log(noOfLines) / Math.log(2));
        //Address of cache
        int cacheAddress;
        if(indexxx == 0)
        {
            cacheAddress = 0;
        }
        else
        {
            //Extracting cache address from the bits
            cacheAddress = address>>>(32-indexxx);
        }
        //Get the cache tag
        int cacheTag = realcachee[cacheAddress].getTag();
        //If Hit then read from cache
        if(cacheTag == address)
        {
            //Add event
            Simulator.getEventQueue().addEvent
            (
                    new MemoryResponseEvent
                    (
                            Clock.getCurrentTime(),
                            this,
                            requestingElement,
                            realcachee[cacheAddress].getData()
                    )
            );
        }
        //Miss
        else
        {
            //Set isRead as true as it is a read miss
            isRead = true;
            //Call the cache miss function
            handleCacheMiss(address, requestingElement);
        }
    }
    
    //Function to handle the cache write operation
     public void cacheWrite(int address, int value, Element requestingElement) {
        //number of bits in the index is log_2_(number of lines)
        int indexxx = (int) (Math.log(noOfLines) / Math.log(2));
        //Address of cache
        int cacheAddress;
        if(indexxx == 0)
        {
            cacheAddress = 0;
        }
        else
        {
            //Extracting cache address from the bits
            cacheAddress = address>>>(32-indexxx);
        }
        //Get the cache tag
        int cacheTag = realcachee[cacheAddress].getTag();
        //If Hit
        if(cacheTag == address)
        {
            realcachee[cacheAddress].setData(value);
            //Add event
            Simulator.getEventQueue().addEvent
            (
                    new MemoryWriteEvent
                    (
                            Clock.getCurrentTime(),
                            this,
                            containingProcessor.getMainMemory(),
                            address,
                            value
                    )
            );
            //Memory Access is not busy
            ((MemoryAccess)requestingElement).EX_MA_Latch.setMA_Busy(false);
            //Register write is enabled
            ((MemoryAccess)requestingElement).MA_RW_Latch.setRW_enable(true);
        }
        //Miss
        else
        {
            //Set the value of data to write
            writeData = value;
            //Set is read as false as it is a write miss
            isRead = false;
            //Call the Cache miss function 
            handleCacheMiss(address, requestingElement);
        }
    }

    public Cache(Processor processor, int size) {
        //Set the processor
        this.containingProcessor = processor;
        //Set the cache size
        this.cacheSize = size;
        //1 line is 4B so divide size by 4
        this.noOfLines = size  >> 2;
        //Associativity is 1 so number of sets = number of lines
        this.noOfSets = noOfLines  ;
        //Set the latency for different cache sizes
        switch(size)
        {
            case 4:
                cacheLatency = 1;
                break;
            case 8:
                cacheLatency = 2;
                break;
            case 32:
                cacheLatency = 4;
                break;
            case 128:
                cacheLatency = 8;
                break;
            case 1024:
                cacheLatency = 12;
                break;
        }
        //Allocate memory space for the cache
        realcachee = new CacheLine[noOfLines];
        for(int i = 0; i < noOfLines; i++)
        {
            realcachee[i] = new CacheLine();
        }
    }

    //Function to get the cache latency value
    public int getCacheLatency() { 
        return cacheLatency;
    }
    
    //Function to handle response from cache
    public void handleResponse(int value) {
         //number of bits in the index is log_2_(number of lines)
        int indexxx = (int) (Math.log(noOfLines) / Math.log(2));
        int cacheAddress;
        //Set the cache address
        if(indexxx == 0)
        {
            cacheAddress = 0;
        }
        else
        {
            //Extracting cache address from the bits
            cacheAddress = cacheMissAddress>>>(32-indexxx);
        }
        //Set data
        realcachee[cacheAddress].setData(value);
        //Set tag
        realcachee[cacheAddress].setTag(cacheMissAddress);
        //If there here was a read
        if(isRead)
        {
            //Add event
            Simulator.getEventQueue().addEvent
            (
                    new MemoryResponseEvent
                    (
                            Clock.getCurrentTime(),
                            this,
                            cacheMissElement,
                            value
                    )
            );
        }
        //Else there was a write
        else
        {
            cacheWrite(cacheMissAddress, writeData, cacheMissElement);
        }
    }

    //Funtion to handle the events
    @Override
    public void handleEvent(Event e) {
        //If the event is a memory read
        if(e.getEventType() == Event.EventType.MemoryRead)
        {
            MemoryReadEvent event = (MemoryReadEvent) e;
            cacheRead(event.getAddressToReadFrom(), event.getRequestingElement());
        }
        //If the event is memory write
        else if(e.getEventType() == Event.EventType.MemoryWrite)
        {
            MemoryWriteEvent event = (MemoryWriteEvent) e;
            cacheWrite(event.getAddressToWriteTo(), event.getValue(), event.getRequestingElement());
        }
        //If the event is memory response
        else if(e.getEventType() == Event.EventType.MemoryResponse)
        {
            MemoryResponseEvent event = (MemoryResponseEvent) e;
            handleResponse(event.getValue());
        }
    }
}


#include "diskdriver.h"
#include <stdio.h>
#include <stdlib.h>
#include <pthreads.h>

void read/*and write*/function(/*shared strut*/)
{
    /* loop until we get told to stop (get a flag or something)
     *
     * pop off read/write buffer to get task, block until it has something to do
     *
     * process the task, wait for the read/write to complete
     *
     * package a response into the response buffer, getting the pid/voucher/whatever of the SD
     *
     * repeat
     */
}

void init_disk_driver(DiskDevice dd, void *mem_start, unsigned long mem_length, FreeSectorDescriptorStore *fsds_ptr)
{
    //diskdevice
    DiskDevice *disk;
    disk = dd;

    //manipluate memory for SectorDiscriptorStore
    *fsds_prt = create_fsds();
    create_free_sector_discriptors(fsds_ptr, mem_start, mem_lenght);

    //allocate buffers
    //not sure how long/short these should really be
    BoundedBuffer readbuf = createBB(100);
    BoundedBuffer writebuf = createBB(100);
    BoundedBuffer responsebuf = createBB(200);

    //sort out threading
    pthread_t readthread, writethread;

    if (pthread_create(&readthread, NULL, /*readfunction*/, (void *)/*pointer to struct with shared data*/)) {
        fprintf(stderr, "Error creating Read Thread!");
        return 1;
    }

    if (pthread_create(&writethread, NULL, /*writefunction*/, (void *)/*pointer to struct with shared data*/)) {
        fprintf(stderr, "Error creating Read Thread!");
        return 1;
    }
}

void blocking_write_sector(SectorDescriptor sd, Voucher *v)
{
    /* check if our buffer has space, block until it does, get mutex
     *
     * stick request on end of buffer
     *
     * edit voucher to represent request status
     */
}

int nonblocking_write_sector(SectorDescriptor sd, Voucher *v)
{
    /* check if buffer has space, if not return appopertily, if it does continue
     *
     * stick request on end of buffer
     *
     * edit voucher to represent request status
     */
}

//see above
void blocking_read_sector(SectorDescriptor sd, Voucher *v)
{
}

int nonblocking_read_sector(SectorDescriptor sd, Voucher *v)
{
}

int redeem_voucher(Voucher v, SectorDescriptor *sd)
{
    /* look at voucher, check the respose buffer
     * if the response isn't there block
     * if it is there see if read/write failed/passed
     *
     * return 1 and SD if passed
     *
     * return 0 and null if failed
     */
}

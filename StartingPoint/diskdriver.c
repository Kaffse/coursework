#include "diskdriver.h"
#include "BoundedBuffer.h"
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

struct sharedres {
    DiskDevice *disk;
    FreeSectorDescriptorStore *store;
    BoundedBuffer readbuf, writebuf, responsebuf;
};

void *readfromdisk(sharedres *share)
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
    BufferedItem item;
    vouchstruct *cur;

    while (true) {
        item = blockingReadBB(share->readbuf);
        cur = (vouchstruct *)item;
        pthread_mutex_lock(&(cur->lock));
        if (read_sector(share->disk, cur->store))
        {
            cur->success = 1;
        }
        else
        {
            cur->success = 0;
        }
        cur->isready = 1;
        pthread_cond_signal(&(cur->readycd));
        pthread_mutex_unlock(&(cur->lock));
    }
}

void *writetodisk(sharedres *share)
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
    BufferedItem item;
    vouchstruct *cur;

    while (true) {
        item = blockingReadBB(share->readbuf);
        cur = (vouchstruct *)item;
        pthread_mutex_lock(&(cur->lock));
        if (write_sector(share->disk, cur->store))
        {
            cur->success = 1;
        }
        else
        {
            cur->success = 0;
        }
        cur->isready = 1;
        pthread_cond_signal(&(cur->readycd));
        pthread_mutex_unlock(&(cur->lock));
    }
}

void init_disk_driver(DiskDevice dd, void *mem_start, unsigned long mem_length, FreeSectorDescriptorStore *fsds_ptr)
{
    sharedres *share;

    struct vouchstrut {
        SectorDescriptor secdes;
        int success;
        int isready;
        pthread_mutex_t lock;
        pthread_cond_t readycd;
    };

    //diskdevice
    DiskDevice *disk;
    share->disk = dd;

    //manipluate memory for SectorDiscriptorStore
    *fsds_ptr = create_fsds();
    create_free_sector_discriptors(fsds_ptr, mem_start, mem_length);
    share->store = *fsds_ptr;

    //allocate buffers
    //not sure how long/short these should really be
    share->readbuf = createBB(100);
    share->writebuf = createBB(100);

    //sort out threading
    pthread_t readthread, writethread;

    if (pthread_create(&readthread, NULL, readfromdisk, (void *)share)) {
        fprintf(stderr, "Error creating Read Thread!");
    }

    if (pthread_create(&writethread, NULL, writetodisk, (void *)share)) {
        fprintf(stderr, "Error creating Read Thread!");
    }
}

void init_voucher(SectorDescriptor sd, Voucher *voucher)
{
    vouchstrut vouch = malloc(sizeof(vouchstruct));
    vouch.store = sd;
    vouch.success = 0;
    vouch.isready = 0;
    pthread_mutex_init(&vouch.lock, NULL);
    pthread_cond_init(&vouchcd.ready, NULL);
    **voucher = vouch;
}

void blocking_write_sector(SectorDescriptor sd, Voucher *v)
{
    /* check if our buffer has space, block until it does, get mutex
     *
     * stick request on end of buffer
     *
     * edit voucher to represent request status
     */
    init_voucher(sd, v);
    blockingWriteBB(share->writebuf, (BufferedItem) *v);
}

int nonblocking_write_sector(SectorDescriptor sd, Voucher *v)
{
    /* check if buffer has space, if not return appopertily, if it does continue
     *
     * stick request on end of buffer
     *
     * edit voucher to represent request status
     */
    init_voucher(sd, v);
    return (nonblockingWriteBB(share->writebuf, (BufferedItem) *v));
}

//see above
void blocking_read_sector(SectorDescriptor sd, Voucher *v)
{
    blocking_get_sd(share->store, sd);
    blockingWriteBB(share->readbuf, (BufferedItem) *v);
}

int nonblocking_read_sector(SectorDescriptor sd, Voucher *v)
{
    if (nonblocking_get_sd(share->store, sd)) {return 0;}
    return (nonblockingWriteBB(share->readbuf, (BufferedItem) *v));
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
    pthread_mutex_lock(&(v->lock));
    while (v->isready < 1) {
        pthread_cond_wait(&(v->readycv), &(v->lock));
    }
    sd = v->store;
    pthread_mutex_unlock(&(v->lock));
    return success;
}

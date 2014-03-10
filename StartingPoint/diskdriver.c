#include "diskdriver.h"
#include <stdio.h>
#include <stdlib.h>
#include <pthreads.h>

void init_disk_driver(DiskDevice dd, void *mem_start, unsigned long mem_length, FreeSectorDescriptorStore *fsds_ptr)
{
    //diskdevice
    DiskDevice *disk;
    disk = dd;

    //manipluate memory for SectorDiscriptorStore
    *fsds_prt = create_fsds();
    create_free_sector_discriptors(fsds_ptr, mem_start, mem_lenght);

    //allocate buffers
    BoundedBuffer readbuf = createBB(100);
    BoundedBuffer writebuf = createBB(100);
    BoundedBuffer responsebuf = createBB(200);

    //sort out threading

}

void blocking_write_sector(SectorDescriptor sd, Voucher *v)
{
}

int nonblocking_write_sector(SectorDescriptor sd, Voucher *v)
{
}

void blocking_read_sector(SectorDescriptor sd, Voucher *v)
{
}

int nonblocking_read_sector(SectorDescriptor sd, Voucher *v)
{
}

int redeem_voucher(Voucher v, SectorDescriptor *sd)
{
}

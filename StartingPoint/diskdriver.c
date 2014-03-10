#include "diskdriver.h"

void init_disk_driver(DiskDevice dd, void *mem_start, unsigned long mem_length, FreeSectorDescriptorStore *fsds_ptr)
{
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

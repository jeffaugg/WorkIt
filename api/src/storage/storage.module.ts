import { Module } from '@nestjs/common';
import { StorageController } from './storage.controller';
import { StorageProvider } from 'src/shared/container/providers/storage/interface/storage.provider';
import { S3StorageProvider } from 'src/shared/container/providers/storage/s3-storage.provider';
import { ConfigModule } from '@nestjs/config';

@Module({
  controllers: [StorageController],
  imports: [ConfigModule],
  providers: [
    {
      provide: StorageProvider,
      useClass: S3StorageProvider,
    },
  ],
  exports: [StorageProvider],
})
export class StorageModule {}
